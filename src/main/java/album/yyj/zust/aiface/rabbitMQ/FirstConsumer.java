package album.yyj.zust.aiface.rabbitMQ;

import album.yyj.zust.aiface.pojo.PhotoFace;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.repository.PhotoFaceDao;
import album.yyj.zust.aiface.service.PhotoFaceService;
import album.yyj.zust.aiface.tools.ImageUtil;
import album.yyj.zust.aiface.tools.OSSImageClient;
import album.yyj.zust.aiface.tools.OSSPathTools;
import album.yyj.zust.aiface.tools.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 消费者1
 */
@Component
public class FirstConsumer {
    private Logger logger = LoggerFactory.getLogger(FirstConsumer.class);

    @Autowired
    private PhotoFaceService photoFaceService;
    @Autowired
    private Ossproperties ossproperties;
    @Autowired
    private RedisUtil redisUtil;

    @RabbitListener(queues = {"first-queue"},containerFactory = "rabbitListenerContainerFactory")
    public void handleMessage(String message){
        // 处理消息
        logger.info("收到处理消息，内容为 ：" + message);
        JSONObject msg = JSON.parseObject(message);
        String photoId = msg.getString("photoId");
        redisUtil.set(RedisUtil.PREFIX_POS_FACE_KEY + photoId,RedisUtil.POS_STATS_POSING,RedisUtil.KEY_EXPIRE_TIME);
        logger.info("更新pos状态为处理中...");
        String userId = msg.getString("userId");
        String faceIds = msg.getString("faceIds");
        if(photoId == null || faceIds == null){
            logger.info("消息内容有误！请检查");
            redisUtil.set(RedisUtil.PREFIX_POS_FACE_KEY + photoId,RedisUtil.POS_STATS_FAIL,RedisUtil.KEY_EXPIRE_TIME);
        }else {
            String[] ids = faceIds.split(",");
            if(ids.length == 0){
                logger.info("人脸坐标信息有误！" + faceIds);
                redisUtil.set(RedisUtil.PREFIX_POS_FACE_KEY + photoId,RedisUtil.POS_STATS_FAIL,RedisUtil.KEY_EXPIRE_TIME);
            }else {
                List<PhotoFace> faces = photoFaceService.findAllFaceByPhotoIdAndFaceIds(photoId,ids);
                for(PhotoFace pf : faces){//把每张人脸裁剪下来发送到OSS上指定地址
                    dowmloadAndCut(pf);
                }
                redisUtil.set(RedisUtil.PREFIX_POS_FACE_KEY + photoId,RedisUtil.POS_STATS_SUCCESS,RedisUtil.KEY_EXPIRE_TIME);
                logger.info("更新pos状态为已完成");
                logger.info("消息处理完毕！");
            }
        }
    }

    /**
     * 按照数据库信息进行裁剪，并上传到OSS指定路径
     * @param photoFace
     * @return
     */
    private void dowmloadAndCut(PhotoFace photoFace){
        OSSImageClient client =  new OSSImageClient(ossproperties.getEndPoint(),ossproperties.getAccessKeyId(),ossproperties.getAccessKeySecret());
        String srcImg = OSSPathTools.getLocalTempPath(photoFace.getUserId(),photoFace.getPhotoId());
        String dest = OSSPathTools.getLocalCutPath(photoFace.getUserId(),photoFace.getId());
        Integer x = Integer.valueOf(photoFace.getPointX());
        Integer y = Integer.valueOf(photoFace.getPointY());
        Integer width = Integer.valueOf(photoFace.getWidth());
        Integer height = Integer.valueOf(photoFace.getHeight());
        File src = new File(srcImg);
        if(!src.exists()){//本地不存在原图，先去OSS上下载
            if(client.exitObj(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.prePhotoPath(photoFace.getUserId(),photoFace.getPhotoId()))){
                client.downloadToPath(OSSPathTools.ORIGIN_BUCKET,OSSPathTools.prePhotoPath(photoFace.getUserId(),photoFace.getPhotoId()),srcImg);
            }
        }
        boolean flag = ImageUtil.cutImage(srcImg,dest,x,y,width,height);
        if(flag){//裁剪成功上传到oss，并删除本地缓存文件
            boolean success = client.upload(OSSPathTools.DETAIL_BUCKET,OSSPathTools.getFaceDetailOSSPath(photoFace.getUserId(),photoFace.getPhotoId(),photoFace.getId()),dest);
            if(success){//上传成功
                logger.info("文件上传成功，文件名：" + dest);
                photoFace.setDeleted(0);
                photoFace.setHasCut(1);
                photoFace.setUpdateTime(new Date());
                PhotoFace save = photoFaceService.updateInfo(photoFace);
                if(new Integer(1).equals(save.getHasCut())){//
                    logger.info("数据库更新成功！");
                }else {
                    logger.info("数据库更新失败，faceId = " + photoFace.getId());
                }
                //删除本地缓存图片
                File tempFile = new File(dest);
                if(tempFile.exists()){
                    if(tempFile.delete()){
                        logger.info("删除缓存图片成功！");
                    }else {
                        logger.info("删除缓存图片失败，文件地址 ：" + dest);
                    }

                }
            }else {
                logger.info("文件上传失败，请检查图片是否存在，文件名：" + dest);
            }
        }else {
            logger.info("图片裁剪失败，请寻找原因，文件名：" + srcImg);
        }
        client.close();
    }
}
