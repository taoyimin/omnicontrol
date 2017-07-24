package cn.diaovision.omnicontrol.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class ByteUtils {

    /*int转byte数组*/
    public static byte[] int2bytes(int val, int len){
        byte[] bytes = new byte[len];
        for (int m = 0; m < len; m ++){
            //big-endian
            bytes[len-m-1] = (byte) ( (val >>(8*m)) & 0xff );
        }
        return bytes;
    }

    /*byte数组转int*/
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

    /*IP字符串转数字*/
    public static int ip2num(String ip){
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();
            int val = 0;
            for (int m = 0; m < bytes.length; m ++){
                val += (bytes[m]&0xff)<<(bytes.length-m-1)*8;
            }
            return val;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /*数字转IP字符串*/
    public static String num2ip(int num){
        byte[] bytes = ByteUtils.int2bytes(num, 4);
        return String.valueOf(bytes[0]&0xff) + '.' + String.valueOf(bytes[1]&0xff) + '.' + String.valueOf(bytes[2]&0xff) + '.' + String.valueOf(bytes[3]&0xff);
    }

    /*byte数组转ascii字符串*/
    public static String bytes2ascii(byte[] bytes){
        StringBuffer sb=new StringBuffer();
        for(byte b:bytes){
            char c= (char) b;
            sb.append(c);
        }
        return sb.toString();
    }
}
