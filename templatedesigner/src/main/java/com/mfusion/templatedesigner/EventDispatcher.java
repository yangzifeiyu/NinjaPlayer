package com.mfusion.templatedesigner;

import com.mfusion.commons.entity.template.ComponentEntity;


public class EventDispatcher {
    private static TemplateDesigner templateDesigner;

    public static void registerDesigner(TemplateDesigner designer){
        templateDesigner=designer;

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


}
