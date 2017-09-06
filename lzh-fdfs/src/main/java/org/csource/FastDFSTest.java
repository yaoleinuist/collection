package org.csource;


import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.csource.cilent.FastDFSClient;
import org.junit.Test;


/**
 * 
 * @描述: FastDFS测试 .
 * @作者: WuShuicheng .
 * @创建时间: 2015-3-29,下午8:11:36 .
 * @版本号: V1.0 .
 */
public class FastDFSTest {
	
	/**
	 * 上传测试.
	 * @throws Exception
	 */
	@Test
	public static void upload() throws Exception {
		String filePath = "D:/dubbosrc/maven-parent/lzh-fdfs/TestFile/DubboVideo.jpg";
		File file = new File(filePath);
		String fileId = FastDFSClient.uploadFile(file, filePath);
		System.out.println("Upload local file " + filePath + " ok, fileid=" + fileId);
		// fileId:	group1/M00/00/00/wKgEfVUYPieAd6a0AAP3btxj__E335.jpg
		// url:	http://192.168.226.4:8888/group1/M00/00/00/wKjiBFmvwl-AGO60AAP3btxj__E633.jpg
	}
	
	/**
	 * 下载测试.
	 * @throws Exception
	 */
 	@Test
	public static void download() throws Exception {
		String fileId = "group1/M00/00/00/wKjiBFmvwl-AGO60AAP3btxj__E633.jpg";
		InputStream inputStream = FastDFSClient.downloadFile(fileId);
		File destFile = new File("D:/dubbosrc/maven-parent/lzh-fdfs/TestFile/DownloadTest.jpg");
		FileUtils.copyInputStreamToFile(inputStream, destFile);
	}

	/***
	 * 删除测试
	 * @throws Exception
	 **/
	@Test
	public static void delete() throws Exception {
		String fileId = "group1/M00/00/00/wKjiBFmvwl-AGO60AAP3btxj__E633.jpg";
		int result = FastDFSClient.deleteFile(fileId);
		System.out.println(result == 0 ? "删除成功" : "删除失败:" + result);
	} 


	
   public static void main(String[] args) {
		try {
			//upload();
			download();
			//delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

}

}
