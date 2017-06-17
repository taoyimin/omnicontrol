package cn.diaovision.omnicontrol.core.model.device.matrix;

import org.junit.Test;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;

import static org.junit.Assert.*;

/**
 * Created by liulingfeng on 2017/6/9.
 */
public class MediaMatrixTest {
    @Test
    public void updateChannelTest() throws Exception {
        MediaMatrix matrix = new MediaMatrix();
        int in = 1;
        int[] outs = {1, 2};
        Channel chn = new Channel(Channel.CHN_VIDEO, in, outs);
        matrix.videoChnSet.add(chn);

        in = 2;
        outs = new int[2];
        outs[0] = 1;
        outs[1] = 2;
        Channel chnUpdate = new Channel(Channel.CHN_VIDEO, in, outs);

        matrix.updateChannel(in, outs, Channel.MOD_NORMAL);
        for (Channel c : matrix.videoChnSet){
            assertNotEquals(chn.hashCode(), c.hashCode());
        }
    }

    @Test
    public void linkedListTest() throws Exception {
    }
}