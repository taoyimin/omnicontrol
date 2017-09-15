package cn.diaovision.omnicontrol.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by liulingfeng on 2017/4/11.
 */
public class ByteUtilsTest {
    @Test
    public void ip2num() throws Exception {
        String ip = "192.168.31.211";

        int num = ByteUtils.ip2num(ip);
        byte[] bytes = ByteUtils.int2bytes(num, 4);
        assertEquals(bytes[0]&0xff, 192);
        assertEquals(bytes[1]&0xff, 168);
        assertEquals(bytes[2]&0xff, 31);
        assertEquals(bytes[3]&0xff, 211);
    }

    @Test
    public void num2ip() throws Exception {
        String ip = "192.168.31.211";

        int num = ByteUtils.ip2num(ip);
        byte[] bytes = ByteUtils.int2bytes(num, 4);
        assertEquals(ip, ByteUtils.num2ip(num));
    }

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

    @Test
    public void bytes2ascii() throws Exception {
        String s="ABC123";
        byte[] bytes=new byte[6];
        bytes[0]=65;
        bytes[1]=66;
        bytes[2]=67;
        bytes[3]=49;
        bytes[4]=50;
        bytes[5]=51;
        assertEquals(s,ByteUtils.bytes2ascii(bytes));
    }

    @Test
    public void bytes2string() throws Exception {
        String s="65,66,67,49,50,51";
        byte[] bytes=new byte[6];
        bytes[0]=65;
        bytes[1]=66;
        bytes[2]=67;
        bytes[3]=49;
        bytes[4]=50;
        bytes[5]=51;
        assertEquals(s,ByteUtils.bytes2string(bytes));
    }

    @Test
    public void string2bytes() throws Exception {
        byte[] bytes1=new byte[6];
        bytes1[0]=65;
        bytes1[1]=66;
        bytes1[2]=67;
        bytes1[3]=49;
        bytes1[4]=50;
        bytes1[5]=51;
        String s="65,66,67,49,50,51";
        byte[] bytes2=ByteUtils.string2bytes(s);
        assertEquals(bytes1[0],bytes2[0]);
        assertEquals(bytes1[1],bytes2[1]);
        assertEquals(bytes1[2],bytes2[2]);
        assertEquals(bytes1[3],bytes2[3]);
        assertEquals(bytes1[4],bytes2[4]);
        assertEquals(bytes1[5],bytes2[5]);
    }
}