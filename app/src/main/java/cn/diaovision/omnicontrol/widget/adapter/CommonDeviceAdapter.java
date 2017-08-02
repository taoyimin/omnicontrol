package cn.diaovision.omnicontrol.widget.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;

/**
 * Created by TaoYimin on 2017/8/2.
 */

public class CommonDeviceAdapter extends RecyclerView.Adapter<CommonDeviceAdapter.CommonDeviceViewHolder> {
    private Context context;
    private List<CommonDevice> data;
    private OnButtonStateChangeListener onButtonStateChangeListener;

    public CommonDeviceAdapter(List<CommonDevice> data) {
        this.data = data;
    }

    @Override
    public CommonDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CommonDeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common_device, parent, false));
    }

    @Override
    public void onBindViewHolder(CommonDeviceViewHolder holder, final int position) {
        if (data == null) {
            return;
        }
        CommonDevice device = data.get(position);
        boolean state = device.getState() == State.ON ? true : false;
        switch (device.getType()) {
            case CommonDevice.TYPE.BARCO_PROJECTOR:
                if (state)
                    holder.image.setImageResource(R.mipmap.device_power_on);
                else
                    holder.image.setImageResource(R.mipmap.device_power_off);
                break;
            default:
                break;
        }
        if (state) {
            holder.state.setTextColor(context.getResources().getColor(R.color.camera_green));
            holder.state.setText("运行中");
        } else {
            holder.state.setTextColor(context.getResources().getColor(R.color.output_return_red));
            holder.state.setText("已停止");
        }
        holder.button.setChecked(state);
        holder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onButtonStateChangeListener != null) {
                    onButtonStateChangeListener.onButtonStateChange(buttonView, position,isChecked);
                }
            }
        });
        holder.ip.setText(device.getIp());
        holder.alias.setText(device.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CommonDeviceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.switch_button)
        SwitchButton button;
        @BindView(R.id.ip)
        TextView ip;
        @BindView(R.id.alias)
        TextView alias;
        @BindView(R.id.state)
        TextView state;

        public CommonDeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnButtonStateChangeListener {
        void onButtonStateChange(CompoundButton buttonView, int position,boolean isChecked);
    }

    public void setOnButtonStateChangeListener(OnButtonStateChangeListener onButtonStateChangeListener) {
        this.onButtonStateChangeListener = onButtonStateChangeListener;
    }

    public List<CommonDevice> getData() {
        return data;
    }
}
