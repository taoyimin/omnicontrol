package cn.diaovision.omnicontrol;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liulingfeng on 2017/4/17.
 */
public abstract class BaseCyclicThread extends Thread{
    AtomicBoolean isRunning = new AtomicBoolean(true);

    public abstract void work();

    public boolean isRunning() {
        return isRunning.get();
    }

    public void setIsRunning(boolean b) {
        this.isRunning.set(b);
    }

    public void quit(){
        isRunning.set(false);
        this.interrupt();
    }

    @Override
    public void run() {
        while(true){
            if (!isRunning.get()) {
                break;
            }

            work();
        }
    }
}
