package cn.diaovision.omnicontrol.core.message.conference;

/**
 * Created by liulingfeng on 2017/4/13.
 */

//新的多画面命令，由于原来的多画面的需求，原来的结构不能满足要求，定义新的结构，
//新的结构增加了分组、轮循间隔时间、轮循画面数等。但仍然使用Request消息类型
public class ReqMultiPicMessage{
    byte type; //多画面
    int	confId; //会议号
    int roundNum; //轮巡终端数
    int roundInterval; //轮巡间隔（秒数)，为0则表示不轮巡
    long termCount; //终端数
    byte[] group; //组号
    long[] termId; //终端ID, 32 bytes
};

