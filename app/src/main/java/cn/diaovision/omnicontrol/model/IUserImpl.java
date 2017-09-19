package cn.diaovision.omnicontrol.model;

import android.os.Environment;

/**
 * Created by TaoYimin on 2017/4/24.
 */

public class IUserImpl implements IUser{

    @Override
    public void login(String name, String password, UserLoginListener listener) {
        //登陆业务
        Config cfg= ConfigXXX.fromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Omnicontrol/config/config.xml");
        String userName=cfg.getMainName();
        String userPassword=cfg.getMainPasswd();
        if(userName.equals(name)&&userPassword.equals(password)){
            listener.success(new User());
        }else{
            listener.fail("用户名或密码错误！");
        }
    }
}
