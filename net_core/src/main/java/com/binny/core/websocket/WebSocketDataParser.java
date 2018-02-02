package com.binny.core.websocket;


import com.binny.sdk.constant.Constant;
import com.binny.core.logger.JJLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;


/**
 * 数据解析
 */
public class WebSocketDataParser {
    private static final String TAG = "WebSocketDataParser";

    private WebSocketHelper mClient;

    /**
     * 是否存在掩码
     */
    private boolean mMasking = true;

    private int mStage;

    /**
     * 是否是尾数据帧
     */
    private boolean mFinal;
    /**
     * 是否是掩码帧
     */
    private boolean mMasked;
    /**
     * 当前数据帧的消息类型
     */
    private int mCurrentOpcode;
    /**
     * 数据长度
     */
    private int mLengthSize;
    private int mShortLength;//0~125之间的长度
    private int mMode;

    private byte[] mMask = new byte[0];//掩码
    private byte[] mPayloadData = new byte[0];//消息实际内容

    private boolean mClosed = false;

    private ByteArrayOutputStream mPayloadByteArrayBuffer = new ByteArrayOutputStream();

    private static final int BYTE = 255;
    /*
    * 数据帧的第一字节标志位
    * */
    private static final int FIN = 128;//如果是尾帧，则该数据帧的第一字节的最高位位1;十进制为128
    private static final int RSV1 = 64;//数据帧的第一字节的第二位，保留位
    private static final int RSV2 = 32;//数据帧的第一字节的第三位，保留位
    private static final int RSV3 = 16;//数据帧的第一字节的第四位，保留位
    private static final int OPCODE = 15;//数据帧的第一字节的低四位，用于规定文件类型

    /*
    * 数据帧的第二字节标志位
    * */
    private static final int MASK = 128;//如果有掩码，则该数据帧的第二字节的最高位位1;十进制为128

    /*
    * 第二字节的后7 位用于标识改数据包的数据(payload data)的长度。
    * 如果值为0-125，那么该值就是payload data的真实长度;
    * 如果值为126，那么该7位后面紧跟着的2个字节就是payload data的真实长度;
    * 如果值为127，那么该7位后面紧跟着的8个字节就是payload data的真实长度。
    * 长度遵循一个原则，就是用最少的字节表示长度。
    * 举个例子，当payload data的真实长度是124时，在0-125之间，必须用7位表示;
    * 不允许将这7位表示成126或者127，然后后面用2个字节或者8个字节表示124，这样做就违反了原则。
    * */
    private static final int PAYLOAD_LEN = 127;//01111111

    /*
    * 文本数据
    * */
    private static final int MODE_TEXT = 1;
    /*
    * 二进制数据
    * */
    private static final int MODE_BINARY = 2;

    /*
    * 下面是16进制的标识符
    * */
    private static final int OP_CONTINUATION = 0;//标识一个中间数据包
    private static final int OP_TEXT = 1;//标识一个文本儿类型的数据包
    private static final int OP_BINARY = 2;//标识一个二进制的数据包
    private static final int OP_CLOSE = 8;//标识一个关闭的数据包
    private static final int OP_PING = 9;//标识一个ping类型的数据包
    private static final int OP_PONG = 10;//标识一个pong类型的数据包

    /**
     * 规定的类型
     * */

    private static final List<Integer> OPCODES = Arrays.asList(
            OP_CONTINUATION,
            OP_TEXT,
            OP_BINARY,
            OP_CLOSE,
            OP_PING,
            OP_PONG
    );
    /*
    * 常用的类型
    * */
    private static final List<Integer> FRAGMENTED_OPCODES = Arrays.asList(
            OP_CONTINUATION, OP_TEXT, OP_BINARY
    );


    public WebSocketDataParser(WebSocketHelper client) {
        mClient = client;
    }

    private static byte[] mask(byte[] payload, byte[] mask, int offset) {
        if (mask.length == 0) return payload;

        for (int i = 0; i < payload.length - offset; i++) {
            payload[offset + i] = (byte) (payload[offset + i] ^ mask[i % 4]);
        }
        return payload;
    }

    private final int PARSE_FIRST_BYTE = 0;
    private final int PARSE_SECOND_BYTE = 1;
    public void parseStream(WebSocketInputStream inputStream, final Socket socket) throws IOException {


        while ((!socket.isClosed())) {

            if (inputStream.available() == -1) {

                break;
            }

            switch (mStage) {
                case PARSE_FIRST_BYTE:
                    byte firstByte = inputStream.readByte();
                    parseFirstByte(firstByte);
                    break;
                case PARSE_SECOND_BYTE:
                    byte secondByte = inputStream.readByte();
                    parseLength(secondByte);
                    break;
                case 2:
                    parseExtendedLength(inputStream.readBytes(mLengthSize));
                    break;
                case 3:
                    mMask = inputStream.readBytes(4);
                    mStage = 4;
                    break;
                case 4:
                    /*
                    * 剩下的都是数据
                    * */
                    mPayloadData = inputStream.readBytes(mShortLength);
                    emitFrame();
                    mStage = 0;
                    break;
            }
        }
        Constant.HANDLER.post(new Runnable() {
            @Override
            public void run() {
                mClient.getListener().onDisconnect("EOF");//post
            }
        });
    }

