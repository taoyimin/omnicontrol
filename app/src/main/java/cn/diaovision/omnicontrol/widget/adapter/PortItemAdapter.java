package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
                    lastSelectedPos = holder.pos;
                    ((CircleCharView) view.findViewById(R.id.port_circle)).select();
                    lastSelectedView = view;
                    if (itemClickListener != null) {
                        itemClickListener.onSelect(view, (int) view.getTag());
                    }
                }
                else {
                    if (lastSelectedPos != holder.pos) {

                        //only unselect old selectedView if the view is still on the window
                        if((int) lastSelectedView.getTag() == lastSelectedPos) {
                            ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect();
                        }
                        lastSelectedPos = holder.pos;
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
                lastSelectedView = holder.getV(); //reset selectedView here (old one may be recycled)
            }
            else {
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


    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        if(ports != null) {
            this.ports = ports;
            notifyDataSetChanged();
        }
    }

    public void changeSelectedItem(int pos, View view){
        if (lastSelectedPos != pos){
            ((CircleCharView) view.findViewById(R.id.port_circle)).select();
            if (lastSelectedView !=null && (int) lastSelectedView.getTag() == lastSelectedPos) {
                ((CircleCharView) lastSelectedView.findViewById(R.id.port_circle)).unselect();
            }
            lastSelectedPos = pos;
            lastSelectedView = view;
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
            cView.unselect(0);
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