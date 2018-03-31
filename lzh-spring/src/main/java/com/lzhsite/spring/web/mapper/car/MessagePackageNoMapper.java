package com.lzhsite.spring.web.mapper.car;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lzhsite.spring.web.entity.car.MessagePackageNo;

/**
 * <p>
  * 消息包编号表 Mapper 接口
 * </p>
 *
 * @author lcj
 * @since 2017-09-25
 */
@Repository
public interface MessagePackageNoMapper {

    /**
     * 保存
     * @param no
     */
    @Insert("INSERT INTO `tb_message_package_no` (`no`, `create_time`) VALUES (#{no.no}, NOW())")
    void insert(@Param("no") MessagePackageNo no);
    
    @Select("select * from `tb_message_package_no where 1=1 and #{eq.sqlSegment}")
	MessagePackageNo selectOne(@Param("eq") Wrapper<MessagePackageNo> eq);
    
}