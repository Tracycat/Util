package com.yonyou.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

	public static boolean write(byte[] bytes, OutputStream output)
			throws Exception {
		try {
			output.write(bytes, 0, bytes.length);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static boolean write(byte[] bytes, File file)
			throws Exception {
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
			output.write(bytes, 0, bytes.length);
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.close(null, output);
		}
	}
	
	public static boolean write(File srcFile, OutputStream output) throws Exception {
		InputStream input = null;
		try {
			input = new FileInputStream(srcFile);
			return FileUtil.write(input, output);
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			FileUtil.close(input, null);
		}
	}
	
	/**
	 * 流操作
	 * @param input
	 * @param output
	 */
	public static boolean write(InputStream input, OutputStream output) throws Exception {
		try {
			byte[] buffer = new byte[1024 * 5];
			int len = 0;
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
			return true;
		} catch (IOException e) {
			throw e;
		}
	}

	public static boolean write(InputStream input, File tagFile) throws Exception {
		OutputStream output = null;
		try {
			output = new FileOutputStream(tagFile);
			return FileUtil.write(input, output);
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			FileUtil.close(input, output);
		}
	}
	
	/**
	 * 关闭Stream
	 * @param input
	 * @param output
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
}
