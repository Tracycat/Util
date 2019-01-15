import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.MapperIF;
/**
 * 
 * 依赖包
 * 1.commons-lang-2.4.jar
 * 2.dozer-4.2.jar
 * 3.commons-collections-3.2.jar
 * 4.commons-beanutil-1.8.0.jar
 */
public class BeanUtil {

	public static final MapperIF mapperIF = new DozerBeanMapper();
	
	/**
	 * 将source对象的同名属性值拷贝到target对象的同名属性
	 * 执行方法后target对象属性的值就有source属性的值
	 * @param source
	 * @param target
	 * 
	 * */
	public static void copyProperties(Object source, Object target) {
		mapperIF.map(source, target);
	}

	/**
	 * 根据clazz类型实例化对象后再将source对象的同名属性赋值给新对象返回
     * @param source
	 * @param target
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static <T> T copyProperties(Object source, Class<T> clazz) {
		return (T) mapperIF.map(source, clazz);
	}
	
	/**
	 * 将source对象与target对象属性名相同的值设置到target对象的同名属性 如果source中属性值为null则不设置
	 * 执行方法后target对象属性的值就有source属性的值
	 * @param source
	 * @param target
	 */
	public static void copyForNotNull(Object source, Object target) {
		Field field = null;
		Field[] fields = source.getClass().getDeclaredFields();
		for(Field f : fields){
			//暴力访问
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
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 将source对象与target对象属性名相同的值设置到target对象的同名属性 如果source中属性值为null则不设置
	 * 执行方法后target对象属性的值就有source属性的值
	 * @param source
	 * @param target
	 * @throws InstantiationException 
	 */
	public static <T> T copyForNotNull(Object source, Class<T> clazz) throws InstantiationException {
		Field field = null;		
		Field[] fields = source.getClass().getDeclaredFields();
		for(Field f : fields){
			//暴力访问
			f.setAccessible(true);
			try {
				T obj=clazz.newInstance();
				if(f.get(source) != null){
					field = clazz.getDeclaredField(f.getName());
					if(field != null && !Modifier.isFinal(field.getModifiers())){
						field.setAccessible(true);
						field.set(obj, f.get(source));
					}
				}
				return obj;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return null;
	}