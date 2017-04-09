package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera.Preset;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.CameraPresetRadioGroupView;
import cn.diaovision.omnicontrol.widget.PlayerControllerView;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class CameraFragment extends BaseFragment implements CameraContract.View{


    @BindView(R.id.preset_list)
    CameraPresetRadioGroupView camerPresetView;

    @BindView(R.id.channel_list)
    PortRadioGroupView cameraList;

    @BindView(R.id.play_controller)
    PlayerControllerView playerControllerView;

    CameraPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set presenter
        presenter = new CameraPresenter(this);
        /* test code */
        final List<Preset> presetList = new ArrayList<>();
        for (int m = 0; m < 12; m ++){
            Preset preset = new Preset(String.valueOf(m*30), 1);
            presetList.add(preset);
        }
        final List<Port> portList = new ArrayList<>();
        for (int m = 0; m < 8; m ++){
            Port port = new Port(1,1, Port.TYPE_VIDEO, Port.DIR_IN);
            port.alias = "测试"+String.valueOf(m);
            port.idx = m;
            port.state = State.ON;
            portList.add(port);
        }

        cameraList.config(portList, R.layout.item_port);
        cameraList.configLayout(GridLayoutManager.VERTICAL, 4);
        camerPresetView.config(presetList, R.layout.item_preset);

        playerControllerView.setOnControlListener(new PlayerControllerView.OnControlListener() {
            @Override
            public void onControl(int cmd) {
                Toast.makeText(getContext(), "cmd = " + cmd, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void bindPresenter() {
        this.presenter = new CameraPresenter(this);

    }

}
