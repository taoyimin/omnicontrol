package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.widget.SlidingItemView;

/**
 * Created by TaoYimin on 2017/4/24.
 * 辅助屏列表适配器(可拖拽侧滑)
 */

public class AuxiliaryPanelItemAdapter extends RecyclerView.Adapter<AuxiliaryPanelItemAdapter.MyViewHolder>{
    Context context;
    List<Term> list;

    public AuxiliaryPanelItemAdapter(Context context, List<Term> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sliding,
                parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.title.setText(list.get(position).getName());
        holder.slidingItemView.setOnHideViewClickListener(new SlidingItemView.OnHideViewClickListener() {
            @Override
            public void onClick1(View view, int pos) {
                Toast.makeText(context,"position="+pos+"操作1",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick2(View view, int pos) {
                Toast.makeText(context,"position="+pos+"操作2",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick3(View view, int pos) {
                Toast.makeText(context,"position="+pos+"操作3",Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=holder.getLayoutPosition();
                Toast.makeText(context,"name="+list.get(pos).getName()+"position="+pos,Toast.LENGTH_SHORT).show();
            }
        });
        holder.slidingItemView.bindViewAndData(holder.itemView, list, holder.getLayoutPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        SlidingItemView slidingItemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            slidingItemView= (SlidingItemView) itemView.findViewById(R.id.item_view);
        }

        public SlidingItemView getSlidingItemView(){
            return slidingItemView;
        }
    }

    public List<Term> getList() {
        return list;
    }
}
