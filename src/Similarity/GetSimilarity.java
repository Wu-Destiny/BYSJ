package Similarity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.URIResolver;

import com.huaban.analysis.jieba.JiebaSegmenter;

public class GetSimilarity {

	public static final String IP_ADDR = "127.0.0.1";//
	public static final int PORT = 18888;

	static String mydetail = "";
	static String mypicurl = "";
	static String sql = null;
	public static jdbc.Sqldrivre db1 = null;
	static ResultSet ret = null;
	public static String[] stopwords;

	public GetSimilarity() {
	}

	public static List<String> Search(String input, String detail, String picurl) {

		GetSimilarity.mydetail = detail;
		GetSimilarity.mypicurl = picurl;
		try {
			download(mypicurl, "PHONE1.jpg", "D:\\Share\\OPENCV\\OPENCV");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<String> Idds = segmenter.sentenceProcess(input);
		Map<String, Integer> Ids = new HashMap<>();

		for (String r : Idds) {
			boolean flag = false;
			for (String word : stopwords) {
				if (r.equals(word)) {
					flag = true;
					// System.out.println("Stop "+r);
					break;
				}
			}
			if (flag)
				continue;
			sql = "select *from index_name where Words = '" + r + "';";
			PreparedStatement pst;
			try {
				pst = db1.conn.prepareStatement(sql);

				ret = pst.executeQuery();
				while (ret.next()) {
					String id = ret.getString(2);
					if (!Ids.containsKey(id)) {
						Ids.put(id, 1);
					} else {
						Ids.put(id, Ids.get(id) + 1);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		Set<String> keys = Ids.keySet();
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(Ids.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		List<String> result = new ArrayList<>();
		for (int i = 0; i < (infoIds.size() < 21 ? infoIds.size() : 21); i++) {
			String id = infoIds.get(i).toString().split("=")[0];
			result.add(id);
		}
		return result;
	}

	public static List<String> Imformation(List<String> Ids) {
		Map<String, Integer> result = new HashMap<>();
		for (String id : Ids) {
			sql = "select *from item where id = '" + id + "';";
			PreparedStatement pst;
			try {
				pst = db1.conn.prepareStatement(sql);
				ret = pst.executeQuery();
				while (ret.next()) {
					String name = ret.getString(1);
					String url = ret.getString(2);
					String picurl = ret.getString(3);
					String detail = ret.getString(4);
					String source = ret.getString(6);
					String price = ret.getString(8).split("_")[0];
					if (price.contains("-")) {
						price = price.split("-")[0];
					}
					String key = name + "%%" + url + "%%" + price + "%%" + source;
					try {
						download(picurl, "PHONE2.jpg", "D:\\Share\\OPENCV\\OPENCV");
					} catch (Exception e1) {
						System.err.println("图片地址失效");
					}
					double TextSi = SimilarText.getSimilarity(mydetail, detail);
					key += "%%" + (int) (TextSi * 100);
					int value = (int) (TextSi * 100 / 2*0.8);
					int picValue = 0;

					try {
						Socket socket = new Socket(IP_ADDR, PORT);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintWriter out = new PrintWriter(socket.getOutputStream());
						String msg = "8";
						out.println(msg);
						out.flush();
						picValue = Integer.valueOf(in.readLine());
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					key += "%%" + picValue;
					key += "%%" + picurl;
					value += picValue*1.5/2;
					System.out.println("value :"+picValue+" "+value);
					result.put(key, value);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		Set<String> keys = result.keySet();
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(result.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});

		List<String> ret = new ArrayList<>();
		int size = 10;
		if(infoIds.size() <= 10  )size = infoIds.size();
		for (int i = 0; ret.size() < size; i++) {
			if(i==21)break;
			String id = infoIds.get(i).toString().split("=")[0];
			// System.out.println(id);
			if (id.split("%%").length == 7) {
				ret.add(id);
			}
		}
		List<String> rets = new ArrayList<>();
		while(!ret.isEmpty()){
			double min = Integer.MAX_VALUE;
			int index = 0;
			for(int i = 0; i < ret.size();i++	){
				String[] ks = ret.get(i).split("%%");
				double price = Double.valueOf(ks[2]);
				if(price<=min){
					min = price;
					index = i;
				}
			}
			rets.add(ret.get(index));
			ret.remove(index);
		}
		return rets;
	}

	public static void display(List<String> rets) {
		for (String key : rets) {
			try {
				String[] keys = key.split("%%");
				System.out.println("|--------------------------|");
				System.out.println("| 名称 " + keys[0]);
				System.out.println("| 网址 " + keys[1]);
				System.out.println("| 价格 " + keys[2]);
				System.out.println("| 所属 " + keys[3]);
				System.out.println("| 文本相似度 " + keys[4]);
				System.out.println("| 图片相似度 " + keys[5]);
				System.out.println("|--------------------------|");
			} catch (Exception e) {

			}
		}
	}

	public static String txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while ((s = br.readLine()) != null) {
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public static void download(String urlString, String filename, String savePath) throws Exception {
		if (!urlString.contains("http")) {
			urlString = "http:" + urlString;
		}
		if (urlString.contains("http:////")) {
			urlString = urlString.replace("http:////", "http://");
		}
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(5 * 1000);
		InputStream is = con.getInputStream();

		byte[] bs = new byte[1024];
		int len;
		File sf = new File(savePath);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
	}

	public static void main(String[] args) {
		File file = new File("src/stop.txt");
		stopwords = txt2String(file).split(" ");
		db1 = new jdbc.Sqldrivre();
		List<String> Ids = Search("华为 HUAWEI 华为畅享6 全网通版 3GB+16GB 5.0英寸 八核 双卡双待 智能手机 畅享6",
				"品牌： 华为/HUAWEI型号：畅享6机身颜色： 白色 金色 灰色像素： 1000-1600万网络： 双卡 移动4G 联通4G 电信4G 移动4G/联通4G/电信4G屏幕尺寸： 5.0-4.6英寸CPU核数： 八核系统： 安卓（Android）机身内存： 16GB运行内存： 3GB电池容量： 4000mAh-5999mAh",
				"http://img3x9.ddimg.cn/94/11/1295182939-1_k_4.jpg");
		display(Imformation(Ids));
	}
}
