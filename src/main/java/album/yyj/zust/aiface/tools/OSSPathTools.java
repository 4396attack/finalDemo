package album.yyj.zust.aiface.tools;

public class OSSPathTools {
    public final static String ORIGIN_BUCKET = "ai-face-yyj";
    public static String prePhotoPath(Integer userId,Integer photoId){
        return "user" + userId + "/photo" + photoId + ".jpg";
    }
}
