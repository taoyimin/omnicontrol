package cn.diaovision.omnicontrol.view;

import java.util.List;

import cn.diaovision.omnicontrol.BasePresenter;
import cn.diaovision.omnicontrol.BaseView;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/* *
 * view + presenter统一的接口
 * 如果fragment不存在presenter和view的替换，可以转换为实体类
 * 默认单独定义presenter
 * Created by liulingfeng on 2017/4/3.
 * */

public interface VideoContract {
    interface View extends BaseView<Presenter>{
    }

    interface Presenter extends BasePresenter{
        Port getOutput(Port input);
        Port getInput(Port output);
        List<Port> getInputList();
        List<Port> getOutputList();
        void setChannel(Port input, List<Port> output/*,Mode*/);
        void switchVideo(int portIn, int[] portOut);
        void stitchVideo(int portIn,  int columnCnt,  int rowCnt,  int[] portOut);

        interface PortStateListener{
            void onPreviewDisconnected();
            void onPreviewConnected();
            void onMatrixDisconnected();
        }
    }
}
