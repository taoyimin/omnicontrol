package cn.diaovision.omnicontrol.view;

import java.util.List;
import java.util.Set;

import cn.diaovision.omnicontrol.BasePresenter;
import cn.diaovision.omnicontrol.BaseView;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/* *
 * view + presenter统一的接口
 * 如果fragment不存在presenter和view的替换，可以转换为实体类
 * 默认单独定义presenter
 * Created by liulingfeng on 2017/4/3.
 * */

public interface AudioContract {
    interface View extends BaseView<Presenter>{
    }

    interface Presenter extends BasePresenter{
        int[] getOutputIdx(int inputIdx);
        int getInputIdx(int outputIdx);
        List<Port> getInputList();
        List<Port> getOutputList();
        Set<Channel> getChannelSet();
        void setChannel(int input, int[] outputs,int mode);
    }
}
