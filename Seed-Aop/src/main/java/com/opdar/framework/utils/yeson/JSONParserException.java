package com.opdar.framework.utils.yeson;

public class JSONParserException extends Throwable {

	public JSONParserException(char lastChar) {
		super(String.format("JSON Parse in escape ‘%s’", lastChar));
	}

}
