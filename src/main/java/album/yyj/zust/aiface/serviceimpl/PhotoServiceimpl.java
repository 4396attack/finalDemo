package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.repository.PhotoDao;
import album.yyj.zust.aiface.service.PhotoService;
import album.yyj.zust.aiface.tools.FaceTools;
import album.yyj.zust.aiface.tools.OSSImageClient;
import album.yyj.zust.aiface.tools.OSSPathTools;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.Map;

@Service
public class PhotoServiceimpl implements PhotoService {
    private Logger logger = LoggerFactory.getLogger(PhotoServiceimpl.class);
    @Autowired
    private PhotoDao photoDao;
    @Autowired
    private Ossproperties ossproperties;

    @Override
    public Integer uploadPhoto(Photo photo,Map<String,Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        OSSImageClient client = new OSSImageClient(ossproperties.getEndPoint(),ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret());
        if(client.exitObj(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.prePhotoPath(photo.getUserId(),photo.getId()))){
            File temp = client.downloadFile(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.prePhotoPath(photo.getUserId(),photo.getId()));
            logger.info(temp.getAbsolutePath());
            String resStr = FaceTools.getFacePos(ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret(),temp);
            logger.info(resStr);
            JSONObject res = JSONObject.parseObject(resStr);
            Integer faceNum = res.containsKey("face_num") == false ? 0  : res.getInteger("face_num");
            String faceRect = res.containsKey("face_rect") == false ? "[]" : res.getString("face_rect");
            data.put("errno",res.getString("errno"));
            data.put("faceNum",faceNum);
            data.put("faceRect",faceRect);
            temp.delete();
            photo.setNum(faceNum);
            photo.setFaceRect(faceRect);
            photo.setDeleted(0);
            if(photoDao.save(photo) == null){
                error = ErrorCodes.DB_UPDATE_ERR;
            }
        }else{
            error = ErrorCodes.NO_SUCH_PHOTO;
        }
        client.close();
        return  error;
    }

    @Override
    public Integer savePhoto(Photo photo, Map<String,Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        photo.setDeleted(1);
        photo.setUpdateTime(new Date());
        photo = photoDao.save(photo);
        if(photo == null){
            return ErrorCodes.DB_INSERT_ERR;
        }
        data.put("photo",photo);
        return error;
    }


}
