package cn.diaovision.omnicontrol.util;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class ByteUtils {

    public static byte[] int2bytes(int val, int len){
        byte[] bytes = new byte[len];
        for (int m = 0; m < len; m ++){
            bytes[m] = (byte) ( (val >>(8*m)) & 0xff );
        }
        return bytes;
    }

    public static int bytes2int(byte[] bytes){
        int val = 0;
        for (int m = 0; m < bytes.length; m ++){
            val += bytes[m] << (8*m);
        }
        return val;
    }

    /*简单的异或校验计算*/
    public static byte checksum (byte[] bytes){
        byte cs = 0x00;
        for (byte b : bytes) {
            cs ^= b;
        }
        return cs;
    }
}
