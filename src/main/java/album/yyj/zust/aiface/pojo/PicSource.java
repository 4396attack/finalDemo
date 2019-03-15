package album.yyj.zust.aiface.pojo;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 这个类用于记录用户用来检索的人脸图像
 */
@Entity
@Table(name = "pic_source")
public class PicSource {
    private Integer id;
    private Integer userId;
    private Date updateTime;
    private Integer deleted;

    public PicSource(){
        super();
    }
    public PicSource(Integer userId) {
        this.userId = userId;
        this.updateTime = new Date();
        this.deleted = 1;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    @Column(name = "deleted")
    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
