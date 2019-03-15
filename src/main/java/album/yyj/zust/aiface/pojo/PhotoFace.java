package album.yyj.zust.aiface.pojo;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "photo_face")
public class PhotoFace {
    private Integer id;
    private Integer photoId;
    private Integer userId;
    private Date updateTime;
    private String width;
    private String height;
    private String pointX;
    private String pointY;
    private Integer hasCut;//是否已裁剪
    private Integer deleted;

    public PhotoFace() {
        super();
    }

    public PhotoFace(Integer photoId, Integer userId) {
        this.photoId = photoId;
        this.userId = userId;
        this.updateTime = new Date();
        this.deleted = 1;
        this.setHasCut(0);
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id",unique = true,nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @Column(name = "photo_id")
    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    @Column(name = "width")
    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
    @Column(name = "height")
    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
    @Column(name = "deleted")
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    @Column(name = "point_x")
    public String getPointX() {
        return pointX;
    }

    public void setPointX(String pointX) {
        this.pointX = pointX;
    }
    @Column(name = "point_y")
    public String getPointY() {
        return pointY;
    }

    public void setPointY(String pointY) {
        this.pointY = pointY;
    }

    @Column(name = "has_cut")
    public Integer getHasCut() {
        return hasCut;
    }

    public void setHasCut(Integer hasCut) {
        this.hasCut = hasCut;
    }
}
