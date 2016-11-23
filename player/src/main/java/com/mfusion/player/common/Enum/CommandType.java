package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {

	Shutdown("Shutdown"),
	WakeUp("WakeUp"),
	Restart("Reboot"),
	Volume("Volume"),
	Unkown("");
	private String value = "";

	private CommandType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, CommandType> stringToEnum = new HashMap<String, CommandType>();
	static {
		// Initialize map from constant name to enum constant
		for(CommandType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}
	public static CommandType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return CommandType.Unkown;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
