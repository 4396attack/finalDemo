package album.yyj.zust.aiface.daoTest;

import album.yyj.zust.aiface.pojo.FindRecords;
import album.yyj.zust.aiface.pojo.Photo;
import album.yyj.zust.aiface.repository.FindRecordsDao;
import album.yyj.zust.aiface.repository.PhotoDao;
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
 * @Date: 2019/4/19 10:21
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FindRecordDaoTest {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private FindRecordsDao findRecordsDao;
    @Autowired
    private PhotoDao photoDao;
    @Test
    public void testGetIds(){
        List<Integer> ids = findRecordsDao.getHasSearchPhotoId(3,11);
        Integer maxId = ids.get(ids.size()-1);
        logger.info("maxId="+maxId);
        List<Photo> list = photoDao.findUnsearchPhotosByUserId(3, ids, maxId);
        for(Photo photo:list){
            logger.info(photo.toString());
        }
    }
}
