package cn.diaovision.omnicontrol.rx;

/**
 * Created by liulingfeng on 2017/3/29.
 */

public class RxMessage {
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
}
