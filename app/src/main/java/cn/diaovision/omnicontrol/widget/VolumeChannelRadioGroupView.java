package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.widget.adapter.VolumeItemAdapter;

/**
 * Created by liulingfeng on 2017/3/9.
 */
@Deprecated
public class VolumeChannelRadioGroupView extends RecyclerView {
    Context ctx;
    List<Channel> channelList;
    int layout;
    LinearLayoutManager layoutMgr;
    VolumeItemAdapter adapter;

    OnItemSelectListener onItemSelectListener;

    boolean configed = false;
    boolean dragging = false;
    boolean scrolling = false;



    public VolumeChannelRadioGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void config(List<Channel> channelList, int layout){
        this.channelList = channelList;
        this.layout = layout;

        layoutMgr = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        adapter = new VolumeItemAdapter(channelList, layout);


        this.setLayoutManager(layoutMgr);
//        Log.i("U", "UI runs here");
        this.setAdapter(adapter);

        adapter.setOnItemClickedListener(new VolumeItemAdapter.OnItemClickListener() {
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

        int firstPosition = ((LinearLayoutManager) this.getLayoutManager()).findFirstVisibleItemPosition();
        int lastPosition = ((LinearLayoutManager) this.getLayoutManager()).findLastVisibleItemPosition();
        if (position < firstPosition || position > lastPosition){
            this.smoothScrollToPosition(position);
            this.addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch(newState){
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            if (!dragging && !scrolling){
                                scrolling = true; //a scrolling occurs
                            }
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            dragging = true;
                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (!dragging && scrolling){
                                dragging = false;
                                scrolling = false;
                                //N.B.: firstVisibleItemPosition is not the first child of layoutmanager
                                adapter.changeSelectedItem(position, layoutMgr.getChildAt(position-(int) layoutMgr.getChildAt(0).getTag()));
                                int firstPosition = layoutMgr.findFirstVisibleItemPosition();
                                int lastPosition = layoutMgr.findLastVisibleItemPosition();

                            }
                            else {
                                //dragging should be unset once the scroll is done
                                dragging = false;
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

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public void configLayout(int direction){
        layoutMgr = new LinearLayoutManager(ctx, direction, false);
        this.setLayoutManager(layoutMgr);
    }


}
