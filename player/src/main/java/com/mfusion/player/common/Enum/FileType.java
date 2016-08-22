package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum FileType {
	Image("Image"), 
	Video("Video"),
	Audio("Sound"),
	Html("Html"),
	Streaming("Streaming"),
	Unkown("");
	private String value = "";

	private FileType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, FileType> stringToEnum = new HashMap<String, FileType>();
	static {
		// Initialize map from constant name to enum constant
		for(FileType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}
	public static FileType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return FileType.Unkown;
		}
	}

	@Override
	public String toString() {
		return value;
	}
	
}
