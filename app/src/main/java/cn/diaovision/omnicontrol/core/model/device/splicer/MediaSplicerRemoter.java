package cn.diaovision.omnicontrol.core.model.device.splicer;

import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.List;

import cn.diaovision.omnicontrol.core.message.SplicerMessage;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.util.ByteUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TaoYimin on 2017/7/27.
 */

public class MediaSplicerRemoter {
    MediaSplicer splicer;

    public MediaSplicerRemoter(MediaSplicer splicer) {
        this.splicer = splicer;
    }

    public MediaSplicer getSplicer() {
        return splicer;
    }

    public void setSplicer(MediaSplicer splicer) {
        this.splicer = splicer;
    }

    /*获取场景列表*/
    public void getSceneList(final int groupId, Subscriber<List<Scene>> subscriber) {
        Flowable.create(new FlowableOnSubscribe<List<Scene>>() {
            @Override
            public void subscribe(FlowableEmitter<List<Scene>> e) throws Exception {
                SplicerMessage msg = SplicerMessage.buildReadSceneMessage(groupId);
                List<byte[]> recvs = splicer.getController().send(msg.toBytes(), msg.toBytes().length, true);
                e.onNext(SplicerMessage.parseSceneMessage(recvs));
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    
    /*调用场景*/
    public void loadScene(final int sceneId,Subscriber<RxMessage> subscriber){
        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                SplicerMessage msg=SplicerMessage.buildLoadSceneMessage(sceneId);
                List<byte[]> recvs = splicer.getController().send(msg.toBytes(),msg.toBytes().length,true);
                if(recvs.size()>0&&ByteUtils.bytes2ascii(recvs.get(recvs.size()-1)).endsWith("OK>")){
                    e.onNext(new RxMessage(RxMessage.DONE));
                }else{
                    e.onError(new IOException());
                }
            }
        },BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /*修改场景名称*/
    public void renameScene(final int sceneId, final String name, final int groupId, Subscriber<RxMessage> subscriber){
        Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                SplicerMessage msg=SplicerMessage.buildRenameSceneMessage(sceneId,name,groupId);
                List<byte[]> recvs=splicer.getController().send(msg.toBytes(),msg.toBytes().length,true);
                if(recvs.size()>0&&ByteUtils.bytes2ascii(recvs.get(recvs.size()-1)).endsWith("OK>")){
                    e.onNext(new RxMessage(RxMessage.DONE));
                }else{
                    e.onError(new IOException());
                }
            }
        },BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
