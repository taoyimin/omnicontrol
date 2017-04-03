package cn.diaovision.omnicontrol;

/**
 * Created by liulingfeng on 2017/4/3.
 */

public interface BaseViewModel<T> {
    void bindView(T view);
    void unbindView();
}
