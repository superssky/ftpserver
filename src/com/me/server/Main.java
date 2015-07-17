package com.me.server;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.ftpserver.ftplet.FtpException;

import com.me.db.DataOperate;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Main {

	public static void main(String[] args) throws FtpException, IOException {
//		FtpServerFactory serverFactory = new FtpServerFactory();
//		FtpServer server = serverFactory.createServer();
//		// start the server
//		server.start();
//		testAddDB();
//		testUpdateDB();
		testQueryDB();
//		testDeleteDB();
		testQueryDB();
		testQueryDB();
//		testBatchDB();
//		pressImage("/home/meng/Desktop/1.png", "/home/meng/Desktop/33.JPG");
	}
	public static void testUpdateDB() {
		DataOperate db = new DataOperate();
		
		System.out.println(db.updateRecord("/00/oow/oow/test.jpg",""));
	}
	public static void testQueryDB() {
		DataOperate db = new DataOperate();
		System.out.println(db.queryUploadedRecord("/00/oow/oow/test.jp"));
	}
	public static void testDeleteDB() {
		DataOperate db = new DataOperate();
		System.out.println(db.deleteRecord());
	}
	public static void testBatchDB() throws IOException {
		DataOperate db = new DataOperate();
//		db.batchInserRecord("/home/meng/workspace/FileUpload/src/data.txt", "192.168.1.1");
	}
	 public static void pressImage(String pressImg, String targetImg) {
	        try {
	            //目标文件
	            File _file = new File(targetImg);
	            Image src = ImageIO.read(_file);
	            int wideth = src.getWidth(null);
	            int height = src.getHeight(null);
	            BufferedImage image = new BufferedImage(wideth, height,
	                    BufferedImage.TYPE_INT_RGB);
	            Graphics g = image.createGraphics();
	            g.drawImage(src, 0, 0, wideth, height, null);
	 
	            //水印文件
	            File _filebiao = new File(pressImg);
	            Image src_biao = ImageIO.read(_filebiao);
	            int wideth_biao = src_biao.getWidth(null);
	            int height_biao = src_biao.getHeight(null);
	            g.drawImage(src_biao, (wideth - wideth_biao) / 2,
	                    (height - height_biao) / 2, wideth_biao, height_biao, null);
	            //水印文件结束
	            g.dispose();
	            FileOutputStream out = new FileOutputStream(targetImg);
	            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	            encoder.encode(image);
	            out.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
