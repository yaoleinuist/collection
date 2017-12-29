package com.lzhsite.technology.shardingjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSource;
import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
 
 
/**
 * http://blog.csdn.net/linuu/article/details/50929635
 * 
 * @author lzhcode
 *
 */
public class TestShardingJdbc {

	public static void main(String[] args) throws SQLException {
		// 数据源
		Map<String, DataSource> dataSourceMap = new HashMap<>(2);
		dataSourceMap.put("sharding_0", createDataSource("sharding_0"));
		dataSourceMap.put("sharding_1", createDataSource("sharding_1"));

		DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);

		// 分表分库的表，第一个参数是逻辑表名，第二个是实际表名，第三个是实际库
		TableRule orderTableRule = new TableRule("t_order", Arrays.asList("t_order_0", "t_order_1"), dataSourceRule);
		TableRule orderItemTableRule = new TableRule("t_order_item", Arrays.asList("t_order_item_0", "t_order_item_1"),
				dataSourceRule);

		/**
		 * DatabaseShardingStrategy 分库策略 参数一：根据哪个字段分库 参数二：分库路由函数
		 * TableShardingStrategy 分表策略 参数一：根据哪个字段分表 参数二：分表路由函数
		 * 
		 */
		ShardingRule shardingRule = new ShardingRule(dataSourceRule, Arrays.asList(orderTableRule, orderItemTableRule),
				Arrays.asList(new BindingTableRule(Arrays.asList(orderTableRule, orderItemTableRule))),
				new DatabaseShardingStrategy("user_id", new ModuloDatabaseShardingAlgorithm()),
				new TableShardingStrategy("order_id", new ModuloTableShardingAlgorithm()));

		DataSource dataSource = new ShardingDataSource(shardingRule);
		String sql = "SELECT i.* FROM t_order o JOIN t_order_item i ON o.order_id=i.order_id WHERE o.user_id=? AND o.order_id=?";
		try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, 10);
			pstmt.setInt(2, 1001);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					System.out.println(rs.getInt(1));
					System.out.println(rs.getInt(2));
					System.out.println(rs.getInt(3));
				}
			}
		}
	}

	/**
	 * 创建数据源
	 * 
	 * @param dataSourceName
	 * @return
	 */
	private static DataSource createDataSource(String dataSourceName) {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
		druidDataSource.setUrl(String.format("jdbc:mysql://localhost:3306/%s", dataSourceName));
		druidDataSource.setUsername("root");
		druidDataSource.setPassword("123456..");
		return druidDataSource;
	}

}
