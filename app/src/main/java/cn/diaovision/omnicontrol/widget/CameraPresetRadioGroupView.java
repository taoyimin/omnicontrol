package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.widget.adapter.CameraPresetItemAdapter;

/**
 * Created by liulingfeng on 2017/3/9.
 */

public class CameraPresetRadioGroupView extends RecyclerView {
    Context ctx;
    List<HiCamera.Preset> presetList;
    int layout;
    GridLayoutManager layoutMgr;
    CameraPresetItemAdapter adapter;

    OnItemSelectListener onItemSelectListener;

    boolean configed = false;
    boolean dragging = false;
    boolean scrolling = false;



    public CameraPresetRadioGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void config(List<HiCamera.Preset> presetList, int layout){
        this.presetList = presetList;
        this.layout = layout;

        layoutMgr = new GridLayoutManager(ctx, 3, GridLayoutManager.VERTICAL, false);
        adapter = new CameraPresetItemAdapter(presetList, layout);


        this.setLayoutManager(layoutMgr);
        this.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CameraPresetItemAdapter.OnItemClickListener() {
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

    public List<HiCamera.Preset> getPresetList() {
        return presetList;
    }

    public void setPresetList(List<HiCamera.Preset> portList) {
        this.presetList = portList;
        adapter = new CameraPresetItemAdapter(presetList, layout);
        adapter.notifyDataSetChanged();
    }

    public void configLayout(int direction, int spancount){
        layoutMgr = new GridLayoutManager(ctx, spancount, direction, false);
        this.setLayoutManager(layoutMgr);
    }


}
