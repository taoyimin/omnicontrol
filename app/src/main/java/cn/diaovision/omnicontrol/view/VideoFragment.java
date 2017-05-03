package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
                        Log.i("info","ACTION_DOWN");
                        canEdit=true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("info","ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("info","ACTION_UP");
                        canEdit=false;
                        if(outputPorts.isEditing()){
                            outputPorts.setEditing(false);
                            //完成编辑的操作
                            Toast.makeText(getContext(),"完成编辑",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        outputPorts.setDispatchTouchEventListener(new PortRadioGroupView.DispatchTouchEventListener() {
            @Override
            public void dispatchTouchEvent(View v, MotionEvent e, int position) {
                switch (e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(canEdit&&!outputPorts.isEditing()){
                            outputPorts.setEditing(true);
                            Toast.makeText(getContext(),"进入编辑模式",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
            }
        });

        inputPorts.setOnItemLongClickListener(new PortRadioGroupView.OnItemLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                //没有在编辑状态则弹出对话框
                canEdit=false;
                if(!outputPorts.isEditing())
                    inputPorts.popupDialog(ports.get(position));
            }
        });

        outputPorts.setOnItemSelectListener(new PortRadioGroupView.OnItemSelectListener() {
            @Override
            public void onSelected(int pos) {

            }

            @Override
            public void onUnselected(int pos) {

            }
        });

        outputPorts.setOnItemLongClickListener(new PortRadioGroupView.OnItemLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                //没有在编辑状态则弹出对话框
                canEdit=false;
                if(!outputPorts.isEditing())
                    outputPorts.popupDialog(outports.get(position));
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

    @Override
    public void bindPresenter() {
        presenter = new VideoPresenter(this);
    }
}
