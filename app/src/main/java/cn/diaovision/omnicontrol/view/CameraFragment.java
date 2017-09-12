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
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.message.MatrixMessage;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.widget.CameraDialog;
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
    @BindView(R.id.video_layout)
    VideoLayout videoLayout;

    @BindView(R.id.camera)
    RecyclerView cameraRecyclerView;

    @BindView(R.id.preset)
    RecyclerView presetRecyclerView;

    @BindView(R.id.camera_button_bg)
    ImageView cameraButtonBg;

    @BindViews({R.id.camera_up, R.id.camera_down, R.id.camera_left, R.id.camera_right, R.id.camera_zoom_in, R.id.camera_zoom_out, R.id.camera_rewind, R.id.camera_stop, R.id.camera_fast_forward})
    List<Button> cameraControlButtons;

    CameraPresenter presenter;
    CameraAdapter cameraAdapter;
    PresetAdapter presetAdapter;
    ItemSelectionSupport cameraSelectionSupport;
    ItemSelectionSupport presetSelectionSupport;

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
        //初始化摄像机列表
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

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, int position) {
                //弹出修改摄像头对话框
                HiCamera camera = presenter.getCameraList().get(position);
                popupDialog(camera, position);
            }
        });

        cameraSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                //初始化预置位
                initPreset(presenter.getCameraList().get(position));
                //播放流媒体
                presenter.switchPreviewVideo(presenter.getCameraList().get(position).getPortIdx(),MainControlActivity.cfg.getMatrixPreviewPort());
                videoLayout.setVideoPath("rtsp://"+MainControlActivity.cfg.getMatrixPreviewIp()+"/test1.ts");
                videoLayout.start();
            }

            @Override
            public void onUnSelectSingle(int position) {
                initPreset(null);
                //停止播放流媒体
                videoLayout.stopPlayback();
            }

            @Override
            public void onSelectMultiple(int position) {

            }

            @Override
            public void onUnSelectMultiple(int position) {

            }
        });
        //初始化预置位列表的点击事件
        presetRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(presetRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                HiCamera camera = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                if (position < camera.getPresetList().size()) {
                    presetSelectionSupport.itemClick(position);
                } else {
                    //如果是最后一个item，则为添加预置位
                    presenter.addPreset(camera.getPortIdx(), camera.getPresetList().size(), "预置位");
                    HiCamera.Preset preset = new HiCamera.Preset("预置位", camera.getPresetList().size());
                    camera.updatePreset(preset);
                    presetAdapter.notifyItemInserted(vh.getLayoutPosition());
                    //写入配置文件
                    MainControlActivity.cfg.addCameraPreset(camera.getPortIdx(),preset);
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, final int position) {
                final HiCamera camera = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                if (position < camera.getPresetList().size()) {
                    //弹出修改预置位对话框
                    HiCamera.Preset preset = camera.getPresetList().get(position);
                    popupDialog(preset, position);
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    vib.vibrate(70);
                }
            }
        });
        //初始化摄像头操作按钮的点击事件
        for (final Button button : cameraControlButtons) {
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (cameraSelectionSupport.getCheckedItemPosition() == -1) {
                                Toast.makeText(getContext(), "当前没有选中摄像机", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            HiCamera camera = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
                            switch (button.getId()) {
                                case R.id.camera_up:
                                    //摄像头向上
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_up);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_UP, 20);
                                    break;
                                case R.id.camera_down:
                                    //摄像头向下
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_down);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_DOWN, 20);
                                    break;
                                case R.id.camera_left:
                                    //摄像头向左
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_left);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_LEFT, 20);
                                    break;
                                case R.id.camera_right:
                                    //摄像头向右
                                    cameraButtonBg.setImageResource(R.mipmap.camera_button_bg_right);
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_RIGHT, 20);
                                    break;
                                case R.id.camera_zoom_in:
                                    //摄像头放大
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_WIDE, 20);
                                    break;
                                case R.id.camera_zoom_out:
                                    //摄像头缩小
                                    presenter.cameraCtrlGo(camera.getPortIdx(), MatrixMessage.CAM_NARROW, 20);
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
                            if (cameraSelectionSupport.getCheckedItemPosition() == -1) {
                                return false;
                            }
                            //摄像头停止
                            HiCamera camera2 = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
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
    }

    @Override
    public void bindPresenter() {
        this.presenter = new CameraPresenter(this);
    }

    /*初始化预置位方法*/
    public void initPreset(final HiCamera camera) {
        //初始化预置位列表
        presetSelectionSupport = new ItemSelectionSupport(presetRecyclerView);
        presetSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        if (camera == null) {
            presetAdapter = new PresetAdapter(null, presetSelectionSupport);
        } else {
            presetAdapter = new PresetAdapter(camera.getPresetList(), presetSelectionSupport);
        }
        //使用WrapContentGridLayoutManager解决RecyclerView刷新数据可能导致应用崩溃的BUG
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
        });
    }

    /*弹出预置位对话框*/
    public void popupDialog(final HiCamera.Preset preset, final int position) {
        final HiCamera camera = presenter.getCameraList().get(cameraSelectionSupport.getCheckedItemPosition());
        final PresetDialog dialog = new PresetDialog(getContext(), preset);
        dialog.show();
        dialog.setOnButtonClickListener(new PresetDialog.OnButtonClickListener() {
            @Override
            public void onConfirmClick() {
                //点击确定修改后的操作
                presetAdapter.notifyItemChanged(position);
                dialog.dismiss();
                //写入配置文件
                MainControlActivity.cfg.setCameraPreset(camera.getPortIdx(),preset);
            }

            @Override
            public void onDeleteClick() {
                dialog.dismiss();
                if(position<camera.getPresetList().size()-1){
                    //从中间删除可能导致后面的预置位错位
                    Toast.makeText(getContext(),"请从最后一个预置位开始删除！",Toast.LENGTH_SHORT).show();
                    return;
                }
                camera.getPresetList().remove(position);
                presetAdapter.notifyItemRemoved(position);
                //写入配置文件
                MainControlActivity.cfg.deleteCameraPreset(camera.getPortIdx(),preset);
            }
        });
    }

    /*弹出摄像机对话框*/
    public void popupDialog(final HiCamera camera, final int position) {
        final CameraDialog dialog = new CameraDialog(getContext(), camera);
        dialog.show();
        dialog.setOnButtonClickListener(new CameraDialog.OnButtonClickListener() {
            @Override
            public void onConfirmClick() {
                dialog.dismiss();
                cameraAdapter.notifyItemChanged(position);
                //写入配置文件中
                MainControlActivity.cfg.setCamera(camera);
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
