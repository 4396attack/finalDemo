package album.yyj.zust.aiface.repository;

import album.yyj.zust.aiface.pojo.FindRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FindRecordsDao extends JpaRepository<FindRecords,Integer> {
    /**
     * 为了减少重复比对的情况，对于同一个source，不再对已比较过的图片再次比对
     * @param userId
     * @param sourceId
     * @return
     */
    @Query("select find.photoId from FindRecords find where find.userId=:userId and deleted = 0 and find.sourceId=:sourceId")
    public List<Integer> getHasSearchPhotoId(@Param("userId")Integer userId,@Param("sourceId")Integer sourceId);

    @Query("select find from FindRecords find where find.userId=:userId and deleted = 0 and find.sourceId=:sourceId")
    public List<FindRecords> getHasSearchPhoto(@Param("userId")Integer userId,@Param("sourceId")Integer sourceId);

    /**
     * 搜索所有已经被匹配到的图片
     */
    @Query("select find from FindRecords find where find.userId=:userId and find.sourceId=:sourceId and find.deleted = 0")
    public List<FindRecords> getAllHasMachePhoto(@Param("userId")Integer userId,@Param("sourceId")Integer sourceId);
}
