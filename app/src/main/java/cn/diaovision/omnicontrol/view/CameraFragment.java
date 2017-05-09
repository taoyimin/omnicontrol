package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
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
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera.Preset;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.model.ConfigFixed;
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

    int lastDeg;
    int lastVelo;
    HiCamera currentCamera= new ConfigFixed().getHiCameraInfo().get(2);

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
                if(currentCamera==null){
                    return;
                }
                if(deg==lastDeg&&velo==lastVelo){
                    return;
                }
                lastDeg=deg;
                lastVelo=velo;
                Log.i("info","deg="+deg+"velo="+velo);
                switch (deg){
                    case 0:
                        //摄像头右移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_RIGHT,velo);
                        break;
                    case 90:
                        //摄像头上移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_UP,velo);
                        break;
                    case 180:
                        //摄像头左移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_LEFT,velo);
                        break;
                    case 270:
                        //摄像头下移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_DOWN,velo);
                        break;
                }
            }

            @Override
            public void onMoveFinish() {
                Log.i("info","移动结束");
                if(currentCamera!=null) {
                    presenter.cameraStopGo(currentCamera.getPortIdx());
                }
            }
        });

        cameraList.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        //设置播放路径
        //videoLayout.setVideoPath("rtsp://192.168.10.108:8554/test.mov");
        videoLayout.setVideoPath("rtsp://192.168.10.31:554/test1.MP4");
        //videoLayout.setVideoPath("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
        //videoLayout.setVideoPath("http://live.3gv.ifeng.com/live/zixun.m3u8");
        //videoLayout.setVideoPath("rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp");
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
        videoLayout.stopPlayback();
        super.onDestroyView();
    }
}
