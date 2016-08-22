package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum TriggerType {
	Mouse("Mouse"),
	TCP("TCP");
	private String value = "";

	private TriggerType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, TriggerType> stringToEnum = new HashMap<String, TriggerType>();
	static {
		// Initialize map from constant name to enum constant
		for(TriggerType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static TriggerType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return TriggerType.Mouse;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
