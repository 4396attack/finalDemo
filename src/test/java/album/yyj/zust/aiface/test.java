package album.yyj.zust.aiface;

import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.properties.Ossproperties;
import album.yyj.zust.aiface.repository.PhotoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SpringBootTest
@RunWith(SpringRunner.class)
public class test {
    Logger logger = LoggerFactory.getLogger(test.class);
    @Autowired
    private PhotoDao photoDao;
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
    @Test
    public void searchAll(){
        List<Photo> list = photoDao.findUndeletedsPhotosByUserId(1);
        for (Photo p : list){
            logger.info(p.toString());
        }

    }
}
