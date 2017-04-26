package cn.diaovision.omnicontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


import cn.diaovision.omnicontrol.core.model.conference.McuCommManager;

/**
 * 通信类服务
 * Created by liulingfeng on 2017/2/21.
 */

public class ConferenceService extends Service {
    private McuCommManager mgr;

    @Override
    public void onCreate() {
        super.onCreate();
//        mgr = new McuCommManager("192.168.2.1", 6000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
