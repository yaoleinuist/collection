package com.lzhsite.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class TestHBaseClient {
	
	
	public static Configuration conf=HBaseConfiguration.create();
//	static {
//		conf = HBaseConfiguration.create();
//		conf.set("hbase.zookeeper.property.clientPort", "2181");
//		conf.set("hbase.zookeeper.quorum", "192.168.226.4");
//		conf.set("hbase.master", "192.168.226.4:600000");
//	}
	
	public static HTable getTable(String name )throws Exception {
		
		HTable table = new HTable(conf, name);
		
		return table;
	}
	
	/**
	 * get 'tb_name' 'rowkey' 'cf:col'
	 * @param table
	 */
	public static void getData(HTable table) throws Exception{
		
		Get get = new Get(Bytes.toBytes("20170819_10003"));
		//get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
		get.addFamily(Bytes.toBytes("info"));
		
		//load the get
		Result rs = table.get(get);
		//print the data
		for(Cell cell : rs.rawCells()){
			System.out.println(Bytes.toString(CellUtil.cloneFamily(cell)) + "->" + 
											Bytes.toString(CellUtil.cloneQualifier(cell)) + "->" +
											Bytes.toString(CellUtil.cloneValue(cell)) + "->" +
											cell.getTimestamp()) ;
			System.out.println("-------------------------------------------------------");
		}
		
		table.get(get);
	}
	
	/**
	 * put 'tb_name' 'rowkey' 'cf:col' 'value'
	 * @param table
	 * @throws Exception
	 */
	public static void putData (HTable table)throws Exception{
		
		Put put = new Put(Bytes.toBytes("20170819_10003"));
		put.add(Bytes.toBytes("info"), 
				       Bytes.toBytes("age"), 
				       Bytes.toBytes("20"));
		table.put(put);
		getData(table);
	}

	public static void deleteData(HTable table)throws Exception{
		
		Delete del = new Delete(Bytes.toBytes("20170819_10003"));
		del.deleteColumn(Bytes.toBytes("info"), Bytes.toBytes("age"));
		
		table.delete(del);
		getData(table);
	}
	
	public static void scanData(HTable table)throws Exception{
		
		Scan scan = new Scan();
		
		table.getScanner(scan);
		
		ResultScanner rsscan = table.getScanner(scan);
		for(Result rs : rsscan){
			System.out.println(Bytes.toString(rs.getRow()));
			for(Cell cell : rs.rawCells()){
				System.out.println(Bytes.toString(CellUtil.cloneFamily(cell)) + "->" + 
												Bytes.toString(CellUtil.cloneQualifier(cell)) + "->" +
												Bytes.toString(CellUtil.cloneValue(cell)) + "->" +
												cell.getTimestamp()) ;		
				}		
			System.out.println("-------------------------------------------------------");
			}
		}

	public static void rangData(HTable table)throws Exception{
		
		Scan scan = new Scan();
		
		//scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
		scan.setStartRow(Bytes.toBytes("20171019_10001"));
		scan.setStopRow(Bytes.toBytes("20171019_10003"));
		
		ResultScanner rsscan = table.getScanner(scan);
		for(Result rs : rsscan){
			System.out.println(Bytes.toString(rs.getRow()));
			for(Cell cell : rs.rawCells()){
				System.out.println(Bytes.toString(CellUtil.cloneFamily(cell)) + "->" + 
												Bytes.toString(CellUtil.cloneQualifier(cell)) + "->" +
												Bytes.toString(CellUtil.cloneValue(cell)) + "->" +
												cell.getTimestamp()) ;		
				}		
			System.out.println("-------------------------------------------------------");
			}
	}
	
	public static void main(String[] args) throws Exception {
		
		HTable table = getTable("nstest:tb1");
		//getData(table);
		//putData(table);
		//deleteData(table);
		scanData(table);
		//rangData(table);
	}
}
