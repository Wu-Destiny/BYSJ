package crawl;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ItemSearch {

	static String sql = null;
	static ResultSet ret = null;

	public ItemSearch() {

	}

	static public void SearchItem(String SOURCE) {
		int count = 0;
		sql = "select *from itemlist where source = '" + SOURCE + "'";
		try {
			PreparedStatement pst = Item.db.conn.prepareStatement(sql);
			ret = pst.executeQuery();
			while (ret.next()) {
				count++;
				String id = ret.getString(1);
				String uid = ret.getString(2);
				String url = ret.getString(3);
				String keyword = ret.getString(4);
				String source = ret.getString(5);
				switch (SOURCE) {
				case "Tmall":
					tmall(url, uid, keyword);
					break;
				case "Jd":
					jd(url, uid, keyword);
					break;
				case "Ymx":
					ymx(url, uid, keyword);
					break;
				case "Dd":
					dd(url, uid, keyword);
					break;
				default:
					System.out.println("You input a wrong SOURCE in ItemSearch.");
				}
				// System.out.println(id + "\t" + uid + "\t" + url + "\t" +
				// keyword + "\t" + source);
			}
			ret.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static void tmall(String url, String uid, String keyword) {
		String source = "Tmall";
		url = "https:" + url;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
				String urlString = doc.toString();
				int priceIndex = urlString.indexOf("defaultItemPrice");
				urlString = urlString.substring(priceIndex, urlString.length());
				int i = urlString.indexOf(",");
				String price = urlString.substring(19, i - 1);
				Element pic = doc.getElementById("J_ImgBooth");
				String name = pic.attr("alt");
				String picurl = pic.attr("src");

				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateNowStr = sdf.format(d);
				// System.out.println("格式化后的日期：" + dateNowStr);
				String price_date = price + "_" + dateNowStr;
				Element details = doc.getElementById("J_AttrUL");
				String detail = details.text();
				Item item = new Item(name, price_date, url, picurl, detail, uid, source, keyword);
				Item.insert(item);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					response1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static void jd(String url, String uid, String keyword) {
		String source = "Jd";
		url = "https:" + url;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				String name = doc.getElementById("spec-img").attr("alt");
				String picurl = doc.getElementById("spec-img").attr("data-origin");
//				System.out.println(picurl);
				Elements details = doc.getElementsByClass("detail");
				String detail = "";
				int i = 0;
				for (Element e : details) {
					i++;
					if (i == 1)
						continue;
					detail += e.text();
				}
				String fuckurl = "http://search.jd.com/Search?keyword=" +URLEncoder.encode(name) + "&enc=utf-8";
				
				httpGet = new HttpGet(fuckurl);
				response1 = httpclient.execute(httpGet);

				entity1 = response1.getEntity();
				doc = Jsoup.parse(EntityUtils.toString(entity1));
							
				EntityUtils.consume(entity1);
				Elements goods = doc.getElementsByClass("J_"+uid);
				String price = goods.get(0).attr("data-price");
				
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateNowStr = sdf.format(d);
				// System.out.println("格式化后的日期：" + dateNowStr);
				String price_date = price + "_" + dateNowStr;
				
				 Item item = new Item(name, price_date, url, picurl, detail,
				 uid, source, keyword);
//				 System.out.println(item.toString());
				 Item.insert(item);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					response1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static void ymx(String url, String uid, String keyword) {
		String source = "Ymx";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
				String name = doc.getElementById("imgTagWrapperId").getElementsByTag("img").get(0).attr("alt");
				String picurl = doc.getElementById("imgTagWrapperId").getElementsByTag("img").get(0).attr("src");
				String price = doc.getElementById("priceblock_ourprice").text().replace("￥", "");
				Elements details = doc.getElementsByClass("content").get(0).getElementsByTag("li");
				String detail = "";
				for(int i = 0; i < details.size()-4;i++){
					detail += details.get(i).text();
				}
				
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateNowStr = sdf.format(d);
				// System.out.println("格式化后的日期：" + dateNowStr);
				String price_date = price + "_" + dateNowStr;
				
				Item item = new Item(name, price_date, url, picurl, detail, uid, source, keyword);
//				System.out.println(item.toString());
				Item.insert(item);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					response1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static void dd(String url, String uid, String keyword) {
		String source = "Dd";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
			//httpGet.addHeader("Cookie", "__permanent_id=20161205214817230140079115853531167; pos_9_end=1481184794423; pos_0_end=1481184794517; pos_0_start=1481184804915; ad_ids=2212733%2C2212548%2C1738929%7C%231%2C1%2C1; __dd_token_id=2016120816210642781828899619ebec; permanent_key=20161208162106369935635701435d2b; login.dangdang.com=.AYH=2016120816210712071690891&.ASPXAUTH=5w9EySB5OXyXCh1K47nDEp0wcDAB2CJ/ZWrZRt6IOhmp9AipdMlCOA==; dangdang.com=email=RTYyMUFERDdCNjBDNTBGNEVBQUJAcXFfdXNlci5jb20=&nickname=RGVzdGlueQ==&display_id=2713756395205&customerid=9y4WVAKixbxfh2t6ArLQew==&viptype=&show_name=Destiny; ddoy=email=E621ADD7B60C50F4EAAB%40qq_user.com&nickname=Destiny&validatedflag=0&agree_date=1; sessionID=pc_f0b482a3e9be5ff4ae2e59524f5b370c83e2eb29b043889486e4be5bb0e4fa4d; order_follow_source=-%7C-O-123%7C%2311%7C%23login_third_qq%7C%230%7C%23; LOGIN_TIME=1481185274874; MDD_sid=af11744f4d1afcab584904c7adb20985; MDD_permanent_id=20161208162217968386850154730595955; MDD_producthistoryids=1295182939; __xsptplusUT_100=1; ddscreen=2; __visit_id=20161208161307765328435118013132929; __out_refer=1481184788%7C!%7Cwww.baidu.com%7C!%7C; __trace_id=20161208163013053227769192989710389; dest_area=country_id%3D9000%26province_id%3D111%26city_id%3D1%26district_id%3D1110101%26town_id%3D-1; _jzqco=%7C%7C%7C%7C%7C1.1582065553.1480945715242.1481185804374.1481185813588.1481185804374.1481185813588.0.0.0.16.16; __xsptplus100=100.2.1481184794.1481185813.9%234%7C%7C%7C%7C%7C%23%23a1hFnnQCxjLighikvmreLa5KEompHvN2%23; pos_1_start=1481185814349; pos_1_end=1481185814408; producthistoryid=1022143948%2C1215542536%2C1295182939; NTKF_T2D_CLIENTID=guestTEMP3D6D-FEEB-5E46-A343-CF401D9759C5; nTalk_CACHE_DATA={uid:dd_1000_ISME9754_225501206,tid:1481184800488857}");
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
//				System.out.println(url);
//				System.out.println(doc.toString());
				String name = doc.getElementById("largePic").attr("alt");
				String picurl = doc.getElementById("largePic").attr("src");
				String price = doc.getElementById("dd-price").text().replace("¥", "");
				String detail = doc.getElementById("detail_all").text();

				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateNowStr = sdf.format(d);
				// System.out.println("格式化后的日期：" + dateNowStr);
				String price_date = price + "_" + dateNowStr;
				
				Item item = new Item(name, price_date, url, picurl, detail, uid, source, keyword);
//				System.out.println(item.toString());
				Item.insert(item);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					response1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Item.db = new jdbc.Sqldrivre();
		SearchItem("Tmall");
//		SearchItem("Jd");
//		SearchItem("Ymx");
//		SearchItem("Dd");
		Item.db.close();
	}
}
