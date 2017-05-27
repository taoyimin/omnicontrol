package cn.diaovision.omnicontrol.core.message.conference;

/**
 * MCU定义的登陆操作用户类
 * Created by liulingfeng on 2017/4/13.
 */

public class UserMessage implements BaseMessage{
    public static final byte LOGIN = 0;
    public static final byte ADDUSER = 1; //添加用户
    public static final byte CHANGEPW = 2; //更改密码
    public static final byte DELUSER = 3; //删除用户
    public static final byte GET_OBJECT_LIST = 4; //得到用户对象列表(会议，模板，终端)

    byte type;
    String name;
    String passwd;

    public UserMessage(){
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[65];
        bytes[0] = type;
        if (name != null){
            System.arraycopy(name.getBytes(), 0, bytes, 1, name.getBytes().length > 32 ? 32: name.getBytes().length);
        }
        if (name != null){
            System.arraycopy(passwd.getBytes(), 0, bytes, 33, passwd.getBytes().length > 32 ? 32: passwd.getBytes().length);
        }
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 65;
    }
}
