package cn.diaovision.omnicontrol.view;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.PresetDialog;
import cn.diaovision.omnicontrol.widget.VideoLayout;
import cn.diaovision.omnicontrol.widget.adapter.CameraAdapter;
import cn.diaovision.omnicontrol.widget.adapter.PresetAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class CameraFragment extends BaseFragment implements CameraContract.View {


/*    @BindView(R.id.preset_list)
    CameraPresetRadioGroupView camerPresetView;*/

/*    @BindView(R.id.channel_list)
    PortRadioGroupView cameraList;*/

/*    @BindView(R.id.pad_direction)
    DirectionPad padDirection;*/

    @BindView(R.id.video_layout)
    VideoLayout videoLayout;

/*    @BindViews({R.id.btn_narrow,R.id.btn_wide,R.id.btn_fast,R.id.btn_slow,R.id.btn_stop})
    List<Button> btnList;*/

    @BindView(R.id.camera)
    RecyclerView cameraRecyclerView;

    @BindView(R.id.preset)
    RecyclerView presetRecyclerView;

    @BindView(R.id.camera_button_bg)
    ImageView cameraButtonBg;

    @BindViews({R.id.camera_up,R.id.camera_down,R.id.camera_left,R.id.camera_right,R.id.camera_zoom_in,R.id.camera_zoom_out,R.id.camera_rewind,R.id.camera_stop,R.id.camera_fast_forward})
    List<Button> cameraControlButtons;

    CameraPresenter presenter;
    CameraAdapter cameraAdapter;
    PresetAdapter presetAdapter;
    ItemSelectionSupport cameraSelectionSupport;
    ItemSelectionSupport presetSelectionSupport;

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
        //currentCamera = presenter.getCamera(1);
        /* test code */
        //final List<Preset> presetList = new ArrayList<>();
        //final List<Preset> presetList = currentCamera.getPresetList();
        //final List<Port> portList = new ArrayList<>();

/*        for (int m = 0; m < 10; m ++){
            Preset preset = new Preset(String.valueOf(m*30), m);
            presetList.add(preset);
        }*/
/*        for (int m = 0; m < 8; m++) {
            Port port = new Port(1, 1, Port.TYPE_VIDEO, Port.DIR_IN, Port.CATEGORY_CAMERA);
            port.alias = "测试" + String.valueOf(m);
            port.idx = m;
            port.state = State.ON;
            portList.add(port);
        }*/

/*        cameraList.config(portList, R.layout.item_port);
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
        }*/

        //设置播放路径
        //videoLayout.setVideoPath("rtsp://192.168.10.108:8554/test.mov");
        //videoLayout.setVideoPath("rtsp://192.168.10.31:554/test1.MP4");
        //videoLayout.setVideoPath("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
        //videoLayout.setVideoPath("http://live.3gv.ifeng.com/live/zixun.m3u8");
        //videoLayout.setVideoPath("rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp");

        cameraSelectionSupport = new ItemSelectionSupport(cameraRecyclerView);
        cameraSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        cameraAdapter = new CameraAdapter(presenter.getCameraList(), cameraSelectionSupport);
        cameraRecyclerView.setAdapter(cameraAdapter);
        cameraRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        cameraRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(cameraRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                cameraSelectionSupport.itemClick(position);
            }
        });

        cameraSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                initPreset(presenter.getCameraList().get(position));
            }

            @Override
            public void onUnSelectSingle(int position) {
                initPreset(null);
            }

            @Override
            public void onSelectMultiple(int position) {

            }

            @Override
            public void onUnSelectMultiple(int position) {

            }

            @Override
            public void onPopupDialog(int position) {

            }

            @Override
            public void onSelectCountChange(int count) {

            }
        });

        presetRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(presetRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                HiCamera camera=presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                if(position<camera.getPresetList().size()) {
                    presetSelectionSupport.itemClick(position);
                }else{
                    presenter.addPreset(camera.getPortIdx(),camera.getPresetList().size(),"预置位");
                    HiCamera.Preset preset = new HiCamera.Preset("预置位", camera.getPresetList().size());
                    camera.updatePreset(preset);
                    presetAdapter.notifyItemInserted(vh.getLayoutPosition());
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, final int position) {
                final HiCamera camera = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                if(position<camera.getPresetList().size()) {
                    HiCamera.Preset preset=camera.getPresetList().get(position);
                    popupDialog(preset,position);
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    vib.vibrate(70);
                }
            }
        });

        for(final Button button:cameraControlButtons){
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(cameraSelectionSupport.getCheckedItemPosition()==-1){
                                Toast.makeText(getContext(),"当前没有选中摄像机",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            HiCamera camera=presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                            switch (button.getId()){
                                case R.id.camera_up:
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_up);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_UP,20);
                                    break;
                                case R.id.camera_down:
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_down);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_DOWN,20);
                                    break;
                                case R.id.camera_left:
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_left);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_LEFT,20);
                                    break;
                                case R.id.camera_right:
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_right);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_RIGHT,20);
                                    break;
                                case R.id.camera_zoom_in:
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_WIDE,20);
                                    break;
                                case R.id.camera_zoom_out:
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_NARROW,20);
                                    break;
                                case R.id.camera_rewind:
                                    //presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage,20);
                                    break;
                                case R.id.camera_stop:
                                    //presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage,20);
                                    break;
                                case R.id.camera_fast_forward:
                                    //presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage,20);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_HOVER_EXIT:
                            if(cameraSelectionSupport.getCheckedItemPosition()==-1){
                                return false;
                            }
                            HiCamera camera2=presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                            cameraButtonBg.setImageResource(R.mipmap.camera_button_bg);
                            presenter.cameraStopGo(camera2.getPortIdx());
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        //videoLayout.setVideoPath("rtsp://192.168.10.100:8554/test.ts");
        //videoLayout.setVideoPath("http://192.168.10.100:8080/Test/video2.mp4");
        videoLayout.setVideoPath("rtsp://192.168.10.31/test1.ts");
        //videoLayout.setVideoPath("http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8");
        //videoLayout.setVideoPath("http://192.168.10.101:8080/Test/video2.mp4");
        videoLayout.start();
    }

    @Override
    public void bindPresenter() {
        this.presenter = new CameraPresenter(this);
    }

