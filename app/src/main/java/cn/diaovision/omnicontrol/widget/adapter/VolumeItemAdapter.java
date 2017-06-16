package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;

/**
 * Universal port recyclerview adapter
 * Created by liulingfeng on 2017/3/2.
 */
@Deprecated
public class VolumeItemAdapter extends RecyclerView.Adapter<VolumeItemAdapter.VolumeItemHolder>{

    List<Channel> channels;
    int layout;

    int lastSelectedPos = -1;
    View lastSelectedView = null;


    Context ctx;

    private AtomicBoolean isBinding;

    private OnItemClickListener itemClickListener;

    public VolumeItemAdapter(List<Channel> channels, int layout){
        this.channels = channels;
        this.layout = layout;
        isBinding = new AtomicBoolean(false);
    }

    @Override
    public VolumeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        VolumeItemHolder vholder = new VolumeItemHolder(v);
        ctx = parent.getContext();

        return vholder;
    }

    @Override
    public void onBindViewHolder(final VolumeItemHolder holder, final int position) {
        Log.i("U", "UI bind audio pos = " + position);
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
        if (channels != null){
            final Channel chn = channels.get(position);
            holder.val = (Integer) chn.getProperty("volume");
            holder.pos = position;
            holder.valTxt.setText(String.valueOf(holder.val));
            holder.aliasTxt.setText(chn.getAlias());
            holder.bar.setProgress(holder.val);

            Log.i("U", "UI bind chn " + position + " val = " + holder.val);

            holder.bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    holder.valTxt.setText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    chn.setProperty("volume", progress);
                    holder.valTxt.setText(String.valueOf(progress));
                }
            });

            //reset the view click state

            if (lastSelectedPos == position){
                holder.getV().setSelected(true);
                lastSelectedView = holder.getV(); //reset selectedView here (old one may be recycled)
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
        return channels == null ? 0 : channels.size();
    }


    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        if(channels != null) {
            this.channels = channels;
        }
        notifyDataSetChanged();
    }

    public void changeSelectedItem(int pos, View view){

        if (lastSelectedPos != pos){
            if (lastSelectedView !=null && (int) lastSelectedView.getTag() == lastSelectedPos) {
                lastSelectedView.setSelected(false);
            }
            view.setSelected(true);
            lastSelectedPos = pos;
            lastSelectedView = view;
        }
    }

    public class VolumeItemHolder extends RecyclerView.ViewHolder{
        View v;

        int val;
        int pos;

//        @BindView(R.id.alias)
        AppCompatTextView aliasTxt;

//        @BindView(R.id.val)
        AppCompatTextView valTxt;

//        @BindView(R.id.seekbar)
        VerticalSeekBar bar;

        public VolumeItemHolder(View itemView) {
            super(itemView);
            v = itemView;
//            ButterKnife.bind(this, itemView);
            aliasTxt = (AppCompatTextView) v.findViewById(R.id.alias);
            valTxt = (AppCompatTextView)  v.findViewById(R.id.val);
            bar = (VerticalSeekBar)  v.findViewById(R.id.seekbar);

        }

        public View getV(){
            return v;
        }

    }

    public interface OnItemClickListener{
        void onSelect(View v, int position);
        void onUnselect(View v, int position);
    }

    public interface OnItemVolumeChangeListener{
        void onVolumeChanged(int val, int position);
    }
}
