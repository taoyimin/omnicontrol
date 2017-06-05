package cn.diaovision.omnicontrol.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
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

    @BindViews({R.id.btn_narrow,R.id.btn_wide,R.id.btn_fast,R.id.btn_slow,R.id.btn_stop})
    List<Button> btnList;

    CameraPresenter presenter;

    int lastDeg;
    int lastVelo;
    HiCamera currentCamera;

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
        currentCamera=presenter.getCamera(1);
        /* test code */
        //final List<Preset> presetList = new ArrayList<>();
        final List<Preset> presetList = currentCamera.getPresetList();
        final List<Port> portList = new ArrayList<>();

/*        for (int m = 0; m < 10; m ++){
            Preset preset = new Preset(String.valueOf(m*30), m);
            presetList.add(preset);
        }*/
        for (int m = 0; m < 8; m ++){
            Port port = new Port(1,1, Port.TYPE_VIDEO, Port.DIR_IN,Port.CATEGORY_CAMERA);
            port.alias = "测试"+String.valueOf(m);
            port.idx = m;
            port.state = State.ON;
            portList.add(port);
        }

        cameraList.config(portList, R.layout.item_port);
        cameraList.configLayout(GridLayoutManager.VERTICAL, 4);
        camerPresetView.config(presetList, R.layout.item_preset);

        View footerview=LayoutInflater.from(getContext()).inflate(R.layout.footer_preset,null,false);
        camerPresetView.adapter.setFooterView(footerview);
        camerPresetView.adapter.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog();
            }
        });

        camerPresetView.setOnItemSelectListener(new CameraPresetRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                presenter.loadPreset(currentCamera.getPortIdx(),pos);
            }

            @Override
            public void onUnselected(int pos) {

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
                switch (deg){
                    case 0:
                        //摄像头右移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_RIGHT,velo/5);
                        break;
                    case 90:
                        //摄像头上移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_UP,velo/5);
                        break;
                    case 180:
                        //摄像头左移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_LEFT,velo/5);
                        break;
                    case 270:
                        //摄像头下移
                        presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_DOWN,velo/5);
                        break;
                }
            }

            @Override
            public void onMoveFinish() {
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

        for(Button btn:btnList){
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            switch (v.getId()){
                                case R.id.btn_narrow:
                                    presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_NARROW, 63);
                                    break;
                                case R.id.btn_wide:
                                    presenter.cameraCtrlGo(currentCamera.getPortIdx(),MatrixMessage.CAM_WIDE, 63);
                                    break;
                                case R.id.btn_fast:
                                    break;
                                case R.id.btn_slow:
                                    break;
                                case R.id.btn_stop:
                                    break;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_HOVER_EXIT:
                            presenter.cameraStopGo(currentCamera.getPortIdx());
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        //设置播放路径
        //videoLayout.setVideoPath("rtsp://192.168.10.108:8554/test.mov");
        //videoLayout.setVideoPath("rtsp://192.168.10.31:554/test1.MP4");
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
                //item.getItemId()要加1，因为有一个默认的presetIdx=0的预置位
                presenter.delPreset(currentCamera.getPortIdx(),item.getItemId()+1);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void popupDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_preset, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setTitle("添加预置位");
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!editText.getText().toString().isEmpty()){
                    String name=editText.getText().toString();
                    //presetIdx从1开始，保留presetIdx=0的预置位作为原始状态（调用presetIdx=0的预置位相当于重置摄像头位置），摄像头每次通电都会调用presetIdx=0的预置位
                    presenter.addPreset(currentCamera.getPortIdx(),currentCamera.getPresetList().size()+1,name);
                    camerPresetView.adapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onDestroyView() {
        //停止视频播放，并释放资源
        videoLayout.stopPlayback();
        super.onDestroyView();
    }
}
