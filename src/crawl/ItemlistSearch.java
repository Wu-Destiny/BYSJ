package crawl;

import java.awt.font.NumericShaper.Range;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ItemlistSearch {

	static int count = 0;

	static void SearchTm(String query, int page) throws ClientProtocolException, IOException {
		String q = query;
		query = URLEncoder.encode(query);
		String s = String.valueOf(page * 60);
		String url = "https://list.tmall.com/search_product.htm?q=" + query + "&s=" + s;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
					String linkHref = link.attr("href");
					if (linkHref.contains("detail")) {
						linkHref = linkHref.substring(0, linkHref.indexOf('&'));
						String uid = linkHref.substring(linkHref.indexOf('=') + 1, linkHref.length());
						count++;
						System.out.println(count + " " + linkHref + " " + uid);
						Itemlist.insert(uid, linkHref, q, "Tmall");
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
	}

	static void SearchJd(String query, int page) throws ClientProtocolException, IOException {
		String q = query;
		query = URLEncoder.encode(query);
		String s = String.valueOf(page);
		String url = "http://search.jd.com/Search?keyword=" + query + "&enc=utf-8&page=" + s;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
					String linkHref = link.attr("href");
					if (linkHref.contains("item") && !linkHref.contains("#")) {
						String uid = linkHref.substring(14, linkHref.length() - 5);
						count++;
						System.out.println(count + " " + linkHref + " " + uid);
						Itemlist.insert(uid, linkHref, q, "Jd");
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
	}

	static void SearchYmx(String query, int page) throws ClientProtocolException, IOException {
		String q = query;
		query = URLEncoder.encode(query);
		String s = String.valueOf(page + 1);
		String url = "https://www.amazon.cn/s/field-keywords=" + query + "&page=" + s;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
					String linkHref = link.attr("href");
					if (linkHref.contains("dp") && !linkHref.contains("#")) {
						String type = linkHref.substring(22, linkHref.indexOf("dp") - 1);
						if (type.contains(query)) {
							String uid = linkHref.substring(linkHref.indexOf("dp") + 3, linkHref.length());
							count++;
							System.out.println(count + " " + linkHref + " " + uid);
							Itemlist.insert(uid, linkHref, q, "Ymx");
						}
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
	}
	
	static void SearchDd(String query, int page) throws ClientProtocolException, IOException {
		String q = query;
		query = URLEncoder.encode(query);
		String s = String.valueOf(page + 1);
		String url = "http://search.dangdang.com/?key="+query+"&act=input" + "&page_index=" + s;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			try {
				HttpEntity entity1 = response1.getEntity();
				Document doc = Jsoup.parse(EntityUtils.toString(entity1));
				EntityUtils.consume(entity1);
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
					String linkHref = link.attr("href");
					if (linkHref.contains("product") && !linkHref.contains("#")) {
						String uid = linkHref.substring(28, linkHref.length() - 5);
						count++;
						System.out.println(count + " " + linkHref + " " + uid);
						Itemlist.insert(uid, linkHref, q, "Dd");
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
	}

	public static void main(String[] args) throws Exception {
		Itemlist.db = new jdbc.Sqldrivre();

		for (int i = 0; i < 100; i++) {
//			SearchTm("手机", i);
//			SearchJd("手机", i);
//			SearchYmx("手机", i);
			SearchDd("手机", i);
		}
		Itemlist.db.close();
	}

}
