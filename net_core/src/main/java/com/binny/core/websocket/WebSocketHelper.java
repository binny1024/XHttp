package com.binny.core.websocket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.binny.sdk.constant.Constant;
import com.binny.core.logger.JJLogger;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


public class WebSocketHelper {
    private static final String TAG = "WebSocketHelper";

    private URI mURI;

    private Listener mListener;

    private List<BasicNameValuePair> mExtraHeaders;

    private Socket mSocket;

    private Thread mThread;


    private SenderThread mSenderThread;
    private WebSocketDataParser mParser;

    private WebSocketInputStream mDataInputStream;

    /**
     * 同步锁
     */
    private final Object mSendLock = new Object();

    private static TrustManager[] sTrustManagers;


    /**
     * 静态实例
     */
    private WebSocketHelper mInstance;
    /**
     * 判断是否是APP主动断开连接
     */
    private boolean bManualOperation = false;

    /**
     * 静态内部类
     */
    private static class SingletonHolder {

        private static final WebSocketHelper TASK_MANAGER = new WebSocketHelper();

    }

    private List<Runnable> mSenderRunnableList;

    /**
     * @param mListener 设置socket 回调监听
     * @return 本类实例
     */
    public WebSocketHelper setSocketListener(final Listener mListener) {
        this.mListener = mListener;
        return mInstance;
    }

    /**
     * @param mURI ws 地址
     * @return 本类实例
     */
    public WebSocketHelper setSocketURI(final URI mURI) {
        this.mURI = mURI;
        return mInstance;
    }


    /**
     * 创建数据解析器
     *
     * @return 本类实例
     */
    public WebSocketHelper createParse() {
        mParser = new WebSocketDataParser(this);
        return mInstance;
    }


    public WebSocketHelper() {
        mInstance = this;
    }

    Listener getListener() {
        return mListener;
    }

