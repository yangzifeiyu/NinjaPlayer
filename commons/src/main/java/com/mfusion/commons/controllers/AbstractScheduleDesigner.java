package com.mfusion.commons.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mfusion.commons.entity.schedule.Schedule;

/**
 * Created by ThinkPad on 2016/7/13.
 */
public abstract class AbstractScheduleDesigner extends LinearLayout {

    private ProgressDialog loadingDialog;

    public AbstractScheduleDesigner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public AbstractScheduleDesigner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractScheduleDesigner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showLoadingPage(){
        loadingDialog = ProgressDialog.show(getContext(), null, "Loading...");
        loadingDialog.show();
    }

    public void hiheLoadingPage(){
        if(loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    public abstract Boolean openSchedule(Schedule schedule);

    public abstract Schedule saveSchedule();

    public abstract void closeDesigner();
}