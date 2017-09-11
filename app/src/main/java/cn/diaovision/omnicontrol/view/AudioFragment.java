package cn.diaovision.omnicontrol.view;

import android.app.Service;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.AudioDrawerLayout;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.VisualizerView;
import cn.diaovision.omnicontrol.widget.adapter.VolumeAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class AudioFragment extends BaseFragment implements AudioContract.View {
    @BindView(R.id.input)
    RecyclerView inputRecyclerView;
    @BindView(R.id.output)
    RecyclerView outputRecyclerView;
    @BindView(R.id.assist_drawer_layout)
    AudioDrawerLayout drawerLayout;
    @BindView(R.id.visual_view)
    VisualizerView visualizerView;

    private VolumeAdapter inputAdapter;
    private VolumeAdapter outputAdapter;
    private ItemSelectionSupport inputSelectionSupport;
    private ItemSelectionSupport outputSelectionSupport;
    private static final int OPEN_DRAWER = 0;
    private static final int CLOSE_DRAWER = 1;
    Port currentPort;

    Visualizer visualizer;
    MediaPlayer mediaPlayer;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_DRAWER:
                    drawerLayout.openDrawer();
                    break;
                case CLOSE_DRAWER:
                    drawerLayout.closeDrawer();
                    break;
            }
        }
    };

    AudioPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audio, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputSelectionSupport = new ItemSelectionSupport(inputRecyclerView);
        outputSelectionSupport = new ItemSelectionSupport(outputRecyclerView);
        inputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        inputAdapter = new VolumeAdapter(presenter.getInputList(), inputSelectionSupport);
        outputAdapter = new VolumeAdapter(presenter.getOutputList(), outputSelectionSupport);
        inputRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        outputRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        inputRecyclerView.setHasFixedSize(true);
        outputRecyclerView.setHasFixedSize(true);
        inputRecyclerView.setAdapter(inputAdapter);
        outputRecyclerView.setAdapter(outputAdapter);
        drawerLayout.setOnEditCompleteListener(new AudioDrawerLayout.OnEditCompleteListener() {
            @Override
            public void onComplete() {
                //当前选中输入端
                int in = inputSelectionSupport.getCheckedItemPosition();
                //当前选中输出端
                List<Integer> selects = outputSelectionSupport.getCheckedItemPositions();
                int[] outs = new int[selects.size()];
                for (int i = 0; i < selects.size(); i++) {
                    outs[i] = selects.get(i);
                }
                presenter.switchVideo(in, outs);
                editComplete();
            }
        });

        inputRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(inputRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                inputSelectionSupport.itemClick(position);
                //初始化输出端选择的颜色和角标
                outputSelectionSupport.initChoiceConfig(inputAdapter.getData().get(position));
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, final int position) {
                if (inputSelectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.SINGLE && outputSelectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.SINGLE) {
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    vib.vibrate(70);
                    //输入端输出端都为单选模式
                    //初始化输出端选择的颜色和角标
                    outputSelectionSupport.initChoiceConfig(inputAdapter.getData().get(position));
                    if (inputSelectionSupport.isItemChecked(position)) {
                        //长按的item已经被选中
                        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                        outputAdapter.notifyDataSetChanged();
                    } else {
                        //长按的item还未被选中
                        outputSelectionSupport.itemLongClick(-1);
                        inputSelectionSupport.itemClick(position);
                        inputAdapter.notifyDataSetChanged();
                    }
                    //弹出抽屉，直接调用drawerLayout.openDrawer()方法没有弹出效果
                    handler.sendEmptyMessage(0);
                } else if (inputSelectionSupport.getChoiceMode() == ItemSelectionSupport.ChoiceMode.MULTIPLE) {
                    //输入端为多选模式，输出端为单选模式
                    inputSelectionSupport.itemLongClick(position);
                } else {
                    //输入端为单选模式，输出端为多选模式
                    inputSelectionSupport.itemClick(position);
                }
            }

            @Override
            public void onItemDoubleClick(RecyclerView.ViewHolder vh, int position) {
            }
        });

        outputRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(outputRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                outputSelectionSupport.itemClick(position);
            }

            @Override
            public void onItemDoubleClick(RecyclerView.ViewHolder vh, int position) {
            }
        });

        inputSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                currentPort = presenter.getInputList().get(position);
                //获取到选中输入端对应的输出端
                int[] outsIdx = presenter.getOutputIdx(presenter.getInputList().get(position).idx);
                outputSelectionSupport.clearChoices();
                if (outsIdx != null) {
                    for (int outIdx : outsIdx) {
                        //将对应的输出端设为选中状态
                        outputSelectionSupport.setItemChecked(outIdx, true);
                    }
                }
                outputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUnSelectSingle(int position) {
                currentPort = null;
                //清空输出端列表的所有选中状态
                outputSelectionSupport.clearChoices();
                outputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelectMultiple(int position) {

            }

            @Override
            public void onUnSelectMultiple(int position) {

            }
        });

        outputSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                //获取选中输出端对应的输入端
                int inIdx = presenter.getInputIdx(presenter.getOutputList().get(position).idx);
                inputSelectionSupport.clearChoices();
                //将对应的输入端设为选中状态
                if (inIdx != -1) {
                    inputSelectionSupport.setItemChecked(inIdx, true);
                    inputAdapter.notifyDataSetChanged();
                    //获取对应输入端所对应的所有输出端
                    int[] outsIdx = presenter.getOutputIdx(inIdx);
                    outputSelectionSupport.clearChoices();
                    //将对应的输出端设为选中状态
                    if (outsIdx != null) {
                        for (int outIdx : outsIdx) {
                            outputSelectionSupport.setItemChecked(outIdx, true);
                        }
                    }
                    outputAdapter.notifyDataSetChanged();
                } else {
                    inputAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onUnSelectSingle(int position) {
                currentPort = null;
                //清空输入端列表所有选中状态
                inputSelectionSupport.clearChoices();
                inputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelectMultiple(int position) {

            }

            @Override
            public void onUnSelectMultiple(int position) {

            }
        });
        //因为矩阵预览卡流媒体只有视频没有音频，所以音频频谱暂时不用
        //initVisualizer();
        //visualizer.setEnabled(true);
        //播放测试音频
        //mediaPlayer.start();
    }

    private void initVisualizer() {
        //测试音频
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.test);
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                visualizerView.updateVisualizer(fft);
            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true);
    }

    public AudioDrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    /*设置完端口切换模式后调用*/
    public void editComplete(){
        //编辑完成后设为单选模式
        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        outputAdapter.notifyDataSetChanged();
        inputAdapter.notifyDataSetChanged();
        //还原输出端选择的颜色和角标
        outputSelectionSupport.initChoiceConfig(null);
        //关闭抽屉，直接调用drawerLayout.closeDrawer()方法没有收回效果
        handler.sendEmptyMessage(CLOSE_DRAWER);
    }

    @Override
    public void bindPresenter() {
        presenter = new AudioPresenter(this);
    }
}
