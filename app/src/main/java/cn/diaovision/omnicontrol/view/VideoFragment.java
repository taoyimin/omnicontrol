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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.adapter.PortItemAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class VideoFragment extends Fragment{

    @BindView(R.id.input)
    PortRadioGroupView inputPorts;
//    PortItemAdapter inputPortAdapter;
//    GridLayoutManager inputPortLayoutMgr;
//    boolean inputPortScrolling = false;
//    boolean inputPortDragging = false;


    @BindView(R.id.output)
    PortRadioGroupView outputPorts;
//    PortItemAdapter outputPortAdapter;
//    GridLayoutManager outputPortLayoutMgr;
//    boolean outputPortScrolling = false;
//    boolean outputPortDragging = false;

    /***********
     *Datum
     ************/
    MediaMatrix mediaMatrix = new MediaMatrix();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, v);

        /* test code */
       List<Port> ports = new ArrayList<>();
        for (int m = 0; m < 32; m ++){
            Port port = new Port();
            port.alias = "测试"+String.valueOf(m);
            port.idx = m;
            port.dir = Port.DIR_IN;
            port.state = m%4;
            ports.add(port);
        }
        final List<Port> outports = new ArrayList<>();
        for (int m = 0; m < 32; m ++){
            Port port = new Port();
            port.alias = "测试"+String.valueOf(m);
            port.idx = m;
            port.dir = Port.DIR_IN;
            port.state = m%4;
            outports.add(port);
        }

        //RecyclerView config
        inputPorts.config(ports, R.layout.item_port);
        outputPorts.config(outports, R.layout.item_port);

        inputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                outputPorts.select(pos);
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        outputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                inputPorts.select(pos);
            }

            @Override
            public void onUnselected(int pos) {

            }
        });
        return v;
    }
}
