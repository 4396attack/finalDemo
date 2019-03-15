package album.yyj.zust.aiface;

import album.yyj.zust.aiface.properties.Ossproperties;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
    @Test

    public void name() {
//        Ossproperties ossproperties = new Ossproperties();
//        System.out.println(ossproperties.toString());
        String ract3 = "[1,2,3,4,5,6,7,8]";
        String ract = "[]";
//        System.out.println(ract2.substring(1,ract2.length()-1));
        ract = ract.substring(1,ract.length()-1);
        String[] split = ract.split(",");
        int index = split.length/4;
        List<String> info = new ArrayList<>();
        for(int i = 0 ; i< index;i++){
            StringBuilder sb = new StringBuilder();
            int suffix = 4 * i;//偏移量
            for (int k = suffix;k< suffix+4;k++){
                sb.append(split[k]);
                if(k != suffix + 3){
                    sb.append(",");
                }
            }
            System.out.println(sb.toString());
            info.add(sb.toString());
        }
        System.out.println(info.size());
        for (String t : info){
            System.out.println(info.size());
        }
//        for(String s : split){
//            System.out.println(s);
//        }
//        System.out.println(ract2.substring(1,ract2.length()-1).trim().equals(""));
    }
}
