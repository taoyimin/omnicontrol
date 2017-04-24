package cn.diaovision.omnicontrol.model;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public class ConfigFixed implements Config{
    @Override
    public String getMainName() {
        return "admin";
    }

    @Override
    public String getMainPasswd() {
        return "diaovision";
    }

    @Override
    public String getConfName() {
        return "admin";
    }

    @Override
    public String getConfPasswd() {
        return "diaovision";
    }
}
