package com.mfusion.templatedesigner;

import android.content.Context;

import com.mfusion.commons.controllers.AbstractTemplateDesigner;
import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.exception.TemplateNotFoundException;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.data.XMLTemplate;
/**
 * Created by ThinkPad on 2016/7/13.
 */
public class TemplateDesigner extends AbstractTemplateDesigner {

    public TemplateDesigner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Boolean openTemplate(String templateName) {
        // TODO Auto-generated method stub
        return null;
    }

    private TemplateEntity getTemplateInfoFromDesigner() {
        // TODO Auto-generated method stub
        return null;
    }

    private Boolean saveTemplateInfo(){
        try {

            XMLTemplate.getInstance().updateTemplate(this.getTemplateInfoFromDesigner());
            return true;
        }catch (IllegalTemplateException ex){

        }catch (Exception ex){

        }
        return false;
    }

    private TemplateEntity getTemplateInfo(String templateName){
        try {

            return XMLTemplate.getInstance().getTemplateById(templateName);

        }catch (TemplateNotFoundException ex){

        }catch (Exception ex){

        }
        return  null;
    }
}
