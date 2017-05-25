package cn.diaovision.omnicontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.Set;

import cn.diaovision.omnicontrol.rx.RxBus;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public abstract class BaseFragment extends Fragment{

    OmniControlApplication app;
    Set<RxBus.RxSubscription> rxSubscriptionSet;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindPresenter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        app = (OmniControlApplication) getActivity().getApplication();
    }

    public OmniControlApplication getApp() {
        if (app == null) {
            app = (OmniControlApplication) getActivity().getApplication();
        }
        return app;
    }

    public SharedPreferences getPreferences() {
        if (app == null) {
            app = (OmniControlApplication) getActivity().getApplication();
        }
        return app.getAppPreferences();
    }

    public RxBus getRxBus() {
        return getApp().getRxBus();
    }

    public void unsubscribeAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (RxBus.RxSubscription subscription : rxSubscriptionSet) {
                    subscription.unsubscribe();
                }
            }
        }).start();
    }

    public abstract void bindPresenter();
}


