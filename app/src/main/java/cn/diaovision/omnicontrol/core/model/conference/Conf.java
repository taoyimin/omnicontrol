package cn.diaovision.omnicontrol.core.model.conference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxSubscriber;

/* Live meeting bean*
 * Created by liulingfeng on 2017/4/5.
 */

public class Conf {
    byte confFlag; //会议召开标志
    byte speech; //MCU点名发言，1：点名发言，0：取消发言
    byte mixAudio;
    byte mixVideo;
    byte mixPic;

    ConfConfigMessage configTemplate;
    ConfConfigMessage config;

    //application specific
    List<Term> termList;
    Term chair;
    Term selectView;


    /*召开会议*/
    public void create(Date start, Date stop){
    }

    /*邀请终端参见会议*/
    public void invite(Term term){
    }

    /*踢出终端*/
    public void hangup(Term term){
    }


    /*静音*/
    public void mute(Term term){
        if (!term.isMuted){
            //TODO: unmute term
        }
    }

    /*取消静音*/
    public void unmute(Term term){
        if (term.isMuted){
            //TODO: unmute term
        }
    }

    /*设置主席*/
    public void chair(Term term){
        if (term.type != Term.TYPE_CHAIR){
            this.chair = term;
        }
    }

    /*设置选看*/
    public void selectview(Term term){
        if (term.type!=Term.TYPE_SELECTVIEW){
        }
    }

    public interface ConfListener{
        void onTermInvited(Term term);
        void onMcuConnectionChanged(int state);
    }

//    /*conference configs*/
//    static public class Config{
//        String name; //32 bytes
//        String descrip; //64 bytes
//        int id; //2 bytes
//        byte type; //1 byte 会议类型: 0=H323; 1= H323&T120; 2=T120; 3=混合会议
//        int audioMode; //2 bytes
//        int videoMode; //2 bytes
//        byte mode; //1 byte 会议组织模式: 0,主席模式1,导演模式 2,语音模式3,自动浏览方式4自动选看方式
//        byte maxTermNum; //1 byte
//        byte termNum; //1 byte
//        byte status; //1 byte 会议状态: 5=正在召开 其它= 准备召开
//
//        int startYear; //2 bytes
//        byte startMonth; //1 byte
//        byte startDay; //1 byte
//        byte startHour; //1 byte
//        byte startMin; //1 byte
//
//        int endYear; //2 bytes
//        byte endMonth; //1 byte
//        byte endDay; //1 byte
//        byte endHour; //1 byte
//        byte endMin; //1 byte
//
//        String creatorName; //32 bytes
//
//        int bandwidth; //2 bytes 会议带宽 100bps为单位
//        byte fps; //1 byte 每秒帧率
//
//        byte mixMode; //1 byte 媒体混合 0--不混; 1--混音; 2--混图像; 3--混音+混图像
//        long streamAddr; // 4 bytes to ip address 流媒体服务器地址
//        int streamAudioPort; //2 bytes, 流媒体音频端口
//        int streamVideoPort; //2 bytes, 流媒体视频端口
//
//        long autoTime; //4 bytes 自动浏览时间
//        byte termNameFlag; //1 byte 终端名字显示方式 0:使用终端传送的名字 1:使用地址簿的名字
//
//        byte autoInvite; //1 byte 自动重邀请
//
//        byte selectViewSync; //1 byte 选看同步标志:1:选看端同步于广播端标志 0:选看端不同步于广播端
//
//        byte autoOperator; //1 byte, 0:操作员不自动选看 1:操作员自动选看
//        long autoOperatorTime; //4 bytes, 操作员自动选看时间长
//
//        long calloverTermId; //4 bytes, 点名发言终端id（ip地址）
//        byte operatorMode; //1 byte 0:正常 1:前台操作员 2：后台操作员
//
//        byte continuousMode; //1 byte, 四画面模式0:普通四画面1:主席四画面
//
//        int termAttrNum; //2 bytes, termattr数量
//        Term.Attr[] termAttrs; //maximal 256
//
//        //dual stream attrs
//        byte dsVideoMode; //1 byte, 双流媒体格式：0:动态双流;1:高清双流;other:DisableDuoVideo
//        byte dsImageMode; //1 byte, 双流图像格式，同前
//        int dsBandwidth; //2 bytes, same as previous bandwidth
//        byte dsStatus; //1 byte, 双流状态，1：已启动，0：停止，
//        byte dsReserved; //1 byte, 保留字节
//
//        int[] BandwithMulti;	//会议的三种带宽
//    }
}
