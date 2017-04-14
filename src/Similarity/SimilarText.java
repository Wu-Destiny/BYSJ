package Similarity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.huaban.analysis.jieba.JiebaSegmenter;

public class SimilarText {
	public static double getTextSimilarity(String doc1, String doc2) {
		
		List<String> strings1 = NameSimilarity.segmenter.sentenceProcess(doc1.replace(" ", ""));
		List<String> strings2 = NameSimilarity.segmenter.sentenceProcess(doc2.replace(" ", ""));

		ArrayList<Integer> ints1 = new ArrayList<>();
		ArrayList<Integer> ints2 = new ArrayList<>();

		int i = 0;
		for (String word : strings1) {
			if (word.equals(":") || word.equals("：")) {
				ints1.add(i);
			}
			i++;
		}

		i = 0;
		for (String word : strings2) {
			if (word.equals(":") || word.equals("：")) {
				ints2.add(i);
			}
			i++;
		}

		Map<String, String> map1 = new HashMap<>();
		for (int j = 0; j < ints1.size(); j++) {
			String key = strings1.get(ints1.get(j) - 1);
			String value = "";
			if (j == ints1.size() - 1) {
				for (int k = ints1.get(j) + 1; k < strings1.size(); k++) {
					value += strings1.get(k);
				}
			} else {
				for (int k = ints1.get(j) + 1; k < ints1.get(j + 1) - 1; k++) {
					value += strings1.get(k);
				}
			}
			map1.put(key, value);
		}

		Map<String, String> map2 = new HashMap<>();
		for (int j = 0; j < ints2.size(); j++) {
			String key = strings2.get(ints2.get(j) - 1);
			String value = "";
			if (j == ints2.size() - 1) {
				for (int k = ints2.get(j) + 1; k < strings2.size(); k++) {
					value += strings2.get(k);
				}
			} else {
				for (int k = ints2.get(j) + 1; k < ints2.get(j + 1) - 1; k++) {
					value += strings2.get(k);
				}
			}
			map2.put(key, value);
		}
		double weight = 0;
		for (String key : map1.keySet()) {
			for (String KEY : map2.keySet()) {
				if(key.equals(KEY)){
					if(map1.get(key).equals(map2.get(KEY))){
						weight++;
					}
				}
			}
		}
		double x = 1+(weight/10);
		return getSimilarity(doc1,doc2)*x;
	}

	public static double getSimilarity(String doc1, String doc2) {
		if (doc1 != null && doc1.trim().length() > 0 && doc2 != null && doc2.trim().length() > 0) {

			Map<Integer, int[]> AlgorithmMap = new HashMap<Integer, int[]>();

			// 将两个字符串中的中文字符以及出现的总数封装到，AlgorithmMap中
			for (int i = 0; i < doc1.length(); i++) {
				char d1 = doc1.charAt(i);
				if (isHanZi(d1)) {
					int charIndex = getGB2312Id(d1);
					if (charIndex != -1) {
						int[] fq = AlgorithmMap.get(charIndex);
						if (fq != null && fq.length == 2) {
							fq[0]++;
						} else {
							fq = new int[2];
							fq[0] = 1;
							fq[1] = 0;
							AlgorithmMap.put(charIndex, fq);
						}
					}
				}
			}

			for (int i = 0; i < doc2.length(); i++) {
				char d2 = doc2.charAt(i);
				if (isHanZi(d2)) {
					int charIndex = getGB2312Id(d2);
					if (charIndex != -1) {
						int[] fq = AlgorithmMap.get(charIndex);
						if (fq != null && fq.length == 2) {
							fq[1]++;
						} else {
							fq = new int[2];
							fq[0] = 0;
							fq[1] = 1;
							AlgorithmMap.put(charIndex, fq);
						}
					}
				}
			}

			Iterator<Integer> iterator = AlgorithmMap.keySet().iterator();
			double sqdoc1 = 0;
			double sqdoc2 = 0;
			double denominator = 0;
			while (iterator.hasNext()) {
				int[] c = AlgorithmMap.get(iterator.next());
				denominator += c[0] * c[1];
				sqdoc1 += c[0] * c[0];
				sqdoc2 += c[1] * c[1];
			}

			return denominator / Math.sqrt(sqdoc1 * sqdoc2);
		} else {
			throw new NullPointerException(" the Document is null or have not cahrs!!");
		}
	}

	public static boolean isHanZi(char ch) {
		// 判断是否汉字
		return (ch >= 0x4E00 && ch <= 0x9FA5);

	}

	/**
	 * 根据输入的Unicode字符，获取它的GB2312编码或者ascii编码，
	 * 
	 * @param ch
	 *            输入的GB2312中文字符或者ASCII字符(128个)
	 * @return ch在GB2312中的位置，-1表示该字符不认识
	 */
	public static short getGB2312Id(char ch) {
		try {
			byte[] buffer = Character.toString(ch).getBytes("GB2312");
			if (buffer.length != 2) {
				// 正常情况下buffer应该是两个字节，否则说明ch不属于GB2312编码，故返回'?'，此时说明不认识该字符
				return -1;
			}
			int b0 = (int) (buffer[0] & 0x0FF) - 161; // 编码从A1开始，因此减去0xA1=161
			int b1 = (int) (buffer[1] & 0x0FF) - 161; // 第一个字符和最后一个字符没有汉字，因此每个区只收16*6-2=94个汉字
			return (short) (b0 * 94 + b1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void main(String[] args) {
		NameSimilarity.segmenter = new JiebaSegmenter();
		System.out.println(getTextSimilarity(
				"品牌： 华为/HUAWEI型号：畅享6机身颜色： 白色 金色 灰色像素： 1000-1600万网络： 双卡 移动4G 联通4G 电信4G 移动4G/联通4G/电信4G屏幕尺寸： 5.0-4.6英寸CPU核数： 八核系统： 安卓（Android）机身内存： 16GB运行内存： 3GB电池容量： 4000mAh-5999mAh",
				"品牌： 小米型号：小米mix机身颜色： 黑色像素： 1600万以上网络： 移动4G 联通4G 电信4G 移动4G/联通4G/电信4G屏幕尺寸： 5.6英寸及以上CPU核数： 其它系统： 安卓（Android）机身内存： 256GB 128GB运行内存： 4GB电池容量： 4000mAh-5999mAh"));
	}
}