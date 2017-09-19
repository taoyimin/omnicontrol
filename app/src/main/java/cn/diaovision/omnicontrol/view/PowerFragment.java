package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.widget.CommandDialog;
import cn.diaovision.omnicontrol.widget.DeviceDialog;
import cn.diaovision.omnicontrol.widget.ItemSelectionSupport;
import cn.diaovision.omnicontrol.widget.OnRecyclerItemClickListener;
import cn.diaovision.omnicontrol.widget.adapter.CommandAdapter;
import cn.diaovision.omnicontrol.widget.adapter.DeviceAdapter;
import cn.diaovision.omnicontrol.widget.adapter.LogAdapter;

/* *
 * 开关控制页面
 * Created by liulingfeng on 2017/2/24.
 * */

public class PowerFragment extends BaseFragment implements PowerContract.View {
    @BindView(R.id.device_recycler)
    RecyclerView deviceRecycler;
    @BindView(R.id.command_recycler)
    RecyclerView commandRecycler;
    @BindView(R.id.log_recycler)
    RecyclerView logRecycler;

    DeviceAdapter deviceAdapter;
    CommandAdapter commandAdapter;
    LogAdapter logAdapter;
    PowerPresenter presenter;
    ItemSelectionSupport deviceSelectionSupport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_power, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    //TODO: 内部生成presenter，不通过外部指定
    @Override
    public void bindPresenter() {
        this.presenter = new PowerPresenter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final List<Device> list = presenter.getDeviceList();
        deviceSelectionSupport = new ItemSelectionSupport(deviceRecycler);
        deviceSelectionSupport.setChoiceMode(ItemSelectionSupport.ChoiceMode.SINGLE);
        deviceAdapter = new DeviceAdapter(list, deviceSelectionSupport);
        commandAdapter = new CommandAdapter(null);
        logAdapter = new LogAdapter(presenter.getLogList());
        deviceRecycler.setLayoutManager(new GridLayoutManager(getContext(), 6));
        commandRecycler.setLayoutManager(new GridLayoutManager(getContext(), 6));
        logRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceRecycler.setAdapter(deviceAdapter);
        commandRecycler.setAdapter(commandAdapter);
        logRecycler.setAdapter(logAdapter);
        deviceRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(deviceRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                if (position == list.size()) {
                    //如果是最后一个，弹出添加设备对话框
                    popupDialog(null, position);
                } else {
                    deviceSelectionSupport.itemClick(position);
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, int position) {
                if (position != list.size()) {
                    popupDialog(list.get(position), position);
                }
            }
        });
        deviceSelectionSupport.setOnItemStatueListener(new ItemSelectionSupport.OnItemStatueListener() {
            @Override
            public void onSelectSingle(int position) {
                commandAdapter.setCmds(list.get(position).getCmds());
                commandAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUnSelectSingle(int position) {
                commandAdapter.setCmds(null);
                commandAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelectMultiple(int position) {

            }

            @Override
            public void onUnSelectMultiple(int position) {

            }
        });
        commandRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(commandRecycler) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                List<Device.Command> cmds = list.get(deviceSelectionSupport.getCheckedItemPosition()).getCmds();
                if (position == cmds.size()) {
                    //如果是最后一个，弹出添加指令对话框
                    popupCommandDialog(null, position);
                } else {
                    //deviceSelectionSupport.itemClick(position);
                    Device device = list.get(deviceSelectionSupport.getCheckedItemPosition());
                    presenter.sendCommand(device, cmds.get(position));
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder vh, int position) {
                List<Device.Command> cmds = list.get(deviceSelectionSupport.getCheckedItemPosition()).getCmds();
                if (position != cmds.size()) {
                    popupCommandDialog(cmds.get(position), position);
                }
            }
        });
    }

    /*弹出设备编辑对话框*/
    private void popupDialog(Device device, final int position) {
        final DeviceDialog dialog = new DeviceDialog(getContext(), device);
        dialog.show();
        dialog.setOnButtonClickListener(new DeviceDialog.OnButtonClickListener() {
            @Override
            public void onConfirmClick() {
                dialog.dismiss();
                deviceAdapter.notifyItemChanged(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }

            @Override
            public void onDeleteClick() {
                dialog.dismiss();
                deviceAdapter.getData().remove(position);
                deviceAdapter.notifyItemRemoved(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }

            @Override
            public void onAddDeviceClick(Device device) {
                dialog.dismiss();
                deviceAdapter.getData().add(device);
                deviceAdapter.notifyItemInserted(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }
        });
    }

    /*弹出指令编辑对话框*/
    private void popupCommandDialog(Device.Command cmd, final int position) {
        final CommandDialog dialog = new CommandDialog(getContext(), cmd);
        dialog.show();
        dialog.setOnButtonClickListener(new CommandDialog.OnButtonClickListener() {
            @Override
            public void onConfirmClick() {
                dialog.dismiss();
                commandAdapter.notifyItemChanged(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }

            @Override
            public void onDeleteClick() {
                dialog.dismiss();
                commandAdapter.getCmds().remove(position);
                commandAdapter.notifyItemRemoved(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }

            @Override
            public void onAddDeviceClick(Device.Command cmd) {
                dialog.dismiss();
                commandAdapter.getCmds().add(cmd);
                commandAdapter.notifyItemInserted(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }
        });
    }

    /*弹出吐司*/
    @Override
    public void showToast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyLogChange(int fromIndex, int count) {
        logAdapter.notifyItemRangeInserted(fromIndex,count);
        logRecycler.smoothScrollToPosition(logAdapter.getItemCount()-1);
    }

    /* *********************************
     * presenter-view interactions
     * *********************************/
}
