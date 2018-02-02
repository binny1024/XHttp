package com.binny.core.websocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public  class WebSocketInputStream extends DataInputStream {
        public WebSocketInputStream(InputStream in) {
            super(in);
        }

        public byte[] readBytes(int length) throws IOException {
            byte[] buffer = new byte[length];//存储读取数据的缓冲区。
            readFully(buffer);
            return buffer;
        }
    }