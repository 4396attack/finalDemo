package album.yyj.zust.aiface.tools;

public class OSSPathTools {
    public final static String ORIGIN_BUCKET = "ai-face-yyj";
    public final static String DETAIL_BUCKET = "ai-face-detail-yyj";
    public static String prePhotoPath(Integer userId,Integer photoId){
        return "user" + userId + "/photo" + photoId + ".jpg";
    }

    public static String getSourcePath(Integer userId, Integer id) {
        return "source/User" + userId + "/" + id + ".jpg";
    }

    /**
     * 原图的本地缓存路径
     * @param userId
     * @param id
     * @return
     */
    public static String getLocalTempPath(Integer userId,Integer id){
        return ImageUtil.TEMP_PATH + userId + "_" + id + ".jpg";
    }

    /**
     * 人脸图的本地路径
     * @param userId
     * @param id
     * @return
     */
    public static String getLocalCutPath(Integer userId,Integer id){
        return ImageUtil.CUT_PATH + userId + "_" + id + ".jpg";
    }

    public static String getFaceDetailOSSPath(Integer userId,Integer photoId,Integer id){
        return "User" + userId + "/photo" + photoId  + "/" + id +".jpg";
    }
}
