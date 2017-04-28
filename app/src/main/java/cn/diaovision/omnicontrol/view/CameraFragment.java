package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import cn.diaovision.omnicontrol.widget.DirectionPad;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.VideoLayout;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class CameraFragment extends BaseFragment implements CameraContract.View{


    @BindView(R.id.preset_list)
    CameraPresetRadioGroupView camerPresetView;

    @BindView(R.id.channel_list)
    PortRadioGroupView cameraList;

    @BindView(R.id.pad_direction)
    DirectionPad padDirection;

    @BindView(R.id.video_layout)
    VideoLayout videoLayout;

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
        final List<Port> portList = new ArrayList<>();

        for (int m = 0; m < 12; m ++){
            Preset preset = new Preset(String.valueOf(m*30), m);
            presetList.add(preset);
        }
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

        View view=LayoutInflater.from(getContext()).inflate(R.layout.footer_preset,null,false);
        camerPresetView.adapter.setFooterView(view);
        camerPresetView.adapter.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"点击了增加按钮",Toast.LENGTH_SHORT).show();
            }
        });

        cameraList.setOnItemLongClickListener(new PortRadioGroupView.OnItemLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                cameraList.popupDialog(portList.get(position));
            }
        });

        padDirection.setOnMoveListener(new DirectionPad.OnMoveListener() {
            @Override
            public void onMove(int deg, int velo) {
            }
        });

        //设置播放路径
        videoLayout.setVideoPath("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
    }

    @Override
    public void bindPresenter() {
        this.presenter = new CameraPresenter(this);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getOrder()){
            case 1:
                Toast.makeText(getContext(),"修改第"+item.getItemId()+"个预置位",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getContext(),"删除第"+item.getItemId()+"个预置位",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        //停止视频播放，并释放资源
        videoLayout.screenShot();
        super.onDestroyView();
    }
}
