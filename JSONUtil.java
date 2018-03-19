package com.yonyou.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class JSONUtil {

	private static final DateMorpher dataMorpher = new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"});
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 将array格式的json字符串转换成list集合
	 * 
	 * @param <T>
	 *            集合元素类型
	 * @param json
	 *            json字符串
	 * @param clazz
	 *            目标数据类型
	 * @return
	 */
	public static <T> List<T> toBeanList(String json, Class<T> clazz) {
		JSONArray array = JSONArray.fromObject(json);
		List<T> list = new ArrayList<T>(array.size());
		for (int i = 0; i < array.size(); i++) {
			list.add(toBean(array.getJSONObject(i).toString(), clazz));
		}
		return list;
	}

	/**
	 * 将object格式的json字符串转换成Java对象
	 * 
	 * @param <T>
	 *            目标对象类型
	 * @param json
	 *            json字符串
	 * @param clazz
	 *            目标数据类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T toBean(String json, Class<T> clazz) {
		JSONUtils.getMorpherRegistry().registerMorpher(dataMorpher);
		return (T) JSONObject.toBean(JSONObject.fromObject(json), clazz);
	}

	/**
	 * 将Java Bean转换成JSON对象字符串
	 * @param obj
	 * @return
	 */
	public static String toJSON(Object obj) {
		try {
			StringBuilder sb = new StringBuilder("");
			toJsonValue(sb, obj);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	/**
	 * 将数据转为json可以通过mapper映射属性名字
	 * @param params
	 * @param mapper
	 * @return
	 * @throws Exception
	 */
	public static String toJsonForMapper(Map<String, Object> params,
			Map<String, String> mapper) {
		try {
			StringBuilder json = new StringBuilder("");
			JSONUtil.toJsonValue(json, params, mapper);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	/**
	 * 将Bean对象转换成Map
	 * @param obj
	 * @return
	 */
	public static Map<String,Object> fromBeanToMap(Object obj){
		Field[] fields = obj.getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>(fields.length);
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj));
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@SuppressWarnings("unchecked") 
	public static <T> List<T> fromJsonArrayToList(String jsonArray, Class<T> clazz){
		JSONArray array = JSONArray.fromObject(jsonArray);
		JSONObject jsonObject = null;
		T obj = null;
		List<T> list = new ArrayList<T>(array.size());
		Object fieldValue = null;
		Iterator<String> keyItera = null;
		String key = null;
		Field field = null;
		
		for(int i = 0; i < array.size(); i ++){
			jsonObject = (JSONObject) array.get(i);
			keyItera = jsonObject.keys();
			try {
				obj = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			while(keyItera.hasNext()){
				key = keyItera.next();
				try {
					field = clazz.getDeclaredField(key);
					field.setAccessible(true);
					if("java.util.Date".equals(field.getType().getName())){
						fieldValue = sdf.parse(jsonObject.getString(key));
					}else{
						fieldValue = jsonObject.getString(key);
					}
					field.set(obj, fieldValue);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * 转换成JSON
	 * @param sb
	 * @param obj
	 * @param mapper
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void toJsonValue(StringBuilder sb, Object obj,
			Map<String, String> mapper) throws Exception {
		if(obj == null){
			sb.append("null");
			return;
		}
		if((obj instanceof Throwable)){
			String name = null;
			sb.append("{");
			Method[] methods = obj.getClass().getMethods();
			for(Method m : methods){
				if ((m.getName().startsWith("get"))
						&& (m.getName().length() > 3)
						&& (!m.getName().equals("getClass"))
						&& (!m.getName().equals("getCause"))
						&& (!m.getName().equals("getStackTrace"))
						&& (!m.getName().equals("getLocalizedMessage"))
						&& (!Void.TYPE.equals(m.getReturnType()))
						&& (m.getParameterTypes().length == 0)) {
					name = m.getName().substring(3);
					if ((name.charAt(0) >= 'A') && (name.charAt(0) <= 'Z')) {
						sb.append((char) (name.charAt(0) + ' '));
					}
					sb.append(name.substring(1) + ":");
					toJsonValue(sb, m.invoke(obj, new Object[0]));
					sb.append(",");
				}
			}
			if (sb.charAt(sb.length() - 1) == ','){
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("}");
			return;
		}else if ((obj instanceof String)) {
			sb.append("\"").append(escape(obj.toString())).append("\"");
			return;
		}else if ((obj instanceof Date)) {
			sb.append("\"").append(sdf.format((Date) obj)).append("\"");
			return;
		}else if ((obj instanceof Number)) {
			sb.append(obj.toString());
			return;
		}else if ((obj instanceof Boolean)) {
			sb.append(obj.equals(Boolean.TRUE) ? "true" : "false");
			return;
		}else if ((obj instanceof Object[])) {
			toJsonArrayValue(sb, (Object[]) obj);
			return;
		}else if ((obj instanceof Set)) {
			toJsonArrayValue(sb, ((Set) obj).toArray());
			return;
		}else if ((obj instanceof List)) {
			toJsonArrayValue(sb, ((List) obj).toArray());
			return;
		}else if ((obj instanceof Map)) {
			sb.append("{");
			for(Object key : ((Map)obj).keySet()){
				sb.append("\"");
				sb.append((mapper==null||mapper.get(key.toString())==null||"".equals(mapper.get(key.toString())))?key.toString():mapper.get(key.toString()));
				sb.append("\"").append(":");
				toJsonValue(sb, ((Map) obj).get(key), mapper);
				sb.append(",");
			}
			if (sb.charAt(sb.length() - 1) == ','){
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("}");
			return;
		}else{
			Map<String,Object> objMap = fromBeanToMap(obj);
			toJsonValue(sb, objMap, mapper);
			return;
		}
	}
	
	/**
	 * 转换成JSON
	 * @param sb
	 * @param obj
	 * @throws Exception
	 */
	public static void toJsonValue(StringBuilder sb, Object obj)
			throws Exception {
		toJsonValue(sb, obj, null);
	}
	
	/**
	 * 将数组转换成JSON字符串
	 * @param sb
	 * @param obj
	 * @throws Exception
	 */
	private static void toJsonArrayValue(StringBuilder sb, Object[] obj)
			throws Exception {
		sb.append("[");
		for (Object o : obj) {
			toJsonValue(sb, o);
			sb.append(",");
		}
		if (sb.charAt(sb.length() - 1) == ','){
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
	}
	
	/**
	 * 将数据转为String
	 * @param obj
	 * @return
	 */
	public static String toStringValue(Object obj) {
		if (obj == null) {
			return "null";
		}
		if ((obj instanceof String)) {
			return escape(obj.toString());
		} else if ((obj instanceof Date)) {
			return sdf.format((Date) obj);
		} else if ((obj instanceof Number)) {
			return obj.toString();
		} else if ((obj instanceof Boolean)) {
			return obj.equals(Boolean.TRUE) ? "true" : "false";
		}
		return "";
	}
	
	/**
	 * 替换转义符
	 * @param str
	 * @return
	 */
	public static String escape(String str) {
		StringBuilder sb = new StringBuilder(str);
		for (int i = sb.length() - 1; i >= 0; i--) {
			if (sb.charAt(i) == '"')
				sb.replace(i, i + 1, "\\\"");
			else if (sb.charAt(i) == '\\')
				sb.replace(i, i + 1, "\\\\");
			else if (sb.charAt(i) == '/')
				sb.replace(i, i + 1, "\\/");
			else if (sb.charAt(i) == '\b')
				sb.replace(i, i + 1, "\\b");
			else if (sb.charAt(i) == '\f')
				sb.replace(i, i + 1, "\\f");
			else if (sb.charAt(i) == '\n')
				sb.replace(i, i + 1, "\\n");
			else if (sb.charAt(i) == '\r')
				sb.replace(i, i + 1, "\\r");
			else if (sb.charAt(i) == '\t') {
				sb.replace(i, i + 1, "\\t");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 拼接字符串 key:value的形式
	 * @param key
	 * @param value
	 * @return
	 */
	public static String toKeyValue(String key, Object value){
		return "\"" + escape(key) + "\":\"" + toStringValue(value) + "\"";
	}
	
	/**
	 * 拼接字符串 {key:value}的形式
	 * @param key
	 * @param value
	 * @return
	 */
	public static String toKeyValueForJson(String key, Object value){
		return "{\"" + escape(key) + "\":\"" + toStringValue(value) + "\"}";
	}
}
