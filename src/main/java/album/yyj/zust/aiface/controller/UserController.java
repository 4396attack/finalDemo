package album.yyj.zust.aiface.controller;

import album.yyj.zust.aiface.info.ResObj;
import album.yyj.zust.aiface.info.ResStatus;
import album.yyj.zust.aiface.pojo.User;
import album.yyj.zust.aiface.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/createUser")
    public ResObj createUser(@ModelAttribute User user){
        Integer error = 0;
        ResObj resObj = new ResObj(ResStatus.FAIL,0,null);
        error = userService.createUser(user);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(user);
        return resObj;
    }
    @PostMapping("/login")
    public ResObj userLogin(@RequestParam("phone")String phone,@RequestParam("pwd")String pwd){
        Integer error = 0;
        ResObj resObj = new ResObj(ResStatus.FAIL,0,null);
        Map<String,Object> data = new HashMap<>();
        error = userService.findUserByPhone(phone,pwd,data);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data.get("user"));
        resObj.setCode(error);
        return resObj;
    }
}

