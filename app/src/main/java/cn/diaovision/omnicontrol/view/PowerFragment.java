package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;
import cn.diaovision.omnicontrol.widget.DeviceDialog;
import cn.diaovision.omnicontrol.widget.adapter.CommonDeviceAdapter;

/* *
 * 开关控制页面
 * Created by liulingfeng on 2017/2/24.
 * */

public class PowerFragment extends BaseFragment implements PowerContract.View {
    @BindView(R.id.device_recycler)
    RecyclerView deviceRecycler;

    CommonDeviceAdapter deviceAdapter;
    PowerContract.Presenter presenter;

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
        List<CommonDevice> list = presenter.getDeviceList();
/*        CommonDevice device1 = new BarcoProjector("123", "192.168.10.102", 1025);
        CommonDevice device2 = new BarcoProjector("321", "192.168.10.103", 1025);
        device1.setState(State.NA);
        device2.setState(State.NA);
        list.add(device1);
        list.add(device2);*/
        deviceAdapter = new CommonDeviceAdapter(list);
        initAdapterListener();
        deviceRecycler.setLayoutManager(new GridLayoutManager(getContext(), 6));
        deviceRecycler.setAdapter(deviceAdapter);
        presenter.initState(deviceAdapter.getData());
    }

    /*初始化设备列表适配器的监听器*/
    @Override
    public void initAdapterListener() {
        deviceAdapter.setOnButtonStateChangeListener(new CommonDeviceAdapter.OnButtonStateChangeListener() {
            @Override
            public void onButtonStateChange(CompoundButton buttonView, int position, boolean isChecked) {
                CommonDevice device = deviceAdapter.getData().get(position);
                if (isChecked) {
                    presenter.powerOn(buttonView, device);
                } else {
                    presenter.powerOff(buttonView, device);
                }
            }
        });
        deviceAdapter.setOnItemClickListener(new CommonDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position==deviceAdapter.getData().size()){
                    popupDialog(null,position);
                }
            }

            @Override
            public void onItemLongClick(int position) {
                popupDialog(deviceAdapter.getData().get(position), position);
            }
        });
    }

    /*弹出设备编辑对话框*/
    private void popupDialog(CommonDevice device, final int position) {
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
            public void onAddDeviceClick(CommonDevice device) {
                dialog.dismiss();
                deviceAdapter.getData().add(device);
                deviceAdapter.notifyItemInserted(position);
                MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
            }
        });
    }

    /*弹出吐司*/
    @Override
    public void showToast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    /*刷新设备列表*/
    @Override
    public void refreshDeviceList() {
        deviceAdapter.notifyDataSetChanged();
        MainControlActivity.cfg.setDeviceList(deviceAdapter.getData());
    }

    /*移除设备列表适配器的监听器*/
    @Override
    public void removeAdapterListener() {
        deviceAdapter.setOnButtonStateChangeListener(null);
    }

    /* *********************************
     * presenter-view interactions
     * *********************************/
}
