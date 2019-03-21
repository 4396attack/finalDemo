package album.yyj.zust.aiface.service;

import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.pojo.PhotoFace;

import java.util.List;

public interface PhotoFaceService {
    public Integer cutFace(Integer photoId);

    public List<PhotoFace> findAllFaceByPhotoIdAndFaceIds(String photoId, String[] ids);

    public PhotoFace updateInfo(PhotoFace photoFace);


}
