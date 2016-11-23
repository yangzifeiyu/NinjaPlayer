package com.mfusion.commons.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.tools.CallbackBundle;

/**
 * Created by guoyu on 2016/7/13.
 */
public abstract class AbstractTemplateDesigner extends LinearLayout {

    private ProgressDialog loadingDialog;

    protected TemplateNameChangingListener templateNameChangingListener;
    //private TemplateEntity m_template;
    public AbstractTemplateDesigner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AbstractTemplateDesigner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnTemplateNameChangingListener(TemplateNameChangingListener templateNameChangingListener){
        this.templateNameChangingListener=templateNameChangingListener;
    }

    public void showLoadingPage(){
        loadingDialog = ProgressDialog.show(getContext(), null, "Loading...");
        loadingDialog.show();
    }

    public void hiheLoadingPage(){
        if(loadingDialog.isShowing())
            loadingDialog.dismiss();
    }
    /**
     * Open a template entity
     * @param template
     * @return
     */
    public abstract Boolean openTemplate  (TemplateEntity template);

    /***
     *Save a template entity
     * @return
     */
    public  abstract TemplateEntity saveTemplate() throws Exception;

    public abstract void saveTemplateResult(TemplateEntity templateEntity);

    public  abstract String getTemplateName();

    public  abstract void renameTemplate(String newName);

    public abstract void closeTemplate();

    public interface TemplateNameChangingListener{
        void templateNameChanging(String templateName);
    }
}
