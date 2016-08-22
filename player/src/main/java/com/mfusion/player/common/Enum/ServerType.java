package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum ServerType {
	Ultimate("Ultimate"),
	NCast("NCast");
	private String value = "";

	private ServerType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, ServerType> stringToEnum = new HashMap<String, ServerType>();
	static {
		// Initialize map from constant name to enum constant
		for(ServerType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static ServerType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return ServerType.Ultimate;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
