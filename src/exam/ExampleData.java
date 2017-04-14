package exam;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawl.Itemlist;

public class ExampleData {
	public static void main(String[] args) {
		getExampleData("阿迪达斯 adidas neo 男子 休闲鞋 DAILY");
	}
	
	static ArrayList<String> getTM(String query, int page) throws ClientProtocolException, IOException {
		ArrayList<String> ret = new ArrayList<>();
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
						ret.add(linkHref);
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
		return ret;
	}
	
	static ArrayList<String> getJD(String query, int page) throws ClientProtocolException, IOException {
		ArrayList<String> ret = new ArrayList<>();
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
						ret.add(linkHref);
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
		return ret;
	}

	static ArrayList<String> getYMX(String query, int page) throws ClientProtocolException, IOException {
		ArrayList<String> ret = new ArrayList<>();
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
					try {
						String type = linkHref.substring(22, linkHref.indexOf("dp") - 1);
						if (type.contains(query)) {
							ret.add(linkHref);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.err.println(linkHref);
						e.printStackTrace();
					}
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
		return ret;
	}
	
	static ArrayList<String> getDD(String query, int page) throws ClientProtocolException, IOException {
		ArrayList<String> ret = new ArrayList<>();
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
						ret.add(linkHref);
					}
				}
			} finally {
				response1.close();
			}

		} finally {
			httpclient.close();
		}
		return ret;
	}
	
	public static ArrayList<String> getExampleData(String query){
		ArrayList<String> results = new ArrayList<>();
		ArrayList<String> tm = new ArrayList<>();
		ArrayList<String> jd = new ArrayList<>();
		ArrayList<String> ymx = new ArrayList<>();
		ArrayList<String> dd = new ArrayList<>();
		try {
			tm = getTM(query, 0);
			jd = getJD(query, 0);
			ymx = getYMX(query, 0);
			dd = getDD(query, 0);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(tm);
		System.out.println(jd);
		System.out.println(ymx);
		System.out.println(dd);
		results.addAll(tm);
		results.addAll(jd);
		results.addAll(ymx);
		results.addAll(dd);
		return results;
	}
}
