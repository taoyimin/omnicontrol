package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.CircleCharView;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.VolumeChannelRadioGroupView;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class AudioFragment extends Fragment{

    @BindView(R.id.input)
    PortRadioGroupView inputList;
    @BindView(R.id.output)
    PortRadioGroupView outputList;
    @BindView(R.id.audioChnList)
    VolumeChannelRadioGroupView volumeChannelList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_audio, container, false);
        ButterKnife.bind(this, v);

           /* test code */

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
        List<Channel> audioChnList = new ArrayList<>();
        for (int m = 0; m < 32; m ++){
            Channel chn = new Channel();
            chn.alias = "测试"+String.valueOf(m);
            chn.idx = m;
            chn.state = m%4;
            chn.val = m;
            audioChnList.add(chn);
        }


        inputList.config(ports, R.layout.item_port);
        inputList.configLayout(LinearLayoutManager.VERTICAL, 3);
        outputList.config(outports, R.layout.item_port);
        outputList.configLayout(LinearLayoutManager.VERTICAL, 3);
        volumeChannelList.config(audioChnList, R.layout.item_volume);

        inputList.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                outputList.select(pos);
                volumeChannelList.select(pos);
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        outputList.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                inputList.select(pos);
                volumeChannelList.select(pos);
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        volumeChannelList.setOnItemSelectListener(new VolumeChannelRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                outputList.select(pos);
                inputList.select(pos);
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        return v;
    }

}
