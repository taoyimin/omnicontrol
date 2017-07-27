package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.message.SplicerMessage;
import cn.diaovision.omnicontrol.core.model.device.splicer.MediaSplicer;
import cn.diaovision.omnicontrol.util.ByteUtils;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class LightFragment extends BaseFragment implements LightContract.View{
    @BindViews({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8})
    List<Button> list;
    MediaSplicer fusion;

    LightPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_light, container, false);
        ButterKnife.bind(this, v);
        fusion = new MediaSplicer();
        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;
            list.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplicerMessage msg = new SplicerMessage(0,new byte[]{'3','0',',','1',',','1'});
                    msg.setType(new byte[]{'S','R','E','N'});
                    Flowable.just(msg)
                            .map(new Function<SplicerMessage, String>() {
                                @Override
                                public String apply(SplicerMessage splicerMessage) throws Exception {
                                    Log.i("info", "send:" + ByteUtils.bytes2ascii(splicerMessage.toBytes()));
                                    String s = "1";
                                    List<byte[]> recvList = fusion.getController().send(splicerMessage.toBytes(), splicerMessage.toBytes().length, true);
                                    for (byte[] bytes:recvList) {
                                        Log.i("info","recv:"+ByteUtils.bytes2ascii(bytes));
                                    }
                                    return s;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    //Log.i("info", s);
                                }
                            });

                }
            });
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void bindPresenter() {
        presenter = new LightPresenter(this);
    }
}
