package Similarity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;

public class NameSimilarity {
	
	static String sql = null;
	static jdbc.Sqldrivre db1 = null;
	static ResultSet ret = null;

	public static void run() {
		sql = "select *from item;";
		db1 = new jdbc.Sqldrivre();
		String id = null;
		String er = null;
		try {
			PreparedStatement pst = db1.conn.prepareStatement(sql);
			ret = pst.executeQuery();
			while (ret.next()) {
				String name = ret.getString(1).replace(" ", "");
				id = ret.getString(9);
				// System.out.println( id + "\t" + name);
				JiebaSegmenter segmenter = new JiebaSegmenter();
				List<String> result = segmenter.sentenceProcess(name);
				for (String r : result) {
					try {
						er = r;
						sql = "INSERT INTO `Index_name` (`Words`, `Ids`,`Words_Id`) VALUES (?,?,?)";
						pst = db1.conn.prepareStatement(sql);
						pst.setString(1, r);
						pst.setString(2, id);
						pst.setString(3, r + id);
						pst.executeUpdate();
					} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
						System.out.println("重复的INDEX " + er + " " + id);
					}
				}
				System.out.println(result);
			}
			ret.close();
			pst.close();
			db1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		run();
	}
}
