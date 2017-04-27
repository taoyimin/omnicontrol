package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

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
    public PortItemAdapter adapter;

    OnItemSelectListener onItemSelectListener;
    OnItemLongClickListener onItemLongClickListener;
    DispatchTouchEventListener dispatchTouchEventListener;

    boolean configed = false;
    boolean dragging = false;
    boolean scrolling = false;


    public PortRadioGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public void config(final List<Port> portList, int layout) {
        this.portList = portList;
        this.layout = layout;

        layoutMgr = new GridLayoutManager(ctx, 6, GridLayoutManager.VERTICAL, false);
        adapter = new PortItemAdapter(portList, layout);


        this.setLayoutManager(layoutMgr);
        this.setAdapter(adapter);

        adapter.setOnItemClickedListener(new PortItemAdapter.OnItemClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                //弹出对话框
                //popupDialog(portList.get(position));
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onLongClick(v, position);
                }
            }

            @Override
            public void onSelect(View v, int position) {
                if (onItemSelectListener != null) {
                    onItemSelectListener.onSelected(position);
                }
            }

            @Override
            public void onUnselect(View v, int position) {
                if (onItemSelectListener != null) {
                    onItemSelectListener.onUnselected(position);
                }

            }
        });

        configed = true;

    }

    public void select(final int position) {
        if (!configed)
            return;

        int firstPosition = ((GridLayoutManager) this.getLayoutManager()).findFirstVisibleItemPosition();
        int lastPosition = ((GridLayoutManager) this.getLayoutManager()).findLastVisibleItemPosition();
        if (position < firstPosition || position > lastPosition) {
            this.smoothScrollToPosition(position);
            this.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            if (!dragging && !scrolling) {
                                scrolling = true; //a scrolling occurs
                            }
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            dragging = true;
                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (!dragging && scrolling) {
                                dragging = false;
                                scrolling = false;
                                //N.B.: firstVisibleItemPosition is not the first child of layoutmanager
                                adapter.changeSelectedItem(position, layoutMgr.getChildAt(position - (int) layoutMgr.getChildAt(0).getTag()));
                                int firstPosition = layoutMgr.findFirstVisibleItemPosition();
                                int lastPosition = layoutMgr.findLastVisibleItemPosition();

                            } else {
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

        } else {
            //a hotfix for layoutmanager.getChildAt
            adapter.changeSelectedItem(position, layoutMgr.getChildAt(position - (int) layoutMgr.getChildAt(0).getTag()));
        }
    }

    public interface OnItemSelectListener {
        void onSelected(int pos);

        void onUnselected(int pos);
    }

    public interface OnItemLongClickListener {
        void onLongClick(View v, int position);
    }

    public interface DispatchTouchEventListener {
        void dispatchTouchEvent(View v, MotionEvent e, int position);
    }

    public void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener) {
        this.dispatchTouchEventListener = dispatchTouchEventListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    /*when data changed*/
    public void updateData() {
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

    public void configLayout(int direction, int spancount) {
        layoutMgr = new GridLayoutManager(ctx, spancount, direction, false);
        this.setLayoutManager(layoutMgr);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (dispatchTouchEventListener != null) {
            adapter.setOnItemTouchListener(new PortItemAdapter.OnItemTouchListener() {
                @Override
                public void onItemTouchEvent(View v, int position) {
                    dispatchTouchEventListener.dispatchTouchEvent(v, ev, position);
                }
            });
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 弹出对话框
     * @param port
     */
    public void popupDialog(final Port port){
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_port, null);
        TextView textView= (TextView) view.findViewById(R.id.dialog_text);
        textView.setText("这是"+port.idx+"号端口");
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

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
}
