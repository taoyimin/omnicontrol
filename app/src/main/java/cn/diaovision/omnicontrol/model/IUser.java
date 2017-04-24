package cn.diaovision.omnicontrol.model;

/**
 * Created by TaoYimin on 2017/4/24.
 */

public interface IUser {
    void login(String name, String password, UserLoginListener listener);

    interface UserLoginListener {
        void success(User user);

        void fail(String e);
    }
}
