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
 * Created by TaoYimin on 2017/5/4.
 */
public class RecyclerSlidingItemAdapter extends RecyclerView.Adapter<RecyclerSlidingItemAdapter.BaseViewHolder> {
    private OnItemClickListener onItemClickListener;

    private Context context;

    private List<Term> list;

    public RecyclerSlidingItemAdapter(Context context, List<Term> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context)
                .inflate(R.layout.item_sliding, parent, false);
        return new BaseViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder,
                                 final int position) {
        Term info = list.get(position);
        holder.title.setText(info.getName());
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
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            }
        });
        holder.slidingItemView.bindViewAndData(holder.itemView,
                SlidingItemView.HideViewMode.MODE_HIDE_BOTTOM, list,
                holder.getLayoutPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        SlidingItemView slidingItemView;

        BaseViewHolder(View convertView) {
            super(convertView);
            title = (TextView) convertView.findViewById(R.id.title);
            image= (ImageView) convertView.findViewById(R.id.image);
            slidingItemView = (SlidingItemView) convertView
                    .findViewById(R.id.item_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<Term> getItemList(){
        return list;
    }
}
