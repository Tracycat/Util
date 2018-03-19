package com.yonyou.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/***
 * FTP 服务器文件操作工具类
 */
public class FtpClientUtil {

	public static final String SERVER_ENCODING = "ISO-8859-1";
	/**
	 * 读ftp文件
	 * @param localFile
	 * @param ftpFile
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * 		0:成功;1:服务器状态异常;2:ftpFile不存在
	 * @throws Exception
	 */
	public static int read(String localFile, String ftpFile, String ftpHost,
			int ftpPort, String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		InputStream input = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				if(FtpClientUtil.isExistsFile(ftpClient, ftpFile)){
					input = ftpClient.retrieveFileStream(new String(ftpFile.getBytes(charset),SERVER_ENCODING));
					FileUtil.write(input, new File(localFile));
				}else{
					return 2;
				}
			}else{
				return 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.close(input, null);
			disconnect(ftpClient);
		}
		return 0;
	}
	
	/**
	 * 写文件到ftp服务器
	 * @param localFile
	 * @param ftpFile
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static int write(String localFile, String ftpFile, String ftpHost,
			int ftpPort, String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		OutputStream output = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				String ftpPath = ftpFile.substring(0, ftpFile.lastIndexOf("/"));
				mkdirs(ftpClient, ftpPath);
				output = ftpClient.storeFileStream(new String(ftpFile.getBytes(charset),SERVER_ENCODING));
				FileUtil.write(new File(localFile), output);
			}else{
				return 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.close(null, output);
			disconnect(ftpClient);
		}
		return 0;
	}
	
	/**
	 * 读取ftp服务器文件到byte数组
	 * @param ftpFile
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static byte[] read(String ftpFile, String ftpHost, int ftpPort,
			String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		InputStream input = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				String fileName = new String(ftpFile.getBytes(charset),SERVER_ENCODING);
				byte[] buffer = new byte[(int) ftpClient.listFiles(fileName)[0].getSize()];
				input = ftpClient.retrieveFileStream(fileName);
				input.read(buffer);
				return buffer;
			}else{
				return null;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.close(input, null);
			disconnect(ftpClient);
		}
	}
	
	/**
	 * 将byte写入到ftp服务器文件
	 * @param buffer
	 * @param ftpFile
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static int write(byte[] buffer, String ftpFile, String ftpHost,
			int ftpPort, String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		OutputStream output = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				String ftpPath = ftpFile.substring(0, ftpFile.lastIndexOf("/"));
				mkdirs(ftpClient, ftpPath);
				output = ftpClient.storeFileStream(new String(ftpFile.getBytes(charset),SERVER_ENCODING));
				FileUtil.write(buffer, output);
			}else{
				return 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.close(null, output);
			disconnect(ftpClient);
		}
		return 0;
	}
	
	/**
	 * 删除文件
	 * @param ftpFile
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * 		0:成功;1:服务器状态异常;2:ftpFile不存在;3:删除失败
	 * @throws Exception
	 */
	public static int rmfile(String ftpFile, String ftpHost, int ftpPort,
			String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				return rmfile(ftpClient, ftpFile);
			} else {
				return 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			disconnect(ftpClient);
		}
	}
	
	/**
	 * 创建目录
	 * @param ftpPath
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static int mkdirs(String ftpPath, String ftpHost, int ftpPort,
			String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				return mkdirs(ftpClient, ftpPath);
			} else {
				return 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			disconnect(ftpClient);
		}
	}
	
	/**
	 * 删除目录
	 * @param ftpPath
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * 		0:成功;1:服务器状态异常;2:ftpPath不存在;3:删除失败
	 * @throws Exception
	 */
	public static int rmdirs(String ftpPath, String ftpHost, int ftpPort,
			String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if (ftpClient != null) {
				// 格式化ftp目录路径
				String path = ftpPath.replaceAll("\\\\", "/");
				String[] strs = path.split("/");
				path = "";
				for(String str : strs){
					if("".equals(str)){
					}else{
						path += "/"+str;
					}
				}
				
				// 递归获取path目录下的所有内容
				List<String> list = new ArrayList<String>();
				getList(ftpClient, list, path);
				
				// 删除目录下的所有内容
				for(int i = list.size() - 1; i > -1; i --){
					path = list.get(i);
					if(isExistsDirs(ftpClient, path)){
//						System.out.println("目录："+path);
						rmdirs(ftpClient, path);
					}else{
//						System.out.println("文件："+path);
						rmfile(ftpClient, path);
					}
				}
			} else {
				return 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			disconnect(ftpClient);
		}
		return 0;
	}
	
	/**
	 * 递归方法 获取path下的多有内容
	 * @param ftpClient
	 * @param list
	 * @param path
	 * @throws Exception
	 */
	public static void getList(FTPClient ftpClient, List<String> list,
			String path) throws Exception {
		list.add(path);
		String[] names = ftpClient.listNames(new String(path.getBytes(),SERVER_ENCODING));
		if (names != null && names.length > 0) {
			for (String name : names) {
				if(isExistsDirs(ftpClient, name)){
					getList(ftpClient, list, name);
				}else{
					list.add(name);
				}
			}
		}
	}
	
	/**
	 * 获取ftpPath目录下的所有文件
	 * @param ftpPath
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String[] listNames(String ftpPath, String ftpHost, int ftpPort,
			String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		try {
			ftpClient = getFTPClient(ftpHost, ftpPort, ftpUser, ftpPass, charset);
			if(isExistsDirs(ftpClient, ftpPath)){
				return ftpClient.listNames();
			}else{
				return new String[]{};
			}
		} catch (Exception e) {
			throw e;
		} finally {
			disconnect(ftpClient);
		}
	}
	
	/**
	 * 获取连接登陆之后的FTPClient
	 * @param ftpHost
	 * @param ftpPort
	 * @param ftpUser
	 * @param ftpPass
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static FTPClient getFTPClient(String ftpHost, int ftpPort,
			String ftpUser, String ftpPass, String charset) throws Exception {
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, ftpPort);
			if (ftpClient.login(ftpUser, ftpPass)) {
				ftpClient.setControlEncoding(charset);
				
				// 如果编码为UTF-8 需要打开UTF-8编码的支持
				if("UTF-8".equals(charset)){
					ftpClient.sendCommand("OPTS UTF8", "ON");
				}
				
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();
				int reply = ftpClient.getReplyCode();
				if (FTPReply.isPositiveCompletion(reply)) {
				}else{
					ftpClient = null;
					throw new Exception("连接FTP服务器【"+ftpHost+":"+ftpPort+"@"+ftpUser+":"+ftpPass+"】异常：");
				}
			}else{
				ftpClient = null;
				throw new Exception("登陆FTP服务器【"+ftpHost+":"+ftpPort+"@"+ftpUser+":"+ftpPass+"】失败：");
			}
		} catch (SocketException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		return ftpClient;
	}
	
	/**
	 * 判断目录是否存在
	 * @param ftpClient
	 * @param ftpPath
	 * @return
	 * @throws Exception
	 */
	public static boolean isExistsDirs(FTPClient ftpClient, String ftpPath)
			throws Exception {
		try {
			return ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes(),SERVER_ENCODING));
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 判断文件是否存在
	 * @param ftpClient
	 * @param ftpFile
	 * @return
	 * @throws Exception
	 */
	public static boolean isExistsFile(FTPClient ftpClient, String ftpFile) throws Exception{
		try {
			String[] names = ftpClient.listNames(new String(ftpFile.getBytes(),SERVER_ENCODING));
			return (names != null && names.length > 0);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 判断并创建ftp目录
	 * @param ftpClient
	 * @param ftpPath
	 * @return
	 * @throws Exception
	 */
	public static int mkdirs(FTPClient ftpClient, String ftpPath) throws Exception{
		try {
			// 判断远程目录是否存在
			if(!FtpClientUtil.isExistsDirs(ftpClient, ftpPath)){
				String[] dirs = ftpPath.split("/");
				String path = "/";
				for(int i = 0; i < dirs.length; i ++){
					path+="/"+(dirs[i]);
					if(StringUtil.isNotEmpty(dirs[i])){
						if(!FtpClientUtil.isExistsDirs(ftpClient, path)){
							if(ftpClient.makeDirectory(new String(path.getBytes(),SERVER_ENCODING))){
							}else{
								throw new Exception("FTP目录【"+path+"】创建失败!");
							}
						}else{
							continue;
						}
					}
				}
			}
			return 0;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 删除文件
	 * @param ftpClient
	 * @param ftpFile
	 * @return
	 * 		0:成功;2:ftpFile不存在;3:删除失败
	 * @throws Exception
	 */
	public static int rmfile(FTPClient ftpClient, String ftpFile)
			throws Exception {
		try {
			if (isExistsFile(ftpClient, ftpFile)) {
				if (ftpClient.deleteFile(new String(ftpFile.getBytes(),SERVER_ENCODING))) {
					return 0;
				} else {
					return 3;
				}
			} else {
				return 2;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 删除目录
	 * @param ftpClient
	 * @param ftpPath
	 * @return
	 * 		0:成功;2:ftpPath不存在;3:删除失败
	 * @throws Exception
	 */
	public static int rmdirs(FTPClient ftpClient, String ftpPath)
			throws Exception {
		try {
			if (isExistsDirs(ftpClient, ftpPath)) {
				if (ftpClient.removeDirectory(new String(ftpPath.getBytes(),SERVER_ENCODING))) {
					return 0;
				} else {
					return 3;
				}
			} else {
				return 2;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 退出登录并关闭连接
	 * @param ftpClient
	 */
	public static void disconnect(FTPClient ftpClient) {
		try {
			if (ftpClient != null) {
				ftpClient.logout();
				if (ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
				ftpClient = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
//		FtpClientUtil.read("E:\\MySpaces\\GAMC\\INFODCS\\bin\\Tomcat\\temp\\A.pdf", "/INFODCS/STATEMENT_DEALER/201803/D187CB4805D04992A94DB4F2AD7ABC918948765992158053934.pdf", "172.18.81.48", 21, "ftpuser", "ftpuser", "UTF-8");
//		FtpClientUtil.write("E:\\MySpaces\\GAMC\\INFODCS\\bin\\Tomcat\\temp\\E86E63BA5C074A2AAE70EDABE318D46E2146202995519308737.pdf", "/INFODCS/STATEMENT_DEALER/201803/E86E63BA5C074A2AAE70EDABE318D46E2146202995519308737.pdf", "172.18.81.48", 21, "ftpuser", "ftpuser", "UTF-8");
//		int result = FtpClientUtil.rmfile("/INFODCS/STATEMENT_DEALER/201803/4004E7049B724D95B02194EBE472F9C4695759209765223511.pdf", "172.18.81.48", 21, "ftpuser", "ftpuser", "UTF-8");
		int result = FtpClientUtil.rmdirs("/INFODCS/新建文件夹/", "172.18.81.48", 21, "ftpuser", "ftpuser", "UTF-8");
		System.out.println(result);
		
//		String[] strs = FtpClientUtil.listNames("/INFODCS/STATEMENT_DEALER/201806/a", "172.18.81.48", 21, "ftpuser", "ftpuser", "UTF-8");
//		System.out.println(strs);
	}
}