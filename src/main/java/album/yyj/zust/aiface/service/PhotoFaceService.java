package album.yyj.zust.aiface.service;

import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.pojo.PhotoFace;

import java.util.List;
import java.util.Map;

public interface PhotoFaceService {
    public Integer cutFace(Integer photoId);

    public List<PhotoFace> findAllFaceByPhotoIdAndFaceIds(String photoId, String[] ids);

    public PhotoFace updateInfo(PhotoFace photoFace);


    public Integer checkPosStatus(Integer photoId, Map<String, Object> data);

    public Integer getAllFacesPos(Integer photoId, Map<String, Object> data);
}
