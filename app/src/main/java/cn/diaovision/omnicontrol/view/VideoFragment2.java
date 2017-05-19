package cn.diaovision.omnicontrol.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.adapter.SelectableAdapter;

/**
 * Created by TaoYimin on 2017/5/18.
 */

public class VideoFragment2 extends BaseFragment implements VideoContract.View {
    @BindView(R.id.input)
    RecyclerView inputRecyclerView;
    @BindView(R.id.output)
    RecyclerView outputRecyclerView;
    @BindViews({R.id.input_count,R.id.output_count,R.id.input_lastposition,R.id.output_lastposition,R.id.input_size,R.id.output_size})
    List<TextView> views;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;

    private SelectableAdapter inputAdapter;
    private SelectableAdapter outputAdapter;
    private ItemSelectionSupport inputSelectionSupport;
    private ItemSelectionSupport outputSelectionSupport;
    VideoContract.Presenter presenter;

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
        presenter = new VideoPresenter(this);
        List<Port> inputs=presenter.getInputList();
        final List<Port> outputs=presenter.getOutputList();
        inputSelectionSupport=new ItemSelectionSupport(inputRecyclerView);
        outputSelectionSupport=new ItemSelectionSupport(outputRecyclerView);
        inputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        inputAdapter=new SelectableAdapter(inputs,inputSelectionSupport);
        outputAdapter=new SelectableAdapter(outputs,outputSelectionSupport);
        inputRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
        outputRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
        inputRecyclerView.setAdapter(inputAdapter);
        outputRecyclerView.setAdapter(outputAdapter);

     /*   btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.MULTIPLE){
                    //输入端列表完成编辑
                    List<Integer> list=new ArrayList<Integer>();
                    SparseBooleanArray sba = inputSelectionSupport.getCheckedItemPositions();
                    for(int i=0;i<sba.size();i++){
                        if(sba.get(sba.keyAt(i))){
                            list.add(sba.keyAt(i));
                            Log.i("info","sba.keyAt(i)="+sba.keyAt(i));
                        }
                    }
                    list.toArray();
                    inputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
                    inputAdapter.notifyDataSetChanged();
                }else{
                    //输出端列表完成编辑
                    int in = 0;
                    SparseBooleanArray inputSba = inputSelectionSupport.getCheckedItemPositions();
                    for(int i=0;i<inputSba.size();i++){
                        if(inputSba.get(inputSba.keyAt(i))){
                            in=inputSba.keyAt(i);
                            break;
                        }
                    }
                    List<Integer> list=new ArrayList<>();
                    SparseBooleanArray outputSba = outputSelectionSupport.getCheckedItemPositions();
                    for(int i=0;i<outputSba.size();i++){
                        if(outputSba.get(outputSba.keyAt(i))){
                            list.add(outputSba.keyAt(i));
                        }
                    }
                    int[] outs=new int[list.size()];
                    for(int i=0;i<list.size();i++){
                        outs[i]=list.get(i);
                    }
                    //Integer[] outs=list.toArray(new Integer[list.size()]);
                    outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
                    outputAdapter.notifyDataSetChanged();
                    //presenter.setChannel(in,outs, Channel.MOD_NORMAL);
                    presenter.switchVideo(in,outs);
                }
            }
        });*/

        inputRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(inputRecyclerView){
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                inputSelectionSupport.itemClick(position);
                updateInfoAfter();
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE&&outputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE){
                    //输入端输出端都为单选模式
                    if(inputSelectionSupport.isItemChecked(position)){
                        //长按的item已经被选中
                        outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                        outputAdapter.notifyDataSetChanged();
                    }else{
                        //长按的item还未被选中
                        outputSelectionSupport.itemLongClick(-1);
                        inputSelectionSupport.itemClick(position);
                    }
                }else if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.MULTIPLE){
                    //输入端为多选模式，输出端为单选模式
                    inputSelectionSupport.itemLongClick(position);
                }else{
                    //输入端为单选模式，输出端为多选模式
                    inputSelectionSupport.itemClick(position);
                }
                updateInfoAfter();
            }
        });

        outputRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(outputRecyclerView){
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                outputSelectionSupport.itemClick(position);
                updateInfoAfter();
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, int position) {
                updateInfoBefore();
                if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE&&outputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.SINGLE){
                    //输入端输出端都为单选模式
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
            }
        });

        inputSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelect(int position) {
                int[] outsIdx=presenter.getOutputIdx(presenter.getInputList().get(position).idx);
                outputSelectionSupport.clearChoices();
                if(outsIdx!=null) {
                    for (int outIdx : outsIdx) {
                        outputSelectionSupport.setItemChecked(outIdx, true);
                    }
                }
                outputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUnSelect(int position) {
                outputSelectionSupport.clearChoices();
                outputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPopupDialog(int position) {
                popupDialog(presenter.getInputList().get(position));
            }

            @Override
            public void onSelectCountChange(int count) {
            }
        });

        outputSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelect(int position) {
                int inIdx=presenter.getInputIdx(presenter.getOutputList().get(position).idx);
                inputSelectionSupport.clearChoices();
                if(inIdx!=-1) {
                    inputSelectionSupport.setItemChecked(inIdx, true);
                }
                inputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUnSelect(int position) {
                inputSelectionSupport.clearChoices();
                inputAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPopupDialog(int position) {
                popupDialog(presenter.getOutputList().get(position));
            }

            @Override
            public void onSelectCountChange(int count) {
            }
        });
    }

    private void updateInfoAfter() {
        views.get(0).setText("输入端选中"+inputSelectionSupport.getCheckedItemCount()+"个");
        views.get(1).setText("输出端选中"+outputSelectionSupport.getCheckedItemCount()+"个");
        views.get(4).setText("输入端"+inputSelectionSupport.getCheckedItemPositions());
        views.get(5).setText("输出端"+outputSelectionSupport.getCheckedItemPositions());
    }
    private void updateInfoBefore() {
        views.get(2).setText("输入端LastPosition="+inputSelectionSupport.getLastPosition());
        views.get(3).setText("输出端LastPosition="+outputSelectionSupport.getLastPosition());
    }

    @OnClick({R.id.btn1,R.id.btn2})
    public void completeEdit(View view){
        if(inputSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.MULTIPLE){
            //输入端列表完成编辑
            List<Integer> list=new ArrayList<>();
            SparseBooleanArray sba = inputSelectionSupport.getCheckedItemPositions();
            for(int i=0;i<sba.size();i++){
                if(sba.get(sba.keyAt(i))){
                    list.add(sba.keyAt(i));
                    Log.i("info","sba.keyAt(i)="+sba.keyAt(i));
                }
            }
            list.toArray();
            inputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
            inputAdapter.notifyDataSetChanged();
        }else{
            //输出端列表完成编辑
            int in = 0;
            SparseBooleanArray inputSba = inputSelectionSupport.getCheckedItemPositions();
            for(int i=0;i<inputSba.size();i++){
                if(inputSba.get(inputSba.keyAt(i))){
                    in=inputSba.keyAt(i);
                    break;
                }
            }
            List<Integer> list=new ArrayList<>();
            SparseBooleanArray outputSba = outputSelectionSupport.getCheckedItemPositions();
            for(int i=0;i<outputSba.size();i++){
                if(outputSba.get(outputSba.keyAt(i))){
                    list.add(outputSba.keyAt(i));
                }
            }
            int[] outs=new int[list.size()];
            for(int i=0;i<list.size();i++){
                outs[i]=list.get(i);
            }
            //Integer[] outs=list.toArray(new Integer[list.size()]);
            outputSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
            outputAdapter.notifyDataSetChanged();
            //presenter.setChannel(in,outs, Channel.MOD_NORMAL);
            switch (view.getId()){
                case R.id.btn1:
                    presenter.switchVideo(in,outs);
                    break;
                case R.id.btn2:
                    presenter.stitchVideo(in,2,1,outs);
                    break;
            }
        }
    }

    public void popupDialog(final Port port){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_port, null);
        TextView textView= (TextView) view.findViewById(R.id.dialog_text);
        textView.setText("这是"+port.idx+"号端口");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view);
        builder.setTitle("编辑端口信息");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 提交端口修改信息
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消修改端口信息
            }
        });
        builder.show();
    }

    @Override
    public void bindPresenter() {
        //presenter = new VideoPresenter(this);
    }
}
