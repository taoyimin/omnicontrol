package cn.diaovision.omnicontrol.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeImageView;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;

/**
 * Created by TaoYimin on 2017/5/17.
 */

public class SelectableAdapter extends RecyclerView.Adapter<SelectableAdapter.SelectableViewHolder> {
    private List<Port> data;
    private ItemSelectionSupport mSelectionSupport;

    public SelectableAdapter(List<Port> data, ItemSelectionSupport selectionSupport) {
        this.data = data;
        mSelectionSupport = selectionSupport;
    }

    public ItemSelectionSupport getSelectionSupport() {
        return mSelectionSupport;
    }

    @Override
    public SelectableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectableViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_port2, parent, false), this);
    }

    @Override
    public void onBindViewHolder(SelectableViewHolder holder, final int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SelectableViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.port_checkbox)
        CheckBox checkBox;
        @BindView(R.id.port_alias)
        TextView portAlias;
        @BindView(R.id.port_image)
        BGABadgeImageView portImage;
        private SelectableAdapter mAdapter;

        public SelectableViewHolder(View itemView, SelectableAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            portAlias.setText("端口："+data.get(position).idx);
            //设置badgeview
            portImage.showTextBadge(data.get(position).idx + "");
            portImage.isShowBadge();
            boolean checked = mAdapter.getSelectionSupport().isItemChecked(position);
            if(data.get(position).dir==0) {
                portImage.setImageResource(checked ? R.mipmap.camera_light : R.mipmap.camera_normal);
            }else{
                portImage.setImageResource(checked ? R.mipmap.screen_light : R.mipmap.screen_normal);
            }
            checkBox.setChecked(checked);
            if(mSelectionSupport.getChoiceMode()== ItemSelectionSupport.ChoiceMode.MULTIPLE){
                checkBox.setVisibility(View.VISIBLE);
            }else{
                checkBox.setVisibility(View.INVISIBLE);
            }
        }
    }
}
