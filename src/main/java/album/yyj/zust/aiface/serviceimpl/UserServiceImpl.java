package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.User;
import album.yyj.zust.aiface.repository.UserDao;
import album.yyj.zust.aiface.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public Integer createUser(User user) {
        Integer errorCode = 0;
        User checkUser = userDao.findUserByPhone(user.getPhone());
        if(checkUser != null){//手机号已经被注册
            return ErrorCodes.ALREADYEXIT;
        }
        user = userDao.save(user);
        if (user == null){
            errorCode = 1;
        }
        return errorCode;
    }

    @Override
    public Integer findUserByPhone(String phone,String pwd, Map<String,Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        User user = userDao.findUserByPhone(phone);
        if(user == null || !pwd.equals(user.getPwd())){
            return ErrorCodes.NO_SUCH_USER;
        }
        data.put("user",user);
        return error;
    }
}
