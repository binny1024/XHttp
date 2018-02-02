package com.binny.core.websocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
* DataInputStream类中的read(byte[] b)和readFully(byte[] b)读取消息到底有什么区别呢？
* 下面让我来分析一下：
* 1.其实read(byte[] b)方法和readFully(byte []b)都是利用InputStream中read（）方法，每次读取的也是一个字节，只是读取字节数组的方式不同。
*
* 2. read(byte[] b)方法实质是读取流上的字节直到流上没有字节为止，如果当声明的字节数组长度大于流上的数据长度时就提前返回;
* 而readFully(byte[] b)方法是读取流上指定长度的字节数组，
* 也就是说如果声明了长度为len的字节数组，readFully(byte[] b)方法只有读取len长度个字节的时候才返回，否则阻塞等待，如果超时，则会抛出异常 EOFException。
*
* 3.那么当发送了长度为len的字节，那么为什么用read方法用户收不全呢，
* 究其原因我们发现消息在网络中传输是没那么理想的，我们发的那部分字节数组在传送过程中可能在接受信息方的缓存当中或者在传输线路，
* 极端情况下可能在发送方的缓存当中，这样就不在流上，所以read方法提前返回了，这样就造成了各种错误。
* */

public  class WebSocketInputStream extends DataInputStream {
        public WebSocketInputStream(InputStream in) {
            super(in);
        }

        /*
        * 保证读取的数据长度
        * */

        public byte[] readBytes(int length) throws IOException {
            byte[] buffer = new byte[length];//存储读取数据的缓冲区。
            readFully(buffer);
            return buffer;
        }
    }