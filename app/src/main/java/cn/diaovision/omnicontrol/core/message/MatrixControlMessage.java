package cn.diaovision.omnicontrol.core.message;

/**
 * Created by liulingfeng on 2017/2/22.
 */

public class MatrixControlMessage {
    byte header[];
    byte tail[];
    byte payload[];

    public MatrixControlMessage createSwitchMessage(int chnIn, int chnOut){
        return new MatrixControlMessage();
    }

    public MatrixControlMessage createNetConfigMessage(String ip, int port){
        return new MatrixControlMessage();
    }
}
