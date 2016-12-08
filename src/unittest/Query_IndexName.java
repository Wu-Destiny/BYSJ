package unittest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Query_IndexName {

	static String sql = null;  
    static jdbc.Sqldrivre db1 = null;  
    static ResultSet ret = null;  
  
    public static void main(String[] args) {  
        sql = "select *from index_name;";
        db1 = new jdbc.Sqldrivre();
        int count = 0;
        try {  
        	
        	PreparedStatement pst = db1.conn.prepareStatement(sql);
            ret = pst.executeQuery();
            while (ret.next()) {  
                String uid = ret.getString(1);  
                String ufname = ret.getString(2);  
                count ++;
                System.out.println(count + " "+uid + "\t" + ufname);  
            }
            ret.close();  
            pst.close();
            db1.close(); 
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}
