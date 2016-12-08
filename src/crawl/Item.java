package crawl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Item {
	private String name;
	private String price;
	private String url;
	private String picurl;
	private String detail;
	private String uid;
	private String source;
	private String keyword;

	public Item(String name, String price, String url, String picurl, String detail, String uid, String source,
			String keyword) {
		super();
		this.name = name;
		this.price = price;
		this.url = url;
		this.picurl = picurl;
		this.detail = detail;
		this.uid = uid;
		this.source = source;
		this.keyword = keyword;
	}

	public static jdbc.Sqldrivre db = null;

	static void delete() {

		String sql = "truncate table item;";

		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			int i = pst.executeUpdate(sql);
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void insert(Item item) {

		String sql = "select * from item where url = ?";
		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);
			pst.setString(1, item.url);
			ResultSet ret = pst.executeQuery();
			ret.last(); // 结果集指针知道最后一行数据
			int n = ret.getRow();
			if (n == 1)
				if (!ret.getString(8).substring(ret.getString(8).indexOf("_"), ret.getString(8).length()).
						equals(item.price.substring(item.price.indexOf("_"),item.price.length()))) {
					System.out.println("Update "+item.price+" "+item.name);
					return;
				} else
					return;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		sql = "INSERT INTO `item` (`name`, `url`, `picurl`, `detail`, `uid`, `source`, `keyword`, `prices_time`, `id`) VALUES (?,?,?,?,?,?,?,?,NULL);";

		try {
			PreparedStatement pst = db.conn.prepareStatement(sql);

			pst.setString(1, item.name);
			pst.setString(2, item.url);
			pst.setString(3, item.picurl);
			pst.setString(4, item.detail);
			pst.setString(5, item.uid);
			pst.setString(6, item.source);
			pst.setString(7, item.keyword);
			pst.setString(8, item.price);

			int flag = pst.executeUpdate();

			System.out.println(pst.toString());

			pst.close();

		} catch (MySQLIntegrityConstraintViolationException e) {
			System.err.println("---------------| 未检测出的重复URL: " + item.url + " |---------------");
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", price=" + price + ", url=" + url + ", picurl=" + picurl + ", detail=" + detail
				+ ", uid=" + uid + ", source=" + source + ", keyword=" + keyword + "]";
	}

	public static void main(String[] args) {
		db = new jdbc.Sqldrivre();
		Item i = new Item("fuck", "fuck", "fuck", "fuck", "fuck", "fuck", "fuck", "fuck");
		insert(i);
		db.close();
	}

}
