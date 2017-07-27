package cn.diaovision.omnicontrol.core.model.device.splicer;

/**
 * Created by TaoYimin on 2017/7/27.
 */

public class Scene {
    private int sum;
    private int index;
    private int id;
    private String name;

    public Scene(int sum, int index, int id, String name) {
        this.sum = sum;
        this.index = index;
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
