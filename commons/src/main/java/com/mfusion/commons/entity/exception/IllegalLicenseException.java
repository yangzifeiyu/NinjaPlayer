package com.mfusion.commons.entity.exception;

import com.mfusion.commons.tools.LicenseStatus;

/**
 * Created by ThinkPad on 2017/1/9.
 */
public class IllegalLicenseException  extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public IllegalLicenseException(){
        super("License is invalid, device is not match");
    }
}
