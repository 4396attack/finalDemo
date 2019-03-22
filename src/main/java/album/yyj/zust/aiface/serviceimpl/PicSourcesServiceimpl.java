package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.*;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.repository.*;
import album.yyj.zust.aiface.service.PicSourceService;
import album.yyj.zust.aiface.tools.*;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PicSourcesServiceimpl implements PicSourceService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PicSourceDao picSourceDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private Ossproperties ossproperties;

    @Autowired
    private PhotoDao photoDao;

    @Autowired
    private PhotoFaceDao photoFaceDao;

    @Autowired
    private FindRecordsDao findRecordsDao;

    @Override
    public Integer findMePre(Integer userId, Map<String, Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        if(!StringTools.checkPram(userId)){
            return ErrorCodes.INVALID_PARAM;
        }
        PicSource picSource = new PicSource(userId);
        picSource = picSourceDao.save(picSource);
        if(picSource.getId() == null){
            return ErrorCodes.DB_INSERT_ERR;
        }
        data.put("id",picSource.getId());
        data.put("path", OSSPathTools.getSourcePath(userId,picSource.getId()));
        data.put("obj",picSource);
        return error;
    }

    @Override
    public Integer checkSourceUpload(Integer userId, Integer sourceId, Map<String, Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        User user = userDao.findUndeletedUserById(userId);
        if (user !=null){
            PicSource picSource = picSourceDao.findByIdAndUserId(sourceId,userId);
            if (picSource == null){
                error = ErrorCodes.DB_NO_RECORD;
            }else {
                OSSImageClient client = new OSSImageClient(ossproperties.getEndPoint(),ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret());
                if (client.exitObj(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.getSourcePath(userId,sourceId))){
                    picSource.setDeleted(0);
                    picSource = picSourceDao.save(picSource);
                    if (!new Integer(0).equals(picSource.getDeleted())){
                        error = ErrorCodes.DB_UPDATE_ERR;
                    }

                }else {
                    error = ErrorCodes.NO_SUCH_PIC_SOURCE;
                }
                client.close();
            }
        }else{
            error = ErrorCodes.NO_SUCH_USER;
        }
        return error;
    }

    @Override
    public Integer startSearchAll(Integer userId, Integer sourceId, Map<String, Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        User user = userDao.findUndeletedUserById(userId);
        if(user != null){
            List<Photo> photos = photoDao.findUndeletedsPhotosByUserId(userId);
            if(photos.size()>0){
                for (Photo p : photos){
                    faceCheck(p,userId,sourceId,data);
                }
            }else{
                error = ErrorCodes.NO_EVEN_UPLOAD_ANY_PHOTO;
            }
        }else {
            error = ErrorCodes.NOT_LOGIN;
        }
        return error;
    }

    /**
     * 对图片中解析出来的人脸一次进行比对
     * 置信度大于85认为是同一个人
     */
    private void faceCheck(Photo photo,Integer userId,Integer sourceId,Map<String, Object> data){
        List<PhotoFace> faces = photoFaceDao.findFacesBelongPhoto(photo.getId());
        OSSImageClient client = new OSSImageClient(ossproperties.getEndPoint(),ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret());
        PicSource picSource = picSourceDao.findUndeletedRecordById(sourceId);
        List<FindRecords> records = new ArrayList<>();
        for(PhotoFace face : faces){
//            logger.info("---------------" + face.getId());
//            logger.info(OSSPathTools.getFaceDetailOSSPath(userId, photo.getId(), face.getId()));
//            logger.info(OSSPathTools.getSourcePath(picSource.getUserId(), sourceId));
            if(client.exitObj(OSSPathTools.DETAIL_BUCKET, OSSPathTools.getFaceDetailOSSPath(userId, photo.getId(), face.getId())) &&
                    client.exitObj(OSSPathTools.ORIGIN_BUCKET, OSSPathTools.getSourcePath(picSource.getUserId(), sourceId))){
                File faceImg = client.downloadFile(OSSPathTools.DETAIL_BUCKET, OSSPathTools.getFaceDetailOSSPath(userId, photo.getId(), face.getId()));
                File sourceImg = client.downloadFile(OSSPathTools.ORIGIN_BUCKET, OSSPathTools.getSourcePath(picSource.getUserId(), sourceId));
                Float confidence = isFaceSame(sourceImg, faceImg);
                if(85 < confidence){
//                    logger.info("****************************");
                    FindRecords findRecords = new FindRecords(userId,photo.getId(),sourceId,confidence,face.getId());
                    FindRecords save = findRecordsDao.save(findRecords);
                    if(save.getId() == null){
                        logger.info("更新数据库失败！");
                    }else {
                        records.add(save);
                    }
                    break;//一般来说 一张照片中只会存在一张自己的人脸，所以检测出来之后就不再往下继续分析
                }
            }

        }
        data.put("obj",records);
    }

    /**
     * 进行人脸校验
     *
     */
    private Float isFaceSame(File sourceImg,File faceImg){
        Float conf = new Float(0.0);
        String resp = FaceTools.sendFaceCheckByContent(ossproperties.getAccessKeyId(), ossproperties.getAccessKeySecret(), sourceImg, faceImg);
        logger.info(resp);
        JSONObject info = JSONObject.parseObject(resp);
        Integer errno = info.getInteger("errno");
        String errMsg = "";
        if(info.containsKey("err_msg")){
            errMsg = info.getString("err_msg");
        }
        if(new Integer(0).equals(errno)){//请求成功
            conf = info.getFloatValue("confidence");

        }else {
            logger.info("人脸对比请求失败，具体原因：" + errMsg );
        }
        return  conf;
    }
}
