package cn.diaovision.omnicontrol.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by liulingfeng on 2017/4/6.
 */
public class ByteBufferTest {
    @Test
    public void crud() throws Exception {
        ByteBuffer byteBuff = new ByteBuffer(10);

        byte[] bytes = new byte[14];
        for (int m = 0; m < 14; m ++){
            bytes[m] = (byte)m;
        }
        byteBuff.push(bytes, bytes.length);
        assertEquals("rw", byteBuff.rw, 4);

        byte[] bs = new byte[10];
        byteBuff.read(bs, 4);
        assertEquals("read", bs[0], 4);
        byteBuff.pop(bs, 4);
        assertEquals("pop", bs[0], 4);
        byteBuff.read(bs, 4);
        assertEquals("read", bs[0], 8);
        assertEquals("rw", byteBuff.rw, 8);
        byteBuff.pop(bs, 4);
        assertEquals("rw", byteBuff.rw, 2);
        byteBuff.pop(bs, 1);
        assertEquals("rw", byteBuff.rw, 3);

        assertEquals("current", byteBuff.c, 4);
        assertEquals("tail", byteBuff.t, 9);
        assertEquals("contentLen", byteBuff.getContentLen(), 1);
    }
}