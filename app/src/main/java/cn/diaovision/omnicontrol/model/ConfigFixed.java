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

    @Override
    public String getMcuIp() {
        return "192.168.1.1";
    }

    @Override
    public int getMcuPort() {
        return 554;
    }
}
