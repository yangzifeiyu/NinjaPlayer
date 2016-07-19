package com.mfusion.commons.entity.exception;

public class ScheduleNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ScheduleNotFoundException(String cause){
		super(cause+" :Schedule is not found");
	}

}
