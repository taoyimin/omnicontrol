package cn.diaovision.omnicontrol.core.model.device.splicer;

import org.reactivestreams.Subscriber;

import java.util.List;

import cn.diaovision.omnicontrol.core.message.SplicerMessage;
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

    public void getSceneList(final int groupId, Subscriber subscriber) {
        Flowable.create(new FlowableOnSubscribe<List<Scene>>() {
            @Override
            public void subscribe(FlowableEmitter<List<Scene>> e) throws Exception {
                SplicerMessage msg = SplicerMessage.buildRlstMessage(groupId);
                List<byte[]> recvs = splicer.getController().send(msg.toBytes(), msg.toBytes().length, true);
                e.onNext(SplicerMessage.parseSceneMessage(recvs));
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
