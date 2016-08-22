package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum ServerConnectStatus {
	Connection("Connection"),
	Unconnection("Unconnection"),
	OverLimit("OverLimit");
	
	private String value = "";

	private ServerConnectStatus(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, ServerConnectStatus> stringToEnum = new HashMap<String, ServerConnectStatus>();
	static {
		// Initialize map from constant name to enum constant
		for(ServerConnectStatus type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static ServerConnectStatus fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return ServerConnectStatus.Unconnection;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
