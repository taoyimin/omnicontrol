package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.CircleCharView;

/**
 * Universal port recyclerview adapter
 * Created by liulingfeng on 2017/3/2.
 */

public class PortItemAdapter extends RecyclerView.Adapter<PortItemAdapter.PortItemViewHolder>{

    List<Port> ports;
    int layout;

    int lastSelectedPos = -1;
    View lastSelectedView = null;


    Context ctx;

    private AtomicBoolean isBinding;

    private OnItemClickListener itemClickListener;

    public PortItemAdapter(List<Port> ports, int layout){
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
                if (lastSelectedPos < 0){
                    //new select
                    lastSelectedPos = position;
                    ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                    lastSelectedView = view;
                    if (itemClickListener != null) {
                        itemClickListener.onSelect(view, (int) view.getTag());
                    }
                }
                else {
                    if (lastSelectedPos != position) {
                        lastSelectedPos = position;
                        ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect();
                        lastSelectedView = view;
                        //reselect
                        ((CircleCharView) view.findViewById(R.id.port_circle)).select();

                        if (itemClickListener != null) { //callback
                            itemClickListener.onSelect(view, (int) view.getTag());
                        }
                    }
                    else {
                        //unselect
                        lastSelectedPos = -1;
                        ((CircleCharView) view.findViewById(R.id.port_circle)).unselect();
                        lastSelectedView = null;
                        if (itemClickListener != null) {
                            itemClickListener.onUnselect(view, (int) view.getTag());
                        }
                    }
                }

            }
        });

        holder.getV().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (itemClickListener!=null){
                    itemClickListener.onLongClick(view, (int) view.getTag());
                }
                return false;
            }
        });

        //bind view here
        if (ports != null){
            Port p = ports.get(position);
            holder.alias.setText(p.alias);
            holder.cView.changeState(p.state);
            holder.cView.setChar(String.valueOf(p.idx)); //port index (may be different from the view position)
            holder.pos = position;

            //reset the view click state
            if (lastSelectedPos == position){
                holder.cView.select(0);
                Log.i("<UI>","<UI> bind position at " + holder.pos + " :selected");
            }
            else {
                Log.i("<UI>","<UI> bind position at " + holder.pos + " :unselected");
                holder.cView.unselect(0);
            }
            holder.v.setTag(position); //view position
        }

        isBinding.set(false);
    }

    public void setOnItemClickedListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return ports.size();
    }

    @Override
    public void onViewDetachedFromWindow(PortItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }


    @Override
    public void onViewRecycled(PortItemViewHolder holder) {
        super.onViewRecycled(holder);
        if (lastSelectedView != null && lastSelectedPos == holder.pos){
            ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect(0);
            ((CircleCharView) holder.v.findViewById(R.id.port_circle)).unselect(0);
            Log.i("<UI>", "<UI> selectedview recycled at " + holder.pos);
        }
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        if(ports != null) {
            this.ports = ports;
            notifyDataSetChanged();
        }
    }

    public class PortItemViewHolder extends RecyclerView.ViewHolder{
        View v;
        AppCompatTextView alias;
        CircleCharView cView;
        int pos;

        public PortItemViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cView.click();
                }
            });

            alias = (AppCompatTextView) v.findViewById(R.id.port_alias);
            cView = (CircleCharView) v.findViewById(R.id.port_circle);
        }

        public View getV(){
            return v;
        }

        public CircleCharView getCView() {
            return cView;
        }
    }

    public interface OnItemClickListener{
        void onLongClick(View v, int position);
        void onSelect(View v, int position);
        void onUnselect(View v, int position);
    }
}
