package cn.diaovision.omnicontrol.core.message;

import java.io.UnsupportedEncodingException;

/**
 * Matrix control ver. 1.0
 * Message in format
 * { header[2] id[2] type[2] payload[x] checksum[2] }
 * ID range: [0-255]
 * Preset range: [0-15]
 * Subtitle range: [0-15]
 * Camera speed range: [0-63]
 * Created by liulingfeng on 2017/2/22.
 */

public class MatrixMessage {
    /*
     * message type
     */
    public final static int MSG_SWITCH = 0; //1对1切换
    public final static int MSG_MULTI_SWITCH = 1; //1对N切换
    public final static int MSG_INQUIRY_PORT = 2; //查询端口
    public final static int MSG_LOCK_PORT = 3; //锁定端口
    public final static int MSG_UNLOCK_PORT = 4; //解锁端口
    public final static int MSG_SET_ID = 5; //设置矩阵ID
    public final static int MSG_GET_ID = 6; //设置矩阵ID
    public final static int MSG_SET_RESOLU = 7; //设置通道分辨率
    public final static int MSG_GET_RESOLU = 8; //设置通道分辨率
    public final static int MSG_STITCH = 9; //拼接
    public final static int MSG_CROP = 10; //切边
    public final static int MSG_SUBTITLE = 11; //字符设置
    public final static int MSG_SUBTITLE_FORMAT = 12; //字符格式设置
    public final static int MSG_START_CAMERA_GO = 13; //摄像机start
    public final static int MSG_STOP_CAMERA_GO = 14; //摄像机stop
    public final static int MSG_SET_CAMERA_INFO = 15; //根据端口读取摄像机地址
    public final static int MSG_GET_CAMERA_INFO = 16; //根据端口设置摄像机地址
    public final static int MSG_SET_CAMERA_PRESET = 17; //设置摄像机预置位
    public final static int MSG_CLEAR_CAMERA_PRESET = 18; //清除摄像机预置位
    public final static int MSG_LOAD_CAMERA_PRESET = 19; //清除摄像机预置位

    /*
     * msg type bytes
     */
    private final static byte[][] TYPE_BYTES = {
            {(byte) 'W', (byte) 'R'}, //0
            {(byte) 'W', (byte) 'M'}, //1
            {(byte) 'R', (byte) 'D'}, //2
            {(byte) 'L', (byte) 'K'}, //3
            {(byte) 'U', (byte) 'K'}, //4
            {(byte) 'I', (byte) 'D'}, //5
            {(byte) 'I', (byte) 'D'}, //6
            {(byte) 'M', (byte) 'S'}, //7
            {(byte) 'M', (byte) 'S'}, //8
            {(byte) 'S', (byte) 'P'}, //9
            {(byte) 'S', (byte) 'P'}, //10
            {(byte) 'O', (byte) 'S'}, //11
            {(byte) 'O', (byte) 'C'}, //12
            {(byte) 'C', (byte) 'D'}, //13
            {(byte) 'C', (byte) 'S'}, //14
            {(byte) 'C', (byte) 'A'}, //15
            {(byte) 'C', (byte) 'A'}, //16
            {(byte) 'C', (byte) 'P'}, //17
            {(byte) 'C', (byte) 'P'}, //18
            {(byte) 'C', (byte) 'P'}, //19
    };

    /*
     * camera control command
     */
    public final static int CAM_UP = 0;
    public final static int CAM_DOWN = 1;
    public final static int CAM_LEFT = 2;
    public final static int CAM_RIGHT = 3;
    public final static int CAM_WIDE = 4; //视野变宽
    public final static int CAM_NARROW = 5; //视野变窄
    public final static int CAM_ZOOMIN = 6;
    public final static int CAM_ZOOMOUT = 7;

    public final static int CAM_MAX_SPEED = 63;
    public final static int CAM_MIN_SPEED = 0;

    public final static int CAM_PROTO_PELCO_D = 0;
    public final static int CAM_PROTO_PELCO_P = 1;
    public final static int CAM_PROTO_VISCA = 2;

    /*
     *font format
     */
    public final static byte FONT_SIZE_8 = '0';
    public final static byte FONT_SIZE_9 = '1';
    public final static byte FONT_SIZE_10 = '2';
    public final static byte FONT_SIZE_11 = '3';

