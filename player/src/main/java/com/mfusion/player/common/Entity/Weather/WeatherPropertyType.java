package com.mfusion.player.common.Entity.Weather;

import java.util.HashMap;
import java.util.Map;

public enum WeatherPropertyType {
	Text,
    Image;
	
	private static final Map<String, WeatherPropertyType> stringToEnum = new HashMap<String, WeatherPropertyType>();
	static {
		// Initialize map from constant name to enum constant
		for(WeatherPropertyType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}
	public static WeatherPropertyType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return WeatherPropertyType.Text;
		}
	}
}
