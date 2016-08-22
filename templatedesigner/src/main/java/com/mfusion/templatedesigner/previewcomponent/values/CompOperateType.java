package com.mfusion.templatedesigner.previewcomponent.values;

import java.util.HashMap;
import java.util.Map;

public enum CompOperateType {
	move,
	moveH,
	n,
	s,
	w,
	e,
	ne,
	nw,
	se,
	sw,
	none;
	private static final Map<String, CompOperateType> stringToEnum = new HashMap<String, CompOperateType>();
	static {
	    // Initialize map from constant name to enum constant
	    for(CompOperateType mode : values()) {
	        stringToEnum.put(mode.toString(), mode);
	    }
	}
	  
	public static CompOperateType fromString(String symbol) {
		if(stringToEnum.containsKey(symbol))
	  		return stringToEnum.get(symbol);
	  	else
	  		return CompOperateType.move;
	}
}
