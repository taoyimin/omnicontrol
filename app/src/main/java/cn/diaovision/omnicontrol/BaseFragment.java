package cn.diaovision.omnicontrol;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.reactivestreams.Subscription;

import java.util.Set;

import cn.diaovision.omnicontrol.rx.RxBus;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class BaseFragment extends Fragment {
    OmniControlApplication app;
    Set<RxBus.RxSubscription> rxSubscriptionSet;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        app = (OmniControlApplication) getActivity().getApplication();
    }

    public OmniControlApplication getApp() {
        if (app == null){
            app = (OmniControlApplication) getActivity().getApplication();
        }
        return app;
    }

    public RxBus getRxBus(){
        return ((OmniControlApplication) getApp()).getRxBus();
    }

    public void unscubscribeAll(){
        for (RxBus.RxSubscription subscription : rxSubscriptionSet){
            subscription.unsubscribe();
        }
    }
}
