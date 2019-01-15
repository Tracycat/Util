package Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;

public class HttpClientUtil {
	
	public static Logger logger = Logger.getLogger(HttpClientUtil.class);
	
	public static void main(String[] args) {
		try {
			//测试调用模拟浏览器跨域发送请求JSON数据
			String url = "http://localhost:8080/INFO_DMS_WEB/JsonInterfaceChannel?";
			//URL后拼接参数
			String params = "{action:SA010,DEALER_CODE:DNML010,CUSTOMER_NO:[CL1609280019],NET_PIN_CODE:[4132000000062]}";							
			String result = HttpClientUtil.request(null, url, params, "form", "UTF-8");
			System.out.println(result);				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
		//完整请求过程
	public static String request(String sessionId, String url, String params,
			String contentType, String charset) {
		HttpURLConnection connection = null;
		OutputStream output = null;
		InputStream input = null;
		BufferedReader reader = null;
		// 响应状态
		int responseCode = 0;
		String uuid = UUID.randomUUID().toString();
		byte[] buffer = null;
		try {
			// 获取连接对象
			connection = getConnection(uuid, sessionId, url, "POST", contentType, charset);
			if (params != null && !params.isEmpty()) {
				// 转换为字节数组，以字节流传输，解决中文乱码传输问题
				buffer = params.getBytes(charset);
			} else {
				buffer = "".getBytes(charset);
			}
			// 设置文件长度
			connection.setRequestProperty("Content-Length", String.valueOf(buffer.length));
			// 开始连接请求
			connection.connect();
			// 获取输出流
			output = connection.getOutputStream();
			// 将请求参数写入输出流
			output.write(buffer);
			// 清空缓存区
			output.flush();

			logger.info("[" + uuid + "][" + url + "]输入【" + params + "】");

			// 获取响应状态 200 为正常 使用httpURLConnection.getResponseMessage()可以获取到[HTTP/1.0 200 OK]中的[OK]
			responseCode = connection.getResponseCode();

			if (200 == responseCode) {
				input = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				StringBuffer result = new StringBuffer("");
				String str = null;
				while ((str = reader.readLine()) != null) {
					result.append(str).append("\n");
				}
				logger.info("[" + uuid + "][" + url + "]输出【" + result + "】");
				logger.info("[" + uuid + "]与[" + url + "]通信正常,返回结果：【" + result.toString() + "】");
				return result.toString();
			} else {
				logger.error("[" + uuid + "][" + url + "]通信异常,响应代码：【" + responseCode + "】");
				return null;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("[" + uuid + "]与[" + url + "]设置请求参数异常:", e);
		} catch (IOException e) {
			logger.error("[" + uuid + "]与[" + url + "]通信异常:", e);
		} finally {
			close(input, output);
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("[" + uuid + "]异常:", e);
			}
			disconnect(connection);
		}
		return null;
	}
	
	//获取网络连接对象
	private static HttpURLConnection getConnection(String uuid,
			String sessionId, String url, String method, String contentType,
			String charset) {
		HttpURLConnection connection = null;
		URL connectionUrl = null;
		try {
			// 获取连接
			connectionUrl = new URL(url);
			connection = (HttpURLConnection) connectionUrl.openConnection();
			// 设置允许输出
			connection.setDoOutput(true);
			// 设置允许输入
			connection.setDoInput(true);
			// 不使用缓存
			connection.setUseCaches(false);
			// 连接超时时间
			connection.setConnectTimeout(30 * 1000);
			// 响应超时时间
			connection.setReadTimeout(30 * 1000);
			// 设置传递方式(GET/POST)
			connection.setRequestMethod(method);
			// 设置cookie传递sessionId
			if (sessionId != null && !"".equals(sessionId)) {
				connection.setRequestProperty("Cookie", sessionId);
			}
//			// 设置维持长连接
//			connection.setRequestProperty("Connection", "Keep-Alive");
			// 设置文件字符集
			connection.setRequestProperty("Charset", "UTF-8");
			// 设置文件类型
			if ("json".equals(contentType)) {
				connection.setRequestProperty("Content-Type", "application/json"); // json格式
			} else if ("xml".equals(contentType)) {
				connection.setRequestProperty("Content-Type", "text/xml"); // xml格式
			} else if ("form".equals(contentType)) {
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // form提交
			}
		} catch (MalformedURLException e) {
			logger.error("[" + uuid + "]与[" + url + "]创建连接失败:", e);
		} catch (ProtocolException e) {
			logger.error("[" + uuid + "]与[" + url + "]设置请求方式异常:", e);
		} catch (IOException e) {
			logger.error("[" + uuid + "]与[" + url + "]打开连接失败:", e);
		}
		return connection;
	}
	
	//参数拼接处理，传入一个MAP，返回一个拼接好的URL参数
	public static String map2params(Map<String, Object> params){
		StringBuffer buffer = new StringBuffer("");
		buffer.append("1=1");
		for(String key : params.keySet()){
			buffer.append("&").append(key).append("=").append(params.get(key));
		}
		return buffer.toString();
	}
	
	/**
	 * 根据http地址获取session id
	 * @param httpUrl
	 * @return
	 */
	public static String getSessionId(String httpUrl) {
		OutputStream out = null;
		InputStream in = null;
		URL url = null;
		try {
			url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			String cookieValue = connection.getHeaderField("Set-Cookie");
			return cookieValue.substring(0, cookieValue.indexOf(";"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
			
	/**
	 * 关闭流
	 * @param input
	 * @param output
	 * @return
	 */
	public static boolean close(InputStream input, OutputStream output) {
		try {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.flush();
				output.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 关闭http连接
	 * @param connection
	 */
	public static void disconnect(HttpURLConnection connection) {
		try {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
