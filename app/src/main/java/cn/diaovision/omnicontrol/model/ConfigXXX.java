package cn.diaovision.omnicontrol.model;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;

/**
 * Created by liulingfeng on 2017/5/8.
 */

public class ConfigXXX implements Config{
    Document document;
    Element root;

    String matrixIp;
    int matrixPort;
    String matrixAlias;
    int matrixPortInNum;
    int matrixPortOutNum;
    List<HiCamera> matrixCameraList;
    List<Channel> matrixChannelList;

    private ConfigXXX(String xmlFile){
/*        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = parserFactory.newSAXParser();
//            parser.parse(is, );
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }*/

        SAXReader reader = new SAXReader();
        try {
            document = reader.read(xmlFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        root = document.getRootElement();
    }

    public static ConfigXXX fromFile(String xmlFile){
        return new ConfigXXX(xmlFile);
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
    public String getMcuId() {
        return null;
    }

    @Override
    public String getMcuKey() {
        return null;
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
        return Integer.parseInt(root.element("matrix").element("preview_video").element("port").getTextTrim());
    }

    @Override
    public int getMatrixPreviewPort() {
        return 0;
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
        Map<Integer,HiCamera> cameras=new HashMap();
        List<Element> elements=root.element("matrix").element("camera_list").elements("camera");
        for(Element element:elements){
            int port=Integer.parseInt(element.element("port").getTextTrim());
            int index=Integer.parseInt(element.element("index").getTextTrim());
            int baudrate=Integer.parseInt(element.element("baudrate").getTextTrim());
            int proto;
            switch (element.element("proctocol").getTextTrim()){
                case "PELCO-D":
                    proto=HiCamera.PROTO_FELICA_D;
                    break;
                case "PELCO-P":
                    proto=HiCamera.PROTO_FELICA_A;
                    break;
                case "VISCA":
                    proto=HiCamera.PROTO_PILSA;
                    break;
                default:
                    proto=HiCamera.PROTO_FELICA_D;
                    break;
            }
            HiCamera hiCamera=new HiCamera(port,index,baudrate,proto);
            cameras.put(port,hiCamera);
        }
        return cameras;
    }

    @Override
    public Set<Channel> getMatrixChannels() {
        Set<Channel> channels=new HashSet<>();
        List<Element> elements=root.element("matrix").element("channel_list").elements("channel");
        for(Element element:elements){
            int in=Integer.parseInt(element.element("input").getTextTrim());
            List<Element> outPutElements=element.element("output_list").elements("output");
            int[] outs=new int[outPutElements.size()];
            for(int i=0;i<outPutElements.size();i++){
                outs[i]=Integer.parseInt(outPutElements.get(i).getTextTrim());
            }
            Channel channel=new Channel(Channel.CHN_VIDEO,in,outs);
            int mode;
            switch (element.attributeValue("mode")){
                case "normal":
                    mode=Channel.MOD_NORMAL;
                    break;
                case "stitch":
                    mode=Channel.MOD_STITCH;
                    break;
                default:
                    mode=Channel.MOD_NORMAL;
                    break;
            }
            channel.mode=mode;
            String alias=element.attributeValue("name");
            if(!alias.isEmpty()){
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
}
