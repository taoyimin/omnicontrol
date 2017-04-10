package cn.diaovision.omnicontrol.core.model.conference;

import java.util.Map;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by liulingfeng on 2017/4/4.
 */

public class Chairman {
    String ip;
    int port;
    String name;

    Map<String, LiveConf> meetingList;


    public void invite(Attend attend){
    }

    public void kickoff(Attend attend){
    }

    public void mute(Attend attend){
    }

    public void unmute(Attend attend){
    }

    public void start(LiveConf meeting){
    }

    public void stop(LiveConf meeting){
    }

}
