package cn.diaovision.omnicontrol.core.message;

import java.io.UnsupportedEncodingException;

/**
 * Matrix control ver. 1.0
 * Created by liulingfeng on 2017/2/22.
 */

public class MatrixMessage {
    byte[] header = {0x11};
    byte[] tail = {0x04};
    byte[] payload;
    byte model;
    byte id;
    byte chksum;


    private MatrixMessage(String id, String msgType, String payload){}

    static public MatrixMessage createSwitchMessage(String id, int chnIn, int chnOut){
        return new MatrixMessage(id, "M", "");
    }
    static public MatrixMessage createCameraGoMessage(String id){
        return new MatrixMessage(id, "M", "");
    }

    public byte[] toBytes(){
        byte[] bytes = new byte[header.length + payload.length + 1 + tail.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(payload, 0, bytes, header.length-1, payload.length);
        System.arraycopy(chksum, 0, bytes, header.length+payload.length, 1);
        System.arraycopy(tail, 0, bytes, header.length+payload.length, 1);
        return bytes;
    }

    static public class MessageUtils{
        static public byte[] toGB3212(String str){
            try {
                String strGb2312 = new String(str.getBytes("GBK"));
                return strGb2312.getBytes();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