    public final static byte FONT_COLOR_WHITE = '0';
    public final static byte FONT_COLOR_BLACK = '1';
    public final static byte FONT_COLOR_RED = '2';
    public final static byte FONT_COLOR_BLUE = '3';
    public final static byte FONT_COLOR_GREEN = '4';
    public final static byte FONT_COLOR_PURPLE = 'F';

    private static final byte MODEL = (byte) 'M'; //supplementary model byte (will be removed in future)

    final byte header = 0x01;
    final byte tail = 0x04;
    byte[] payload;
    byte[] id;

    boolean checked = true;
    byte[] type;


    private MatrixMessage(byte[] id, int type, byte[] payload, boolean checked){
        this.id = new byte[2];

        if (type > TYPE_BYTES.length) {
            this.type = new byte[2];
            this.type[0] = 0x00;
            this.type[1] = 0x00;
        }
        else {
            this.type = TYPE_BYTES[type];
        }

        System.arraycopy(id, 0, this.id, 0, 2);

        this.payload = payload;

        this.checked = checked;
    }

    public byte[] toBytes(){
        /*append checksum*/
        byte[] bytes;
        if (checked) {
            //header + id + type + payload + tail
            bytes = new byte[1 + 2 + 2 + payload.length + 2 + 1];
            bytes[0] = header;
            bytes[1] = id[0];
            bytes[2] = id[1];
            bytes[3] = type[0];
            bytes[4] = type[1];
            System.arraycopy(payload, 0, bytes, 5, payload.length);

            byte[] chk = calcCheckSum();
            bytes[bytes.length-3] = chk[1];
            bytes[bytes.length-2] = chk[0];
            bytes[bytes.length-1] = tail;
        }
        else {
            //header + id + type + payload + checksum + tail
            bytes = new byte[1 + 2 + payload.length + 1];
            bytes[0] = header;
            bytes[1] = id[0];
            bytes[2] = id[1];
            bytes[3] = type[0];
            bytes[4] = type[1];
            System.arraycopy(payload, 0, bytes, 5, payload.length);
            bytes[bytes.length-1] = tail;
        }

        return bytes;
    }

    private byte[] calcCheckSum(){
        if (payload == null){
            return new byte[0];
        }
        byte chk = 0x00;
        for (byte b : id){
            chk ^= b;
        }
        for (byte b : type){
            chk ^= b;
        }
        for (byte b : payload){
            chk ^= b;
        }
        return hex2char(chk);
    }


    /*
     * message create
     */
    static public MatrixMessage buildSwitchMessage(int id, int portIn, int portOut){
        byte[] payload = new byte[7];
        payload[0] = MODEL;
        System.arraycopy(hex2char(portOut,3), 0, payload, 1, 3);
        System.arraycopy(hex2char(portIn,3), 0, payload, 4, 3);
        return new MatrixMessage(hex2char(id, 2), MSG_SWITCH, payload, true);
    }

    static public MatrixMessage buildMultiSwitchMessage(int id, int portIn, int[] portOuts){
        byte[] payload = new byte[1 + 3 + portOuts.length*3];
        payload[0] = MODEL;
        System.arraycopy(hex2char(portIn,3), 0, payload, 1, 3);
        int offset = 1;
        for (int i : portOuts) {
            offset += 3;
            System.arraycopy(hex2char(i, 3), 0, payload, offset, 3);
        }
        return new MatrixMessage(hex2char(id, 2), MSG_MULTI_SWITCH, payload, true);
    }

    static public MatrixMessage buildPortInquiryMessage(int id, int portOut){
        byte[] payload = new byte[1 + 3];
        payload[0] = MODEL;
        System.arraycopy(hex2char(portOut,3), 0, payload, 1, 3);
        return new MatrixMessage(hex2char(id, 2), MSG_INQUIRY_PORT, payload, true);
    }

    static public MatrixMessage buildLockPortMessage(int id, int portOut){
        byte[] payload = new byte[1 + 3];
        payload[0] = MODEL;
        System.arraycopy(hex2char(portOut,3), 0, payload, 1, 3);
        return new MatrixMessage(hex2char(id, 2), MSG_LOCK_PORT, payload, true);
    }

