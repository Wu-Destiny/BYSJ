package jdbc;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.SQLException; 

public class Sqldrivre {
	  public static final String url = "jdbc:mysql://5832049f5a4e8.sh.cdb.myqcloud.com:7098/shopping";  
	    public static final String name = "com.mysql.jdbc.Driver";  
	    public static final String user = "cdb_outerroot";  
	    public static final String password = "skyrimfuck88";  
	  
	    public Connection conn = null;  
	   
	    public Sqldrivre() {  
	        try {  
	            Class.forName(name);
	            conn = DriverManager.getConnection(url, user, password);
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    public void close() {  
	        try {  
	            this.conn.close();  
	        } catch (SQLException e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
