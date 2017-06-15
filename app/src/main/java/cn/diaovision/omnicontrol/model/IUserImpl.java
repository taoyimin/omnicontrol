package cn.diaovision.omnicontrol.model;

/**
 * Created by TaoYimin on 2017/4/24.
 */

public class IUserImpl implements IUser{

    @Override
    public void login(String name, String password, UserLoginListener listener) {
        //登陆业务
        if("admin".equals(name)&&"123456".equals(password)){
            listener.success(new User());
        }else{
            listener.fail("用户名或密码错误！");
        }
    }
}
