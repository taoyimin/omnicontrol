package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
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

    Context ctx;
    private AtomicBoolean isBinding;

    private OnItemClickListener itemClickListener;

    public PortItemAdapter(List<Port> ports, int layout){
        this.ports = ports;
        this.layout = layout;
        isBinding = new AtomicBoolean(false);
    }

    public PortItemAdapter(int layout){
        this.ports = new ArrayList<>();
        this.layout = layout;
        isBinding = new AtomicBoolean(false);
    }

    @Override
    public PortItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener!=null){
                    itemClickListener.onClick(view, (int) view.getTag());
                }
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (itemClickListener!=null){
                    itemClickListener.onLongClick(view, (int) view.getTag());
                }
                return false;
            }
        });
        PortItemViewHolder vholder = new PortItemViewHolder(v);
        ctx = parent.getContext();
        return vholder;
    }

    @Override
    public void onBindViewHolder(PortItemViewHolder holder, int position) {
        isBinding.set(true);
        if (ports != null){
            Port p = ports.get(position);
            holder.alias.setText(p.alias);
            holder.cView.changeState(p.state);
//            holder.cView.setText(p.idx); //port index (may be different from the view position)

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

    public class PortItemViewHolder extends RecyclerView.ViewHolder{
        View v;
        int position;
        AppCompatTextView alias;
        CircleCharView cView;

        public PortItemViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            alias = (AppCompatTextView) v.findViewById(R.id.port_alias);
            cView = (CircleCharView) v.findViewById(R.id.port_circle);
        }

        public View getV(){
            return v;
        }
    }

    public interface OnItemClickListener{
        void onClick(View v, int position);
        void onLongClick(View v, int position);
    }
}