    static public MatrixMessage buildUnlockPortMessage(int id, int portOut){
        byte[] payload = new byte[1 + 3];
        payload[0] = MODEL;
        System.arraycopy(hex2char(portOut,3), 0, payload, 1, 3);
        return new MatrixMessage(hex2char(id, 2), MSG_UNLOCK_PORT, payload, true);
    }

    static public MatrixMessage buildSetIdMessage(int id, int newId){
        byte[] payload = new byte[2];
        System.arraycopy(hex2char(newId,2), 0, payload, 0, 2);
        byte[] idByte = {'F', 'S'};
        return new MatrixMessage(idByte, MSG_SET_ID, payload, true);
    }

    static public MatrixMessage buildGetIdMessage(int id){
        byte[] payload = new byte[2];
        payload[0] = (byte) 'F';
        payload[1] = (byte) 'S';
        byte[] idByte = {(byte) 'F', (byte) 'S'};
        return new MatrixMessage(idByte, MSG_GET_ID, payload, true);
    }

    static public MatrixMessage buildStartCameraGoMessage(int id, int baudrate, int proto, int port, int cmd, int speed){
        byte[] payload = new byte[11];

        payload[0] = getCamBaudrateByte(baudrate);
        payload[1] = getCamProtoByte(proto);

        System.arraycopy(hex2char(port, 3), 0, payload, 2, 3);

        byte[] cmdByte = new byte[2];
        switch (cmd){
            case CAM_UP:
                cmdByte[0] = (byte) '8';
                cmdByte[1] = (byte) '0';
                break;
            case CAM_DOWN:
                cmdByte[0] = (byte) '4';
                cmdByte[1] = (byte) '0';
                break;
            case CAM_LEFT:
                cmdByte[0] = (byte) '2';
                cmdByte[1] = (byte) '0';
                break;
            case CAM_RIGHT:
                cmdByte[0] = (byte) '1';
                cmdByte[1] = (byte) '0';
                break;
            case CAM_WIDE:
                cmdByte[0] = (byte) '0';
                cmdByte[1] = (byte) '8';
                break;
            case CAM_NARROW:
                cmdByte[0] = (byte) '0';
                cmdByte[1] = (byte) '4';
                break;
            case CAM_ZOOMIN:
                cmdByte[0] = (byte) '0';
                cmdByte[1] = (byte) '2';
                break;
            case CAM_ZOOMOUT:
                cmdByte[0] = (byte) '0';
                cmdByte[1] = (byte) '1';
                break;
            default:
                cmdByte[0] = (byte) 'F';
                cmdByte[1] = (byte) 'F';
                break;
        }
        System.arraycopy(cmdByte, 0, payload, 5, 2);
        //TODO: Speed in hex format ?
        System.arraycopy(hex2char(speed, 2), 0, payload, 7, 2);

        /*光圈开关，默认开*/
        payload[9] = (byte) '8';
        payload[10] = (byte) '0';

        return new MatrixMessage(hex2char(id, 2), MSG_START_CAMERA_GO, payload, true);
    }

    static public MatrixMessage buildStopCameraGoMessage(int id, int baudrate, int proto, int port){
        byte[] payload = new byte[5];

        payload[0] = getCamBaudrateByte(baudrate);
        payload[1] = getCamProtoByte(proto);

        System.arraycopy(hex2char(port, 3), 0, payload, 2, 3);

        return new MatrixMessage(hex2char(id, 2), MSG_STOP_CAMERA_GO, payload, true);
    }

    static public MatrixMessage buildGetCameraInfoMessage(int id, int port){
        byte[] payload = new byte[5];
        System.arraycopy(hex2char(port,3), 0, payload, 0, 3);
        payload[3] = (byte) 'F';
        payload[4] = (byte) 'S';
        return new MatrixMessage(hex2char(id, 2), MSG_GET_CAMERA_INFO, payload, true);
    }

    /*addr: address of the camera*/
    static public MatrixMessage buildSetCameraInfoMessage(int id, int port, int addr){
        byte[] payload = new byte[5];
        System.arraycopy(hex2char(port,3), 0, payload, 0, 3);
        System.arraycopy(hex2char(addr,2), 0, payload, 3, 2);
        return new MatrixMessage(hex2char(id, 2), MSG_SET_CAMERA_INFO, payload, true);
    }

