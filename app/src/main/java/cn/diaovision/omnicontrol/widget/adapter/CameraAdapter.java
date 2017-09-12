package cn.diaovision.omnicontrol.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;

/**
 * Created by TaoYimin on 2017/6/8.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.CameraViewHolder>{
    private List<HiCamera> cameras;
    private ItemSelectionSupport mSelectionSupport;

    public CameraAdapter(List<HiCamera> cameras, ItemSelectionSupport mSelectionSupport) {
        this.cameras = cameras;
        this.mSelectionSupport = mSelectionSupport;
    }

    public ItemSelectionSupport getSelectionSupport() {
        return mSelectionSupport;
    }

    @Override
    public CameraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CameraViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_port, parent, false));
    }

    @Override
    public void onBindViewHolder(CameraViewHolder holder, int position) {
        holder.portBg.setBackgroundResource(R.drawable.button_blue_selector);
        holder.portImage.setBackgroundResource(R.drawable.camera_blue_selector);
        //设置摄像机别名
        holder.portAlias.setText(cameras.get(position).getAlias());
        //设置item的选中状态
        boolean checked = getSelectionSupport().isItemChecked(position);
        holder.checkBox.setChecked(checked);
        if (checked) {
            holder.portBg.setSelected(true);
            holder.portImage.setSelected(true);
        } else {
            holder.portBg.setSelected(false);
            holder.portImage.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return cameras.size();
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.port_bg)
        RelativeLayout portBg;
        @BindView(R.id.port_checkbox)
        CheckBox checkBox;
        @BindView(R.id.port_alias)
        TextView portAlias;
        @BindView(R.id.port_image)
        ImageView portImage;

        public CameraViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
