package album.yyj.zust.aiface.repository;

import album.yyj.zust.aiface.pojo.PicSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PicSourceDao extends JpaRepository<PicSource,Integer> {
    @Query("select picSource from PicSource picSource where picSource.id=:sourceId and picSource.userId=:userId")
    public PicSource findByIdAndUserId(@Param("sourceId") Integer sourceId,@Param("userId") Integer userId);

    @Query("select picSource from PicSource picSource where picSource.id =:sourceId and picSource.deleted = 0")
    public PicSource findUndeletedRecordById(@Param("sourceId")Integer sourceId);
}
