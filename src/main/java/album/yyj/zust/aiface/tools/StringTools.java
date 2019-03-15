package album.yyj.zust.aiface.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类主要是一些字符串处理工具类
 * 处理结果数据格式 【“1,2,3,4”，“5,6,7,8”】
 */
public class StringTools {
    public static List<String> getRactInfo(String ractFace){
        List<String> info = new ArrayList<>();
        if(ractFace != null){
            ractFace = ractFace.substring(1,ractFace.length()-1);// 将【】去除
            if(!ractFace.trim().equals("")){//如果图片中没有人脸信息，那么这里是没有坐标信息的;有信息的话是4个数字一组
                String[] split = ractFace.split(",");
                int index = split.length/4;
                for(int i = 0 ; i< index;i++){
                    StringBuilder sb = new StringBuilder();
                    int suffix = 4 * i;//偏移量
                    for (int k = suffix;k< suffix+4;k++){
                        sb.append(split[k]);
                        if(k != suffix + 3){
                            sb.append(",");
                        }
                    }
                    info.add(sb.toString());
                }
            }
        }
        return info;
    }

    /**
     * 校验参数合理性
     * @param para
     * @return
     */
    public static boolean checkPram(Object para){
        boolean flag = false;
        if(para != null){
            flag = true;
        }
        return flag;
    }
}
