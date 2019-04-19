package album.yyj.zust.aiface.daoTest;

import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.pojo.PhotoFace;
import album.yyj.zust.aiface.repository.PhotoDao;
import album.yyj.zust.aiface.repository.PhotoFaceDao;
import album.yyj.zust.aiface.tools.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 杨玉杰
 * @Date: 2019/3/22 13:57
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PhotoFaceDaoTest {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PhotoFaceDao photoFaceDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PhotoDao photoDao;

    @Test
    public void redisTest(){
        redisUtil.set("name","zxc",10*60);
        logger.info("插入成功");
        String name = redisUtil.get("name").toString();
        logger.info("取值" + name);
    }

    @Test
    public void findAllFaces(){
        List<PhotoFace> faces = photoFaceDao.findAllFacesByPhotoId(6);
        for(PhotoFace pf : faces){
            logger.info("---------------");
            logger.info(pf.getId() + "");
            logger.info("---------------");
        }
    }
    @Test
    public void findUnPhoto(){
        List<Integer>ids = new ArrayList<>();
//        ids.add(45);
//        ids.add(49);
        Integer maxId = 0;
        if(ids.size() > 0){
            maxId = ids.get(ids.size() -1);//去最大的id
        }
        if(ids.size() == 0){
            ids.add(0);
        }
        List<Photo> photo = photoDao.findUnsearchPhotosByUserId(3, ids,maxId);
        for(Photo photo1 : photo){
            logger.info(photo1.getId()+"");
        }
    }
}
