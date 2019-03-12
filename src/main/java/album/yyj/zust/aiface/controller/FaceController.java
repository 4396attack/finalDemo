package album.yyj.zust.aiface.controller;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.info.ResObj;
import album.yyj.zust.aiface.info.ResStatus;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.service.PhotoService;
import album.yyj.zust.aiface.tools.OSSImageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("aiface")
public class FaceController {
    private Logger logger = LoggerFactory.getLogger(FaceController.class);
    @Autowired
    private Ossproperties ossproperties;
    @Autowired
    private PhotoService photoService;

    @RequestMapping("uploadPhoto")
    public ResObj uploadPhoto(@RequestParam("fileName")String fileName){
        Integer error = ErrorCodes.SUCCESS;
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        OSSImageClient client = new OSSImageClient();
        if(true){
            client.upload(ossproperties.getBucketFace(),"test",fileName);
            client.close();
            resObj.setMsg(ResStatus.SUCCESS);
        }
        return resObj;
    }
    @RequestMapping("preUpload")
    public ResObj preUpload(@ModelAttribute Photo photo){
        Integer error = ErrorCodes.SUCCESS;
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        Map<String,Object> data = new HashMap<>();
        error = photoService.savePhoto(photo,data);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(photo);
        resObj.setCode(error);
        return resObj;
    }
    @RequestMapping("checkUpload")
    public ResObj checkUpload(@ModelAttribute Photo photo){
        Integer error = ErrorCodes.SUCCESS;
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        Map<String,Object> data = new HashMap<>();
        error = photoService.uploadPhoto(photo,data);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        resObj.setCode(error);
        return resObj;
    }
    @RequestMapping("test")
    public String test(){
        return ossproperties.toString();
    }
}
