package crawl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Itemlist {
	
	public static jdbc.Sqldrivre db = null;

	static void delete(){
		
		String sql = "truncate table itemlist;";
		
		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			int i=pst.executeUpdate(sql);  
			pst.close();
		} catch (Exception e) {
            e.printStackTrace();  
		}		
		
	}
	
	public static void insert(String uid,String url,String keyword,String resource){
		
		String sql = "select * from itemlist where url = ?";
		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			pst.setString(1, url);
			ResultSet ret = pst.executeQuery();
			ret.last(); //结果集指针知道最后一行数据  
			int n = ret.getRow();
			if(n == 1)return;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		sql = "INSERT INTO `itemlist` (`ID`, `UID`, `URL`, `KEYWORD`, `SOURCE`) VALUES (NULL, ?,?,?,?);";	
		
		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			pst.setString(1,uid);
			pst.setString(2, url);
			pst.setString(3, keyword);
			pst.setString(4, resource);
			int flag = pst.executeUpdate();
			System.out.println(pst.toString());
			pst.close();
			
		}catch(MySQLIntegrityConstraintViolationException e){
			System.err.println("---------------| 未检测出的重复URL: " + url +" |---------------");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
		
	public static void main(String[] args) {
		db = new jdbc.Sqldrivre();
		
		for(int i = 0; i < 2; i++){
			insert("1", "-", "艹", "Tmall");
		}
		
		db.close();
	}
	
}
