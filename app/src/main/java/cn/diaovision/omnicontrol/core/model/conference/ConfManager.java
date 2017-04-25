package cn.diaovision.omnicontrol.core.model.conference;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.core.message.conference.ConfInfoMessage;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.core.message.conference.StreamMediaMessage;
import cn.diaovision.omnicontrol.core.message.conference.TermInfoMessage;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import cn.diaovision.omnicontrol.rx.RxThen;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public class ConfManager {
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_READY = 2;

    AtomicInteger state;

    Mcu mcu;
    McuCommManager mcuCommMgr;

    Conf conf; //this manager only handles one conf

    ConfInfoMessage confInfoTemplate;

    Map<Integer, TermInfoMessage> termInfos;

    public ConfManager() {
        this.state = new AtomicInteger(STATE_IDLE);
        this.termInfos = new ConcurrentHashMap<>();
    }

    public int getState() {
        return state.get();
    }

    public void setState(AtomicInteger state) {
        this.state = state;
    }

    /*初始化，异步调用*/
    public void init(Config cfg, RxSubscriber<RxMessage> subscriber){

        //1. 初始化commMgr
        mcu = new Mcu(cfg.getMcuIp(), cfg.getMcuPort());
        mcuCommMgr = new McuCommManager(mcu);

        //2. connect mcu, get confTemplate, get confInfo become ready
        //   if any error -> onError -> state idle

        mcuCommMgr.connect(new RxSubscriber<RxMessage>() {
                               @Override
                               public void onRxResult(RxMessage rxMsg) {

                                   McuMessage reqConfInfoMsg = McuMessage.buildReqConfAll();

                                   RxReq reqConfInfo = new RxReq() {
                                       @Override
                                       public RxMessage request() {
                                           //TODO: save conf info
                                           return null;
                                       }
                                   };

                                   McuMessage reqConfTemplateMsg = McuMessage.buildReqConfConfiged();

                                   RxReq reqConfTemplate = new RxReq() {
                                       @Override
                                       public RxMessage request() {
                                           //TODO: save conf template
                                           return null;
                                       }
                                   };

                                   List<McuCommManager.McuBundle> bundleList = new ArrayList<McuCommManager.McuBundle>();

                                   McuCommManager.McuBundle bundle = new McuCommManager.McuBundle();
                                   bundle.msg = reqConfInfoMsg;
                                   bundle.req = reqConfInfo;
                                   bundleList.add(bundle);

                                   bundle = new McuCommManager.McuBundle();
                                   bundle.msg = reqConfTemplateMsg;
                                   bundle.req = reqConfTemplate;
                                   bundleList.add(bundle);

                                   mcuCommMgr.sendSequential(bundleList, new RxSubscriber() {
                                       @Override
                                       public void onRxResult(Object o) {
                                           state.set(STATE_READY);
                                       }

                                       @Override
                                       public void onRxError(Throwable e) {
                                           state.set(STATE_IDLE);
                                       }
                                   });
                               }

                               @Override
                               public void onRxError(Throwable e) {
                                   state.set(STATE_IDLE);
                               }
                           }
        );
    }

    /*退出当前UI请务必调用这个方法*/
    public void stopWork(){
        mcuCommMgr.disconnect();
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
        ConfConfigMessage confConfigMessage = null;
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
        mcuCommMgr.send(msg, subscriber);
        return 0;
    }

    public int endConf(int confId, RxSubscriber<RxMessage> subscriber){

        if (confIdValid(confId)){
            McuMessage msg = McuMessage.buildReqDeleteConf(confId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        } else {
            return -1;
        }


    }

    public int inviteTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)){
            McuMessage msg = McuMessage.buildReqInviteTerm(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }

    public int hangupTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqHangupTerm(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        } else {
            return -1;
        }

    }

    public int muteTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqMute(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }

    public int unmuteTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqUnmute(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        } else {
            return -1;
        }
    }

    /*点名发言*/
    public int speechTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermSpeach(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }

    }

    /*取消点名发言*/
    public int cancelSpeechTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermCancelSpeach(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        } else {
            return -1;
        }
    }


    public int makeChair(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermBroadcast(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        } else {
            return -1;
        }
    }

    public int makeSelectview(int confId, long termId, RxSubscriber<RxMessage> subscriber) {
        if (termIdValid(confId, termId)) {
            McuMessage msg = McuMessage.buildReqTermSelectView(confId, termId);
            mcuCommMgr.send(msg, subscriber);
            return 0;
        }
        else {
            return -1;
        }
    }

    /*打开主席花蜜那*/
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

