package cn.diaovision.omnicontrol.core.model.device.splicer;

/**
 * Created by TaoYimin on 2017/7/25.
 * 窗口实体类
 */
@Deprecated
public class Windows {
    //窗口id
    private int id;
    //窗口信号源主通道
    private int channel;
    //窗口信号源字通道
    private int subChannel;
    //窗口左上角x轴坐标
    private int x0;
    //窗口左上角y轴坐标
    private int y0;
    //窗口右下角x轴坐标
    private int x1;
    //窗口右下角y轴坐标
    private int y1;

    public Windows(int id, int channel, int subChannel, int x0, int y0, int x1, int y1) {
        this.id = id;
        this.channel = channel;
        this.subChannel = subChannel;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(int subChannel) {
        this.subChannel = subChannel;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getY0() {
        return y0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }
}
