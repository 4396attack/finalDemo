package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.*;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.rabbitMQ.SecondSender;
import album.yyj.zust.aiface.repository.*;
import album.yyj.zust.aiface.service.PicSourceService;
import album.yyj.zust.aiface.tools.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private SecondSender secondSender;

    @Autowired
    private RedisUtil redisUtil;

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
            List<Photo> photos = new ArrayList<>();
            redisUtil.set(RedisUtil.PREFIX_FACE_MACHE_KEY + sourceId,RedisUtil.MACHE_STATS_UNDO,10*60);
            List<Integer> photoIds = findRecordsDao.getHasSearchPhotoId(userId,sourceId);
            if(photoIds.size() == 0){
                logger.info("此模板图可能是新上传的，没有历史匹配");
                photos = photoDao.findUndeletedsPhotosByUserId(userId);
                //匹配该用户相册下所有的图片
            }else {
                logger.info("该模板有历史匹配记录，只需匹配新增的图片即可");
                Integer maxId = photoIds.get(photoIds.size()-1);
                photos = photoDao.findUnsearchPhotosByUserId(userId,photoIds,maxId);
            }
            if(photos.size()>0){
                StringBuilder sb = new StringBuilder();
                for (Photo p : photos){
                    sb.append(p.getId());
                    sb.append(",");
                }
                String idInfo = sb.toString();
                idInfo = idInfo.substring(0,idInfo.length()-1);//把最后的，去掉
                String message = "{\"photoId\" : \""+ idInfo + "\" , \"userId\" : \""+userId+"\" , \"sourceId\" : \""+sourceId+"\"}";
                String uuid = UUID.randomUUID().toString();
                try {
                    secondSender.send(uuid, message);
                    logger.info("成功发送消息到消息队列中，内容为: " + message );
//                    redisUtil.set(RedisUtil.PREFIX_POS_FACE_KEY + photoId,RedisUtil.POS_STATS_UNDO,RedisUtil.KEY_EXPIRE_TIME);
                    logger.info("插入redis成功");
                }catch (Exception e){
                    logger.info("尝试发送消息失败，内容为 ： " + message + " ; 错误原因 ：" +e);
                    error = ErrorCodes.RABBIT_MSG_SEND_FAIL;
                }

            }else{
                redisUtil.set(RedisUtil.PREFIX_FACE_MACHE_KEY + sourceId,RedisUtil.MACHE_STATS_FAIL,10*60);
                error = ErrorCodes.NO_EVEN_UPLOAD_ANY_PHOTO;
            }
        }else {
            error = ErrorCodes.NOT_LOGIN;
        }
        return error;
    }

    @Override
    public Integer checkMacheStatus(Integer sourceId, Map<String, Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        Integer status = (Integer)redisUtil.get(RedisUtil.PREFIX_FACE_MACHE_KEY + sourceId);
        if(status == null){
            error = ErrorCodes.NO_SUCH_REDIS_KEY;
        }else {
            data.put("obj",status);
        }
        return error;
    }

    @Override
    public Integer getAllMachePhotos(Integer sourceId, Integer userId, Map<String, Object> data) {
        Integer error = ErrorCodes.SUCCESS;
        List<FindRecords> result = findRecordsDao.getHasSearchPhoto(userId, sourceId);
        data.put("obj",result);
        return error;
    }
}
