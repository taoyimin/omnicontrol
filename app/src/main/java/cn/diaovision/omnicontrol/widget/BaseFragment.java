package cn.diaovision.omnicontrol.widget;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class BaseFragment extends Fragment {
    Application app;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        app = getActivity().getApplication();
    }

    public Application getApp() {
        return app;
    }
}
