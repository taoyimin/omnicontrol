package cn.diaovision.omnicontrol.view;

import android.app.Service;
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
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.util.PortHelper;
import cn.diaovision.omnicontrol.widget.AssistDrawerLayout;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.PortDialog;
import cn.diaovision.omnicontrol.widget.VideoLayout;
import cn.diaovision.omnicontrol.widget.adapter.PortAdapter;

/**
 * Created by TaoYimin on 2017/5/18.
 */

public class VideoFragment2 extends BaseFragment implements VideoContract.View {
    @BindView(R.id.input)
    RecyclerView inputRecyclerView;
    @BindView(R.id.output)
    RecyclerView outputRecyclerView;
    @BindViews({R.id.input_count,R.id.output_count,R.id.input_lastposition,R.id.output_lastposition,R.id.input_size,R.id.output_size,R.id.channel_size})
    List<TextView> views;
    @BindView(R.id.assist_drawer_layout)
    AssistDrawerLayout drawerLayout;
/*    @BindView(R.id.set_subtitle)
    Button setSubtitle;*/
    @BindView(R.id.edit_subtitle)
    EditText editSubtitle;
    @BindView(R.id.video_layout)
    VideoLayout ijkVideoView;

    private PortAdapter inputAdapter;
    private PortAdapter outputAdapter;
    private ItemSelectionSupport inputSelectionSupport;
    private ItemSelectionSupport outputSelectionSupport;
    VideoContract.Presenter presenter;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    drawerLayout.openDrawer();
                    break;
                case 1:
                    drawerLayout.closeDrawer();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video2, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Set<Channel> channelSet=presenter.getChannelSet();
        List<Port> inputs=presenter.getInputList();
        final List<Port> outputs=presenter.getOutputList();
        PortHelper.getInstance().init(inputs,outputs,channelSet);
        inputSelectionSupport=new ItemSelectionSupport(inputRecyclerView);
        outputSelectionSupport=new ItemSelectionSupport(outputRecyclerView);
        inputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        inputAdapter=new PortAdapter(inputs,inputSelectionSupport);
        outputAdapter=new PortAdapter(outputs,outputSelectionSupport);
        inputRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
        outputRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
        inputRecyclerView.setHasFixedSize(true);
        outputRecyclerView.setHasFixedSize(true);
        inputRecyclerView.setAdapter(inputAdapter);
        outputRecyclerView.setAdapter(outputAdapter);

        drawerLayout.setOnEditCompleteListener(new AssistDrawerLayout.OnEditCompleteListener() {
            @Override
            public void onComplete(int mode) {
                //当前选中输入端
                int in = inputSelectionSupport.getCheckedItemPosition();
                //当前选中输出端
                List<Integer> selects= outputSelectionSupport.getCheckedItemPositions();
                int[] outs=new int[selects.size()];
                for(int i=0;i<selects.size();i++){
                    outs[i]=selects.get(i);
                }
                switch (mode){
                    case AssistDrawerLayout.MODE_1XN:
                        presenter.switchVideo(in,outs);
                        break;
                    case AssistDrawerLayout.MODE_2X1:
                        presenter.stitchVideo(in,2,1,outs);
                        break;
                    case AssistDrawerLayout.MODE_2X2:
                        presenter.stitchVideo(in,2,2,outs);
                        break;
                    case AssistDrawerLayout.MODE_3X3:
                        presenter.stitchVideo(in,3,3,outs);
                        break;
                }
                //presenter.setChannel(in,outs,Channel.MOD_NORMAL);
                //编辑完成后设为单选模式
                outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
                outputAdapter.notifyDataSetChanged();
                inputAdapter.notifyDataSetChanged();
                //还原输出端选择的颜色和角标
                outputSelectionSupport.initChoiceConfig(null);
                //关闭抽屉，直接调用drawerLayout.closeDrawer()方法没有收回效果
                handler.sendEmptyMessage(1);
            }
        });

        inputRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(inputRecyclerView){
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                inputSelectionSupport.itemClick(position);
                updateInfoAfter();
                //初始化输出端选择的颜色和角标
                outputSelectionSupport.initChoiceConfig(inputAdapter.getData().get(position));
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, final int position) {
                updateInfoBefore();
                if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE&&outputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE){
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    vib.vibrate(70);
                    //输入端输出端都为单选模式
                    //初始化输出端选择的颜色和角标
                    outputSelectionSupport.initChoiceConfig(inputAdapter.getData().get(position));
                    if(inputSelectionSupport.isItemChecked(position)){
                        //长按的item已经被选中
                        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                        outputAdapter.notifyDataSetChanged();
                    }else{
                        //长按的item还未被选中
                        outputSelectionSupport.itemLongClick(-1);
                        inputSelectionSupport.itemClick(position);
                        inputAdapter.notifyDataSetChanged();
                    }
                    //弹出抽屉，直接调用drawerLayout.openDrawer()方法没有弹出效果
                    handler.sendEmptyMessage(0);
                }else if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.MULTIPLE){
                    //输入端为多选模式，输出端为单选模式
                    inputSelectionSupport.itemLongClick(position);
                }else{
                    //输入端为单选模式，输出端为多选模式
                    inputSelectionSupport.itemClick(position);
                }
                updateInfoAfter();
            }

            @Override
            public void onItemDoubleClick(RecyclerView.ViewHolder vh, int position) {
                popupDialog(presenter.getInputList().get(position),position);
            }
        });

        outputRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(outputRecyclerView){
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                outputSelectionSupport.itemClick(position);
                updateInfoAfter();
            }

