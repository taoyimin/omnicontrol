package cn.diaovision.omnicontrol.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

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
    public void setMainName(String name) {

    }

    @Override
    public void setMainPasswd(String password) {

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
//        return "192.168.10.100";
        return "127.0.0.1";
    }

    @Override
    public int getMcuPort() {
        return 6190;
    }

    @Override
    public String getMcuId() {
        return "Admin";
    }

    @Override
    public String getMcuKey() {
        return "123";
    }

    @Override
    public int getMatrixId() {
        return 1;
    }

    @Override
    public void setMatrixId(String id) {

    }

    @Override
    public String getMatrixIp() {
        return "192.168.10.11";
    }

    @Override
    public void setMatrixIp(String ip) {

    }

    @Override
    public int getMatrixUdpIpPort() {
        return 5000;
    }

    @Override
    public void setMatrixUdpIpPort(String port) {

    }

    @Override
    public String getMatrixPreviewIp() {
        return "192.168.10.31";
    }

    @Override
    public int getMatrixPreviewPort() {
        return 29;
    }

    @Override
    public Map<Integer, HiCamera> getMatrixCameras() {
        return null;
    }

    @Override
    public Set<Channel> getMatrixChannels() {
        return null;
    }

    @Override
    public List<Port> getInputPortList() {
        return null;
    }

    @Override
    public List<Port> getOutputPortList() {
        return null;
    }

    @Override
    public void setPort(Port port) {

    }

    @Override
    public void setChannelSet(Set<Channel> channelSet) {

    }

    @Override
    public void setPreviewVideoPort(int portIdx) {

    }

    @Override
    public void setCamera(HiCamera hiCamera) {

    }

    @Override
    public void deleteCamera(HiCamera camera) {

    }

    @Override
    public void setCameraPreset(int cameraIdx, HiCamera.Preset preset) {

    }

    @Override
    public void addCameraPreset(int cameraIdx, HiCamera.Preset preset) {

    }

    @Override
    public void deleteCameraPreset(int cameraIdx, HiCamera.Preset preset) {

    }

    @Override
    public List<Device> getDeviceList() {
        return null;
    }

    @Override
    public void setDeviceList(List<Device> devices) {

    }

}
