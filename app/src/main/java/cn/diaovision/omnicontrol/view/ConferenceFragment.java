package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.MyItemTouchCallback;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.RecyclerViewWithSlidingItem;
import cn.diaovision.omnicontrol.widget.adapter.AuxiliaryPanelItemAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class ConferenceFragment extends BaseFragment implements ConferenceContract.View {
    @BindView(R.id.port)
    PortRadioGroupView portRadioGroupView;
    @BindView(R.id.auxiliary_recycler)
    RecyclerViewWithSlidingItem auxiliaryRecycler;

    AuxiliaryPanelItemAdapter adapter;
    List<Term> list;

    ConferencePresenter presenter;
    private ItemTouchHelper itemTouchHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meeting, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*test code*/
        final List<Port> ports = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Port port = new Port(i, i, i, i);
            ports.add(port);
        }
        //RecyclerView config
        portRadioGroupView.config(ports, R.layout.item_port);
        portRadioGroupView.configLayout(RecyclerView.VERTICAL, 9);
        portRadioGroupView.updateData();

        /*test code*/
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Term term = new Term(i);
            term.setName("position=" + i);
            list.add(term);
        }
        adapter = new AuxiliaryPanelItemAdapter(getContext(), list);
        auxiliaryRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1));
        auxiliaryRecycler.setHasFixedSize(true);
        auxiliaryRecycler.setAdapter(adapter);
        auxiliaryRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(auxiliaryRecycler){
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition()!=list.size()-1) {
                    Toast.makeText(getContext(),"长按了",Toast.LENGTH_SHORT).show();
                    itemTouchHelper.startDrag(vh);
                    //VibratorUtil.Vibrate(getActivity(), 70);   //震动70ms
                }
            }
        });
        itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(adapter));
        //和RecyclerView进行关联
        itemTouchHelper.attachToRecyclerView(auxiliaryRecycler);
    }

    @Override
    public void bindPresenter() {
        presenter = new ConferencePresenter(this);
    }
}
