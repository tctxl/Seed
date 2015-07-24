package com.opdar.framework.utils.yeson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Random;

public class YesonParser {
	// {
	private int LEFT_CURLY_BRACE = 123;
	// }
	private int RIGHT_CURLY_BRACE = 125;
	// [
	private int LEFT_SQUARE_BRACE = 91;
	// ]
	private int RIGHT_SQUARE_BRACE = 93;
	// :
	private int COLON = 58;
	// ,
	private int COMMA = 44;
	// "
	private int QUOTATION = 34;
	private int BACKSLASH = 92;
	private int index;
	char[] jsonBuffer;
	private final static ThreadLocal<SoftReference<char[]>> charsBufLocal = new ThreadLocal<SoftReference<char[]>>();

	boolean isParseArray = false;
	boolean isInit = true;

	public YesonParser() {
	}

	public JSONObject parse(String json) {
		jsonBuffer = json.toCharArray();
		index = 0;
		isInit = true;
		isParseArray = false;
		return toJSONObject();
	}

	private JSONArray toJSONArray() {
		isParseArray = true;
		JSONArray array = new JSONArray();
		for (int i = index; i < jsonBuffer.length; i++) {
			index++;
			char ch = jsonBuffer[i];
			if (ch == LEFT_CURLY_BRACE) {
				array.add(toJSONObject());
				i = --index;
				continue;
			}
			if (ch == RIGHT_SQUARE_BRACE) {
				break;
			}
		}
		return array;
	}

	StringBuilder buff = new StringBuilder(1024);

	private JSONObject toJSONObject() {
		isParseArray = false;
		JSONObject root = new JSONObject();
		String key = null;
		Object value = null;

		/**
		 * 1.parsing 2.stop parse
		 */

		boolean isKey = true;

		try {
			for (int i = index; i < jsonBuffer.length; i++) {
				index++;
				char ch = jsonBuffer[i];
				if (root.type == 2 && ch < 33)
					continue;

				if (ch == LEFT_CURLY_BRACE) {
					if (isInit) {
						isInit = false;
						continue;
					}
					if (root.type == 2 && !isKey) {
						value = toJSONObject();
						root.put(key, value);
						i = index;
						isKey = true;
					}
					continue;
				}

				if (ch == RIGHT_SQUARE_BRACE) {
					break;
				}
				if (ch == QUOTATION) {
					if (root.type == 2 && isKey) {
						root.type = 1;
					} else if (root.type == 1 && isKey) {
						root.type = 2;
						key = buff.toString();
						buff.delete(0, buff.length());
						isKey = false;
					} else if (root.type == 2 && !isKey) {
						root.type = 1;
					} else if (root.type == 1 && !isKey) {
						root.type = 2;
						value = buff.toString();
						buff.delete(0, buff.length());
						root.put(key, value);
						isKey = true;
					}
					continue;
				}

				if (ch == RIGHT_CURLY_BRACE) {
					if (root.lastchar == COLON && root.type == 2 && !isKey) {
						value = buff.toString();
						buff.delete(0, buff.length());
						root.put(key, value);
						isKey = true;
						root.lastchar = RIGHT_CURLY_BRACE;
					}
					return root;
				}
				if (ch == LEFT_SQUARE_BRACE) {
					value = toJSONArray();
					root.put(key, value);
					i = index;
					isKey = true;
					continue;
				}
				// if(ch == RIGHT_SQUARE_BRACE){
				// continue;
				// }
				if (ch == COLON) {
					root.lastchar = COLON;
					continue;
				}
				if (ch == COMMA) {
					if (root.lastchar == COLON && root.type == 2 && !isKey) {
						value = buff.toString();
						buff.delete(0, buff.length());
						root.put(key, value);
						isKey = true;
						root.lastchar = COMMA;
					}
					continue;
				}
				buff.append(ch);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// throw new JSONParserException(sb.charAt(sb.length()));
		}
		return root;
	}

}
