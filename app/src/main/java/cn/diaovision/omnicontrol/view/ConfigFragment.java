package cn.diaovision.omnicontrol.view;

import android.graphics.Bitmap;

import cn.diaovision.omnicontrol.BaseFragment;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class ConfigFragment extends BaseFragment implements ConfigContract.View{
    ConfigContract.Presenter presenter;

    @Override
    public void changeTitle() {

    }

    @Override
    public void bindPresenter() {
        presenter = new ConfigPresenter(this);
    }
}
