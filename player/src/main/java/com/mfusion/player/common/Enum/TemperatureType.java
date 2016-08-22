package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum TemperatureType {
	 Celsius,
     Fahrenheit;
	 
	 private static final Map<String, TemperatureType> stringToEnum = new HashMap<String, TemperatureType>();
	 static {
		// Initialize map from constant name to enum constant
		for(TemperatureType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	 }
	 public static TemperatureType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return TemperatureType.Celsius;
		}
	 }
}
