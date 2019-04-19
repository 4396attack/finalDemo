package album.yyj.zust.aiface.rabbitMQ;

import album.yyj.zust.aiface.pojo.FindRecords;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.pojo.PhotoFace;
import album.yyj.zust.aiface.pojo.PicSource;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.repository.FindRecordsDao;
import album.yyj.zust.aiface.repository.PhotoDao;
import album.yyj.zust.aiface.repository.PhotoFaceDao;
import album.yyj.zust.aiface.repository.PicSourceDao;
import album.yyj.zust.aiface.tools.FaceTools;
import album.yyj.zust.aiface.tools.OSSImageClient;
import album.yyj.zust.aiface.tools.OSSPathTools;
import album.yyj.zust.aiface.tools.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 杨玉杰
 * @Date: 2019/4/19 14:44
 * @Description: 消费者2
 */
@Component
public class SecondConsumer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PhotoDao photoDao;
    @Autowired
    private Ossproperties ossproperties;
    @Autowired
    private PhotoFaceDao photoFaceDao;
    @Autowired
    private FindRecordsDao findRecordsDao;
    @Autowired
    private PicSourceDao picSourceDao;
    @Autowired
    private RedisUtil redisUtil;
    @RabbitListener(queues = {"second-queue"},containerFactory = "rabbitListenerContainerFactory")
    public void handleMessage(String message){
        // 处理消息
        logger.info("收到处理消息，内容为 ：" + message);
        JSONObject info = JSON.parseObject(message);
        String sourceId = info.getString("sourceId");
        redisUtil.set(RedisUtil.PREFIX_FACE_MACHE_KEY + sourceId,RedisUtil.MACHE_STATS_MACHING,RedisUtil.KEY_EXPIRE_TIME);
        logger.info("更新mache状态为匹配中");
        String photoId = info.getString("photoId");
        String userId = info.getString("userId");
        if(photoId == null || userId == null){
            logger.info("信息内容有误，请检查" + message);
            logger.info("未检测新图片");
            redisUtil.set(RedisUtil.PREFIX_FACE_MACHE_KEY + sourceId,RedisUtil.MACHE_STATS_FAIL,RedisUtil.KEY_EXPIRE_TIME);
        }else {
//            List<String> ids = new ArrayList<>();
            Integer source = Integer.valueOf(sourceId);
            Integer user = Integer.valueOf(userId);
            String[] split = photoId.split(",");
            for(String s : split){
                Integer id = Integer.valueOf(s);
                faceCheck(id,user,source);
            }
            logger.info("图片检索完成");
            redisUtil.set(RedisUtil.PREFIX_FACE_MACHE_KEY + sourceId,RedisUtil.MACHE_STATS_SUCCESS,RedisUtil.KEY_EXPIRE_TIME);
        }
    }
    /**
     * 对图片中解析出来的人脸一次进行比对
     * 置信度大于85认为是同一个人
     */
    private void faceCheck(Integer photoId, Integer userId, Integer sourceId){
        List<PhotoFace> faces = photoFaceDao.findFacesBelongPhoto(photoId);
        OSSImageClient client = new OSSImageClient(ossproperties.getEndPoint(),ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret());
        PicSource picSource = picSourceDao.findUndeletedRecordById(sourceId);
        List<FindRecords> records = new ArrayList<>();
        for(PhotoFace face : faces){
//            logger.info("---------------" + face.getId());
//            logger.info(OSSPathTools.getFaceDetailOSSPath(userId, photo.getId(), face.getId()));
//            logger.info(OSSPathTools.getSourcePath(picSource.getUserId(), sourceId));
            if(client.exitObj(OSSPathTools.DETAIL_BUCKET, OSSPathTools.getFaceDetailOSSPath(userId, photoId, face.getId())) &&
                    client.exitObj(OSSPathTools.ORIGIN_BUCKET, OSSPathTools.getSourcePath(picSource.getUserId(), sourceId))){
                File faceImg = client.downloadFile(OSSPathTools.DETAIL_BUCKET, OSSPathTools.getFaceDetailOSSPath(userId, photoId, face.getId()));
                File sourceImg = client.downloadFile(OSSPathTools.ORIGIN_BUCKET, OSSPathTools.getSourcePath(picSource.getUserId(), sourceId));
                Float confidence = isFaceSame(sourceImg, faceImg);
                if(85 < confidence){
//                    logger.info("****************************");
                    FindRecords findRecords = new FindRecords(userId,photoId,sourceId,confidence,face.getId());
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
