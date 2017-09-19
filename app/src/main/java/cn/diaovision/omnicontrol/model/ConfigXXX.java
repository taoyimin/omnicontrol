package cn.diaovision.omnicontrol.model;

import android.text.TextUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.OmniControlApplication;
import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.util.ByteUtils;

/**
 * Created by liulingfeng on 2017/5/8.
 */

public class ConfigXXX implements Config {
    Document document;
    Element root;
    OutputFormat format;
    File configFile;

    private ConfigXXX(String xmlFile) {
        SAXReader reader = new SAXReader();
        format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        configFile = new File(xmlFile);
        if (configFile.exists()) {
            //如果已经存在，直接读取配置文件
            try {
                document = reader.read(configFile);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            //如果不存在，创建文件并将assets文件夹下的默认配置写入
            try {
                document = reader.read(OmniControlApplication.getContext().getAssets().open("config_template.xml"));
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                configFile.createNewFile();
                XMLWriter writer = new XMLWriter(new FileOutputStream(configFile), format);
                writer.write(document);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        root = document.getRootElement();
    }

    /*根据文件路径读取XML文件*/
    public static ConfigXXX fromFile(String xmlFile) {
        return new ConfigXXX(xmlFile);
    }

    /*更新端口的属性，编辑完端口信息后调用*/
    @Override
    public void setPort(Port port) {
        List<Element> elements = new ArrayList<>();
        if (port.dir == Port.DIR_IN) {
            elements = root.element("matrix").element("input_list").elements("input");
        } else if (port.dir == Port.DIR_OUT) {
            elements = root.element("matrix").element("output_list").elements("output");
        }
        for (Element element : elements) {
            int index = Integer.parseInt(element.element("index").getTextTrim());
            if (port.idx == index) {
                element.element("parent_index").setText(port.parentIdx + "");
                element.element("type").setText(port.type + "");
                element.element("dir").setText(port.dir + "");
                element.element("category").setText(port.category + "");
                element.element("alias").setText(port.alias);
                save();
                return;
            }
        }
    }

    /*更新通道信息，在设置完通道后调用*/
    @Override
    public void setChannelSet(Set<Channel> channelSet) {
        List<Element> elements = root.element("matrix").element("channel_list").elements("channel");
        for (Element element : elements) {
            element.detach();
        }
        for (Channel channel : channelSet) {
            Element channelElement = root.element("matrix").element("channel_list").addElement("channel");
            channelElement.addAttribute("name", channel.getAlias());
            channelElement.addAttribute("mode", channel.mode == Channel.MOD_NORMAL ? "normal" : "stitch");
            channelElement.addElement("input").setText(channel.getInputIdx() + "");
            Element outputListElement = channelElement.addElement("output_list");
            for (int outputIdx : channel.getOutputIdx()) {
                outputListElement.addElement("output").setText(outputIdx + "");
            }
        }
        save();
    }

    /*设置预览卡端口信息，在修改完端口后，如果端口是流媒体卡端口时调用*/
    @Override
    public void setPreviewVideoPort(int portIdx) {
        Element element = root.element("matrix").element("preview_video").element("port");
        element.setText(portIdx + "");
        save();
    }

    /*添加摄像机的配置信息*/
    @Override
    public void setCamera(HiCamera camera) {
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            if (Integer.parseInt(element.element("port").getTextTrim()) == camera.getPortIdx()) {
                //当前端口摄像机配置已经存在的话，先删除摄像机配置信息
                element.detach();
            }
        }
        Element cameraElement = root.element("matrix").element("camera_list").addElement("camera");
        cameraElement.addElement("port").setText(camera.getPortIdx() + "");
        cameraElement.addElement("index").setText(camera.getIdx() + "");
        cameraElement.addElement("baudrate").setText(camera.getBaudrate() + "");
        cameraElement.addElement("alias").setText(camera.getAlias());
        cameraElement.addElement("preset_list");
        switch (camera.getProto()) {
            case HiCamera.PROTO_FELICA_D:
                cameraElement.addElement("proto").setText("PELCO-D");
                break;
            case HiCamera.PROTO_FELICA_A:
                cameraElement.addElement("proto").setText("PELCO-P");
                break;
            case HiCamera.PROTO_PILSA:
                cameraElement.addElement("proto").setText("VISCA");
                break;
            default:
                break;
        }
        for (HiCamera.Preset preset : camera.getPresetList()) {
            Element presetElement = cameraElement.addElement("preset");
            presetElement.addElement("idx").setText(preset.getIdx() + "");
            presetElement.addElement("name").setText(preset.getName());
        }
        save();
    }

    /*删除摄像机的配置信息*/
    @Override
    public void deleteCamera(HiCamera camera) {
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            if (Integer.parseInt(element.element("port").getTextTrim()) == camera.getPortIdx()) {
                element.detach();
            }
        }
        save();
    }

    /*修改预置位的配置信息*/
    @Override
    public void setCameraPreset(int cameraIdx, HiCamera.Preset preset) {
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            if (Integer.parseInt(element.element("port").getTextTrim()) == cameraIdx) {
                List<Element> presetElements = element.element("preset_list").elements("preset");
                for (Element presetElement : presetElements) {
                    if (Integer.parseInt(presetElement.element("idx").getTextTrim()) == preset.getIdx()) {
                        presetElement.element("name").setText(preset.getName());
                    }
                }
            }
        }
        save();
    }

