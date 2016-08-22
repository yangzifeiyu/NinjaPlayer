package com.mfusion.player.common.Enum;

import java.util.HashMap;
import java.util.Map;

public enum TransitionEffectType {
	fadein("plfadein"), 
	fadeout("fadeout"),
	
	slidein("slidein"), 
	slideout("slideOut"),
	
	scalein("scalein"), 
	scaleout("scaleout"), 
	
	rotatein("rotatein"),
	rotateout("rotateout"),
	
	scalerotateout("scalerotateout"),
	scalerotatein("scalerotatein"), 
	
	slidefadein("slidefadein"),
	slidefadeout("slidefadeout"),
	plnone("plnone"),
	none("none");

	private String value = "";

	private TransitionEffectType(String value) {
		this.value = value;  
	}

	public String getValue() {  
		return value;  
	}

	public void setValue(String value) {
		this.value = value;  
	}


	private static final Map<String, TransitionEffectType> stringToEnum = new HashMap<String, TransitionEffectType>();
	static {
		// Initialize map from constant name to enum constant
		for(TransitionEffectType type : values()) {
			stringToEnum.put(type.toString(), type);
		}
	}


	public static TransitionEffectType fromString(String symbol) {
		try
		{
			return stringToEnum.get(symbol);
		}catch(Exception e)
		{
			return TransitionEffectType.plnone;
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
