package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TermItemAdapter extends RecyclerView.Adapter<TermItemAdapter.MyViewHolder> {
    Context context;
    List<Term> list;
    public static final int TYPE_NORMAL = 0;  //说明是不带有footer的
    public static final int TYPE_FOOTER = 1;  //说明是带有footer的
    private View mFooterView;
    OnHideViewClickListener onHideViewClickListener;

    public TermItemAdapter(Context context, List<Term> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_NORMAL:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_term,
                        parent, false);
                break;
            case TYPE_FOOTER:
                itemView = mFooterView;
                break;
        }
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                Term term=list.get(position);
                holder.title.setText(term.getName());
                String isSpeaking=term.isSpeaking()?"是":"否";
                String isMuted=term.isMuted()?"是":"否";
                holder.speech.setText("黑屏："+isSpeaking);
                holder.mute.setText("消音："+isMuted);
                holder.slidingItemView.setOnHideViewClickListener(new SlidingItemView.OnHideViewClickListener() {
                    @Override
                    public void onClick1(View view, int pos) {
                        if(onHideViewClickListener!=null){
                            onHideViewClickListener.onClick1(view,pos);
                        }
                    }

                    @Override
                    public void onClick2(View view, int pos) {
                        if(onHideViewClickListener!=null){
                            onHideViewClickListener.onClick2(view,pos);
                        }
                    }

                    @Override
                    public void onClick3(View view, int pos) {
                        if(onHideViewClickListener!=null){
                            onHideViewClickListener.onClick3(view,pos);
                        }
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        Toast.makeText(context, "name=" + list.get(pos).getName() + "position=" + pos, Toast.LENGTH_SHORT).show();
                    }
                });
                holder.slidingItemView.bindViewAndData(holder.itemView, list,SlidingItemView.HideViewMode.MODE_HIDE_BOTTOM,holder.getLayoutPosition());
                break;
            case TYPE_FOOTER:
                //holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mFooterView == null) {
            return list.size();
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null) {
            return TYPE_NORMAL;
        }
        if (position == getItemCount() - 1) {
            //最后一个,应该加载Footer
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView mute;
        TextView speech;
        SlidingItemView slidingItemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            if (itemView == mFooterView){
                return;
            }
            title = (TextView) itemView.findViewById(R.id.title);
            mute= (TextView) itemView.findViewById(R.id.mute);
            speech= (TextView) itemView.findViewById(R.id.speech);
            slidingItemView = (SlidingItemView) itemView.findViewById(R.id.item_view);
        }

        public SlidingItemView getSlidingItemView() {
            return slidingItemView;
        }
    }

    public List<Term> getList() {
        return list;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    public void setOnHideViewClickListener(OnHideViewClickListener onHideViewClickListener) {
        this.onHideViewClickListener = onHideViewClickListener;
    }

    public interface OnHideViewClickListener{
        void onClick1(View view, int pos);
        void onClick2(View view, int pos);
        void onClick3(View view, int pos);
    }
}
