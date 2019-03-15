package album.yyj.zust.aiface.repository;

import album.yyj.zust.aiface.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao extends JpaRepository<User,Integer>{
    @Query("select user from User user where user.phone=:phone and user.deleted = 0")
    public User findUserByPhone(@Param("phone") String phone);

    @Query("select user from User user where user.id =:userId and user.deleted = 0")
    public User findUndeletedUserById(@Param("userId") Integer userId);
}
