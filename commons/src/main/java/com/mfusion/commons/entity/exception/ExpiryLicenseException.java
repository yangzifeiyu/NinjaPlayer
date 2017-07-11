package com.mfusion.commons.entity.exception;

import com.mfusion.commons.tools.DateConverter;

import java.util.Date;

/**
 * Created by ThinkPad on 2017/5/26.
 */
public class ExpiryLicenseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ExpiryLicenseException(Date start, Date end){
        super(String.format("License is expiry. it valid between %s and %s.", DateConverter.convertToDisplayStr(start), DateConverter.convertToDisplayStr(end,"N/A")));
    }
}
