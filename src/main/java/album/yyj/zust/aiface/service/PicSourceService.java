package album.yyj.zust.aiface.service;

import java.util.Map;

public interface PicSourceService {
    public Integer findMePre(Integer userId, Map<String, Object> data);

    public Integer checkSourceUpload(Integer userId, Integer sourceId, Map<String, Object> data);

    public Integer startSearchAll(Integer userId, Integer sourceId, Map<String, Object> data);

    public Integer checkMacheStatus(Integer sourceId, Map<String, Object> data);

    public Integer getAllMachePhotos(Integer sourceId,Integer userId, Map<String, Object> data);
}