    /*添加预置位的配置信息*/
    @Override
    public void addCameraPreset(int cameraIdx, HiCamera.Preset preset) {
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            if (Integer.parseInt(element.element("port").getTextTrim()) == cameraIdx) {
                Element presetElement = element.element("preset_list").addElement("preset");
                presetElement.addElement("idx").setText(preset.getIdx() + "");
                presetElement.addElement("name").setText(preset.getName());
            }
        }
        save();
    }

    /*删除预置位的配置信息*/
    @Override
    public void deleteCameraPreset(int cameraIdx, HiCamera.Preset preset) {
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            if (Integer.parseInt(element.element("port").getTextTrim()) == cameraIdx) {
                List<Element> presetElements = element.element("preset_list").elements("preset");
                for (Element presetElement : presetElements) {
                    if (Integer.parseInt(presetElement.element("idx").getTextTrim()) == preset.getIdx()) {
                        presetElement.detach();
                    }
                }
            }
        }
        save();
    }

    /*获取设备列表*/
    @Override
    public List<Device> getDeviceList() {
        List<Device> devices = new ArrayList<>();
        if (!root.element("common_device").elementIterator().hasNext()) {
            return devices;
        }
        List<Element> elements = root.element("common_device").elements("device");
        for (Element element : elements) {
            Device device = new Device();
            String alias = element.element("alias").getText();
            String ip = element.element("ip").getTextTrim();
            int port = Integer.parseInt(element.element("port").getTextTrim());
            Element commandsElement=element.element("command_list");
            List<Device.Command> cmds=new ArrayList<>();
            if(commandsElement.elementIterator().hasNext()){
                if(commandsElement.element("command").elementIterator().hasNext()){
                    List<Element> commandElements=commandsElement.elements("command");
                    for(int i=0;i<commandElements.size();i++){
                        Element commandElement=commandElements.get(i);
                        String name=commandElement.element("alias").getText();
                        String stringCmd=commandElement.element("string_cmd").getText();
                        String byteString=commandElement.element("bytes_cmd").getText();
                        byte[] byteCmd=null;
                        if(TextUtils.isEmpty(byteString)){
                            byteCmd=new byte[0];
                        }else{
                            byteCmd=ByteUtils.string2bytes(byteString);
                        }
                        Device.Command cmd=new Device.Command();
                        cmd.setName(name);
                        cmd.setStringCmd(stringCmd);
                        cmd.setByteCmd(byteCmd);
                        cmds.add(cmd);
                    }
                }
            }
            device.setName(alias);
            device.setIp(ip);
            device.setPort(port);
            device.setCmds(cmds);
            devices.add(device);
        }
        return devices;
    }

    /*修改设备的配置信息*/
    @Override
    public void setDeviceList(List<Device> devices) {
        if (root.element("common_device").elementIterator().hasNext()) {
            List<Element> elements = root.element("common_device").elements("device");
            for (Element element : elements) {
                element.detach();
            }
        }
        for (Device device : devices) {
            Element deviceElement = root.element("common_device").addElement("device");
            deviceElement.addElement("alias").setText(device.getName());
            deviceElement.addElement("ip").setText(device.getIp());
            deviceElement.addElement("port").setText(device.getPort() + "");
            Element commandsElement = deviceElement.addElement("command_list");
            for (int i = 0; i < device.getCmds().size(); i++) {
                Element commandElement=commandsElement.addElement("command");
                commandElement.addElement("alias").setText(device.getCmds().get(i).getName());
                commandElement.addElement("string_cmd").setText(device.getCmds().get(i).getStringCmd());
                byte[] bytes = device.getCmds().get(i).getByteCmd();
                if (bytes != null && bytes.length > 0) {
                    commandElement.addElement("bytes_cmd").setText(ByteUtils.bytes2string(bytes));
                }else{
                    commandElement.addElement("bytes_cmd").setText("");
                }
            }
        }
        save();
    }

    @Override
    public String getMainName() {
        return root.element("user").element("name").getTextTrim();
    }

    @Override
    public String getMainPasswd() {
        return root.element("user").element("password").getTextTrim();
    }

    @Override
    public void setMainName(String name) {
        root.element("user").element("name").setText(name);
        save();
    }

    @Override
    public void setMainPasswd(String password) {
        root.element("user").element("password").setText(password);
        save();
    }

    @Override
    public String getConfName() {
        return null;
    }

    @Override
    public String getConfPasswd() {
        return null;
    }

    /*获取MCU服务器的IP地址*/
    @Override
    public String getMcuIp() {
        return root.element("mcu").element("ip").getTextTrim();
    }

    /*获取MCU服务器的通信端口*/
    @Override
    public int getMcuPort() {
        return Integer.parseInt(root.element("mcu").element("port_config").getTextTrim());
    }

    /*获取多媒体矩阵的ID*/
    @Override
    public String getMcuId() {
        return null;
    }

    @Override
    public String getMcuKey() {
        return null;
    }

    @Override
    public int getMatrixId() {
        return Integer.parseInt(root.element("matrix").element("id").getTextTrim());
    }

    @Override
    public void setMatrixId(String id) {
        root.element("matrix").element("id").setText(id);
        save();
    }

    /*获取多媒体矩阵的IP*/
    @Override
    public String getMatrixIp() {
        return root.element("matrix").element("ip").getTextTrim();
    }

    @Override
    public void setMatrixIp(String ip) {
        root.element("matrix").element("ip").setText(ip);
        save();
    }

    /*获取多媒体矩阵的UDP通信端口*/
    @Override
    public int getMatrixUdpIpPort() {
        return Integer.parseInt(root.element("matrix").element("port").getTextTrim());
    }

    @Override
    public void setMatrixUdpIpPort(String port) {
        root.element("matrix").element("port").setText(port);
        save();
    }

    /*获取预览卡IP*/
    @Override
    public String getMatrixPreviewIp() {
        return root.element("matrix").element("preview_video").element("ip").getTextTrim();
    }

    /*获取预览卡IP端口号*/
