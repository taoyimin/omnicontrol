package cn.diaovision.omnicontrol.rx;

/**
 * Created by liulingfeng on 2017/3/29.
 */

public class RxMessage {
    //TODO: add what type for RxMessage
    public static final String DONE = "DONE"; //request is done
    public static final String ACK = "ACK"; //ack returned
    public static final String CONNECTED = "CONNECTED"; //connected
    public static final String DISCONNECTED = "DISCONNECTED"; //connected

    public String what;
    public String str;
    public int intVal;
    public float floatVal;
    public double doubleVal;

    public Object val;

    public RxMessage(String what, Object val) {
        this.what = what;
        this.val = val;
    }

    public RxMessage(String what){
        this.what = what;
    }

}
