package album.yyj.zust.aiface.controller;

import album.yyj.zust.aiface.info.ResObj;
import album.yyj.zust.aiface.info.ResStatus;
import album.yyj.zust.aiface.serviceimpl.AliOSSService;
import com.aliyun.oss.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: 杨玉杰
 * @Date: 2019/4/10 10:47
 * @Description:
 * 为Android端获取OSS直传权限
 */

@RestController
@RequestMapping("policyOSS")
public class OSSController {
    @Autowired
    private AliOSSService aliOSSService;
    @RequestMapping(value="/getALiYunOSSToken", method = RequestMethod.GET)
    public ResObj getALiYunOSSToken(@RequestParam(value = "tokenName", required = true)  String tokenName){
        ResObj resObj = new ResObj(ResStatus.FAIL,0,null);
//        if (StringUtils.isBlank(cardNumber) || StringUtils.isBlank(tokenName)) {
//            return new BaseVO<Map<String, String>>(2, "cardNumber或tokenName不能为空！");
//        }
            Map<String,Object> data = new HashMap<>();
            // 获取临时授权token
        Integer error = aliOSSService.assumeRole(tokenName, data);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        resObj.setCode(error);
        return resObj;
    }
}
