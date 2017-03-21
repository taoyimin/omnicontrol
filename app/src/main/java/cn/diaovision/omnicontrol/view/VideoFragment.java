package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.OmniControlApplication;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.BaseFragment;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.adapter.PortItemAdapter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class VideoFragment extends BaseFragment{

    @BindView(R.id.input)
    PortRadioGroupView inputPorts;


    @BindView(R.id.output)
    PortRadioGroupView outputPorts;

    /***********
     *Datum
     ************/
    MediaMatrix mediaMatrix = new MediaMatrix();
    TcpClient client = new TcpClient("127.0.0.1", 5000);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, v);



        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* test code */
        final List<Port> ports = new ArrayList<>();
        final List<Port> outports = new ArrayList<>();

        //RecyclerView config
        inputPorts.config(ports, R.layout.item_port);
        outputPorts.config(outports, R.layout.item_port);
        inputPorts.updateData();
        outputPorts.updateData();

        Flowable.create(new FlowableOnSubscribe<Port>() {
            @Override
            public void subscribe(FlowableEmitter<Port> e) throws Exception {
                for (int m = 0; m < 32; m ++) {
                    Port port = new Port();
                    port.alias = "测试" + String.valueOf(m);
                    port.idx = m;
                    port.dir = Port.DIR_IN;
                    port.state = m % 4;
                    ports.add(port);
                }
                e.onNext(new Port());
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Port>() {
                    @Override
                    public void accept(Port port) throws Exception {
                        inputPorts.updateData();
                    }
                });


        Flowable.create(new FlowableOnSubscribe<Port>() {
            @Override
            public void subscribe(FlowableEmitter<Port> e) throws Exception {
                for (int m = 0; m < 32; m ++){
                    Port port = new Port();
                    port.alias = "测试"+String.valueOf(m);
                    port.idx = m;
                    port.dir = Port.DIR_IN;
                    port.state = m%4;
                    outports.add(port);
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Port>() {
                    @Override
                    public void accept(Port port) throws Exception {
                        outputPorts.updateData();
                    }
                });


        inputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                outputPorts.select(pos);

                Log.i("U", "MSG " + MatrixMessage.MessageUtils.toGB3212("123"));
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        outputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                inputPorts.select(pos);

                String str = "hello";
                int n = client.send(str.getBytes());
                Log.i("U", "C n = " + n);
            }

            @Override
            public void onUnselected(int pos) {

            }
        });
    }
}
