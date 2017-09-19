package cn.diaovision.omnicontrol.view;

import java.util.List;

import cn.diaovision.omnicontrol.BasePresenter;
import cn.diaovision.omnicontrol.BaseView;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;

/* *
 * view + presenter统一的接口
 * 如果fragment不存在presenter和view的替换，可以转换为实体类
 * 默认单独定义presenter
 * Created by liulingfeng on 2017/4/3.
 * */

public interface CameraContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void cameraCtrlGo(int portIdx, int cmd, int speed);

        void cameraStopGo(int portIdx);

        void addPreset(int portIdx, int presetIdx, String name);

        void delPreset(int portIdx, int presetIdx);

        //void updatePreset();
        void loadPreset(int portIdx, int presetIdx);

        HiCamera getCamera(int port);

        List<HiCamera> getCameraList();

        void switchPreviewVideo(int portIn, int portOut);
    }
}
