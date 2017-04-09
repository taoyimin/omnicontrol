package cn.diaovision.omnicontrol.core.model.conference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/* Live meeting bean*
 * Created by liulingfeng on 2017/4/5.
 */

public class LiveConf {
    String name;

    Chairman chairman;
    private Map<String, Attend> attends;

    public LiveConf(String name, Chairman chairman) {
        this.name = name;
        this.chairman = chairman;
        this.attends = new TreeMap<>();
    }

    public void addAttend(Attend attend){
        this.attends.put(attend.name, attend);
    }

    public void delAttend(String name){
        this.attends.remove(name);
    }

    /* return attend list sorted by name*/
    public List<Attend> getAttendList(){
        return new ArrayList<>(this.attends.values());
    }
}
