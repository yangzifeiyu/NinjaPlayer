package com.mfusion.commons.entity.exception;

public class TemplateNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TemplateNotFoundException(String cause){
		super(cause+" :Template is not found");
	}

}
