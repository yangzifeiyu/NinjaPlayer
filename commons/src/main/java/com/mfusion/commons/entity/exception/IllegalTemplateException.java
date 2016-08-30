package com.mfusion.commons.entity.exception;

public class IllegalTemplateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalTemplateException(String cause){
		super(cause+" ");
	}
}
