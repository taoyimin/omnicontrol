package cn.diaovision.omnicontrol.view;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.model.ConfigFixed;
import cn.diaovision.omnicontrol.widget.MyItemTouchCallback;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.PortRadioGroupView;
import cn.diaovision.omnicontrol.widget.RecyclerViewWithSlidingItem;
import cn.diaovision.omnicontrol.widget.SlidingItemView;
import cn.diaovision.omnicontrol.widget.adapter.AuxiliaryPanelItemAdapter;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class ConferenceFragment extends BaseFragment implements ConferenceContract.View {
    @BindView(R.id.port)
    PortRadioGroupView portRadioGroupView;
    @BindView(R.id.auxiliary_recycler)
    RecyclerViewWithSlidingItem auxiliaryRecycler;
    @BindView(R.id.video_layout)
    RelativeLayout videoLayout;

    AuxiliaryPanelItemAdapter adapter;
    List<Term> list;
    Term currentTerm;
    int dragPosition = -1;
    Rect rect = new Rect();

    ConferencePresenter presenter;
    private ItemTouchHelper itemTouchHelper;
    Config cfg = new ConfigFixed();

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
        currentTerm = new Term(666);
        currentTerm.setName("position=666");
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Term term = new Term(i);
            term.setName("position=" + i);
            list.add(term);
        }
        adapter = new AuxiliaryPanelItemAdapter(getContext(), list);
        View footerview = LayoutInflater.from(getContext()).inflate(R.layout.footer_auxiliary, null, false);
        adapter.setFooterView(footerview);
        adapter.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog();
            }
        });
        adapter.setOnHideViewClickListener(new AuxiliaryPanelItemAdapter.OnHideViewClickListener() {
            @Override
            public void onClick1(View view, int pos) {
                //发言与取消发言
            }

            @Override
            public void onClick2(View view, int pos) {
                //静音与取消静音
            }

            @Override
            public void onClick3(View view, int pos) {
                //踢除终端
            }
        });
        auxiliaryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        auxiliaryRecycler.setHasFixedSize(true);
        auxiliaryRecycler.setAdapter(adapter);
        auxiliaryRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(auxiliaryRecycler) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                //最后一个是加添按钮,不能拖拽
                if (vh.getLayoutPosition() != adapter.getItemCount() - 1) {
                    SlidingItemView slidingItemView = ((AuxiliaryPanelItemAdapter.MyViewHolder) vh).getSlidingItemView();
                    if (!slidingItemView.isCanDrag()) {
                        slidingItemView.setCanDrag(true);
                        return;
                    }
                    itemTouchHelper.startDrag(vh);
                    dragPosition = vh.getLayoutPosition();
                }
            }
        });
        itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(adapter).setOnDragListener(new MyItemTouchCallback.OnDragListener() {
            @Override
            public void onFinishDrag() {
                //拖拽完成的回掉
                dragPosition = -1;
            }
        }));
        //和RecyclerView进行关联
        itemTouchHelper.attachToRecyclerView(auxiliaryRecycler);
    }

    @Override
    public void bindPresenter() {
        presenter = new ConferencePresenter(this);
    }

    public void getActivityDispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (dragPosition < 0 || videoLayout == null)
                return;
            videoLayout.getGlobalVisibleRect(rect);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean flag = rect.contains((int) event.getRawX(), (int) event.getRawY());
                if (flag) {
                    Term temp = currentTerm;
                    currentTerm = list.get(dragPosition);
                    list.set(dragPosition, temp);
                    auxiliaryRecycler.getAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick({R.id.start_conf, R.id.end_conf})
    void onButtonClick(Button btn) {
        switch (btn.getId()) {
            case R.id.start_conf:
                //开始会议
                //参数confId需要修改
                presenter.startConf(cfg.getConfStartDate(), cfg.getConfEndDate(), 0);
                break;
            case R.id.end_conf:
                //结束会议
                //参数confId需要修改
                presenter.endConf(0);
                break;
            default:
                break;
        }
    }

    public void popupDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_invite_term, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setTitle("邀请终端");
        builder.setPositiveButton("邀请", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!editText.getText().toString().isEmpty()){
                    int termId=Integer.parseInt(editText.getText().toString());
                    //参数confId需要修改
                    presenter.inviteTerm(0,termId);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
