package cn.diaovision.omnicontrol.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import cn.diaovision.omnicontrol.BaseFragment;

/**
 * Created by liulingfeng on 2017/2/24.
 */

public class LightFragment extends BaseFragment implements LightContract.View{

    LightPresenter presenter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void bindPresenter() {
        presenter = new LightPresenter(this);
    }
}
