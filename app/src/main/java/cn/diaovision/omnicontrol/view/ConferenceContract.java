package cn.diaovision.omnicontrol.view;

import java.util.Date;
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

public interface ConferenceContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        boolean login(String name, String passwd);

        List<Port> getInputPortList();

        void setSubtitle(int portIdx, String str);

        void setSubtitleFormat(int portIdx, int sublen, byte fontSize, byte fontColor);

        void reqConfTemplate();

        void reqConfInfo();

        void startConf(Date startTime, Date endTime, int confId);

        void endConf(int confId);

        void inviteTerm(int confId, long termId);

        void hangupTerm(int confId, long termId);

        void muteTerm(int confId, long termId);

        void unmuteTerm(int confId, long termId);

        void speechTerm(int confId, long termId);

        void cancelSpeechTerm(int confId, long termId);

        void makeChair(int confId, long termId);

        void makeSelectview(int confId, long termId);
    }
}
