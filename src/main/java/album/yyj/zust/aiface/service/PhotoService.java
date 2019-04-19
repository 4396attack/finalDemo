package album.yyj.zust.aiface.service;

import album.yyj.zust.aiface.pojo.Photo;

import java.util.Map;

public interface PhotoService {
    public Integer uploadPhoto(Photo photo,Map<String,Object> data);

    public Integer savePhoto(Photo photo, Map<String,Object> data);

    public Integer findAllPhotos(Integer userId, Map<String, Object> data);
}
