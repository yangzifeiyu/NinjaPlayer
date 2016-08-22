package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum PlayMode {
	sequence("PLSequence"),
	random("Random");
	private String value = "";

	private PlayMode(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, PlayMode> stringToEnum = new HashMap<String, PlayMode>();
	static {
		// Initialize map from constant name to enum constant
		for(PlayMode type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static PlayMode fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return PlayMode.sequence;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}