    /**
     * 解析数据帧的第一字节
     *
     * @param firstByte 第一字节
     * @throws ProtocolError 协议异常
     */
    private void parseFirstByte(byte firstByte) throws ProtocolError {
        /*
        * 解析第2 位、3 位、4 位 是否有扩展约定
        * */
        /*
        * RSV1 = 01000000 = 64
        * RSV2 = 00100000 = 32
        * RSV3 = 00010000 = 16
        * */
        boolean rsv1 = (firstByte & RSV1) == RSV1;

        boolean rsv2 = (firstByte & RSV2) == RSV2;

        boolean rsv3 = (firstByte & RSV3) == RSV3;
        /*
        * 如果没有扩展约定。则此三位置必须是0
        * */
        if (rsv1 || rsv2 || rsv3) {
            throw new ProtocolError("RSV not zero");
        }

        /*
        * 该函数的核心业务，判断数据帧中的消息类型
        * */
        mCurrentOpcode = (firstByte & OPCODE);

        mMask = new byte[0];

        mPayloadData = new byte[0];

        /*
        * 如果不是暂定的数据类型；则抛出异常
        * */
        if (!OPCODES.contains(mCurrentOpcode)) {
            throw new ProtocolError("Bad opcode");
        }
        /*
        * 进一步判断是否是 中间数据帧、文本或者二进制,或者消息是否在结束了
        * */

        /*
        * 是否消息结束了
        * */
        mFinal = (firstByte & FIN) == FIN;

        if (!FRAGMENTED_OPCODES.contains(mCurrentOpcode) && !mFinal) {
            throw new ProtocolError("Expected non-final packet");
        }

        mStage = 1;
    }

    /** 解析数据帧的第二字节
     * @param secondByte 第二字节
     */
    private void parseLength(byte secondByte) {

        /*
        * 是否是掩码帧
        * mMasked = 1,为掩码帧
        * */
        mMasked = (secondByte & MASK) == MASK;
        /*
        * 取第二字节的后7位，转换为十进制
        * */
        mShortLength = (secondByte & PAYLOAD_LEN);

        if (mShortLength >= 0 && mShortLength <= 125) {
            /* 在 0~125之间
            * mLength则为数据的真实长度;
            * 1、如果是掩码帧，
            * */
            mStage = mMasked ? 3 : 4;
        } else {
            mLengthSize = (mShortLength == 126) ? 2 : 8;
            mStage = 2;
        }
    }

    private void parseExtendedLength(byte[] buffer) throws ProtocolError {
        mShortLength = getInteger(buffer);
        mStage = mMasked ? 3 : 4;
    }

    public byte[] frame(String data) {
        return frame(data, OP_TEXT, -1);
    }

    public byte[] frame(byte[] data) {
        return frame(data, OP_BINARY, -1);
    }

    private byte[] frame(byte[] data, int opcode, int errorCode) {
        return frame((Object) data, opcode, errorCode);
    }

    private byte[] frame(String data, int opcode, int errorCode) {
        return frame((Object) data, opcode, errorCode);
    }


