package cn.diaovision.omnicontrol.rx;

/**
 * Created by liulingfeng on 2017/3/29.
 */

public class RxMessage {
    //TODO: add what type for RxMessage
    public static final int DONE = 1; //request is done
    public static final int ACK = 2; //ack returned

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
