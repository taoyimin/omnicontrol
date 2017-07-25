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
import cn.diaovision.omnicontrol.core.message.FusionMessage;
import cn.diaovision.omnicontrol.core.model.device.fusion.MediaFusion;
import cn.diaovision.omnicontrol.util.ByteUtils;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class DvdFragment extends BaseFragment implements DvdContract.View {
    @BindViews({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8})
    List<Button> list;

    DvdPresenter presenter;
    MediaFusion fusion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dvd, container, false);
        ButterKnife.bind(this, v);
        fusion = new MediaFusion();
        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;
            list.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FusionMessage msg = FusionMessage.buildCallMessage(finalI + 1);
                    Flowable.just(msg)
                            .map(new Function<FusionMessage, String>() {
                                @Override
                                public String apply(FusionMessage fusionMessage) throws Exception {
                                    Log.i("info", "send:" + ByteUtils.bytes2ascii(fusionMessage.toBytes()));
                                    String s = "1";
                                    List<byte[]> recvList = fusion.getController().send(fusionMessage.toBytes(), fusionMessage.toBytes().length, true);
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
                                    Log.i("info", s);
                                }
                            });

                }
            });
        }
        return v;
    }

    @Override
    public void bindPresenter() {
        presenter = new DvdPresenter(this);
    }
}
