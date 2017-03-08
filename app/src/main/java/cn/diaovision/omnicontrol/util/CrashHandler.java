package cn.diaovision.omnicontrol.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by liulingfeng on 2017/3/8.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler{
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        if (mDefaultHandler == null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    new AlertDialog.Builder(mContext).setTitle("告诉你一个不幸的消息")
                            .setCancelable(false)
                            .setMessage("程序崩溃了..." + get(ex))
                            .setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    Looper.loop();
                }
            }.start();
        }
    }

    private String get(Throwable exception)
    {
        StringWriter sw = null;
        PrintWriter pw = null;
        try
        {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            Log.getStackTraceString(exception);
            return sw.toString();
        } finally
        {
            if (pw != null)
            {
                pw.close();
            }
        }

    }

}
