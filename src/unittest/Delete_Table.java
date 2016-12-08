package unittest;

import java.sql.PreparedStatement;

public class Delete_Table{
	static jdbc.Sqldrivre db = null;

	static void delete(String tablename){
		
		String sql = "truncate table "+tablename;
		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			pst.executeUpdate(sql);  
			pst.close();
		} catch (Exception e) {
            e.printStackTrace();  
		}		
		
	}
	
	static void insert(){
		
	}
	
	public static void main(String[] args) {
		db = new jdbc.Sqldrivre();
		
		delete("index_name");
		
		db.close();
	}
}
