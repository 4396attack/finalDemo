package album.yyj.zust.aiface.serviceimpl;

import album.yyj.zust.aiface.info.ErrorCodes;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.pojo.PicSource;
import album.yyj.zust.aiface.pojo.User;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.repository.PhotoDao;
import album.yyj.zust.aiface.repository.PicSourceDao;
import album.yyj.zust.aiface.repository.UserDao;
import album.yyj.zust.aiface.service.PicSourceService;
import album.yyj.zust.aiface.tools.OSSImageClient;
import album.yyj.zust.aiface.tools.OSSPathTools;
import album.yyj.zust.aiface.tools.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PicSourcesServiceimpl implements PicSourceService {
    @Autowired
    private PicSourceDao picSourceDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private Ossproperties ossproperties;

    @Autowired
    private PhotoDao photoDao;

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
     */
    private void faceCheck(Photo photo,Integer userId,Integer sourceId,Map<String, Object> data){

    }
}
