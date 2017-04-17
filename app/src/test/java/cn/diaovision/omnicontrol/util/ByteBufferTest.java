package cn.diaovision.omnicontrol.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by liulingfeng on 2017/4/6.
 */
public class ByteBufferTest {
    @Test
    public void push1() throws Exception {

    }

    @Test
    public void read1() throws Exception {

    }

    @Test
    public void pop1() throws Exception {

    }

    @Test
    public void flush1() throws Exception {

    }

    @Test
    public void reset1() throws Exception {

    }

    @Test
    public void getContentLen1() throws Exception {

    }

    @Test
    public void push() throws Exception {
        ByteBuffer byteBuff = new ByteBuffer(10);

        byte[] bytes = new byte[14];
        for (int m = 0; m < 14; m ++){
            bytes[m] = (byte)m;
        }
        byteBuff.push(bytes, bytes.length);

        assertEquals("current", byteBuff.c, 3);
        assertEquals("tail", byteBuff.t, 4);
    }


    @Test
    public void read() throws Exception {

    }

    @Test
    public void pop() throws Exception {

    }

    @Test
    public void flush() throws Exception {

    }

    @Test
    public void reset() throws Exception {

    }

    @Test
    public void getContentLen() throws Exception {

    }

}