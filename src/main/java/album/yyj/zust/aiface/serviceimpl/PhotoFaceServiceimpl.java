package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.pojo.PhotoFace;
import album.yyj.zust.aiface.rabbitMQ.FirstSender;
import album.yyj.zust.aiface.repository.PhotoDao;
import album.yyj.zust.aiface.repository.PhotoFaceDao;
import album.yyj.zust.aiface.service.PhotoFaceService;
import album.yyj.zust.aiface.tools.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoFaceServiceimpl implements PhotoFaceService {
    Logger logger = LoggerFactory.getLogger(PhotoServiceimpl.class);

    @Autowired
    private PhotoFaceDao photoFaceDao;

    @Autowired
    private FirstSender firstSender;

    @Autowired
    private PhotoDao photoDao;
    /**
     * 根据人脸识别解析出的坐标信息，存入消息队列，并在数据库中做好记录
     * @param
     * @return
     */
    @Override
    public Integer cutFace(@RequestParam Integer photoId) {
        Integer error = ErrorCodes.SUCCESS;
        Photo photo = photoDao.findUndeletedPhotoById(photoId);
        if(photo == null){
            return ErrorCodes.NO_SUCH_PHOTO;
        }
        List<PhotoFace> faces = photoFaceDao.findAllFacesByPhotoId(photoId);
        if(faces.size() > 0){
            logger.info("此前已经插入过，无需再次添加");
            StringBuilder idStr = new StringBuilder();
            for(PhotoFace pf : faces){
                idStr.append(pf.getId());
            }
            String str = idStr.toString();
            str = str.substring(0,str.length()-1);//将最后一个，去掉 str就是本照片中含有的人脸记录id
            String message = "{\"photoId\" : \""+ photo.getId()+"\" , \"userId\" : \""+photo.getUserId()+"\" , \"faceIds\" : \""+str+"\"}";
            String uuid = UUID.randomUUID().toString();
            try {
                firstSender.send(uuid, message);
                logger.info("成功发送消息到消息队列中，内容为: " + message );
            }catch (Exception e){
                logger.info("尝试发送消息失败，内容为 ： " + message + " ; 错误原因 ：" +e);
                error = ErrorCodes.RABBIT_MSG_SEND_FAIL;
            }
            return error;
        }
        String ract = photo.getFaceRect();
        if(ract != null){//检测出人脸信息
            logger.info("图片（id = "+photo.getId()+"）中人脸坐标信息为： "  + ract);
            List<String> ractInfo = StringTools.getRactInfo(ract);
            if(ractInfo.size() == 0){//未检测出人脸信息
                error = ErrorCodes.NO_FACE_INFO;
            }else{
                StringBuilder ids = new StringBuilder();
                for(String s : ractInfo){//将每一张人脸信息解析出来
                    String[] params = s.split(",");
                    PhotoFace photoFace = new PhotoFace(photo.getId(),photo.getUserId());
                    photoFace.setPointX(params[0]);
                    photoFace.setPointY(params[1]);
                    photoFace.setWidth(params[2]);
                    photoFace.setHeight(params[3]);
                    photoFace = photoFaceDao.save(photoFace);
                    if(photoFace == null){
                        return ErrorCodes.DB_INSERT_ERR;
                    }
                    ids.append(photoFace.getId());
                    ids.append(",");
                }
                String str = ids.toString();
                str = str.substring(0,str.length()-1);//将最后一个，去掉 str就是本照片中含有的人脸记录id
                String message = "{\"photoId\" : \""+ photo.getId()+"\" , \"userId\" : \""+photo.getUserId()+"\" , \"faceIds\" : \""+str+"\"}";
                String uuid = UUID.randomUUID().toString();
                try {
                    firstSender.send(uuid, message);
                    logger.info("成功发送消息到消息队列中，内容为: " + message );
                }catch (Exception e){
                    logger.info("尝试发送消息失败，内容为 ： " + message + " ; 错误原因 ：" +e);
                    error = ErrorCodes.RABBIT_MSG_SEND_FAIL;
                }
            }
        }

        return error;
    }

    @Override
    public List<PhotoFace> findAllFaceByPhotoIdAndFaceIds(String photoId, String[] ids) {
        List<PhotoFace> result = new ArrayList<>();
        Integer pId = Integer.valueOf(photoId);
        for(String s : ids){
            Integer id = Integer.valueOf(s);
            PhotoFace pf = photoFaceDao.findUncutPhotoFace(id,pId);
            if (pf != null){
                result.add(pf);
            }
        }
        return result;
    }

    @Override
    public PhotoFace updateInfo(PhotoFace photoFace) {
        return photoFaceDao.save(photoFace);
    }
}
