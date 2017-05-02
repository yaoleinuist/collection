package com.dao;

import tk.mybatis.mapper.common.ExampleMapper;
import tk.mybatis.mapper.common.Marker;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.RowBoundsMapper;
import tk.mybatis.mapper.common.base.BaseInsertMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

public interface BaseMapper<T> extends ExampleMapper<T>, RowBoundsMapper<T>, Marker, BaseSelectMapper<T>,
BaseInsertMapper<T>, BaseUpdateMapper<T>, MySqlMapper<T> {

	 int delete(Long key);
 
}
 