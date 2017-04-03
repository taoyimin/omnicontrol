package cn.diaovision.omnicontrol;

/**
 * Created by liulingfeng on 2017/4/3.
 */

public interface  BaseView<T> {
    void setPresenter(); //内部初始化态的MVP模式
    //void bindViewModel(T viewModel); //MVVM模式
}
