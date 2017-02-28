package cn.diaovision.omnicontrol.core.protocol.camera;

/**
 * Created by liulingfeng on 2017/2/28.
 */

public interface CameralController {
    void goDown(int offset);
    void goUp(int offset);
    void goLeft(int offset);
    void goRight(int offset);
    void zoomIn(int offset);
    void zoomOut(int offset);
    void pull(int offset);
    void push(int offset);
}
