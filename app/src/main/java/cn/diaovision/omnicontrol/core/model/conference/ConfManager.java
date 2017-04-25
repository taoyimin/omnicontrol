package cn.diaovision.omnicontrol.core.model.conference;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.core.message.conference.ConfInfoMessage;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.core.message.conference.StreamMediaMessage;
import cn.diaovision.omnicontrol.core.message.conference.TermInfoMessage;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxSubscriber;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public class ConfManager {
    public static final int STATE_PREPARING = 1;
    public static final int STATE_READY = 2;

    AtomicInteger state;

    Mcu mcu;
    McuCommManager mcuCommMgr;


    ConfInfoMessage confInfoTemplate;

    Map<Integer, TermInfoMessage> termInfos;

    public ConfManager() {
        this.state = new AtomicInteger(STATE_PREPARING);
        this.termInfos = new ConcurrentHashMap<>();
    }

    public int getState() {
        return state.get();
    }

    public void setState(AtomicInteger state) {
        this.state = state;
    }

    public void init(Config cfg){
//
//        //1. 初始化commMgr
//        mcu = new Mcu();
//        mcu.init(cfg);
//
//        mcuCommMgr = new McuCommMgr(mcu);
//
//        mcuCommMgr.connect(new RxSubscriber<RxMessage>() {
//            @Override
//            public void onRxResult(RxMessage rxMessage) {
//                //connection error
//                if (rxMessage.what == RxMessage.DISCONNECTED){
//                    mcu.state = Mcu.STATE_OFFLINE;
//                }
//                else {
//                     mcu.state = Mcu.STATE_ONLINE;
//
//                    //2. 获得会议配置模板
//                    McuMessage msg = McuMessage.buildReqConfAll();
//                    mcuCommMgr.send(msg, new RxSubscriber<RxMessage>() {
//                        @Override
//                        public void onRxResult(RxMessage rxMessage) {
//                            //3. 获得term列表
//                            confInfoTemplate = (ConfInfoMessage) rxMessage.val;
//                            int confNum = confInfoTemplate.getConfNum();
//
//                            for (int m = 0; m < confNum; m ++){
//                                McuMessage msg = McuMessage.buildReqTermAll(confInfoTemplate.getConfConfig()[m].getId());
//                                mcuCommMgr.send(msg, new RxSubscriber<RxMessage>() {
//                                    @Override
//                                    public void onRxResult(RxMessage rxMsg) {
//                                        termInfos.put(confInfoTemplate.getConfConfig()[m].getId(), rxMsg.val);
//                                        state.set(STATE_READY);
//                                    }
//                                    @Override
//                                    public void onRxError(Throwable e) {
//                                        mcu.state = Mcu.STATE_OFFLINE;
//                                    }
//                                });
//                            }
//                        }
//                        @Override
//                        public void onRxError(Throwable e) {
//                            mcu.state = Mcu.STATE_OFFLINE;
//                        }
//                    });
//
//                }
//            }
//
//            @Override
//            public void onRxError(Throwable e) {
//                //connection error
//                mcu.state = Mcu.STATE_OFFLINE;
//            }
//        });
    }

    /*获得会议的全部配置模板，这里不需要单独获得某个会议的模板*/
    private void reqConfTemplate(int confId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqConf(confId);
        mcuCommMgr.send(msg, subscriber);
    }

    /*hangup*/
    public void handup(int confId, int termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqHangupTerm(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    /***********************************
     * start conference
     * confId: where termList belongs
     ***********************************/
    public int startConf(Date startTime, Date endTime, int confId, RxSubscriber<RxMessage> subscriber){
        //TODO: return -1 if no template found

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

    public void endConf(int confId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqDeleteConf(confId);
        mcuCommMgr.send(msg, subscriber);
    }

    public void inviteTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqInviteTerm(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    public void hangupTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqHangupTerm(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    public void muteTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqMute(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    public void unmuteTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqUnmute(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    /*点名发言*/
    public void speechTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqTermSpeach(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    /*取消点名发言*/
    public void cancelSpeechTerm(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqTermCancelSpeach(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }


    public void makeChair(int confId, long termId, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqTermBroadcast(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    public void makeSelectview(int confId, long termId, RxSubscriber<RxMessage> subscriber) {
        McuMessage msg = McuMessage.buildReqTermSelectView(confId, termId);
        mcuCommMgr.send(msg, subscriber);
    }

    /*打开主席花蜜那*/
    public void openChairStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.OPEN_SERVER, localIp);
        mcuCommMgr.send(msg, subscriber);
    }

    /*关闭主席画面*/
    public void closeChairStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.CLOSE_SERVER, localIp);
        mcuCommMgr.send(msg, subscriber);
    }

    /*打开选看画面*/
    public void openSelectviewStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.OPEN_SELECTVIEW, localIp);
        mcuCommMgr.send(msg, subscriber);
    }

    /*关闭选看画面*/
    public void closeSelectviewStream(int confId, String localIp, RxSubscriber<RxMessage> subscriber){
        McuMessage msg = McuMessage.buildReqStream(confId, StreamMediaMessage.CLOSE_SELECTVIEW, localIp);
        mcuCommMgr.send(msg, subscriber);
    }
}

