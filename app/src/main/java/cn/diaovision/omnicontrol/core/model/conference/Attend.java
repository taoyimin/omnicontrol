package cn.diaovision.omnicontrol.core.model.conference;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/** 参会者
 * Created by liulingfeng on 2017/4/5.
 */

@RealmClass
public class Attend implements RealmModel {
    String ip = "192.168.1.1";
    int port = 553;
    String name = "";

    public Attend(){
    }

    public Attend(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }
}
