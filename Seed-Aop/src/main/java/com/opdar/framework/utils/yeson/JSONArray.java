package com.opdar.framework.utils.yeson;

import java.util.*;

public class JSONArray extends AbstractList<Object> {
	private ArrayList<Object> arrays;
	public JSONArray() {
		arrays = new ArrayList<Object>();
	}
	public void add(int arg0, Object arg1) {
		arrays.add(arg0, arg1);
	}

	public boolean add(Object arg0) {
		return arrays.add(arg0);
	}

	public boolean addAll(Collection<? extends Object> arg0) {
		return arrays.addAll(arg0);
	}

	public boolean addAll(int arg0, Collection<? extends Object> arg1) {
		return arrays.addAll(arg0, arg1);
	}

	public void clear() {
		arrays.clear();
	}

	public Object clone() {
		return arrays.clone();
	}

	public boolean contains(Object arg0) {
		return arrays.contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return arrays.containsAll(arg0);
	}

	public void ensureCapacity(int arg0) {
		arrays.ensureCapacity(arg0);
	}

	public boolean equals(Object arg0) {
		return arrays.equals(arg0);
	}

	public Object get(int arg0) {
		return arrays.get(arg0);
	}

	public JSONObject getJSONObject(int arg0) {
		return (JSONObject) arrays.get(arg0);
	}
	public int hashCode() {
		return arrays.hashCode();
	}

	public int indexOf(Object arg0) {
		return arrays.indexOf(arg0);
	}

	public boolean isEmpty() {
		return arrays.isEmpty();
	}

	public Iterator<Object> iterator() {
		return arrays.iterator();
	}

	public int lastIndexOf(Object arg0) {
		return arrays.lastIndexOf(arg0);
	}

	public ListIterator<Object> listIterator() {
		return arrays.listIterator();
	}

	public ListIterator<Object> listIterator(int arg0) {
		return arrays.listIterator(arg0);
	}

	public Object remove(int arg0) {
		return arrays.remove(arg0);
	}

	public boolean remove(Object arg0) {
		return arrays.remove(arg0);
	}

	public boolean removeAll(Collection<?> arg0) {
		return arrays.removeAll(arg0);
	}

	public boolean retainAll(Collection<?> arg0) {
		return arrays.retainAll(arg0);
	}

	public Object set(int arg0, Object arg1) {
		return arrays.set(arg0, arg1);
	}

	public int size() {
		return arrays.size();
	}

	public List<Object> subList(int arg0, int arg1) {
		return arrays.subList(arg0, arg1);
	}

	public Object[] toArray() {
		return arrays.toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		return arrays.toArray(arg0);
	}

	public String toString() {
		return arrays.toString();
	}

	public void trimToSize() {
		arrays.trimToSize();
	}


	public <T> List<T> getArray(Class<T> clz) {
		List<T> list = new ArrayList<T>();
		for(int i=0;i<size();i++){
			JSONObject object = getJSONObject(i);
			list.add(object.getObject(clz));
		}
		return list;
	}
}