/*    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getOrder()) {
            case 1:
                Toast.makeText(getContext(), "修改第" + item.getItemId() + "个预置位", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                //item.getItemId()要加1，因为有一个默认的presetIdx=0的预置位
                presenter.delPreset(currentCamera.getPortIdx(), item.getItemId() + 1);
                break;
        }
        return super.onContextItemSelected(item);
    }*/

    public void initPreset(final HiCamera camera) {
        presetSelectionSupport = new ItemSelectionSupport(presetRecyclerView);
        presetSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        if (camera == null) {
            presetAdapter = new PresetAdapter(null, presetSelectionSupport);
        } else {
            presetAdapter = new PresetAdapter(camera.getPresetList(), presetSelectionSupport);
        }
        presetRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        presetRecyclerView.setAdapter(presetAdapter);
        presetAdapter.notifyDataSetChanged();
        presetSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                presenter.loadPreset(camera.getPortIdx(), camera.getPresetList().get(position).getIdx());
            }

            @Override
            public void onUnSelectSingle(int position) {
                presenter.loadPreset(camera.getPortIdx(), 0);
            }

            @Override
            public void onSelectMultiple(int position) {

            }

            @Override
            public void onUnSelectMultiple(int position) {

            }

            @Override
            public void onPopupDialog(int position) {

            }

            @Override
            public void onSelectCountChange(int count) {

            }
        });
    }

    public void popupDialog(HiCamera.Preset preset, final int position){
        final HiCamera camera = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
        final PresetDialog dialog = new PresetDialog(getContext(), preset);
        dialog.show();
        dialog.setOnButtonClickListener(new PresetDialog.OnButtonClickListener() {
            @Override
            public void onConfirmClick() {
                presetAdapter.notifyItemChanged(position);
                dialog.dismiss();
            }

            @Override
            public void onDeleteClick() {
                camera.getPresetList().remove(position);
                presetAdapter.notifyItemRemoved(position);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        //停止视频播放，并释放资源
        videoLayout.stopPlayback();
        //IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
