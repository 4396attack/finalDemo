package album.yyj.zust.aiface.controller;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.info.ResObj;
import album.yyj.zust.aiface.info.ResStatus;
import album.yyj.zust.aiface.service.PicSourceService;
import album.yyj.zust.aiface.tools.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("find")
public class FindController {
    @Autowired
    private PicSourceService picSourceService;

    @RequestMapping("choose/pic/source")
    public ResObj choosePicSource(Integer userId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data = new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        if(!StringTools.checkPram(userId)){
            error = ErrorCodes.NOT_LOGIN;
        }else{
            error = picSourceService.findMePre(userId,data);
        }
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }
    @RequestMapping("check/source/upload")
    public ResObj checkSourceUpload(@RequestParam Integer sourceId,Integer userId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data = new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        if(!StringTools.checkPram(userId)){
            error = ErrorCodes.NOT_LOGIN;
        }else{
            error = picSourceService.checkSourceUpload(userId,sourceId,data);
        }
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }

    /**
     * 根据用户上传的参照人脸，检索该用户目录下所有包含此人脸的照片
     * @param userId
     * @param sourceId
     * @return
     */
    @RequestMapping("start/search/all")
    public ResObj startSearch(Integer userId,Integer sourceId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data = new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        if(!StringTools.checkPram(userId)){
            error = ErrorCodes.NOT_LOGIN;
        }else{
            error = picSourceService.startSearchAll(userId,sourceId,data);
        }
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }
    /**
     * 检测图片匹配是否完成
     */
    @RequestMapping("check/mache/status")
    public ResObj checkMacheStatus(Integer sourceId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data = new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        error = picSourceService.checkMacheStatus(sourceId,data);
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }
    /**
     * 获取所有检测出的图片
     */
    @RequestMapping("get/all/mache/photos")
    public ResObj getAllMachePhotos(Integer sourceId,Integer userId){
        Integer error = ErrorCodes.SUCCESS;
        Map<String,Object> data = new HashMap<>();
        ResObj resObj = new ResObj(ResStatus.FAIL,error,null);
        error = picSourceService.getAllMachePhotos(sourceId,userId,data);
        resObj.setCode(error);
        resObj.setMsg(ResStatus.getInfo(error));
        resObj.setObj(data);
        return resObj;
    }
}
