package com.me.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 取sql语句
 * @author meng
 *
 */
public class SqlClauseHelper {

	static {

		  InputStream fi = SqlClauseHelper.class.getResourceAsStream("sql.xml") ; 
		  try 
		  { 
		   SAXBuilder sb=new SAXBuilder(); 
		   Document doc=sb.build(fi); 
		   Element root=doc.getRootElement(); 
		   String defaultDB = root.getChild("defaultDB").getText();
		   Element dbElement = root.getChild(defaultDB);
		   
		   insertSql = dbElement.getChildText("insertSql");
		   selectSql = dbElement.getChildText("selectSql");
		   deleteSql = dbElement.getChildText("deleteSql");
		   updateSql = dbElement.getChildText("updateSql");
		   
		  } catch (FileNotFoundException e) { 
		   e.printStackTrace(); 
		  } catch (JDOMException e) { 
		   e.printStackTrace(); 
		  } catch (IOException e) { 
		   e.printStackTrace(); 
		  }  finally 
		  { 
		   try { 
		    fi.close(); 
		   } catch (IOException e) { 
		    e.printStackTrace(); 
		   } 
		  } 
	}
/** 
  * 读取xml配置文件 
  * @param path 
  * @return 
  */ 

private static String insertSql;
private static String selectSql;
private static String deleteSql;
private static String updateSql;

public static String getInsertSql() {
	return insertSql;
}

public static String getSelectSql() {
	return selectSql;
}

public static String getDeleteSql() {
	return deleteSql;
}

public static String getUpdateSql() {
	return updateSql;
}
}
