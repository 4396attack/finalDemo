package album.yyj.zust.aiface.repository;

import album.yyj.zust.aiface.pojo.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoDao extends JpaRepository<Photo,Integer> {

    @Query("select photo from Photo photo where photo.id =:id and photo.deleted = 0")
    public Photo findUndeletedPhotoById(@Param("id") Integer id);

    @Query("select photo from Photo photo where photo.userId=:userId and photo.deleted = 0")
    public List<Photo> findUndeletedsPhotosByUserId(@Param("userId")Integer userId);
}
