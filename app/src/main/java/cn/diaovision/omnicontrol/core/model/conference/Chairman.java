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

    Map<String, LiveConf> confList;


    public void invite(Term term){
    }

    public void kickoff(Term term){
    }

    public void mute(Term term){
    }

    public void unmute(Term term){
    }

    public void start(LiveConf conf){
    }

    public void stop(LiveConf conf){
    }
}
