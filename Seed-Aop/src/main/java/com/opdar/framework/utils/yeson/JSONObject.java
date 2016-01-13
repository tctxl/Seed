package com.opdar.framework.utils.yeson;

import com.opdar.framework.aop.SeedInvoke;
import com.opdar.framework.aop.interfaces.SeedExcuteItrf;
import com.opdar.framework.utils.Utils;
import com.opdar.framework.utils.yeson.annotations.JSONField;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class JSONObject implements Map<String,Object> {
	private YesonParser yesonParser;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return map.toString();
	}
	public JSONObject(YesonParser yesonParser) {
		// TODO Auto-generated constructor stub
		map = new HashMap<String, Object>();
		this.yesonParser = yesonParser;
	}
	private Map<String, Object> map;
	/**
	 * 1.parsing
	 * 2.stop parse
	 */
	protected int type = 2;
	int lastchar = 0;
	public void clear() {
		map.clear();
	}
	public boolean containsKey(Object arg0) {
		return map.containsKey(arg0);
	}
	public boolean containsValue(Object arg0) {
		return map.containsValue(arg0);
	}
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}
	public boolean equals(Object arg0) {
		return map.equals(arg0);
	}
	public Object get(Object arg0) {
		return map.get(arg0);
	}
	public int hashCode() {
		return map.hashCode();
	}
	public boolean isEmpty() {
		return map.isEmpty();
	}
	public Set<String> keySet() {
		return map.keySet();
	}
	public Object put(String arg0, Object arg1) {
		return map.put(arg0, arg1);
	}
	public void putAll(Map<? extends String, ? extends Object> arg0) {
		map.putAll(arg0);
	}
	public Object remove(Object arg0) {
		return map.remove(arg0);
	}
	public int size() {
		return map.size();
	}
	public Collection<Object> values() {
		return map.values();
	}

	public <T> T getObject(Type type){
		try {
			return invokeObject(this,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T> T invokeObject(JSONObject object, Type classType) throws Exception {
		Class<T> clz = null;
		Type[] types = null;
		List<TypeVariable<Class<T>>> varsType = null;
        if(classType instanceof ParameterizedTypeImpl){
			clz = (Class<T>) ((ParameterizedTypeImpl) classType).getRawType();
            types = ((ParameterizedTypeImpl) classType).getActualTypeArguments();
			varsType = Arrays.asList(clz.getTypeParameters());
        }else if (classType instanceof Class){
			clz = (Class) classType;
        }
		SeedExcuteItrf execute = SeedInvoke.buildObject(clz);
		for (Iterator<String> it = object.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			Field field = clz.getDeclaredField(key);
			JSONField field1 = field.getAnnotation(JSONField.class);
			if(field1 != null && !field1.serializable())continue;
			Object o = object.get(key);
			if (o instanceof JSONArray) {
				Class cls = null;
				Class rawType = null;
				if(varsType!=null && field.getGenericType() !=null && varsType.contains(field.getGenericType())){
					Object c = types[varsType.indexOf(field.getGenericType())];
					if(c instanceof ParameterizedTypeImpl){
						cls = (Class) ((ParameterizedTypeImpl) c).getActualTypeArguments()[0];
						rawType = ((ParameterizedTypeImpl) c).getRawType();
					}
				}else{
					rawType = (Class) ((ParameterizedType) field.getGenericType()).getRawType();

					cls = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
				}
				Collection collection = null;
				if (rawType.isInterface()) {
					collection = new ArrayList();
				} else {
					collection = (Collection) rawType.newInstance();
				}
				for (int i = 0; i < ((JSONArray) o).size(); i++) {
					JSONObject object1 = ((JSONArray) o).getJSONObject(i);
					Object o2 = invokeObject(object1, cls);
					if (o2 != null)
						collection.add(o2);
				}
				execute.invokeMethod("set" + Utils.testField(key), collection);
			} else {
				if(o != null){
					Class<?> type = field.getType();
					if(varsType!=null && field.getGenericType() !=null && varsType.contains(field.getGenericType())){
						type = (Class<?>) types[varsType.indexOf(field.getGenericType())];
					}
					if (o instanceof JSONObject){
						o = ((JSONObject) o).getObject(type);
					}else{
						if(yesonParser.converts.get(type) != null) {
							o = yesonParser.converts.get(type).reconvert(o);
						}else{
							o = null;
						}
					}
				}
				execute.invokeMethod("set" + Utils.testField(key), o);
			}
		}
		return (T) execute;
	}
}