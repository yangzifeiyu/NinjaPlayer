package com.mfusion.templatedesigner.olddesigner;

import android.app.AlertDialog;

import com.mfusion.commons.entity.template.ComponentEntity;

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

    /**
     * to be fixed
     * @param componentEntity
     * @throws Exception
     */
    public static void dispatchBringToFront(ComponentEntity componentEntity) throws Exception {
        if(templateDesigner!=null)
            templateDesigner.bringToFront(componentEntity);
        else
            throw new Exception("Designer is not registered");
    }


}
