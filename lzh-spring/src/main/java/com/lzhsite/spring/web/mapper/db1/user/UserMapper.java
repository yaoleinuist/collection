package com.lzhsite.spring.web.mapper.db1.user;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.lzhsite.spring.web.entity.db1.User;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author lcj
 * @since 2017-09-27
 */
@Repository
public interface UserMapper {

    @Insert("INSERT INTO `tb_user` (`id`, `name`, `age`, `create_time`) VALUES (NULL, #{user.name}, #{user.age}, NOW())")
    void insert(@Param("user")User user);

}