package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.bingoogolapple.badgeview.BGABadgeImageView;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.CircleCharView;

/**
 * Universal port recyclerview adapter
 * Created by liulingfeng on 2017/3/2.
 */

public class PortItemAdapter extends RecyclerView.Adapter<PortItemAdapter.PortItemViewHolder> {

    List<Port> ports;
    int layout;

    int lastSelectedPos = -1;
    View lastSelectedView = null;


    Context ctx;

    private AtomicBoolean isBinding;

    private OnItemClickListener itemClickListener;
    OnItemTouchListener onItemTouchListener;

    private boolean isEditing = false;
    //存放选中item的position
    List<Integer> selects = new ArrayList<>();

    public PortItemAdapter(List<Port> ports, int layout) {
        this.ports = ports;
        this.layout = layout;
        isBinding = new AtomicBoolean(false);
    }

    @Override
    public PortItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        PortItemViewHolder vholder = new PortItemViewHolder(v);
        ctx = parent.getContext();
        return vholder;
    }


    @Override
    public void onBindViewHolder(final PortItemViewHolder holder, final int position) {
        isBinding.set(true);

        holder.getV().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (lastSelectedPos < 0) {
                    //new select
                    Log.i("info","holder.pos"+holder.pos);
                    ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                    lastSelectedPos = holder.pos;
                    lastSelectedView = view;
                    if (itemClickListener != null) {
                        itemClickListener.onSelect(view, (int) view.getTag());
                    }
                } else {
                    if (lastSelectedPos != holder.pos) {
                        //only unselect old selectedView if the view is still on the window
                        if ((int) lastSelectedView.getTag() == lastSelectedPos) {
                            ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect();
                        }
                        lastSelectedPos = holder.pos;
                        lastSelectedView = view;
                        //reselect
                        ((CircleCharView) view.findViewById(R.id.port_circle)).select();

                        if (itemClickListener != null) { //callback
                            itemClickListener.onSelect(view, (int) view.getTag());
                        }
                    } else {
                        //unselect
                        lastSelectedPos = -1;
                        ((CircleCharView) view.findViewById(R.id.port_circle)).unselect();
                        lastSelectedView = null;
                        if (itemClickListener != null) {
                            itemClickListener.onUnselect(view, (int) view.getTag());
                        }
                    }
                }*/

                switch (selects.size()) {
                    case 0:
                        //之前没有选中
                        selects.add(position);
                        ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                        lastSelectedPos = holder.pos;
                        lastSelectedView = view;
                        if (itemClickListener != null) {
                            itemClickListener.onSelect(view, (int) view.getTag());
                        }
                        break;
                    case 1:
                        //之前选中一个
                        if (selects.get(0) != holder.pos) {
                            //选中的和之前不同
                            if (isEditing) {
                                selects.add(position);
                                ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                                lastSelectedPos = holder.pos;
                                lastSelectedView = view;
                            } else {
                                selects.remove(0);
                                selects.add(position);
                                if ((int) lastSelectedView.getTag() == lastSelectedPos) {
                                    ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect();
                                }
                                ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                                if (itemClickListener != null) {
                                    itemClickListener.onSelect(view, (int) view.getTag());
                                }
                                lastSelectedPos = holder.pos;
                                lastSelectedView = view;
                            }
                        } else {
                            //选中的和之前相同
                            selects.remove(0);
                            ((CircleCharView) view.findViewById(R.id.port_circle)).unselect();
                            if (itemClickListener != null) {
                                itemClickListener.onUnselect(view, (int) view.getTag());
                            }
                            lastSelectedPos=-1;
                            lastSelectedView = null;
                        }
                        break;
                    default:
                        //之前选中多个
                        if (isEditing) {
                            if (selects.contains(position)) {
                                selects.remove(selects.indexOf(position));
                                ((CircleCharView) view.findViewById(R.id.port_circle)).unselect();
                            } else {
                                selects.add(position);
                                ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                                lastSelectedPos = holder.pos;
                                lastSelectedView = view;
                            }
                        } else {
                            selects.clear();
                            notifyDataSetChanged();
                            selects.add(position);
                            ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                            lastSelectedPos = holder.pos;
                            lastSelectedView = view;
                        }
                        break;
                }
            }
        });

        holder.getV().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onLongClick(v, position);
                }
                return true;
            }
        });

        holder.getV().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onItemTouchListener != null) {
                    onItemTouchListener.onItemTouchEvent(v, position);
                }
                return false;
            }
        });

        //设置badgeview
        holder.image.showTextBadge(ports.get(position).idx + "");
        holder.image.isShowBadge();

        //bind view here
        if (ports != null) {
            Port p = ports.get(position);
            holder.alias.setText(p.alias);
            holder.cView.changeState(p.state);
            holder.cView.setChar(String.valueOf(p.idx)); //port index (may be different from the view position)
            holder.pos = position;

            //reset the view click state
/*            if (lastSelectedPos == position) {
                Log.i("info","lastSelectedPos="+lastSelectedPos+"holder.pos="+holder.pos);
                holder.cView.select(0);
                lastSelectedView = holder.getV(); //reset selectedView here (old one may be recycled)
            } else {
                holder.cView.unselect(0);
            }*/

            if (selects.contains(position)) {
                holder.cView.select();
                lastSelectedView = holder.getV();
            } else {
                holder.cView.unselect();
            }
            holder.v.setTag(position); //view position
        }
        isBinding.set(false);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return ports.size();
    }


    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        if (ports != null) {
            this.ports = ports;
            notifyDataSetChanged();
        }
    }

    public void changeSelectedItem(int pos, View view) {
        if (lastSelectedPos != pos) {
            ((CircleCharView) view.findViewById(R.id.port_circle)).select();
            if (lastSelectedView != null && (int) lastSelectedView.getTag() == lastSelectedPos) {
                ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect();
            }
            lastSelectedPos = pos;
            lastSelectedView = view;
        }
    }

    public class PortItemViewHolder extends RecyclerView.ViewHolder {
        View v;
        AppCompatTextView alias;
        CircleCharView cView;
        BGABadgeImageView image;
        int pos;

        public PortItemViewHolder(View itemView) {
            super(itemView);
            v = itemView;
/*            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cView.click();
                }
            });*/

            alias = (AppCompatTextView) v.findViewById(R.id.port_alias);
            cView = (CircleCharView) v.findViewById(R.id.port_circle);
            image = (BGABadgeImageView) v.findViewById(R.id.port_image);
            //cView.unselect(0);
        }

        public View getV() {
            return v;
        }

        public CircleCharView getCView() {
            return cView;
        }
    }

    public interface OnItemClickListener {
        void onLongClick(View v, int position);

        void onSelect(View v, int position);

        void onUnselect(View v, int position);
    }

    public interface OnItemTouchListener {
        void onItemTouchEvent(View v, int position);
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        if (selects != null && editing == true) {
            selects.clear();
        }
        isEditing = editing;
    }

    public List<Integer> getSelects() {
        return selects;
    }

    public void setSelects(List<Integer> selects) {
        this.selects = selects;
    }
}
