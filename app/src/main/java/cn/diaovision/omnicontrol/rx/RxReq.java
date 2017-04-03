package cn.diaovision.omnicontrol.rx;

/**
 * 异步请求接口
 * 根据业务实际需求重载request
 * Created by liulingfeng on 2017/4/3.
 */

public interface RxReq {
    RxMessage request();
}
