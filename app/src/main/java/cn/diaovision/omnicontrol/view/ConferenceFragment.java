package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.OnItemMoveListener;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.adapter.RecyclerSlidingItemAdapter;
import cn.diaovision.omnicontrol.widget.RecyclerViewWithSlidingItem;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class ConferenceFragment extends BaseFragment implements ConferenceContract.View{
    @BindView(R.id.port)
    PortRadioGroupView portRadioGroupView;
    @BindView(R.id.auxiliary_recycler)
    RecyclerViewWithSlidingItem auxiliaryRecycler;

    RecyclerSlidingItemAdapter adapter;
    List<Term> list;

    ConferencePresenter presenter;

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
        portRadioGroupView.configLayout(RecyclerView.VERTICAL,9);
        portRadioGroupView.updateData();

        auxiliaryRecycler.setLongPressDragEnabled(true);
        auxiliaryRecycler.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                // 当Item被拖拽的时候。
                Log.i("info", "fromPosition=" + fromPosition + ";toPosition=" + toPosition);
                swipePosition(fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;// 返回true表示处理了，返回false表示没有处理。
            }

            @Override
            public void onItemDismiss(int position) {

            }
        });
        initListView();
    }

    private void swipePosition(int fromPosition, int toPosition) {
        if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        } else {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        }
    }

    @Override
    public void bindPresenter() {
        presenter = new ConferencePresenter(this);
    }

    private void initListView() {
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Term term = new Term(i);
            term.setName("position=" + i);
            list.add(term);
        }
        adapter = new RecyclerSlidingItemAdapter(getContext(), list);
        adapter.setOnItemClickListener(
                new RecyclerSlidingItemAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getContext(),"position="+position + "title="+list.get(position).getName(),Toast.LENGTH_SHORT).show();
                    }
                });
        auxiliaryRecycler.setLayoutManager(new GridLayoutManager(getContext(),1));
        auxiliaryRecycler.setHasFixedSize(true);
        auxiliaryRecycler.setAdapter(adapter);
    }
}
