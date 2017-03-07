package cn.diaovision.omnicontrol.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liulingfeng on 2017/2/21.
 */

public class PreferenceHelper{
    SharedPreferences pref;
    Context ctx;
    public PreferenceHelper(Context ctx, String prefName) {
        pref = ctx.getSharedPreferences(prefName, -1);
    }

    public Object getPreference(Class cls, String name){
        Object obj = pref.getAll().get(name);
        if (obj!= null && obj.getClass().equals(cls)){
            return obj;
        }
        else {
            return null;
        }
    }

    public void setPreference(String name, int val){
        SharedPreferences.Editor editor = ctx.getSharedPreferences("app", 0).edit();
        editor.putInt(name, val);
        editor.commit();
    }

    public void setPreference(String name, String val){
        SharedPreferences.Editor editor = ctx.getSharedPreferences("app", 0).edit();
        editor.putString(name, val);
        editor.commit();
    }

    public void setPreference(String name, boolean val){
        SharedPreferences.Editor editor = ctx.getSharedPreferences("app", 0).edit();
        editor.putBoolean(name, val);
        editor.commit();
    }
}
