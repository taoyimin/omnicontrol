package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.CircleCharView;
import cn.diaovision.omnicontrol.widget.adapter.PortItemAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class VideoFragment extends Fragment{

    @BindView(R.id.input)
    RecyclerView inputPorts;
    PortItemAdapter inputPortAdapter;
    GridLayoutManager inputPortLayoutMgr;
    boolean inputPortScrolling = false;
    boolean inputPortDragging = false;


    @BindView(R.id.output)
    RecyclerView outputPorts;
    PortItemAdapter outputPortAdapter;
    GridLayoutManager outputPortLayoutMgr;
    boolean outputPortScrolling = false;
    boolean outputPortDragging = false;

    /***********
     *Datum
     ************/
    MediaMatrix mediaMatrix = new MediaMatrix();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, v);

        /* test code */
       List<Port> ports = new ArrayList<>();
        for (int m = 0; m < 32; m ++){
            Port port = new Port();
            port.alias = "测试"+String.valueOf(m);
            port.idx = m;
            port.dir = Port.DIR_IN;
            port.state = m%4;
            ports.add(port);
        }
        final List<Port> outports = new ArrayList<>();
        for (int m = 0; m < 32; m ++){
            Port port = new Port();
            port.alias = "测试"+String.valueOf(m);
            port.idx = m;
            port.dir = Port.DIR_IN;
            port.state = m%4;
            outports.add(port);
        }

        //Input port adapter
        inputPortAdapter = new PortItemAdapter(ports, R.layout.item_port);
        inputPortLayoutMgr = new GridLayoutManager(getActivity().getApplicationContext(), 6, GridLayoutManager.VERTICAL, false);
        inputPorts.setLayoutManager(inputPortLayoutMgr);
        inputPorts.setAdapter(inputPortAdapter);


//        //Output port adapter
        outputPortAdapter = new PortItemAdapter(outports, R.layout.item_port);
        outputPortLayoutMgr = new GridLayoutManager(getActivity().getApplicationContext(), 6, GridLayoutManager.VERTICAL, false);
        outputPorts.setLayoutManager(outputPortLayoutMgr);
        outputPorts.setAdapter(outputPortAdapter);

        inputPortAdapter.setOnItemClickedListener(new PortItemAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(View v, int position) { }

            @Override
            public void onUnselect(View v, int position) {
                Toast.makeText(getActivity().getApplicationContext(), "port unselected = " + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelect(View v, final int position) {
                Toast.makeText(getActivity().getApplicationContext(), "port selected = " + String.valueOf(position), Toast.LENGTH_SHORT).show();

                int firstPosition = outputPortLayoutMgr.findFirstVisibleItemPosition();
                int lastPosition = outputPortLayoutMgr.findLastVisibleItemPosition();
                Log.i("<UI>", "<UI> firstpos = " + firstPosition + " lastpos = " + lastPosition + " pos = " + position);
                if (position < firstPosition || position > lastPosition){
                    outputPorts.smoothScrollToPosition(position);
                    outputPorts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            int firstPosition;
//                            Log.i("<UI>", "<UI> scroll state" + newState);
                            switch(newState){
                                case RecyclerView.SCROLL_STATE_SETTLING:
                                    if (!outputPortDragging && !outputPortScrolling){
//                                    firstPosition = outputPortLayoutMgr.findFirstVisibleItemPosition();
                                        outputPortScrolling = true; //a scrolling occurs
//                                        Log.i("<UI>", "<UI> settling" + outputPortLayoutMgr.findFirstVisibleItemPosition());
                                    }
                                    break;
                                case RecyclerView.SCROLL_STATE_DRAGGING:
                                    outputPortDragging = true;
//                                    firstPosition = outputPortLayoutMgr.findFirstVisibleItemPosition();
//                                    Log.i("<UI>", "<UI> dragging, pos");
                                    break;
                                case RecyclerView.SCROLL_STATE_IDLE:
                                    if (!outputPortDragging && outputPortScrolling){
                                        outputPortDragging = false;
                                        outputPortScrolling = false;
                                        firstPosition = outputPortLayoutMgr.findFirstVisibleItemPosition();
                                        int lastPos = outputPortLayoutMgr.findLastVisibleItemPosition();
                                        //N.B.: firstVisibleItemPosition is not the first child of layoutmanager
                                        outputPortAdapter.changeSelectedItem(position, outputPortLayoutMgr.getChildAt(position-(int) outputPortLayoutMgr.getChildAt(0).getTag()));
                                        Log.i("<UI>", "<UI> change item selection, first pos = " + firstPosition + " pos = " + position + " postag = "+outputPortLayoutMgr.getChildAt(position-firstPosition).getTag());
                                        Log.i("<UI>", "<UI> childCount = " + outputPortLayoutMgr.getChildAt(0).getTag() + " position visible  = " +lastPos + " " + firstPosition);
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
//                            Log.i("<UI>", "<UI> scrolled, dy = " + dy);
                        }
                    });

                }
                else {
                    //a hotfix for layoutmanager.getChildAt
                    outputPortAdapter.changeSelectedItem(position, outputPortLayoutMgr.getChildAt(position-(int) outputPortLayoutMgr.getChildAt(0).getTag()));
                    Log.i("<UI>", "<UI> change item selection, first pos = " + firstPosition + " pos = " + position + " postag = "+outputPortLayoutMgr.getChildAt(position-firstPosition).getTag());
                    Log.i("<UI>", "<UI> childCount = " + outputPortLayoutMgr.getChildAt(0).getTag() + " position visible  = " + lastPosition + " " + firstPosition);

                }
            }

        });

        outputPortAdapter.setOnItemClickedListener(new PortItemAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(View v, int position) { }

            @Override
            public void onUnselect(View v, int position) {
                Toast.makeText(getActivity().getApplicationContext(), "port unselected = " + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelect(View v, int position) {
//                Toast.makeText(getActivity().getApplicationContext(), "port selected = " + String.valueOf(position), Toast.LENGTH_SHORT).show();

                int firstPosition = inputPortLayoutMgr.findFirstVisibleItemPosition();
                int lastPosition = inputPortLayoutMgr.findLastVisibleItemPosition();
                if (position <= firstPosition || position > lastPosition){
                    inputPorts.smoothScrollToPosition(position);
                }

//                firstPosition = inputPortLayoutMgr.findFirstVisibleItemPosition();
//                ((CircleCharView) inputPortLayoutMgr.getChildAt(position - firstPosition).findViewById(R.id.port_circle)).select();
//                inputPortAdapter.changeSelectedItem(position, inputPortLayoutMgr.getChildAt(position-firstPosition));
            }

        });

        return v;
    }
}
