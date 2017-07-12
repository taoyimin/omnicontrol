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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.OmniControlApplication;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

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

    public static ConfigXXX fromFile(String xmlFile) {
        return new ConfigXXX(xmlFile);
    }

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
                try {
                    XMLWriter writer = new XMLWriter(new FileOutputStream(configFile), format);
                    writer.write(document);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

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
        try {
            XMLWriter writer = new XMLWriter(new FileOutputStream(configFile), format);
            writer.write(document);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMainName() {
        return null;
    }

    @Override
    public String getMainPasswd() {
        return null;
    }

    @Override
    public String getConfName() {
        return null;
    }

    @Override
    public String getConfPasswd() {
        return null;
    }

    @Override
    public String getMcuIp() {
        return root.element("mcu").element("ip").getTextTrim();
    }

    @Override
    public int getMcuPort() {
        return Integer.parseInt(root.element("mcu").element("port_config").getTextTrim());
    }

    @Override
    public int getMatrixId() {
        return Integer.parseInt(root.element("matrix").attributeValue("id").trim());
    }

    @Override
    public String getMatrixIp() {
        return root.element("matrix").element("ip").getTextTrim();
    }

    @Override
    public int getMatrixUdpIpPort() {
        return Integer.parseInt(root.element("matrix").element("port").getTextTrim());
    }

    @Override
    public String getMatrixPreviewIp() {
        return root.element("matrix").element("preview_video").element("ip").getTextTrim();
    }

    @Override
    public int getMatrixPreviewIpPort() {
        return Integer.parseInt(root.element("matrix").element("preview_video").element("ip_port").getTextTrim());
    }

    @Override
    public int getMatrixPreviewPort() {
        return Integer.parseInt(root.element("matrix").element("preview_video").element("port").getTextTrim());
    }

    @Override
    public int getMatrixInputVideoNum() {
        return Integer.parseInt(root.element("matrix").element("input_number").getTextTrim());
    }

    @Override
    public int getMatrixOutputVideoNum() {
        return Integer.parseInt(root.element("matrix").element("output_number").getTextTrim());
    }

    @Override
    public byte getSubtitleFontSize() {
        return 0;
    }

    @Override
    public byte getSubtitleFontColor() {
        return 0;
    }

    @Override
    public Map<Integer, HiCamera> getMatrixCameras() {
        Map<Integer, HiCamera> cameras = new HashMap();
        List<Element> elements = root.element("matrix").element("camera_list").elements("camera");
        for (Element element : elements) {
            int port = Integer.parseInt(element.element("port").getTextTrim());
            int index = Integer.parseInt(element.element("index").getTextTrim());
            int baudrate = Integer.parseInt(element.element("baudrate").getTextTrim());
            int proto;
            switch (element.element("proctocol").getTextTrim()) {
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
            HiCamera hiCamera = new HiCamera(port, index, baudrate, proto);
            cameras.put(port, hiCamera);
        }
        return cameras;
    }

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

    @Override
    public Date getConfStartDate() {
        return null;
    }

    @Override
    public Date getConfEndDate() {
        return null;
    }

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
}
