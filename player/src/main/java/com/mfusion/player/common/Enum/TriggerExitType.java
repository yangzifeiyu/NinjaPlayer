package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum TriggerExitType {
	Exit("Exit"),
	Minimize("Minimize");
	private String value = "";

	private TriggerExitType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, TriggerExitType> stringToEnum = new HashMap<String, TriggerExitType>();
	static {
		// Initialize map from constant name to enum constant
		for(TriggerExitType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static TriggerExitType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return TriggerExitType.Exit;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
