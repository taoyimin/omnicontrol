package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.adapter.PortItemAdapter;

/**
 * Created by liulingfeng on 2017/3/9.
 */

public class PortRadioGroupView extends RecyclerView {
    Context ctx;
    List<Port> portList;
    int layout;
    GridLayoutManager layoutMgr;
    PortItemAdapter adapter;

    OnItemSelectListener onItemSelectListener;

    boolean configed = false;
    boolean dragging = false;
    boolean scrolling = false;



    public PortRadioGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void config(List<Port> portList, int layout){
        this.portList = portList;
        this.layout = layout;

        layoutMgr = new GridLayoutManager(ctx, 6, GridLayoutManager.VERTICAL, false);
        adapter = new PortItemAdapter(portList, layout);


        this.setLayoutManager(layoutMgr);
        this.setAdapter(adapter);

        adapter.setOnItemClickedListener(new PortItemAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(View v, int position) {

            }

            @Override
            public void onSelect(View v, int position) {
                if (onItemSelectListener != null){
                    onItemSelectListener.onSelected(position);
                }
            }

            @Override
            public void onUnselect(View v, int position) {
                if (onItemSelectListener != null){
                    onItemSelectListener.onUnselected(position);
                }

            }
        });

        configed = true;

    }

    public void select(final int position){
        if (!configed)
            return;

        int firstPosition = ((GridLayoutManager) this.getLayoutManager()).findFirstVisibleItemPosition();
        int lastPosition = ((GridLayoutManager) this.getLayoutManager()).findLastVisibleItemPosition();
        if (position < firstPosition || position > lastPosition){
            this.smoothScrollToPosition(position);
            this.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch(newState){
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            if (!dragging && !scrolling){
                                scrolling = true; //a scrolling occurs
                            }
                            Log.i("<UI>", "<UI> scrolling: " + dragging + " " + scrolling );
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            dragging = true;
                            Log.i("<UI>", "<UI> dragging: " + dragging + " " + scrolling );
                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (!dragging && scrolling){
                                dragging = false;
                                scrolling = false;
                                //N.B.: firstVisibleItemPosition is not the first child of layoutmanager
                                adapter.changeSelectedItem(position, layoutMgr.getChildAt(position-(int) layoutMgr.getChildAt(0).getTag()));
                                int firstPosition = layoutMgr.findFirstVisibleItemPosition();
                                int lastPosition = layoutMgr.findLastVisibleItemPosition();

                                Log.i("<UI>", "<UI> cild0tag = " + layoutMgr.getChildAt(0).getTag() +  " postag = " + layoutMgr.getChildAt(position-firstPosition).getTag() + " last first pos = " +lastPosition + " " + firstPosition + " " + position);
                            }
                            else {
                                dragging = false;
                                Log.i("<UI>", "<UI> dragging + scrolling" + dragging + " " + scrolling);
                            }
                            break;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });

        }
        else {
            //a hotfix for layoutmanager.getChildAt
            adapter.changeSelectedItem(position, layoutMgr.getChildAt(position-(int) layoutMgr.getChildAt(0).getTag()));
            Log.i("<UI>", "<UI> cild0tag = " + layoutMgr.getChildAt(0).getTag() +  " postag = " + layoutMgr.getChildAt(position-firstPosition).getTag() + " last first pos = " +lastPosition + " " + firstPosition + " " + position);
        }
    }

    public interface OnItemSelectListener {
        void onSelected(int pos);
        void onUnselected(int pos);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    /*when data changed*/
    public void updateData(){
        if (!configed)
            return;

        this.getAdapter().notifyDataSetChanged();
    }

    public List<Port> getPortList() {
        return portList;
    }

    public void setPortList(List<Port> portList) {
        this.portList = portList;
    }

    public void configLayout(int direction, int spancount){
        layoutMgr = new GridLayoutManager(ctx, spancount, direction, false);
        this.setLayoutManager(layoutMgr);
    }


}