    private void createScoket() {

        JJLogger.logInfo(TAG, "WebSocketHelper.createSocketAndConnect :");

        if (mThread != null && mThread.isAlive()) {
            JJLogger.logInfo(TAG, "createSocketAndConnect: ");
            return;
        }

        if (mSocket != null && mSocket.isConnected()) {
            JJLogger.logError(TAG, "socket has already connected!");
            return;
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter out = null;
                try {
                    mThread.setName("socket 的创建线程 id = " + mThread.getId());

                    String secret = createSecret();
                    /*
                    * http://www.runoob.com/index.html?language=cn#j2se
                    * */
                    /*
                    * 获取端口号
                    * 默认端口：80
                    * */
                    int port = (mURI.getPort() != -1) ? mURI.getPort() : (mURI.getScheme().equals("wss") ? 443 : 80);
                    /*
                    * 获取路径部分
                    *
                    * 路径：/index.html
                    * */
                    String path = TextUtils.isEmpty(mURI.getPath()) ? "/" : mURI.getPath();
                    /*
                    * 查询部分
                    * 请求参数：language=cn
                    * */
                    if (!TextUtils.isEmpty(mURI.getQuery())) {
                        path += "?" + mURI.getQuery();
                    }
                    /*
                    * 协议类型
                    * */
                    String originScheme = mURI.getScheme().equals("wss") ? "https" : "http";
                    URI origin = new URI(originScheme, "//" + mURI.getHost(), null);
                    //TODO deal with SSLContext
                    mSocket = new Socket();
                    mSocket.setTcpNoDelay(true);
                    mSocket.setKeepAlive(true);
                    InetSocketAddress address = new InetSocketAddress(mURI.getHost(), port);
                    mSocket.connect(address, 10000);
                    out = new PrintWriter(mSocket.getOutputStream());
                    /*
                    * 状态行
                    * */
                    out.print("GET " + path + " HTTP/1.1\r\n");
                    out.print("Host: " + mURI.getHost() + "\r\n");
                    /*
                    * Upgrade: websocket
                    * Connection: Upgrade
                    * 这个就是Websocket的核心了，告诉 Apache 、 Nginx 等服务器：注意啦，
                    * 我发起的是Websocket协议，快点帮我找到对应的助理处理~不是那个老土的HTTP。
                    * */
                    out.print("Upgrade: websocket\r\n");
                    out.print("Connection: Upgrade\r\n");


                    /*
                    * 首先， Sec-WebSocket-Key 是一个 Base64 encode 的值，
                    * 这个是浏览器随机生成的，告诉服务器：泥煤，不要忽悠窝，我要验证尼是不是真的是Websocket助理。
                    *
                    * 然后， Sec_WebSocket-Protocol 是一个用户定义的字符串，
                    * 用来区分同URL下，不同的服务所需要的协议。简单理解：今晚我要服务A，别搞错啦~
                    * */
                    out.print("Sec-WebSocket-Key: " + secret + "\r\n");
                    /*
                    *
                    * Sec-WebSocket-Version 是告诉服务器所使用的 Websocket Draft（协议版本），
                    * 在最初的时候，Websocket协议还在 Draft 阶段，各种奇奇怪怪的协议都有，
                    * 而且还有很多期奇奇怪怪不同的东西，什么Firefox和Chrome用的不是一个版本之类的，
                    * 当初Websocket协议太多可是一个大难题。。
                    * 不过现在还好，已经定下来啦~大家都使用的一个东西~ 脱水： 服务员，我要的是13岁的噢→_→
                    * */
                    out.print("Sec-WebSocket-Version: 13\r\n");

                    out.print("Origin: " + origin.toString() + "\r\n");

                    if (mExtraHeaders != null) {
                        for (NameValuePair pair : mExtraHeaders) {
                            out.print(String.format("%s: %s\r\n", pair.getName(), pair.getValue()));
                        }
                    }
                    out.print("\r\n");
                    out.flush();

                    mDataInputStream = new WebSocketInputStream(mSocket.getInputStream());
                    /* 然后服务器会返回下列东西，表示已经接受到请求， 成功建立Websocket啦！
                    *
                    * HTTP/1.1 101 Switching Protocols
                    *
                    * Upgrade: websocket
                    *
                    * Connection: Upgrade
                    *
                    * Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=
                    *
                    * Sec-WebSocket-Protocol: chat
                    *
                    * */
                    // Read HTTP response status line.

                    StatusLine statusLine = parseStatusLine(readStatusLine(mDataInputStream));

                    if (statusLine == null) {
                        throw new HttpException("Received no reply from server.");
                    } else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
                        throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }
                    // Read HTTP response headers.
                    String line;
                    boolean validated = false;
                    while (!TextUtils.isEmpty(line = readStatusLine(mDataInputStream))) {
                        Header header = parseHeader(line);
                        if (header.getName().equals("Sec-WebSocket-Accept")) {
                            String expected = createSecretValidation(secret);
                            String actual = header.getValue().trim();
                            if (!expected.equals(actual)) {
                                throw new HttpException("Bad Sec-WebSocket-Accept header value.");
                            }
                            validated = true;
                        }
                    }
                    if (!validated) {
                        throw new HttpException("No Sec-WebSocket-Accept header.");
                    }

                    Constant.HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            mSenderRunnableList = new ArrayList<Runnable>();
                            mSenderThread = new SenderThread();
                            mSenderThread.start();
                            mListener.onConnectSuccess();
                        }
                    });
                    /*
                    * 处理网络数据在子线程中
                    * */
                    mParser.parseStream(mDataInputStream, mSocket);

                } catch (final Exception ex) {
                    if (out != null) out.close();
                    Constant.HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mSocket != null) {
                                try {
                                    if (mSocket.isConnected() && !mSocket.isInputShutdown()) {
                                        mSocket.shutdownInput();
                                    }
                                    if (mSocket.isConnected() && !mSocket.isOutputShutdown()) {
                                        mSocket.shutdownOutput();
                                    }
                                    mSocket.close();
                                } catch (IOException e) {
                                    JJLogger.logError(TAG, "could not close socket!" + e.getMessage());
                                }
                            }
                            mSocket = null;
                            if (bManualOperation) {
                                JJLogger.logInfo(TAG, "手动断开");
                                bManualOperation = false;
                                return;
                            }
                            mListener.onDisconnect("SOCKET -- 关闭");//post
                        }
                    });
                }
            }
        });
    }

    /**
     * 创建并连接socket
     */
    public void createSocketAndConnect() {
        createScoket();
        connectSocket();
    }

    private void connectSocket() {
        JJLogger.logInfo(TAG, "连接socket :");
        mThread.start();
    }

    /**
     *
     */
    public void close() {
        if (mSenderThread != null && mSenderThread.mSenderThreadHandler != null) {
            if (mSenderRunnableList != null) {
                int size = mSenderRunnableList.size();
                for (int i = 0; i < size; i++) {
                    mSenderThread.mSenderThreadHandler.removeCallbacks(mSenderRunnableList.get(i));
                }
                mSenderThread.mSenderThreadHandler.getLooper().quit();
            }
        }
        try {
            if (mSocket != null && !mSocket.isClosed()) {
                JJLogger.logInfo(TAG, "WebSocketHelper---执行---socket----close :");
                bManualOperation = true;//主动断开连接
                mDataInputStream.close();
                mSocket = null;
            }
        } catch (IOException ex) {
            mSocket = null;
            mDataInputStream = null;
            JJLogger.logInfo(TAG, "IOException--close : mSocket = null;");
        }
    }

    public boolean socketAvailable() {
        return !(mSocket == null || mSocket.isClosed());
    }

    public void send(String data) {
        sendFrame(mParser.frame(data), "");
    }

    public void send(final byte[] data, final String tag) {
//        sendFrame(mParser.frame(data), tag);
        JJLogger.logInfo("线程测试", tag);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendFrame(mParser.frame(data), tag);
            }
        };

        if (mSenderThread != null && mSenderThread.mSenderThreadHandler != null) {
            /*
            * 如果消息队列创建成功，将任务放到发送线程中执行
            * */
            Log.i(TAG, "send: 消息队列");
            mSenderRunnableList.add(runnable);
            mSenderThread.mSenderThreadHandler.post(runnable);
        } else {
            /*
            * 如果消息队列创建失败，起一个新线程执行
            * */
            Log.i(TAG, "send: 新线程");
            new Thread(runnable).start();
        }
    }

    private StatusLine parseStatusLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }
        return BasicLineParser.parseStatusLine(line, new BasicLineParser());
    }

    private Header parseHeader(String line) {
        return BasicLineParser.parseHeader(line, new BasicLineParser());
    }

    // Can't use BufferedReader because it buffers past the HTTP data.
    private String readStatusLine(WebSocketInputStream reader) throws IOException {
        int readChar = reader.read();
        if (readChar == -1) {
            return null;
        }
        StringBuilder string = new StringBuilder("");
        while (readChar != '\n') {
            if (readChar != '\r') {
                string.append((char) readChar);
            }

            readChar = reader.read();
            if (readChar == -1) {
                return null;
            }
        }
        String linr = string.toString();
        JJLogger.logInfo("line",linr);
        return linr;
    }

    private String createSecret() {
        byte[] nonce = new byte[16];
        for (int i = 0; i < 16; i++) {
            nonce[i] = (byte) (Math.random() * 256);
        }
        return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
    }

    private String createSecretValidation(String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
            return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param frame 数据
     * @param tag   数据来源
     */
    void sendFrame(final byte[] frame, final String tag) {
        try {
            synchronized (mSendLock) {
                if (mSocket == null) {
                    Log.i(TAG, "手动关闭");
                    return;
                }
                OutputStream outputStream = mSocket.getOutputStream();
                outputStream.write(frame);
                outputStream.flush();
            }

        } catch (final IOException e) {
            JJLogger.logInfo(TAG, "sendFrame :");
            mSocket = null;
            Constant.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDisconnect(e.getMessage());//post
                }
            });
        }
    }

    public interface Listener {
        void onConnectSuccess();

        void onMessage(String message);

        void onMessage(byte[] data);

        void onDisconnect(String reason);
    }

    public static void setTrustManagers(TrustManager[] tm) {
        sTrustManagers = tm;
    }

    private SSLSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, sTrustManagers, null);
        return context.getSocketFactory();
    }

    private class SenderThread extends Thread {
        Handler mSenderThreadHandler;

        @Override
        public void run() {
            JJLogger.logInfo("start","启动发送线程");
            Looper.prepare();
            mSenderThreadHandler = new Handler() {
                @Override
                public void handleMessage(final Message msg) {
                    super.handleMessage(msg);
                }
            };
            Looper.loop();
            JJLogger.logInfo("exit","终止发送线程");
        }
    }
}
