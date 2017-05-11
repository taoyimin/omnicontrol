package cn.diaovision.omnicontrol.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public class ConfigFixed implements Config{

    public Config getInstance(){
        return new ConfigFixed();
    }

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

    @Override
    public int getMatrixId() {
        return 1;
    }

    @Override
    public String getMatrixIp() {
        return "192.168.10.11";
    }

    @Override
    public int getMatrixUdpIpPort() {
        return 5000;
    }

    @Override
    public String getMatrixPreviewIp() {
        return "192.168.10.31";
    }

    @Override
    public int getMtatrixPreviewIpPort() {
        return 554;
    }

    @Override
    public int getMatrixPreviewPort() {
        return 29;
    }

    @Override
    public Map<Integer,HiCamera> getHiCameraInfo() {
        Map<Integer,HiCamera> cameras = new HashMap<>();
        cameras.put(1,new HiCamera(1, 1, 9600, HiCamera.PROTO_PILSA));
        return cameras;
    }

    @Override
    public int getMatrixInputVideoNum() {
        return 32;
    }

    @Override
    public int getMatrixOutputVideoNum() {
        return 32;
    }

    @Override
    public byte getSubtitleFontSize() {
        return 13;
    }

    @Override
    public byte getSubtitleFontColor() {
        return 10;
    }

    @Override
    public Date getConfStartDate() {
        return new Date();
    }

    @Override
    public Date getConfEndDate() {
        Date date = new Date();
        Calendar calendar = Calendar. getInstance();
        calendar.setTime(date);
        calendar. add(Calendar.HOUR, 1);
        return date;
    }


}
