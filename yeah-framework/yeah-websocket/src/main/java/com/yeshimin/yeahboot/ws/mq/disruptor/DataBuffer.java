package com.yeshimin.yeahboot.ws.mq.disruptor;

import java.nio.ByteBuffer;

public class DataBuffer {

    private ByteBuffer bb;
    private int length;

    public DataBuffer() {
        this.bb = ByteBuffer.allocate(8);
        this.length = 0;
    }

    public void putString(String s) {
        int reqCapacity = this.calcSize(bb, s);
        if (bb.capacity() < reqCapacity) {
            bb = ByteBuffer.allocate(reqCapacity);
        }
        byte[] data = s.getBytes();
        bb.clear();
        bb.put(data, 0, data.length);
        this.length = data.length;
    }

    public byte[] getBytes() {
        byte[] data = new byte[this.length];
        bb.flip();
        bb.get(data, 0, this.length);
        return data;
    }

    public String getString() {
        byte[] data = new byte[this.length];
        bb.flip();
        bb.get(data, 0, this.length);
        return new String(data);
    }

    // ================================================================================

    /**
     * calc ByteBuffer required size
     * 按照二进制指数放大一倍
     */
    private int calcSize(ByteBuffer bb, String s) {
        int size = s.getBytes().length;
        int capacity = bb.capacity();
        while (capacity < size) {
            capacity <<= 1;
        }
        return capacity;
    }
}
