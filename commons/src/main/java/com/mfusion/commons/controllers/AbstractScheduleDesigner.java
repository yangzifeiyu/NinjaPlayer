package com.mfusion.commons.controllers;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by ThinkPad on 2016/7/13.
 */
public abstract class AbstractScheduleDesigner extends LinearLayout {

    public AbstractScheduleDesigner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public abstract Boolean openSchedule(String scheName);

    public abstract Boolean openSchedule();
}