    static public MatrixMessage buildSetCameraPresetMessgae(int id, int baudrate, int proto, int port, int presetIdx){
        byte[] payload = new byte[8];
        payload[0] = getCamBaudrateByte(baudrate);
        payload[1] = getCamProtoByte(proto);
        System.arraycopy(hex2char(port,3), 0, payload, 2, 3);
        payload[5] = 0x30;
        System.arraycopy(hex2char(presetIdx, 2), 0, payload, 6, 2);
        return new MatrixMessage(hex2char(id, 2), MSG_SET_CAMERA_PRESET, payload, true);
    }

    static public MatrixMessage buildLoadCameraPresetMessgae(int id, int baudrate, int proto, int port, int presetIdx){
        byte[] payload = new byte[8];
        payload[0] = getCamBaudrateByte(baudrate);
        payload[1] = getCamProtoByte(proto);
        System.arraycopy(hex2char(port,3), 0, payload, 2, 3);
        payload[5] = 0x32;
        System.arraycopy(hex2char(presetIdx, 2), 0, payload, 6, 2);
        return new MatrixMessage(hex2char(id, 2), MSG_LOAD_CAMERA_PRESET, payload, true);
    }

    static public MatrixMessage buildClearCameraPresetMessgae(int id, int baudrate, int proto, int port, int presetIdx){
        byte[] payload = new byte[8];
        payload[0] = getCamBaudrateByte(baudrate);
        payload[1] = getCamProtoByte(proto);
        System.arraycopy(hex2char(port,3), 0, payload, 2, 3);
        payload[5] = 0x31;

        //TODO: num to hex
        System.arraycopy(hex2char(presetIdx, 2), 0, payload, 6, 2);
        return new MatrixMessage(hex2char(id, 2), MSG_CLEAR_CAMERA_PRESET, payload, true);
    }

    static public MatrixMessage buildSetResoluMessage(int id, int portOut, String resolu){
        byte[] payload = new byte[7];
        payload[0] = (byte) 'O';
        System.arraycopy(hex2char(portOut, 3), 0, payload, 1, 3);
        payload[4] = (byte) '3';

        switch ( resolu ){
            case "480p":
                payload[5] = (byte) '0';
                payload[6] = (byte) '4';
                break;
            case "480i":
                payload[5] = (byte) '0';
                payload[6] = (byte) '2';
                break;
            case "576p":
                payload[5] = (byte) '0';
                payload[6] = (byte) '5';
                break;
            case "576i":
                payload[5] = (byte) '0';
                payload[6] = (byte) '3';
                break;
            case "720p":
                payload[5] = (byte) '0';
                payload[6] = (byte) '6';
                break;
            case "1080p":
                payload[5] = (byte) '0';
                payload[6] = (byte) 'A';
                break;
            case "1080i":
                payload[5] = (byte) '0';
                payload[6] = (byte) '9';
                break;
            default:
                payload[5] = (byte) '0';
                payload[6] = (byte) '0';
                break;
        }
        return new MatrixMessage(hex2char(id, 2), MSG_SET_RESOLU, payload, true);
    }

    static public MatrixMessage buildGetResoluMessage(int id, int portOut){
        byte[] payload = new byte[7];
        payload[0] = (byte) 'O';
        System.arraycopy(hex2char(portOut, 3), 0, payload, 1, 3);
        payload[4] = (byte) 'A';
        payload[5] = (byte) '0';
        payload[6] = (byte) '0';

        return new MatrixMessage(hex2char(id, 2), MSG_GET_RESOLU, payload, true);
    }

    /*multiSwitchMessage should be called prior to the stitch*/
    static public MatrixMessage buildStitchMessage(int id, int column, int row, int[] portOuts){
        byte[] payload = new byte[1 + 1 + 2 + 2 + portOuts.length*3];
        payload[0] = (byte) 'P';
        payload[1] = (byte) 'W';
        System.arraycopy(hex2char(column,2), 0, payload, 2, 2);
        System.arraycopy(hex2char(row,2), 0, payload, 4, 2);
        for (int i : portOuts) {
            System.arraycopy(hex2char(i, 3), 0, payload, 4, 2);
        }
        return new MatrixMessage(hex2char(id, 2), MSG_STITCH, payload, true);
    }

