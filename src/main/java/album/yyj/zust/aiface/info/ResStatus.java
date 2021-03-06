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
        MsgInfo.put(1,"用户未登入");
        MsgInfo.put(2,"参数错误");
        MsgInfo.put(3,"改手机号已被注册");
        MsgInfo.put(4,"账户名或密码错误");
        MsgInfo.put(5,"该照片不存在");
        MsgInfo.put(6,"数据库插入失败");
        MsgInfo.put(7,"数据库更新失败");
        MsgInfo.put(8,"数据库删除失败");
        MsgInfo.put(9,"数据库中无此记录");
        MsgInfo.put(10,"未检测出人脸信息");
        MsgInfo.put(11,"用户还未上传过图片");
        MsgInfo.put(20,"rabbitMQ发送消息失败");
        MsgInfo.put(21,"获取OSS权限失败");
        MsgInfo.put(22,"redis中无此key");
        MsgInfo.put(30,"还未上传对照参数");
    }
    public static String getInfo(Integer errorCode){
        return MsgInfo.get(errorCode);
    }
}
