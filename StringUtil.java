package com.yonyou.util;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;

/**
 * String工具类
 * @author Administrator
 */
public class StringUtil {

	public static void main(String[] args) throws Exception {
	}

	/**
	 * 将obj转为String obj为null时转为""
	 * @param obj
	 * @return
	 */
	public static String getStringValue(Object obj){
		if(isEmpty(obj)){
			return "";
		}else{
			return (String) obj;
		}
	}
	
	/**
	 * 是否null对象
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj){
		return obj == null;
	}
	
	/**
	 * 是否非null对象
	 * @param obj
	 * @return
	 */
	public static boolean isNotNull(Object obj){
		return !isNull(obj);
	}
	
	/**
	 * 是否(null对象或者""字符串)
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object str){
		return (isNull(str) || "".equals(str));
	}
	
	/**
	 * 是否非(null对象或者""字符串)
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(Object str){
		return !isEmpty(str);
	}
	
	/**
	 * 判断数组为空(为null对象或者length为0)
	 * @param obj
	 * @return
	 */
	public static <T> boolean isEmpty(T[] obj) {
		return (isNull(obj) || obj.length == 0);
	}
	
	/**
	 * 判断数组非空(为null对象或者length为0)
	 * @param obj
	 * @return
	 */
	public static <T> boolean isNotEmpty(T[] obj) {
		return !isEmpty(obj);
	}
	
	/**
	 * gbk转utf8
	 * @param str
	 * @return
	 */
	public static String fromGBKToUTF8(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		String s = fromGBKToUnicode(str);
		return fromUnicodeToUTF8(s);
	}

	/**
	 * utf8转gbk
	 * @param str
	 * @return
	 */
	public static String fromUTF8ToGBK(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		String s = fromUTF8ToUnicode(str);
		return fromUnicodeToGBK(s);
	}

	public static boolean isNeedConvert(char c) {
		return ((c & (0x00FF)) != c);
	}
	
	/**
	 * gbk转unicode
	 * @param str
	 * @return
	 */
	public static String fromGBKToUnicode(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = (char) str.charAt(i);
			if (!isNeedConvert(c)) {
				buffer.append(c);
				continue;
			}
			buffer.append("\\u" + Integer.toHexString((int) c));
		}
		return buffer.toString();
	}
	
	/**
	 * unicode转gbk
	 * @param str
	 * @return
	 */
	public static String fromUnicodeToGBK(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		int index = 0;
		StringBuffer buffer = new StringBuffer();
		int len = str.length();
		while (index < len) {
			if (index >= len - 1 || !"\\u".equals(str.substring(index, index + 2))) {
				buffer.append(str.charAt(index));
				index++;
				continue;
			}
			String charStr = "";
			charStr = str.substring(index + 2, index + 6);
			char letter = (char) Integer.parseInt(charStr, 16);
			buffer.append(letter);
			index += 6;
		}
		return buffer.toString();
	}

	/**
	 * utf8转unicode
	 * @param str
	 * @return
	 */
	public static String fromUTF8ToUnicode(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		char[] buffer = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(buffer[i]);
			if (ub == UnicodeBlock.BASIC_LATIN) {
				sb.append(buffer[i]);
			} else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				int j = (int) buffer[i] - 65248;
				sb.append((char) j);
			} else {
				short s = (short) buffer[i];
				String hex = Integer.toHexString(s);
				String unicode = "\\u" + hex;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * unicode转utf8
	 * @param str
	 * @return
	 */
	public static String fromUnicodeToUTF8(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		char c;
		int len = str.length();
		StringBuffer sb = new StringBuffer(len);
		for (int x = 0; x < len;) {
			c = str.charAt(x++);
			if (c == '\\') {
				c = str.charAt(x++);
				if (c == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						c = str.charAt(x++);
						switch (c) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + c - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + c - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + c - 'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
						}
					}
					sb.append((char) value);
				} else {
					if (c == 't')
						c = '\t';
					else if (c == 'r')
						c = '\r';
					else if (c == 'n')
						c = '\n';
					else if (c == 'f')
						c = '\f';
					sb.append(c);
				}
			} else
				sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * 根据gbk编码的字符串获取utf8编码的byte数组
	 * @param gbkStr
	 * 		gbk编码的字符串
	 * @return
	 */
	public static byte[] getUTF8BytesFromGBK(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "".getBytes();
		int n = str.length();
		byte[] utfBytes = new byte[3 * n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			int m = str.charAt(i);
			if (m < 128 && m >= 0) {
				utfBytes[k++] = (byte) m;
				continue;
			}
			utfBytes[k++] = (byte) (0xe0 | (m >> 12));
			utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
			utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
		}
		if (k < utfBytes.length) {
			byte[] tmp = new byte[k];
			System.arraycopy(utfBytes, 0, tmp, 0, k);
			return tmp;
		}
		return utfBytes;
	}

	/**
	 * 将中文转为Unicode编码
	 * @param str
	 * @return
	 */
	public static String decToHex(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		StringBuffer result = new StringBuffer("");
		for (int i = 0; i < str.length(); i++) {
			int strInt = str.charAt(i);
			if (strInt > 127) {
				result.append("\\u").append(Integer.toHexString(strInt));
			} else {
				result.append(String.valueOf(str.charAt(i)));
			}
		}
		return result.toString();
	}

	/**
	 * 将Unicode编码转为中文
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String hexToDec(String str) {
		if(isNull(str)) return null;
		if(isEmpty(str)) return "";
		StringBuilder result = new StringBuilder("");
		int i = -1;
		int pos = 0;
		while ((i = str.indexOf("\\u", pos)) != -1) {
			result.append(str.substring(pos, i));
			if (i + 5 < str.length()) {
				pos = i + 6;
				result.append((char) Integer.parseInt(str.substring(i + 2, i + 6), 16));
			}
		}
		result.append(str.substring(pos));
		return result.toString();
	}
}