/*            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE&&outputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE){
                    if(outputSelectionSupport.isItemChecked(position)){
                        //长按的item已经被选中
                        inputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                        inputAdapter.notifyDataSetChanged();
                    }else{
                        //长按的item还未被选中
                        inputSelectionSupport.itemLongClick(-1);
                        outputSelectionSupport.itemClick(position);
                    }
                }else if(outputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.MULTIPLE){
                    //输入端为单选模式，输出端为多选模式
                    outputSelectionSupport.itemLongClick(position);
                }else{
                    //输入端为多选模式，输出端为单选模式
                    outputSelectionSupport.itemClick(position);
                }
                updateInfoAfter();
            }*/

            @Override
            public void onItemDoubleClick(RecyclerView.ViewHolder vh, int position) {
                popupDialog(presenter.getOutputList().get(position),position);
            }
        });

        inputSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                //获取到选中输入端对应的输出端
                int[] outsIdx=presenter.getOutputIdx(presenter.getInputList().get(position).idx);
                outputSelectionSupport.clearChoices();
                if(outsIdx!=null) {
                    for (int outIdx : outsIdx) {
                        //将对应的输出端设为选中状态
                        outputSelectionSupport.setItemChecked(outIdx, true);
                    }
                }
                outputAdapter.notifyDataSetChanged();
/*                ijkVideoView.stopPlayback();
                presenter.switchVideo(position,new int[]{25});
                ijkVideoView.setVideoPath("rtsp://192.168.10.31/test1.ts");
                ijkVideoView.start();*/
                presenter.switchPreviewVideo(position,28);
            }

            @Override
            public void onUnSelectSingle(int position) {
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

            @Override
            public void onPopupDialog(int position) {
                //popupDialog(presenter.getInputList().get(position));
            }

            @Override
            public void onSelectCountChange(int count) {
            }
        });

        outputSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                //获取选中输出端对应的输入端
                int inIdx=presenter.getInputIdx(presenter.getOutputList().get(position).idx);
                inputSelectionSupport.clearChoices();
                //将对应的输入端设为选中状态
                if(inIdx!=-1) {
                    inputSelectionSupport.setItemChecked(inIdx, true);
                    inputAdapter.notifyDataSetChanged();
                    //获取对应输入端所对应的所有输出端
                    int[] outsIdx=presenter.getOutputIdx(inIdx);
                    outputSelectionSupport.clearChoices();
                    //将对应的输出端设为选中状态
                    if(outsIdx!=null) {
                        for (int outIdx : outsIdx) {
                            outputSelectionSupport.setItemChecked(outIdx, true);
                        }
                    }
                    outputAdapter.notifyDataSetChanged();
                }else{
                    inputAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onUnSelectSingle(int position) {
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

            @Override
            public void onPopupDialog(int position) {
                //popupDialog(presenter.getOutputList().get(position));
            }

            @Override
            public void onSelectCountChange(int count) {
            }
        });

        ijkVideoView.setVideoPath("rtsp://192.168.10.31/test1.ts");
        ijkVideoView.start();

/*        setSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPort==null){
                    Toast.makeText(getContext(),"当前没有选中任何输入输出端!",Toast.LENGTH_SHORT).show();
                    return;
                }
                String subtitle=editSubtitle.getText().toString();
                if(subtitle.isEmpty()){
                    Toast.makeText(getContext(),"标题不能为空!",Toast.LENGTH_SHORT).show();
                    return;
                }
                presenter.setSubtitle(currentPort.idx,subtitle);
            }
        });*/
    }

    private void updateInfoAfter() {
        views.get(0).setText("输入端选中"+inputSelectionSupport.getCheckedItemCount()+"个");
        views.get(1).setText("输出端选中"+outputSelectionSupport.getCheckedItemCount()+"个");
        views.get(4).setText("输入端"+inputSelectionSupport.getCheckedItemPositions());
        views.get(5).setText("输出端"+outputSelectionSupport.getCheckedItemPositions());
        views.get(6).setText("当前已配置通道"+presenter.getChannelSet().size()+"个");
    }

    private void updateInfoBefore() {
        views.get(2).setText("输入端LastPosition="+inputSelectionSupport.getLastPosition());
        views.get(3).setText("输出端LastPosition="+outputSelectionSupport.getLastPosition());
    }

    public void popupDialog(final Port port, final int position){
        final PortDialog dialog=new PortDialog(getContext(),port);
        dialog.show();
        dialog.setOnButtonClickListener(new PortDialog.OnButtonClickListener() {
            @Override
            public void onConfirmClick() {
                //对话框的确定按钮按下，回调到这里
                dialog.dismiss();
                if(port.dir==Port.DIR_IN){
                    inputAdapter.notifyItemChanged(position);
                }else if(port.dir==Port.DIR_OUT){
                    outputAdapter.notifyItemChanged(position);
                }else{
                    inputAdapter.notifyDataSetChanged();
                    outputAdapter.notifyDataSetChanged();
                }
                //存储到配置文件
                MainControlActivity.cfg.modifyPort(port);
            }
        });
    }

    @Override
    public void bindPresenter() {
        presenter = new VideoPresenter(this);
    }
}
