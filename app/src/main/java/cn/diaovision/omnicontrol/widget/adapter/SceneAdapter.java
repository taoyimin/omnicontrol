package cn.diaovision.omnicontrol.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.splicer.Scene;

/**
 * Created by TaoYimin on 2017/7/27.
 */

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHold>{
    List<Scene> list;
    OnItemClickListener onItemClickListener;

    public SceneAdapter(List<Scene> list) {
        this.list = list;
    }

    @Override
    public SceneViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene,parent,false);
        return new SceneViewHold(itemView);
    }

    @Override
    public void onBindViewHolder(SceneViewHold holder, final int position) {
        Scene scene=list.get(position);
        holder.textId.setText(position+1+"");
        holder.textName.setText(scene.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SceneViewHold extends RecyclerView.ViewHolder{
        @BindView(R.id.id)
        public TextView textId;
        @BindView(R.id.name)
        TextView textName;

        public SceneViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
