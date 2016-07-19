package com.mfusion.commons.controllers;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by guoyu on 2016/7/13.
 */
public abstract class AbstractTemplateDesigner extends LinearLayout {

    public AbstractTemplateDesigner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public abstract Boolean openTemplate(String templateName);
}
