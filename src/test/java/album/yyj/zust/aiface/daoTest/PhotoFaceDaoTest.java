package album.yyj.zust.aiface.daoTest;

import album.yyj.zust.aiface.pojo.PhotoFace;
import album.yyj.zust.aiface.repository.PhotoFaceDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void findAllFaces(){
        List<PhotoFace> faces = photoFaceDao.findAllFacesByPhotoId(6);
        for(PhotoFace pf : faces){
            logger.info("---------------");
            logger.info(pf.getId() + "");
            logger.info("---------------");
        }
    }

}
