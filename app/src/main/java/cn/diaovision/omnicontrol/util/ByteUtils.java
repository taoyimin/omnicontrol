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

    /*ascii字符串转byte数组*/
    public static byte[] ascii2bytes(String string){
        char[] chars=string.toCharArray();
        byte[] bytes=new byte[chars.length];
        for(int i=0;i<chars.length;i++){
            bytes[i]= (byte) chars[i];
        }
        return bytes;
    }

    /*byte数组转数字字符串，数字之间用逗号隔开*/
    public static String bytes2string(byte[] bytes){
        StringBuffer sb=new StringBuffer();
        for(byte b:bytes){
            sb.append(b+",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    /*数字字符串转byte数组*/
    public static byte[] string2bytes(String string){
        String[] strs=string.split(",|，");
        byte[] bytes=new byte[strs.length];
        for(int i=0;i<strs.length;i++){
            bytes[i]=Byte.parseByte(strs[i]);
        }
        return bytes;
    }
}
