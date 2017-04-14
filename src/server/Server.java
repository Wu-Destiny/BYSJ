package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.sun.jna.platform.unix.X11.Xrender.PictFormat;

import Similarity.GetSimilarity;
import Similarity.NameSimilarity;

public class Server {
	public static final int PORT = 12345;// 监听的端口号

	public static void main(String[] args) {
		NameSimilarity.segmenter = new JiebaSegmenter();
		File file = new File("src/stop.txt");
		GetSimilarity.stopwords = GetSimilarity.txt2String(file).split(" ");
		GetSimilarity.db1 = new jdbc.Sqldrivre();

		System.out.println("服务器启动...\n");
		Server server = new Server();
		server.init();
	}

	public void init() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				// 一旦有堵塞, 则表示服务器与客户端获得了连接
				Socket client = serverSocket.accept();
				// 处理这次连接
				new HandlerThread(client);
			}
		} catch (Exception e) {
			System.out.println("服务器异常: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private class HandlerThread implements Runnable {
		private Socket socket;

		public HandlerThread(Socket client) {
			socket = client;
			new Thread(this).start();
		}

		public void run() {
			try {
				// 读取客户端数据
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				String clientInputStr = input.readLine();
				System.out.println(clientInputStr);
				String[] tags = clientInputStr.split("=");
				System.out.println(tags.length);
				String name = URLDecoder.decode(tags[1]).split("&")[0];
				String picurl = URLDecoder.decode(tags[2]).split("&")[0];
				String describe = URLDecoder.decode(tags[3]).replace(" ", "");
				System.out.println(name);
				System.out.println(picurl);
				System.out.println(describe);
				
				List<String> Ids = GetSimilarity.Search(name, describe, picurl);
				String result = "";
				List<String> ret = GetSimilarity.Imformation(Ids);
				for (String key : ret) {
					result += key + "&&";
				}
				GetSimilarity.display(ret);
				out.write(result.getBytes());
				out.flush();
				out.close();
				System.out.println("发送完成");
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("服务器 run 异常: " + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (Exception e) {
						socket = null;
						System.out.println("服务端 finally 异常:" + e.getMessage());
					}
				}
			}
		}
	}
}