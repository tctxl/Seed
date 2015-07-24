package com.opdar.framework.utils.yeson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JSONObject implements Map<String,Object> {
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return map.toString();
	}
	public JSONObject() {
		// TODO Auto-generated constructor stub
		map = new HashMap<String, Object>();
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
	
}