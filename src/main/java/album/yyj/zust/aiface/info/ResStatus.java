package album.yyj.zust.aiface.info;

import java.util.HashMap;
import java.util.Map;

public class ResStatus {
    public final static String SUCCESS = "success";
    public final static String FAIL = "fail";
    public final static String ERROR = "error";
    public final static Map<Integer,String> MsgInfo= new HashMap<>();
    static{
        MsgInfo.put(0,"成功");
        MsgInfo.put(1,"失败");
        MsgInfo.put(3,"改手机号已被注册");
        MsgInfo.put(4,"账户名或密码错误");
        MsgInfo.put(5,"该照片不存在");
        MsgInfo.put(6,"数据库插入失败");
        MsgInfo.put(7,"数据库更新失败");
        MsgInfo.put(8,"数据库删除失败");
    }
    public static String getInfo(Integer errorCode){
        return MsgInfo.get(errorCode);
    }
}
