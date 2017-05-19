package cn.diaovision.omnicontrol.model;

import org.xml.sax.SAXException;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;

/**
 * Created by liulingfeng on 2017/5/8.
 */

public class ConfigXml implements Config{

    String matrixIp;
    int matrixPort;
    String matrixAlias;
    int matrixPortInNum;
    int matrixPortOutNum;
    List<HiCamera> matrixCameraList;
    List<Channel> matrixChannelList;

    private ConfigXml(String xmlFile){
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = parserFactory.newSAXParser();
//            parser.parse(is, );
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    public static ConfigXml fromFile(String xmlFile){
        return new ConfigXml(xmlFile);
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
        return null;
    }

    @Override
    public int getMcuPort() {
        return 0;
    }

    @Override
    public int getMatrixId() {
        return 0;
    }

    @Override
    public String getMatrixIp() {
        return null;
    }

    @Override
    public int getMatrixUdpIpPort() {
        return 0;
    }

    @Override
    public String getMatrixPreviewIp() {
        return null;
    }

    @Override
    public int getMtatrixPreviewIpPort() {
        return 0;
    }

    @Override
    public int getMatrixPreviewPort() {
        return 0;
    }

    @Override
    public Map<Integer,HiCamera> getHiCameraInfo() {
        return null;
    }

    @Override
    public int getMatrixInputVideoNum() {
        return 0;
    }

    @Override
    public int getMatrixOutputVideoNum() {
        return 0;
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
    public Date getConfStartDate() {
        return null;
    }

    @Override
    public Date getConfEndDate() {
        return null;
    }
}
