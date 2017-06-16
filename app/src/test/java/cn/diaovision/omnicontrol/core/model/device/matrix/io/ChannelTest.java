package cn.diaovision.omnicontrol.core.model.device.matrix.io;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by liulingfeng on 2017/6/9.
 */
public class ChannelTest {

    @Test
    public void hashCodeTest() throws Exception {
        int[] outs = new int[2];
        outs[0] =  1;
        outs[1] =  2;
        int in = 1;
        Channel chn1 = new Channel(Channel.CHN_VIDEO, in, outs);

        outs = new int[2];

        outs[0] =  1;
        outs[1] =  2;
        in = 1;
        Channel chn2 = new Channel(Channel.CHN_VIDEO, in, outs);

        assertEquals(chn1.hashCode(), chn2.hashCode());
    }

}