package cn.diaovision.omnicontrol.util;

/**
 * A ring buff supportting block bytes read / write
 * Created by liulingfeng on 2017/2/28.
 */

public class ByteBuffer {
    private static final int DEFAULT_SIZE = 512;
    byte buff[];
    int h;
    int t;
    int c;
    int rw;
    boolean overflow;
    volatile int contentLen;

    public ByteBuffer(){
        buff = new byte[DEFAULT_SIZE];
        reset();
    }

    public ByteBuffer(int len){
        buff = new byte[DEFAULT_SIZE];
        reset();
    }

    public void push(byte buff[], int len){
        len = buff.length > len ? len : buff.length;
        for (int m = 0; m < len; m ++){
            buff[c] = buff[m];
            c++;
            if (c>t && !overflow){
                c = h;
                overflow = true;
            }
            else if (c > t && overflow){
                c = h;
                if (rw == t){
                    rw = c;
                }
            }

            //push the p_rw so p_c < = p_rw and is overflowed
            if (c > rw && overflow)
            {
                rw = c;
            }
        }

        contentLen = getContentLen();
    }

    public void read(byte buff[], int len){
        if (!(contentLen < len || len == 0)) {
            for (int m = 0; m < len; m ++){
                if (rw + m <= t){
                    buff[m] = this.buff[rw + m];
                }
                else {
                    buff[m] = this.buff[h + m - (t - rw) - 1];
                }
            }
        }
    }

    public void pop(byte buff[], int len){
        if (contentLen > len){
            if (buff != null){
                for (int m = 0; m < len; m ++){
                    buff[m] = this.buff[rw];
                    rw ++;
                    if (rw > t){
                        rw = h;
                        overflow = false;
                    }
                }
            }
        }
        else {
            flush();
        }

        contentLen = contentLen > len ? contentLen - len : 0;
    }

    /*clear the whole buffer and reset its state*/
    public void flush(){
        buff = new byte[buff.length];
        reset();
    }

    /*reset the buffer*/
    public void reset(){
        h = 0;
        t = buff.length - 1;
        c = 0;
        rw = 0;
        overflow = false;
        contentLen = 0;
    }

    public int getContentLen(){
        if (overflow){
            return t - rw + c - h +1;
        }
        else {
            return c - rw;
        }
    }
}
