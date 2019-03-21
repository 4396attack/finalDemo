package album.yyj.zust.aiface.repository;

import album.yyj.zust.aiface.pojo.PhotoFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoFaceDao extends JpaRepository<PhotoFace,Integer> {
    @Query("select photoFace from PhotoFace photoFace where photoFace.id =:id and photoFace.photoId=:photoId and photoFace.deleted = 1 and photoFace.hasCut = 0")
    public PhotoFace findUncutPhotoFace(@Param("id")Integer id, @Param("photoId")Integer photoId);

    @Query("select photoFace from PhotoFace photoFace where photoFace.photoId =:photoId")
    public List<PhotoFace> findAllFacesByPhotoId(@Param("photoId")Integer photoId);

}
