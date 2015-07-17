package com.me.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.me.db.conn.DBConnectionManager;

public class DBHelper {
	//得到唯一实例 
	public static DBConnectionManager   connectionMan = DBConnectionManager .getInstance();
	public static PreparedStatement pst;
	public static Connection  con;

	public static void close() {
		try {
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connectionMan.freeConnection(con);//释放，但并未断开连接 
	}
	public static PreparedStatement getPreparedStatement(String sql) {
		Connection conn = getConnection();
		try {
			pst = conn.prepareStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pst;
	}
	public static Connection getConnection() {
		try {
			   //得到连接 
			//从上下文得到你要访问的数据库的名字 
			con=connectionMan.getConnection(); 
//			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
}
