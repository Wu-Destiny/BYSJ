package unittest;

import java.sql.PreparedStatement;

public class Delete_condition {
	static jdbc.Sqldrivre db = null;

	static void delete(String tablename, String key,String value) {

		String sql = "DELETE FROM " + tablename + " WHERE "+key+ " = '"+value+"'";
		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			//pst.setString(1,source);
			System.out.println(pst.toString());
			int n = pst.executeUpdate(sql);
			System.out.println("------删除了 "+n+"行------");
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void insert() {
		
	}

	public static void main(String[] args) {
		db = new jdbc.Sqldrivre();

		delete("item","SOURCE", "Tmall");

		db.close();
	}
}
