package album.yyj.zust.aiface.service;

import album.yyj.zust.aiface.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    public Integer createUser(User user);

    public Integer findUserByPhone(String phone,String pwd, Map<String,Object> data);
}
