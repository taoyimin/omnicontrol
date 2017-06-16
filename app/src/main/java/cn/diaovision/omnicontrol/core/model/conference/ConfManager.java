package cn.diaovision.omnicontrol.core.model.conference;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.core.message.conference.ConfInfoMessage;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.core.message.conference.ResMessage;
import cn.diaovision.omnicontrol.core.message.conference.StreamMediaMessage;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import cn.diaovision.omnicontrol.util.ByteUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple conference manager
 * Created by liulingfeng on 2017/4/24.
 */

public class ConfManager {
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_READY = 2;

    AtomicInteger state;

    Mcu mcu;
    McuCommManager mcuCommMgr;

    Conf conf; //the current handled conference

    ConfInfoMessage confInfoTemplate;
    ConfInfoMessage confInfo;


    public ConfManager() {
        this.state = new AtomicInteger(STATE_IDLE);
    }

    public int getState() {
        return state.get();
    }

    public void setState(AtomicInteger state) {
        this.state = state;
    }

    /*初始化，异步调用*/
    public void init(Config cfg, final RxSubscriber<RxMessage> subscriber){

        //1. 初始化commMgr
        mcu = new Mcu(cfg.getMcuIp(), cfg.getMcuPort());
        mcuCommMgr = new McuCommManager(mcu);

        //2. connect mcu, get confTemplate, get confInfo become ready
        //   if any error -> onError -> state idle
        mcuCommMgr.connect()
                .flatMap(new Function<RxMessage, Publisher<RxMessage>>() {
                    @Override
                    public Publisher<RxMessage> apply(RxMessage rxMessage) throws Exception {
                        //Log.i("conf","McuMessage.buildReqConfAll()");
                        McuMessage reqConfInfoMsg = McuMessage.buildReqConfAll();

                        ConfEditor confInfoEditor = new ConfEditor() {
                            @Override
                            public void edit(Object o) {
                                if (McuMessage.class.isInstance(o)){
                                    McuMessage mcuMsg = (McuMessage) o;
                                    ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                                    ConfInfoMessage infoMsg = (ConfInfoMessage) resMsg.infoMsg;
                                    confInfo = infoMsg;
                                }
                            }
                        };


                        McuMessage reqConfTemplateMsg = McuMessage.buildReqConfConfiged();

                        ConfEditor confTemplateEditor = new ConfEditor() {
                            @Override
                            public void edit(Object o) {
                                if (McuMessage.class.isInstance(o)){
                                    McuMessage mcuMsg = (McuMessage) o;
                                    ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                                    ConfInfoMessage infoMsg = (ConfInfoMessage) resMsg.infoMsg;
                                    confInfoTemplate = infoMsg;
                                }
                            }
                        };

                        //Log.i("conf","confInfoTemplate.getConfNum()="+confInfoTemplate.getConfNum());
                        List<McuCommManager.McuBundle> bundleList = new ArrayList<McuCommManager.McuBundle>();

                        McuCommManager.McuBundle bundle = new McuCommManager.McuBundle();
                        bundle.msg = reqConfInfoMsg;
                        bundle.confEditor = confInfoEditor;
                        bundleList.add(bundle);

                        bundle = new McuCommManager.McuBundle();
                        bundle.msg = reqConfTemplateMsg;
                        bundle.confEditor = confTemplateEditor;
                        bundleList.add(bundle);

                        return mcuCommMgr.sendSequential(bundleList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(3) //retry initialize 3 times before it really calls to the onError
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        state.set(STATE_READY);
                    }
                })
                .subscribe(subscriber);
    }

    /*退出当前UI请务必调用这个方法*/
    public void stopWork(){
        mcuCommMgr.disconnect();
    }

    public Term getTerm(long termId){
        if (conf!= null) {
            return conf.getTerm(termId);
        }
        else {
            return null;
        }
    }

    /*获得会议的全部配置模板，这里不需要单独获得某个会议的模板*/
    private void reqConfTemplate(RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqConfConfiged();
        mcuCommMgr.send(msg, subscriber);
    }

    /*获得所有的会议, 不需要单独获得某个会议的模板*/
    private void reqConfInfo(RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqConfAll();
        mcuCommMgr.send(msg, subscriber);
    }

    /*hangup*/
    public void handupTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqHangupTerm(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    /***********************************
     * start conference
     * confId: where termList belongs
     ***********************************/
    public int startConf(Date startTime, Date endTime, int confId, RxSubscriber<RxMessage> subscriber){
        final ConfConfigMessage confConfigMessage = null;
        if (confInfoTemplate == null)
            return -1;

        for (int m = 0; m < confInfoTemplate.getConfNum(); m ++){
            if (confInfoTemplate.getConfConfig()[m].getId() == confId){
                confConfigMessage.copyFrom(confInfoTemplate.getConfConfig()[m]);
                break;
            }
        }

        if (confConfigMessage == null ){
            return -1;
        }

        confConfigMessage.setDate(startTime, endTime);

        McuMessage msg = McuMessage.buildReqCreateConf(startTime, endTime, confConfigMessage);
        mcuCommMgr.send(msg, subscriber, new ConfEditor() {
            @Override
            public void edit(Object o) {
                if (RxMessage.class.isInstance(o)) {
                    //before subscribe, modify the conf state
                    RxMessage rxMessage = (RxMessage)  o;
                    McuMessage mcuMsg = (McuMessage) rxMessage.val;
                    ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                    if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                        int confId = resMsg.error;
                        conf = new Conf(confId, confConfigMessage);
                        conf.configTemplate = confConfigMessage;
                    }
                }
            }
        });

        return 0;
    }

    /*结束会议*/
    public int endConf(int confId, RxSubscriber<RxMessage> subscriber){
        if (confIdValid(confId)){
            McuMessage msg = McuMessage.buildReqDeleteConf(confId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    if (RxMessage.class.isInstance(o)) {
                        //before subscribe, modify the conf state
                        RxMessage rxMessage = (RxMessage)  o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if(resMsg.status == ResMessage.STATUS_SUCCESS){
                            conf = null;
                        }
                    }
                }
            });
            return 0;
        } else {
            return -1;
        }
    }


    /*邀请终端入会*/
    public int inviteTerm(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)){
            McuMessage msg = McuMessage.buildReqInviteTerm(confId, termId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    //before subscribe, modify the conf state
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            List<Term> termList = conf.getTermList();
                            boolean containTerm = false;
                            for (int m = 0; m < termList.size(); m++) {
                                if (termList.get(m).id == termId) {
                                    containTerm = true;
                                    break;
                                }
                            }
                            if (!containTerm) {
                                Term term = new Term(termId);
                                ConfConfigMessage.TermAttr[] termAttrs = conf.getConfig().getTermAttrs();
                                for (int m = 0; m < conf.getConfig().getTermAttrNum(); m++) {
                                    if (conf.getConfig().getTermAttrs()[m].id == termId) {
                                        ConfConfigMessage.TermAttr attr = conf.getConfig().getTermAttrs()[m];
                                        term.ip = ByteUtils.num2ip((int) attr.addr);
                                        break;
                                    }
                                }

                                termList.add(term);
                            }
                        }
                    }
                }
            });
            return 0;
        }
        else {
            return -1;
        }
    }


    /*挂断终端*/
    public int hangupTerm(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqHangupTerm(confId, termId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        //before subscribe, modify the conf state
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            List<Term> termList = conf.getTermList();
                            for (int m = 0; m < termList.size(); m++) {
                                if (termList.get(m).id == termId) {
                                    termList.remove(m);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
            return 0;
        } else {
            return -1;
        }

    }

    /*静音终端*/
    public int muteTerm(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqMute(confId, termId);
             mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        //before subscribe, modify the conf state
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            conf.getTerm(termId).isMuted = true;
                        }
                    }
                }
            });
            return 0;
        }
        else {
            return -1;
        }
    }

    public int unmuteTerm(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqUnmute(confId, termId);
            //before subscribe, modify the conf state
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            conf.getTerm(termId).isMuted = false;
                        }
                    }
                }
            });
            return 0;
        } else {
            return -1;
        }
    }

    /*点名发言*/
    public int speechTerm(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermSpeach(confId, termId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    //before subscribe, modify the conf state
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            conf.getTerm(termId).isSpeaking = true;
                        }
                    }
                }
            });
            return 0;
        }
        else {
            return -1;
        }

    }

    /*取消点名发言*/
    public int cancelSpeechTerm(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermCancelSpeach(confId, termId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    //before subscribe, modify the conf state
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            conf.getTerm(termId).isSpeaking = false;
                        }
                    }
                }
            });
            return 0;
        } else {
            return -1;
        }
    }

    public int makeChair(int confId, final long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermCancelSpeach(confId, termId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    //before subscribe, modify the conf state
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            conf.setChair(conf.getTerm(termId));
                        }
                    }
                }
            });
            return 0;
        } else {
            return -1;
        }
    }


    public int makeSelectview(int confId, final long termId, RxSubscriber<RxMessage> subscriber) {
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermCancelSpeach(confId, termId);
            mcuCommMgr.send(msg, subscriber, new ConfEditor() {
                @Override
                public void edit(Object o) {
                    //before subscribe, modify the conf state
                    if (RxMessage.class.isInstance(o)) {
                        RxMessage rxMessage = (RxMessage) o;
                        McuMessage mcuMsg = (McuMessage) rxMessage.val;
                        ResMessage resMsg = (ResMessage) mcuMsg.getSubmsg();
                        if (resMsg.status == ResMessage.STATUS_SUCCESS) {
                            conf.setSelectView(conf.getTerm(termId));
                        }
                    }
                }
            });
            return 0;
        } else {
            return -1;
        }
    }

    /*打开主席画面*/
    public int openChairStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        if (confIdValid(confId)) {
            McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.OPEN_SERVER, localIp);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }

    /*关闭主席画面*/
    public int closeChairStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        if (confIdValid(confId)) {
            McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.CLOSE_SERVER, localIp);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }

    /*打开选看画面*/
    public int openSelectviewStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        if (confIdValid(confId)) {
            McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.OPEN_SELECTVIEW, localIp);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }

    /*关闭选看画面*/
    public int closeSelectviewStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        if (confIdValid(confId)) {
            McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.CLOSE_SELECTVIEW, localIp);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }


    public Mcu getMcu() {
        return mcu;
    }

    public void setMcu(Mcu mcu) {
        this.mcu = mcu;
    }

    public Conf getConf() {
        return conf;
    }

    public void setConf(Conf conf) {
        this.conf = conf;
    }

    public boolean confIdValid(int confId){
        if (confInfoTemplate == null)
            return false;

        boolean idValid = false;
        for (int m = 0; m < confInfoTemplate.getConfNum(); m ++){
            if (confInfoTemplate.getConfConfig()[m].getId() == confId){
                idValid = true;
                break;
            }
        }

        return idValid;
    }

    public boolean termIdValid(int confId, long termId){
        boolean idValid = false;
        if (confIdValid(confId)){
            for (int m = 0; m < confInfoTemplate.getConfNum(); m ++){
                if (confInfoTemplate.getConfConfig()[m].getId() == confId){
                    for (int n = 0; n < confInfoTemplate.getConfConfig()[m].getTermAttrNum(); n ++){
                        if (confInfoTemplate.getConfConfig()[m].getTermAttrs()[n].id == termId){
                            idValid = true;
                            break;
                        }
                    }
                    break;
                }
            }

            return idValid;
        }
        else {
            return idValid;
        }
    }
}

