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
import cn.diaovision.omnicontrol.core.model.device.common.DeviceLog;

/**
 * Created by TaoYimin on 2017/9/19.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder>{
    List<DeviceLog> list;

    public LogAdapter(List<DeviceLog> list) {
        this.list = list;
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false));
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        holder.log.setText(list.get(position).getLog());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class LogViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.log)
        TextView log;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
