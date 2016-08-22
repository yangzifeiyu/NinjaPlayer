package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum TriggerOpenType {
	Minimize("Minimize"),
	Maximize("Maximize"),
	Normal("Normal");
	private String value = "";

	private TriggerOpenType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, TriggerOpenType> stringToEnum = new HashMap<String, TriggerOpenType>();
	static {
		// Initialize map from constant name to enum constant
		for(TriggerOpenType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static TriggerOpenType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return TriggerOpenType.Normal;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
