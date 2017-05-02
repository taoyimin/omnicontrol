package cn.diaovision.omnicontrol.util;

import android.os.Environment;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class FileHelper {
    public static String getSrceenShotPath(){
        String path="";
        //判断SD卡是否挂载
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Omnicontrol/screenshot";
        }
        return path;
    }
}
