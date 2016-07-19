package com.mfusion.commons.entity.values;

import java.util.HashMap;
import java.util.Map;


public enum ComponentType {

	DateTime("DateTime"),
	TickerText("TickerText"),
	ScheduleMedia("ScheduleMedia"),
	Interactive("Interactive"),
	RSSComponent("RSSText"),
	AudioComponent("AudioComponent"),
	WeatherComponent("WeatherComponent"),
	Unkown("");
	private String value = "";

	private ComponentType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, ComponentType> stringToEnum = new HashMap<String, ComponentType>();
	static {
		// Initialize map from constant name to enum constant
		for(ComponentType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}
	public static ComponentType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return ComponentType.Unkown;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
