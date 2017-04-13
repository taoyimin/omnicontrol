package cn.diaovision.omnicontrol.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class ByteUtils {

    public static byte[] int2bytes(int val, int len){
        byte[] bytes = new byte[len];
        for (int m = 0; m < len; m ++){
            //big-endian
            bytes[len-m-1] = (byte) ( (val >>(8*m)) & 0xff );
        }
        return bytes;
    }

    public static int bytes2int(byte[] bytes, int offset, int len){
        if (bytes.length < offset || bytes.length < len){
            return 0;
        }

        //big-endian
        int val = 0;
        for (int m = 0; m < bytes.length; m ++){
            val += bytes[offset+len-m-1] << (8*m);
        }
        return val;
    }

    public static int ip2num(String ip){
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();
            return ByteUtils.bytes2int(bytes, 0, 4);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
