package com.yonyou.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.MapperIF;

public class BeanUtil {

	public static final MapperIF mapperIF = new DozerBeanMapper();

	/**
	 * 将source对象的同名属性值拷贝到target对象的同名属性。
	 * */
	public static void copyProperties(Object source, Object target) {
		mapperIF.map(source, target);
	}

	/**
	 * 根据clazz类型实例化对象后再将source对象的同名属性赋值给新对象返回
	 * */
	@SuppressWarnings("unchecked")
	public static <T> T copyProperties(Object source, Class<T> clazz) {
		return (T) mapperIF.map(source, clazz);
	}
	
	/**
	 * 将source对象与target对象属性名相同的值设置到target对象的同名属性 如果source中属性值为null则不设置
	 * @param source
	 * @param target
	 */
	public static <T> void copyForNotNull(Object source, Object target) {
		Field field = null;
		Field[] fields = source.getClass().getDeclaredFields();
		for(Field f : fields){
			f.setAccessible(true);
			try {
				if(f.get(source) != null){
					field = target.getClass().getDeclaredField(f.getName());
					if(field != null && !Modifier.isFinal(field.getModifiers())){
						field.setAccessible(true);
						field.set(target, f.get(source));
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
			}
		}
	}
}