    /**
     * 构建数据帧
     *
     * @param data      元数据
     * @param opcode
     * @param errorCode
     * @return 数据包
     */
    private byte[] frame(Object data, int opcode, int errorCode) {
        if (mClosed) {
            JJLogger.logInfo(TAG, "连接断开!");
            return null;
        }


        byte[] buffer = (data instanceof String) ? decode((String) data) : (byte[]) data;//将数据转换成byte数组
        int insert = (errorCode > 0) ? 2 : 0;

        int length = buffer.length + insert;

        int header = (length <= 125) ? 2 : (length <= 65535 ? 4 : 10);

        int offset = header + (mMasking ? 4 : 0);
        int masked = mMasking ? MASK : 0;
        byte[] frame = new byte[length + offset];

        frame[0] = (byte) ((byte) FIN | (byte) opcode);

        if (length <= 125) {
            frame[1] = (byte) (masked | length);
        } else if (length <= 65535) {
            frame[1] = (byte) (masked | 126);
            frame[2] = (byte) Math.floor(length / 256);
            frame[3] = (byte) (length & BYTE);
        } else {
            frame[1] = (byte) (masked | 127);
            frame[2] = (byte) (((int) Math.floor(length / Math.pow(2, 56))) & BYTE);
            frame[3] = (byte) (((int) Math.floor(length / Math.pow(2, 48))) & BYTE);
            frame[4] = (byte) (((int) Math.floor(length / Math.pow(2, 40))) & BYTE);
            frame[5] = (byte) (((int) Math.floor(length / Math.pow(2, 32))) & BYTE);
            frame[6] = (byte) (((int) Math.floor(length / Math.pow(2, 24))) & BYTE);
            frame[7] = (byte) (((int) Math.floor(length / Math.pow(2, 16))) & BYTE);
            frame[8] = (byte) (((int) Math.floor(length / Math.pow(2, 8))) & BYTE);
            frame[9] = (byte) (length & BYTE);
        }

        if (errorCode > 0) {
            frame[offset] = (byte) (((int) Math.floor(errorCode / 256)) & BYTE);
            frame[offset + 1] = (byte) (errorCode & BYTE);
        }
        System.arraycopy(buffer, 0, frame, offset + insert, buffer.length);

        if (mMasking) {
            byte[] mask = {
                    (byte) Math.floor(Math.random() * 256), (byte) Math.floor(Math.random() * 256),
                    (byte) Math.floor(Math.random() * 256), (byte) Math.floor(Math.random() * 256)
            };
            System.arraycopy(mask, 0, frame, header, mask.length);
            mask(frame, mask, offset);
        }

        return frame;
    }

    public void ping(String message) {
        mClient.send(frame(message, OP_PING, -1), "ping");
    }

    public void close(int code, String reason) {
        if (mClosed) return;
        mClient.send(frame(reason, OP_CLOSE, code), "removeRunnableAndCloseSocket");
        mClosed = true;
    }

    private void emitFrame() throws IOException {

        final byte[] payload = mask(mPayloadData, mMask, 0);

        int opcode = mCurrentOpcode;

        if (opcode == OP_CONTINUATION) {
            if (mMode == 0) {
                throw new ProtocolError("Mode was not set.");
            }
            mPayloadByteArrayBuffer.write(payload);

            /*
            * 如果是最后一帧
            * */
            if (mFinal) {
                final byte[] message = mPayloadByteArrayBuffer.toByteArray();
                if (mMode == MODE_TEXT) {
                    mClient.getListener().onMessage(encode(message));
                } else {
                    Constant.HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            mClient.getListener().onMessage(message);
                        }
                    });
                }
                mMode = 0;
                mPayloadByteArrayBuffer.reset();
            }

        } else if (opcode == OP_TEXT) {
            if (mFinal) {
                String messageText = encode(payload);
                mClient.getListener().onMessage(messageText);
            } else {
                mMode = MODE_TEXT;
                mPayloadByteArrayBuffer.write(payload);
            }

        } else if (opcode == OP_BINARY) {
            if (mFinal) {
                Constant.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mClient.getListener().onMessage(payload);
                    }
                });
            } else {
                mMode = MODE_BINARY;
                mPayloadByteArrayBuffer.write(payload);
            }

        } else if (opcode == OP_CLOSE) {
            int code = (payload.length >= 2) ? 256 * payload[0] + payload[1] : 0;
            final String reason = (payload.length > 2) ? encode(slice(payload, 2)) : null;
            JJLogger.logInfo(TAG, "Got removeRunnableAndCloseSocket op! " + code + " " + reason);
            Constant.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    mClient.getListener().onDisconnect(reason);
                }
            });

        } else if (opcode == OP_PING) {
            if (payload.length > 125) {
                throw new ProtocolError("Ping payload too large");
            }
            JJLogger.logInfo(TAG, "Sending pong!!");
            mClient.sendFrame(frame(payload, OP_PONG, -1), "");

        } else if (opcode == OP_PONG) {
            String message = encode(payload);
            // FIXME: Fire callback...
            JJLogger.logInfo(TAG, "Got pong! " + message);
        }
    }

    private String encode(byte[] buffer) {
        try {
            return new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decode(String string) {
        try {
            return (string).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private int getInteger(byte[] bytes) throws ProtocolError {
        long i = byteArrayToLong(bytes, 0, bytes.length);
        if (i < 0 || i > Integer.MAX_VALUE) {
            throw new ProtocolError("Bad integer: " + i);
        }
        return (int) i;
    }

    private byte[] slice(byte[] array, int start) {
        return Arrays.copyOfRange(array, start, array.length);
    }

    public static class ProtocolError extends IOException {
        public ProtocolError(String detailMessage) {
            super(detailMessage);
        }
    }

    private static long byteArrayToLong(byte[] b, int offset, int length) {
        if (b.length < length)
            throw new IllegalArgumentException("length must be less than or equal to b.length");

        long value = 0;
        for (int i = 0; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

}
