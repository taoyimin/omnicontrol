package cn.diaovision.omnicontrol.model;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public interface Config {
    String getMainName();
    String getMainPasswd();

    String getConfName();
    String getConfPasswd();

    String getMcuIp();
    int getMcuPort();
}
