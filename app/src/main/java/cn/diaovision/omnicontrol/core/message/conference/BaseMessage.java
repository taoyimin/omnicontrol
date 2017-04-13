package cn.diaovision.omnicontrol.core.message.conference;

/**
 * Created by liulingfeng on 2017/4/13.
 */

public interface BaseMessage {
    byte[] toBytes();
    int calcMessageLength();
}
