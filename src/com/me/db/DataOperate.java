package com.me.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据表操作
 * @author meng
 *
 */
public class DataOperate {
	public Logger log = LoggerFactory.getLogger(DataOperate.class);
	 /**
     * 解析数据文件，批量插入
     * @param fileStream
	 * @param inetSocketAddress 
     * @throws IOException 
     */
    public void batchInserRecord(InputStream fileStream, String address) throws IOException {
    	BufferedReader inputStream = new BufferedReader(new InputStreamReader(fileStream, "UTF-8")) ;
		try{
    		String line = null;
    		int num = 0;
    		String sql = SqlClauseHelper.getInsertSql();
//    		String sql = "insert ignore into test(menu, mode, imgurl, imgname, imgset, mark, sort, isself, isload, createtime, ip) values(?,?,?,?,?,?,?,?,?,?,?)";
    		PreparedStatement pst = DBHelper.getPreparedStatement(sql);
    		Pattern p = Pattern.compile("\\(([^)]+)\\)|（([^）]+)）");
    		//读取数据文件，并插入数据库
    		while((line = inputStream.readLine()) != null) {
    			if(line == null || line.trim().equals("")) {
    				continue;
    			}
    			String [] datas = line.split("\\|\\|");
    			//栏目
    			pst.setString(1, datas[2]);
    			//版块
    			pst.setString(2, datas[3]);
    			
    			String absoluteFile = datas[0]+"/"+datas[1];
        		if(datas[0].endsWith("/")) {
        			absoluteFile = datas[0] + datas[1];
        		}
    			//图片全路径
    			pst.setString(3, absoluteFile);
    			//图片名称
    			pst.setString(4, datas[1]);
    			//图片集
    			pst.setString(5, datas[0]);
    			
    			StringBuffer mark = new StringBuffer();
    			String[] marks = datas[0].split("/");
    			for(String dir : marks) {
    				Matcher m =p.matcher(dir);
    				while(m.find()){
    					String markStr = "";
    					if(m.group(1) != null) {
    						markStr = m.group(1);
    					} else {
    						markStr = m.group(2);
    					}
    					markStr = markStr.replace('-', '/').replace('_','/');
    					mark.append(markStr+"、");
    				}
    			}
    			if(mark.length() > 0)mark.delete(mark.length()-1,mark.length());
    			//标签
    			pst.setString(6, mark.toString());
    			//排序 固定为0
    			pst.setInt(7, 0);
    			//是否上架 固定为0
    			pst.setString(8, "0");
    			//是否上传 0 否，1 是
    			pst.setString(9, "0");
    			//上传时间
    			pst.setTimestamp(10, new Timestamp(new Date().getTime()));
    			//IP地址
    			pst.setString(11, address);
    			
    			pst.addBatch();
    			
    			num++;
    			if(num == 1000) {
    				pst.executeBatch();
    				num = 0;
    			}
    		}
    		pst.executeBatch();
		} catch (SQLException e) {
			log.info("batch:"+e.getMessage(), e);
		} finally {
			inputStream.close();
			DBHelper.close();
		}
    }
	/**
	 * 更新记录
	 */
	public int updateRecord(String imgurl, String address) {
		String sql = SqlClauseHelper.getUpdateSql();
//		String sql = "update test set isload = '1'  where imgurl = ? and ip = ?";
		int result = 0;
		PreparedStatement pst = DBHelper.getPreparedStatement(sql);
		
    	try {
    		//图片全路径
    		pst.setString(1, imgurl);
    		//IP地址
    		pst.setString(2, address);
    		
			result = pst.executeUpdate();
		} catch (SQLException e) {
			log.info("update:"+e.getMessage(), e);
		} finally {
			DBHelper.close();//关闭连接
		}
    	return result;
	}
	/**
	 * 删除24小时前的还没有上传文件的记录
	 * @return
	 */
	public int deleteRecord() {
		String sql = SqlClauseHelper.getDeleteSql();
//		String sql = "delete from test where isload = '0'  and createTime+1000000 < sysdate()+0 ";
		int result = 0;
		PreparedStatement pst = DBHelper.getPreparedStatement(sql);
		
    	try {
			result = pst.executeUpdate();
		} catch (SQLException e) {
			log.info("delete:"+e.getMessage(), e);
		} finally {
			DBHelper.close();//关闭连接
		}
    	return result;
	}
	/**
	 * 查询是否存在记录
	 * @return
	 */
	public String queryUploadedRecord(String imgurl) {
		String sql = SqlClauseHelper.getSelectSql();
//		String sql = "select distinct 1 from test where imgurl = ?";
		PreparedStatement pst = DBHelper.getPreparedStatement(sql);
		String isload = null;
		ResultSet rs = null;
    	try {
    		//图片全路径
    		pst.setString(1, imgurl);
    		
			rs = pst.executeQuery();
			while(rs.next()) {
				isload = rs.getString(1);
			}
		} catch (SQLException e) {
			log.info("query:"+e.getMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			DBHelper.close();//关闭连接
		}
		return isload;
	}
	
}
