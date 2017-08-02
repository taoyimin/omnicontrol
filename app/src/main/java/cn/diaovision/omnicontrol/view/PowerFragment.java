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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.BaseFragment;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.common.BarcoProjector;
import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;
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
        List<CommonDevice> list = new ArrayList<>();
        CommonDevice device1 = new BarcoProjector("123", "192.168.10.102", 1025);
        CommonDevice device2 = new BarcoProjector("321", "192.168.10.103", 1025);
        device1.setState(State.OFF);
        device2.setState(State.OFF);
        list.add(device1);
        list.add(device2);
        deviceAdapter = new CommonDeviceAdapter(list);
        initAdapterListener();
        deviceRecycler.setLayoutManager(new GridLayoutManager(getContext(), 6));
        deviceRecycler.setAdapter(deviceAdapter);
    }

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
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshDeviceList() {
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeAdapterListener() {
        deviceAdapter.setOnButtonStateChangeListener(null);
    }

    /* *********************************
     * presenter-view interactions
     * *********************************/
}
