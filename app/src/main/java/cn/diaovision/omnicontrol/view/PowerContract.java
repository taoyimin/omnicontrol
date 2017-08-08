package cn.diaovision.omnicontrol.view;

import android.widget.CompoundButton;

import java.util.List;

import cn.diaovision.omnicontrol.BasePresenter;
import cn.diaovision.omnicontrol.BaseView;
import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;

/* *
 * view + presenter统一的接口
 * 如果fragment不存在presenter和view的替换，可以转换为实体类
 * 默认单独定义presenter
 * Created by liulingfeng on 2017/4/3.
 * */

public interface PowerContract {
    interface View extends BaseView<Presenter>{
//        void changeTitle();
        void showToast(String str);
        void refreshDeviceList();
        void initAdapterListener();
        void removeAdapterListener();
    }

    interface Presenter extends BasePresenter{
//        void func();
        void powerOn(CompoundButton buttonView,CommonDevice device);
        void powerOff(CompoundButton buttonView,CommonDevice device);
        void initState(List<CommonDevice> devices);
        List<CommonDevice> getDeviceList();
    }
}
