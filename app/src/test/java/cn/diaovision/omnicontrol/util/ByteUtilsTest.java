package cn.diaovision.omnicontrol.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by liulingfeng on 2017/4/11.
 */
public class ByteUtilsTest {
    @Test
    public void int2bytes() throws Exception {
        int a = 0x12345678;
        byte[] bytes = ByteUtils.int2bytes(a, 4);
        assertEquals(bytes[0], 0x12);
        assertEquals(bytes[1], 0x34);
        assertEquals(bytes[2], 0x56);
        assertEquals(bytes[3], 0x78);
    }

    @Test
    public void bytes2int() throws Exception {
        byte[] bytes = new byte[4];
        bytes[0] = 0x12;
        bytes[1] = 0x34;
        bytes[2] = 0x56;
        bytes[3] = 0x78;
        int a = ByteUtils.bytes2int(bytes, 0, 4);
        assertEquals(a, 0x12345678);
    }

}