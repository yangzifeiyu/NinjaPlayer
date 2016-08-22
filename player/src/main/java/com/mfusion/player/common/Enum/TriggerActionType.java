package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum TriggerActionType {
	App("App"),
	PBU("PBU");
	private String value = "";

	private TriggerActionType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, TriggerActionType> stringToEnum = new HashMap<String, TriggerActionType>();
	static {
		// Initialize map from constant name to enum constant
		for(TriggerActionType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static TriggerActionType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return TriggerActionType.App;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
