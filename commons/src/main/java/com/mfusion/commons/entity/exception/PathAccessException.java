package com.mfusion.commons.entity.exception;

public class PathAccessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PathAccessException(String cause){
		super(cause+" :File access is denied");
	}
}
