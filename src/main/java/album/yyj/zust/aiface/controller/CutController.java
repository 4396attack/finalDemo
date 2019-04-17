package album.yyj.zust.aiface.controller;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.info.ResObj;
import album.yyj.zust.aiface.info.ResStatus;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.rabbitMQ.FirstSender;
import album.yyj.zust.aiface.service.PhotoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("cutPhoto")
public class CutController {
    @Autowired
    private PhotoFaceService photoFaceService;

    @Autowired
    private FirstSender firstSender;

    /**
     * 当有一张照片上传时，访问这个请求，截取每张图片中的人脸部分 （消息队列异步处理）
     * @return
     */
    @RequestMapping("sendMsg")
    public ResObj sendMsg(@RequestParam Integer photoId){
        Integer error = ErrorCodes.SUCCESS;
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        error = photoFaceService.cutFace(photoId);
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        return resObj;
    }
    /**
     * 通过用户上传的照片，检索此用户上传图片包含此人的所有照片
     */
    @RequestMapping("findMe")
    public ResObj findMe(){
        Integer error = ErrorCodes.SUCCESS;
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
//        error = photoFaceService.cutFace(photoId);
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        return resObj;
    }

    /**
     * 提供给app端的轮询接口，用来判断人脸定位是否完成
     * @return
     */
    @RequestMapping("checkStatus")
    public ResObj checkStatus(@RequestParam Integer photoId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data =  new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        error = photoFaceService.checkPosStatus(photoId,data);
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }

    @RequestMapping("get/all/face/pos")
    public ResObj getAllFacesPos(@RequestParam Integer photoId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data =  new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        error = photoFaceService.getAllFacesPos(photoId,data);
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }
}
