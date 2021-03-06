package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum AndroidType {
	Panasonic("Panasonic"),
	Common("Common");
	private String value = "";

	private AndroidType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, AndroidType> stringToEnum = new HashMap<String, AndroidType>();
	static {
		// Initialize map from constant name to enum constant
		for(AndroidType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}
	public static AndroidType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return AndroidType.Panasonic;
		}
	}

	@Override
	public String toString() {
		return value;
	}

}
