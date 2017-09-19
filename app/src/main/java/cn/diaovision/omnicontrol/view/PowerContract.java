package cn.diaovision.omnicontrol.view;

import java.util.List;

import cn.diaovision.omnicontrol.BasePresenter;
import cn.diaovision.omnicontrol.BaseView;
import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.core.model.device.common.DeviceLog;

/* *
 * view + presenter统一的接口
 * 如果fragment不存在presenter和view的替换，可以转换为实体类
 * 默认单独定义presenter
 * Created by liulingfeng on 2017/4/3.
 * */

public interface PowerContract {
    interface View extends BaseView<Presenter>{
        void showToast(String str);
        void notifyLogChange(int fromIndex,int count);
    }

    interface Presenter extends BasePresenter{
        List<Device> getDeviceList();
        void sendCommand(Device device, Device.Command cmd);
        List<DeviceLog> getLogList();
    }
}
