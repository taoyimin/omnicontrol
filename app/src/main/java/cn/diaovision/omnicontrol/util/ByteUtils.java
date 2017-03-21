package cn.diaovision.omnicontrol.util;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class ByteUtils {
    public static byte[] toHexChar(byte b, int len){
        if (len == 2) {
            byte[] bytes = new byte[2];

            int val = (int) (b & 0xff);
            if (val <= 0x0f) {
                bytes[0] = '0';
                bytes[1] = (byte) (val&0x0f);
            }
            return bytes;
        }
        else if (len == 3) {
            byte[] bytes = new byte[3];
            int val = (int) (b & 0xff);
            if (val <= 0x0f) {
                bytes[0] = '0';
                bytes[1] = '0';
                bytes[2] = (byte) (val&0x0f);
            }
            else {
                bytes[0] = '0';
                bytes[1] = (byte) ((val >> 8) & 0x0f);
                bytes[2] = (byte) (val & 0x0f);
            }
            return bytes;
        }
        else{
            return null;
        }
    }

    public static byte checksum (byte[] bytes){
        byte cs = 0x00;
        for (byte b : bytes) {
            cs ^= b;
        }
        return cs;
    }
}
