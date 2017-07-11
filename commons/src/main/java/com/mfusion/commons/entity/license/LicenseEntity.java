package com.mfusion.commons.entity.license;

import com.mfusion.commons.tools.LicenseStatus;

import java.util.Date;

/**
 * Created by ThinkPad on 2017/1/9.
 */
public class LicenseEntity {

    public String license;

    public String deviceId;

    public int deviceHashCode;

    public Date startDate;

    public Date validDate;

    public LicenseStatus validity=LicenseStatus.expiry;
}
