package org.myutiltest.biz;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastdfsTest {
	public static void main(String[] args) {
		try {
			ClientGlobal.init("fdfs_client.conf");
			TrackerClient trackerClient = new TrackerClient();
			TrackerServer trackerServer = trackerClient.getConnection();
			// StorageServer storageServer = trackerClient.getStoreStorage(trackerServer,
			// "group1");
			StorageServer storageServer = new StorageServer("192.168.0.106", 23000, 0);
			NameValuePair[] meta_list = new NameValuePair[1];
			meta_list[0] = new NameValuePair("author", "star");
			String[] uploadResults = null;
			StorageClient storageClient = new StorageClient(trackerServer, storageServer);
			uploadResults = storageClient.upload_file("/Users/lixing/Documents/pn.jpeg", "jpeg", meta_list);
			System.out.println(uploadResults[0] + ":" + uploadResults[1]);
			// http://localhost:8888/group1/M00/00/00/rBEAAlr4I82AVbQjAACFxeGzp3E84.jpeg
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