/*    @Override
    public int getMatrixPreviewIpPort() {
        return Integer.parseInt(root.element("matrix").element("preview_video").element("ip_port").getTextTrim());
    }*/

    /*获取预览卡在矩阵上的端口号*/
    @Override
    public int getMatrixPreviewPort() {
        return Integer.parseInt(root.element("matrix").element("preview_video").element("port").getTextTrim());
    }

    /*获取矩阵的输入端个数*/
/*    @Override
    public int getMatrixInputVideoNum() {
        return Integer.parseInt(root.element("matrix").element("input_number").getTextTrim());
    }*/

    /*获取矩阵的输出端个数*/
/*    @Override
    public int getMatrixOutputVideoNum() {
        return Integer.parseInt(root.element("matrix").element("output_number").getTextTrim());
    }*/

    /*获取摄像机的集合*/
    @Override
    public Map<Integer, HiCamera> getMatrixCameras() {
        Map<Integer, HiCamera> cameras = new HashMap();
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            int port = Integer.parseInt(element.element("port").getTextTrim());
            int index = Integer.parseInt(element.element("index").getTextTrim());
            int baudrate = Integer.parseInt(element.element("baudrate").getTextTrim());
            int proto;
            switch (element.element("proto").getTextTrim()) {
                case "PELCO-D":
                    proto = HiCamera.PROTO_FELICA_D;
                    break;
                case "PELCO-P":
                    proto = HiCamera.PROTO_FELICA_A;
                    break;
                case "VISCA":
                    proto = HiCamera.PROTO_PILSA;
                    break;
                default:
                    proto = HiCamera.PROTO_FELICA_D;
                    break;
            }
            String alias = element.element("alias").getTextTrim();
            List<HiCamera.Preset> presetList = new ArrayList<>();
            List<Element> presetElements = element.element("preset_list").elements("preset");
            for (Element presetElement : presetElements) {
                String name = presetElement.element("name").getTextTrim();
                int idx = Integer.parseInt(presetElement.element("idx").getTextTrim());
                HiCamera.Preset preset = new HiCamera.Preset(name, idx);
                presetList.add(preset);
            }
            HiCamera hiCamera = new HiCamera(port, index, baudrate, proto);
            hiCamera.setAlias(alias);
            hiCamera.setPresetList(presetList);
            cameras.put(port, hiCamera);
        }
        return cameras;
    }

    /*获取多媒体矩阵的通道信息*/
    @Override
    public Set<Channel> getMatrixChannels() {
        Set<Channel> channels = new HashSet<>();
        List<Element> elements = root.element("matrix").element("channel_list").elements("channel");
        for (Element element : elements) {
            int in = Integer.parseInt(element.element("input").getTextTrim());
            List<Element> outPutElements = element.element("output_list").elements("output");
            int[] outs = new int[outPutElements.size()];
            for (int i = 0; i < outPutElements.size(); i++) {
                outs[i] = Integer.parseInt(outPutElements.get(i).getTextTrim());
            }
            Channel channel = new Channel(Channel.CHN_VIDEO, in, outs);
            int mode;
            switch (element.attributeValue("mode")) {
                case "normal":
                    mode = Channel.MOD_NORMAL;
                    break;
                case "stitch":
                    mode = Channel.MOD_STITCH;
                    break;
                default:
                    mode = Channel.MOD_NORMAL;
                    break;
            }
            channel.mode = mode;
            String alias = element.attributeValue("name");
            if (!TextUtils.isEmpty(alias)) {
                channel.setAlias(alias);
            }
            channels.add(channel);
        }
        return channels;
    }

    /*获取所有输入端口信息*/
    public List<Port> getInputPortList() {
        List<Port> inputList = new ArrayList<>();
        List<Element> elements = root.element("matrix").element("input_list").elements("input");
        for (Element element : elements) {
            int parentIdx = Integer.parseInt(element.element("parent_index").getTextTrim());
            int index = Integer.parseInt(element.element("index").getTextTrim());
            int type = Integer.parseInt(element.element("type").getTextTrim());
            int dir = Integer.parseInt(element.element("dir").getTextTrim());
            int category = Integer.parseInt(element.element("category").getTextTrim());
            String alias = element.element("alias").getTextTrim();
            Port port = new Port(parentIdx, index, type, dir, category);
            port.alias = alias;
            inputList.add(port);
        }
        return inputList;
    }

    /*获取所有输出端口信息*/
    public List<Port> getOutputPortList() {
        List<Port> outputList = new ArrayList<>();
        List<Element> elements = root.element("matrix").element("output_list").elements("output");
        for (Element element : elements) {
            int parentIdx = Integer.parseInt(element.element("parent_index").getTextTrim());
            int index = Integer.parseInt(element.element("index").getTextTrim());
            int type = Integer.parseInt(element.element("type").getTextTrim());
            int dir = Integer.parseInt(element.element("dir").getTextTrim());
            int category = Integer.parseInt(element.element("category").getTextTrim());
            String alias = element.element("alias").getTextTrim();
            Port port = new Port(parentIdx, index, type, dir, category);
            port.alias = alias;
            outputList.add(port);
        }
        return outputList;
    }

    /*将配置信息写入外部存储*/
    public void save() {
        try {
            XMLWriter writer = new XMLWriter(new FileOutputStream(configFile), format);
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
