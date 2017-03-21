package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera.Preset;
import cn.diaovision.omnicontrol.widget.CircleCharView;

/**
 * Preset recyclerview adapter
 * Created by liulingfeng on 2017/3/2.
 */

public class CameraPresetItemAdapter extends RecyclerView.Adapter<CameraPresetItemAdapter.CameraPresetItemViewHolder>{

    List<Preset> presets;
    int layout;

    int lastSelectedPos = -1;
    View lastSelectedView = null;


    Context ctx;

    private AtomicBoolean isBinding;

    private OnItemClickListener itemClickListener;

    public CameraPresetItemAdapter(List<Preset> presets, int layout){
        this.presets = presets;
        this.layout = layout;
        isBinding = new AtomicBoolean(false);
    }

    @Override
    public CameraPresetItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        CameraPresetItemViewHolder vholder = new CameraPresetItemViewHolder(v);
        ctx = parent.getContext();
        return vholder;
    }


    @Override
    public void onBindViewHolder(final CameraPresetItemViewHolder holder, final int position) {
        isBinding.set(true);

        holder.getV().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastSelectedPos < 0){
                    //new select
                    lastSelectedPos = holder.pos;
                    view.setSelected(true);

                    lastSelectedView = view;
                    if (itemClickListener != null) {
                        itemClickListener.onSelect(view, (int) view.getTag());
                    }
                }
                else {
                    if (lastSelectedPos != holder.pos) {

                        //only unselect old selectedView if the view is still on the window
                        if((int) lastSelectedView.getTag() == lastSelectedPos) {
                            lastSelectedView.setSelected(false);
                        }
                        lastSelectedPos = holder.pos;
                        lastSelectedView = view;
                        //reselect
                        view.setSelected(true);

                        if (itemClickListener != null) { //callback
                            itemClickListener.onSelect(view, (int) view.getTag());
                        }
                    }
                    else {
                        //unselect
                        lastSelectedPos = -1;
                        view.setSelected(false);
                        lastSelectedView = null;
                        if (itemClickListener != null) {
                            itemClickListener.onUnselect(view, (int) view.getTag());
                        }
                    }
                }

            }
        });

        //bind view here
        if (presets != null){
            Preset p = presets.get(position);
            holder.info.setText(p.getName());
            int iconRes;
            switch (p.getAngle()){
                case 90:
                    iconRes = R.drawable.ic_campos_00;
                    break;
                default:
                    iconRes = R.drawable.ic_campos_00;
                    break;
            }
            holder.icon.setImageDrawable(ctx.getDrawable(iconRes));
            holder.pos = position;

            //reset the view click state
            if (lastSelectedPos == position){
                lastSelectedView = holder.getV(); //reset selectedView here (old one may be recycled)
                holder.getV().setSelected(true);
            }
            else {
                holder.getV().setSelected(false);
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
        return presets.size();
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        if(presets != null) {
            this.presets = presets;
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

    public class CameraPresetItemViewHolder extends RecyclerView.ViewHolder{
        View v;
        AppCompatImageView icon;
        AppCompatTextView info;
        int pos;

        public CameraPresetItemViewHolder(View itemView) {
            super(itemView);
            v = itemView;

            icon = (AppCompatImageView) v.findViewById(R.id.icon);
            info = (AppCompatTextView) v.findViewById(R.id.info);
        }

        public View getV(){
            return v;
        }
    }

    public interface OnItemClickListener{
        void onLongClick(View v, int position);
        void onSelect(View v, int position);
        void onUnselect(View v, int position);
    }
}
