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
import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;

/**
 * Created by TaoYimin on 2017/8/2.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private ItemSelectionSupport mSelectionSupport;

    public DeviceAdapter(List<Device> devices, ItemSelectionSupport mSelectionSupport) {
        this.devices = devices;
        this.mSelectionSupport = mSelectionSupport;
    }

    public ItemSelectionSupport getSelectionSupport() {
        return mSelectionSupport;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, int position) {
        if (devices == null) {
            return;
        }
        if (position < devices.size()) {
            Device device = devices.get(position);
            holder.deviceAdd.setVisibility(View.GONE);
            holder.deviceImage.setVisibility(View.VISIBLE);
            holder.deviceAlias.setText(device.getName());
            holder.deviceImage.setImageResource(R.drawable.device_blue_selector);
            holder.deviceBg.setBackgroundResource(R.drawable.button_blue_selector);
            //设置item的选中状态
            boolean checked = getSelectionSupport().isItemChecked(position);
            holder.checkBox.setChecked(checked);
            if (checked) {
                holder.deviceBg.setSelected(true);
                holder.deviceImage.setSelected(true);
            } else {
                holder.deviceBg.setSelected(false);
                holder.deviceImage.setSelected(false);
            }
        }else {
            holder.deviceAdd.setVisibility(View.VISIBLE);
            holder.deviceImage.setVisibility(View.GONE);
            holder.deviceAlias.setText("添加");
        }
    }

    @Override
    public int getItemCount() {
        return devices.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == devices.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.device_bg)
        RelativeLayout deviceBg;
        @BindView(R.id.device_checkbox)
        CheckBox checkBox;
        @BindView(R.id.device_alias)
        TextView deviceAlias;
        @BindView(R.id.device_image)
        ImageView deviceImage;
        @BindView(R.id.device_add)
        TextView deviceAdd;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public List<Device> getData() {
        return devices;
    }
}
