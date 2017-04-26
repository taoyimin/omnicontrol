package cn.diaovision.omnicontrol.rx;

import java.io.IOException;

import io.reactivex.functions.Function;

/**
 * Created by liulingfeng on 2017/4/25.
 */

@Deprecated
public abstract  class RxThen<T, R> implements Function<T, R> {
    public abstract R then(T t) throws Exception;

    @Override
    public R apply(T t) throws Exception {
        return then(t);
    }
}
