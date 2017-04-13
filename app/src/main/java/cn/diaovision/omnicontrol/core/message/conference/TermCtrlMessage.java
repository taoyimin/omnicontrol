package cn.diaovision.omnicontrol.core.message.conference;

import cn.diaovision.omnicontrol.util.ByteUtils;

/* ********************
 * 终端控制消息
 * Created by liulingfeng on 2017/4/13.
 * ********************/
public class TermCtrlMessage implements BaseMessage{
    public final static int ENABLE_MICMUTE = 0;
    public final static int DISABLE_MICMUTE = 1;
    public final static int ENABLE_SPEAKERMUTE = 2;
    public final static int DISABLE_SPEAKERMUTE = 3;
    public final static int MIC_VOL = 4;
    public final static int SPEAKER_VOL = 5;
    public final static int NO_SEND_VIDEO = 6;
    public final static int SEND_VIDEO = 7;

    //0.终端麦克风静音; 1.终端麦克风不静音;
    //2.终端扬声器静音; 3.终端扬声器不静音;
    //4.调节终端麦克风音量; 5.调节终端扬声器音量;
    //6.终端部发送视频图像
    byte type;

    int confId;		//会议ID;
    long termId;	//终端ID;
    long termIp;	//终端IP;
    long val;       //值

    public TermCtrlMessage(byte type) {
        this.type = type;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[calcMessageLength()];
        bytes[0] = type;
        System.arraycopy(ByteUtils.int2bytes(confId, 2), 0, bytes, 0, 2);
        System.arraycopy(ByteUtils.int2bytes((int) termId, 4), 0, bytes, 2, 4);
        System.arraycopy(ByteUtils.int2bytes((int) termIp, 4), 0, bytes, 6, 4);
        System.arraycopy(ByteUtils.int2bytes((int) val, 4), 0, bytes, 10, 4);

        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 1+4+4+4;
    }
}

