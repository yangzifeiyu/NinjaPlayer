package com.mfusion.templatedesigner;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.mfusion.commons.entity.template.ComponentEntity;

/**
 * Created by 1B15182 on 27/7/2016 0027.
 */
public class EventDispatcher {
    private static TemplateDesigner templateDesigner;
    private static AlertDialog progressDialog;

    public static void registerDesigner(TemplateDesigner designer){
        templateDesigner=designer;

    }
    public static void registerProgressDialog(AlertDialog dialog){
        progressDialog=dialog;
    }
    public static void dispatchRefreshEvent() throws Exception {
        if(templateDesigner!=null){

            templateDesigner.showTemplateOnScreen();
        }
        else
            throw new Exception("Designer is not registered");

    }
    public static void dispatchSaveEvent() throws Exception {
        if(templateDesigner!=null){
            templateDesigner.saveComponentEntityInfoFromDesigner();
        }
        else
            throw new Exception("Designer is not registered");
    }
    public static void dispatchDeleteEvent(ComponentEntity entity) throws Exception {
        if(templateDesigner!=null){
            templateDesigner.deleteComponent(entity);
        }
        else
            throw new Exception("Designer is not registered");
    }
    public static void dispatchDisposeProgressDialog() throws Exception {
        if(progressDialog!=null)
            progressDialog.dismiss();
        else
            throw new Exception("Progress dialog is not registered");
    }


}
