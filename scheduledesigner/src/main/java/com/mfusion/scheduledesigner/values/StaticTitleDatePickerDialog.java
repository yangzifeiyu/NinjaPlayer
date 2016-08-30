package com.mfusion.scheduledesigner.values;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

/**
 * Created by ThinkPad on 2016/8/23.
 */
public class StaticTitleDatePickerDialog extends DatePickerDialog {

    public StaticTitleDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public StaticTitleDatePickerDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, listener, year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        getDatePicker().init(year, month, day, this);
        //updateTitle(year, month, day);
    }
}
