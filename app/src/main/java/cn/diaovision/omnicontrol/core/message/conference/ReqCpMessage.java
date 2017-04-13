package cn.diaovision.omnicontrol.core.message.conference;

/**
 * 4画面命令，由于多图像台的需求，原来的结构不能满足要求，定义新的
 * 结构，但仍然使用Request消息类型
 * Created by liulingfeng on 2017/4/13.
 */

public class ReqCpMessage{
    byte type;  //多画面
    int confId;    //会议号
    long termCount;
    long[] termId; //long[32]
}
