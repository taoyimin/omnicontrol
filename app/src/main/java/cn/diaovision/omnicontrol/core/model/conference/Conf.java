package cn.diaovision.omnicontrol.core.model.conference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxSubscriber;

/* Live meeting bean*
 * Created by liulingfeng on 2017/4/5.
 */

public class Conf {
//    byte confFlag; //会议召开标志
//    byte speech; //MCU点名发言，1：点名发言，0：取消发言
//    byte mixAudio;
//    byte mixVideo;
//    byte mixPic;

    ConfConfigMessage configTemplate;
    ConfConfigMessage config;

    int id;

    List<Term> termList;

    Term chair;
    Term selectView;

    public Conf(int id, ConfConfigMessage config){
        this.id = id;
        this.config = config;
        termList = new ArrayList<>();
        chair = null;
        selectView = null;
    }

     public List<Long> getTermIdList(){
        List<Long> idList = new ArrayList<>();
            if (configTemplate!= null){
                for (int m = 0; m < configTemplate.getTermAttrNum(); m ++){
                    idList.add((long) configTemplate.getTermAttrs()[m].id);
                }
            }
        return idList;
     }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ConfConfigMessage getConfigTemplate() {
        return configTemplate;
    }

    public void setConfigTemplate(ConfConfigMessage configTemplate) {
        this.configTemplate = configTemplate;
    }

    public ConfConfigMessage getConfig() {
        return config;
    }

    public void setConfig(ConfConfigMessage config) {
        this.config = config;
    }

    public Term getChair() {
        return chair;
    }

    public void setChair(Term chair) {
        this.chair = chair;
    }

    public Term getSelectView() {
        return selectView;
    }

    public void setSelectView(Term selectView) {
        this.selectView = selectView;
    }

    public List<Term> getTermList() {
        return termList;
    }

    public void setTermList(List<Term> termList) {
        this.termList = termList;
    }

    public Term getTerm(long termId){
        for (Term t : termList){
            if (t.id == termId){
                return t;
            }
        }
        return null;
    }
}
