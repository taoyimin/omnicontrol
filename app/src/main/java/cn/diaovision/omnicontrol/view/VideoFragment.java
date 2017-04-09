package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.rx.RxBus;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class VideoFragment extends BaseFragment implements VideoContract.View{

    @BindView(R.id.input)
    PortRadioGroupView inputPorts;


    @BindView(R.id.output)
    PortRadioGroupView outputPorts;

    @BindView(R.id.txt_output)
    AppCompatTextView txtOutput;


    /***********
     *Datum
     ************/
    MediaMatrix mediaMatrix = new MediaMatrix();

    VideoContract.Presenter presenter;
//    RxBus.RxSubscription rxSubscription = new RxBus.RxSubscription() {
//        @Override
//        public void accept(Object o) throws Exception {
//            if (String.class.isInstance(o)){
//            }
//        }
//    };



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



        inputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                outputPorts.select(pos);
                //TODO: send udp packet to server
//                getRxBus().post(new String("Message matrix"));
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
            }

            @Override
            public void onUnselected(int pos) {

            }
        });
    }


    @Override
    public void bindPresenter() {
        presenter = new VideoPresenter(this);
    }
}
