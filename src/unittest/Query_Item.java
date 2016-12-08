package unittest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Query_Item {
	
	static String sql = null;  
    static jdbc.Sqldrivre db1 = null;  
    static ResultSet ret = null;  
  
    public static void main(String[] args) {  
        sql = "select *from item;";
        db1 = new jdbc.Sqldrivre();
  
        try {  
        	
        	PreparedStatement pst = db1.conn.prepareStatement(sql);
            ret = pst.executeQuery();
            while (ret.next()) {  
                String uid = ret.getString(1);  
                String ufname = ret.getString(2);  
                String ulname = ret.getString(3);  
                String udate = ret.getString(4);  
                String source = ret.getString(5);  
                System.out.println(uid + "\t" + ufname + "\t" + ulname + "\t" + udate+"\t"+source );  
            }
            ret.close();  
            pst.close();
            db1.close(); 
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
    
}