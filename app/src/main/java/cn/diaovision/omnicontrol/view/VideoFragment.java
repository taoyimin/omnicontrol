package cn.diaovision.omnicontrol.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.adapter.AuxiliaryPanelItemAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class VideoFragment extends BaseFragment implements VideoContract.View{

    @BindView(R.id.input)
    PortRadioGroupView inputPorts;


    @BindView(R.id.output)
    PortRadioGroupView outputPorts;

    @BindView(R.id.auxiliary)
    RecyclerView auxiliary;

    boolean canEdit = false;
    boolean editing=false;

    /***********
     *Datum
     ************/
    MediaMatrix mediaMatrix = new MediaMatrix();

    VideoContract.Presenter presenter;
//    RxBus.RxSubscription rxSubscription = new RxBus.RxSubscription() {
//        @Override
//        public void accept(Object o) throws Exception {
//            if (String.class.isInstance(o)){
//            }
//        }
//    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* test code */
        final List<Port> ports = new ArrayList<>();
        final List<Port> outports = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Port port = new Port(i, i, i, i);
            ports.add(port);
            outports.add(port);
        }
        //RecyclerView config
        inputPorts.config(ports, R.layout.item_port);
        outputPorts.config(outports, R.layout.item_port);
        inputPorts.updateData();
        outputPorts.updateData();

        inputPorts.setDispatchTouchEventListener(new PortRadioGroupView.DispatchTouchEventListener() {
            @Override
            public void dispatchTouchEvent(View v, MotionEvent e, int position) {
                switch (e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //手指按下只会触发item的down事件
                        canEdit=true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        canEdit=false;
                        if(editing){
                            editing=false;
                            //完成编辑的操作
                            Toast.makeText(getContext(),"完成编辑",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        inputPorts.setOnItemLongClickListener(new PortRadioGroupView.OnItemLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                //没有在编辑状态则弹出对话框
                canEdit=false;
                if(!editing)
                    popupDialog(ports.get(position));
            }
        });

        outputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                if(canEdit&&!editing){
                    editing=true;
                    Toast.makeText(getContext(),"进入编辑模式",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUnselected(int pos) {

            }
        });

/*        inputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                outputPorts.select(pos);
                //TODO: send udp packet to server
//                getRxBus().post(new String("Message matrix"));
            }

            @Override
            public void onUnselected(int pos) {

            }
        });





        outputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {
                inputPorts.select(pos);

                String str = "hello";
            }

            @Override
            public void onUnselected(int pos) {

            }
        });*/

        /*test code*/
        List<String> list=new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add("第"+i+"项");
        }
        AuxiliaryPanelItemAdapter adapter=new AuxiliaryPanelItemAdapter(list);
        auxiliary.setLayoutManager(new LinearLayoutManager(getContext()));
        auxiliary.setAdapter(adapter);
    }

    /**
     * 弹出对话框
     * @param port
     */
    void popupDialog(Port port){
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
        presenter = new VideoPresenter(this);
    }
}