    /*multiSwitchMessage should be called prior to the crop*/
    //TODO: crop not supported
    @Deprecated
    static public MatrixMessage buildCropMessage(int id, int cropX, int cropY, int[] portOuts){
        byte[] payload = new byte[1 + 1 + 2 + 2 + portOuts.length*3];
        payload[0] = (byte) 'P';
        payload[1] = (byte) 'W';
        System.arraycopy(hex2char(cropX,2), 0, payload, 2, 2);
        System.arraycopy(hex2char(cropY,2), 0, payload, 4, 2);
        for (int i : portOuts) {
            System.arraycopy(hex2char(i, 3), 0, payload, 4, 2);
        }
        return new MatrixMessage(hex2char(id, 2), MSG_CROP, payload, true);
    }

    static public MatrixMessage buildSetSubtitleMessage(int id, int port, String str){
        byte[] strBytes = toGBK(str);
        byte[] payload = new byte[3 + 1 + 2 + strBytes.length];
        System.arraycopy(hex2char(port, 3), 0, payload, 0, 3);
        payload[3] = 0x00;
        //TODO: hex format
        System.arraycopy(hex2char(strBytes.length/2, 2), 0, payload, 4, 2);
        System.arraycopy(strBytes, 0, payload, 6, strBytes.length);

        return new MatrixMessage(hex2char(id, 2), MSG_SUBTITLE, payload, true);
    }

    static public MatrixMessage buildSetSubtitleFormatMessage(int id, int port, byte size, byte color){
        byte[] payload = new byte[3 + 2 + 1 + 1 + 2 + 1];
        System.arraycopy(hex2char(port,3), 0, payload, 0, 3);

        //fixed content
        payload[3] = '0';
        payload[4] = '1';

        payload[5] = size;
        payload[6] = color;

        //fixed content
        payload[7] = '0';
        payload[8] = '0';
        payload[9] = '0';

        return new MatrixMessage(hex2char(id, 2), MSG_SUBTITLE_FORMAT, payload, true);
    }

    /*
     * Utilities
     */
    static public byte[] toGBK(String str){
        try {
            return str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    static private byte[] hex2char(byte b){
        byte[] bytes = new byte[2];
        int val = b & 0x0f;
        if (val <= 9){
            val += 0x30;
        }
        else {
            val += 0x30 + 7;
        }
        bytes[0] = (byte) val;

        val = (b >> 4) & 0x0f;
        if (val <= 9){
            val += 0x30;
        }
        else {
            val += 0x30 + 7;
        }
        bytes[1] = (byte) val;

        return bytes;
    }

    static private byte[] hex2char(int i, int len){
        byte[] bytes = new byte[len];
        len = len > 4 ? 4 : len;
        for (int m = 0; m < len; m ++){
            int val = (i>>(4*m)) & 0x0f;
            if (val <= 9){
                val += 0x30;
            }
            else {
                val += 0x30 + 7;
            }
            bytes[len-m-1] = (byte) val;
        }
        return bytes;
    }

    /*十进制转2字节*/
    @Deprecated
    static private byte[] deci2char(int i){
        byte[] deciBytes = new byte[2];
        if (i > 100 || i < 0){
            deciBytes[0] = (byte) 'F';
            deciBytes[1] = (byte) 'F';
            return deciBytes;
        }

        byte[] bytes = String.valueOf(i).getBytes();

        if (i < 10){
            deciBytes[0] = (byte) '0';
            deciBytes[1] = bytes[0];
        }
        else {
            deciBytes[0] = bytes[0];
            deciBytes[1] = bytes[1];
        }

        return deciBytes;
    }

    /*云台波特率*/
    static private byte getCamBaudrateByte(int baudrate){
        switch(baudrate) {
            case 1200:
                return '1';
            case 2400:
                return '2';
            case 4800:
                return '3';
            case 9600:
                return '4';
            case 19200:
                return '5';
            case 38400:
                return '6';
            case 57600:
                return '7';
            case 115200:
                return '8';
            default:
                return '0';
        }
    }

    /*云台协议*/
    static private byte getCamProtoByte(int proto){
        switch(proto){
            case CAM_PROTO_PELCO_D:
                return '1';
            case CAM_PROTO_PELCO_P:
                return '2';
            case CAM_PROTO_VISCA:
                return '3';
            default:
                return '0';
        }
    }
}
