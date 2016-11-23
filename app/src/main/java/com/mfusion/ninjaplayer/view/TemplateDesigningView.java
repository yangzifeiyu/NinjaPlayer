package com.mfusion.ninjaplayer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.controllers.AbstractTemplateDesigner;
import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.exception.TemplateNotFoundException;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.ResourceSourceType;
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.FileNameTextWatcher;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.FileNameEditor;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.ninjaplayer.R;
import com.mfusion.templatedesigner.TemplateEditPreviewLayout;

import java.io.FileNotFoundException;


public class TemplateDesigningView extends LinearLayout {
    private static final String TAG = "TemplateDesigningView";

    private Context context;
    private AbstractTemplateDesigner designer;
    private ImageTextVerticalView btnSave,btnCancel,btnRename;

    private TextView m_warning_text;

    private TemplateFragmentListener listener;

    public TemplateDesigningView(Context context) {
        this(context,null);
    }

    public TemplateDesigningView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.fragment_template_designing_view,this,false);
        m_warning_text=(TextView)  view.findViewById(R.id.template_warning_view);
        designer= (TemplateEditPreviewLayout) view.findViewById(R.id.template_designer);

        btnRename= (ImageTextVerticalView) view.findViewById(R.id.template_designing_view_btn_rename);
        btnRename.setText("Rename");
        btnRename.setImage(R.drawable.mf_rename);
        btnSave= (ImageTextVerticalView) view.findViewById(R.id.template_designing_view_btn_save);
        btnSave.setText("Save");
        btnSave.setImage(R.drawable.mf_save);
        btnCancel=(ImageTextVerticalView)view.findViewById(R.id.template_designing_view_btn_cancel);
        btnCancel.setImage(R.drawable.mf_undo);
        btnCancel.setText("Back");

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((TemplateEditPreviewLayout)designer).parentFragment.getIsEditing()){
                    listener.goUserCreatedView();
                    return;
                }

                AlertDialogHelper.showAlertDialog(context, "Information", "Do you want to save these modification ?", new CallbackBundle() {
                    @Override
                    public void callback(Bundle bundle) {
                        saveTemplate(true,null);
                    }
                }, new CallbackBundle() {
                    @Override
                    public void callback(Bundle bundle) {
                        listener.goUserCreatedView();
                    }
                });

            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTemplate(false,null);
            }
        });

        btnRename.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldName=designer.getTemplateName();
                FileNameEditor.createDialog(context, "Rename Template", "Please input name for this template",oldName, new OperateCallbackBundle() {
                    @Override
                    public void onConfim(String content) {
                        renameTemplate(oldName,content);
                    }

                    @Override
                    public void onCancel(String errorMsg) {

                    }
                });
            }
        });

        addView(view);
    }

    public void clearUI(){
        if(designer!=null)
            designer.closeTemplate();
    }

    public Boolean saveTemplate(final Boolean isExit, final OperateCallbackBundle callbackBundle){

        try{
            final TemplateEntity savedEntity=designer.saveTemplate();

            if(savedEntity.id!=null&&!savedEntity.id.isEmpty()){
                if(updateTemplate(savedEntity)){
                    if(isExit)
                        listener.goUserCreatedView();
                    return true;
                }
                if(callbackBundle!=null)
                    callbackBundle.onCancel("");
                return false;
            }

            FileNameEditor.createDialog(context, "Create New Template", "Please input name for this template", new OperateCallbackBundle() {
                @Override
                public void onConfim(String content) {
                    savedEntity.id=content;
                    if(addTemplate(savedEntity)) {
                        if (isExit)
                            listener.goUserCreatedView();
                        if(callbackBundle!=null)
                            callbackBundle.onConfim("");
                    }
                }

                @Override
                public void onCancel(String errorMsg) {
                    if(callbackBundle!=null)
                        callbackBundle.onCancel("");
                }
            });
        }catch (IllegalTemplateException ex){
            ex.printStackTrace();
            AlertDialogHelper.showWarningDialog(getContext(),"Save Template",ex.getMessage(),null);
        }catch (Exception ex){
            ex.printStackTrace();
            AlertDialogHelper.showWarningDialog(getContext(),"Save Template","Open failed",null);
        }
        if(callbackBundle!=null)
            callbackBundle.onCancel("");
        return false;
    }

    public void openTemplate(VisualTemplate visualTemplate, AbstractFragment parentFragment) {
        //designer.clearComponentOnScreen();
        String result="";
        designer.showLoadingPage();
        ((TemplateEditPreviewLayout)designer).parentFragment=parentFragment;
        TemplateEntity selectedEntity =null;
        try {
            if(visualTemplate.templateOriginal== ResourceSourceType.internal) {
                ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(true);
                selectedEntity = XMLTemplate.getInstance().getSampleLayout(visualTemplate, getContext().getAssets());
            }
            else {
                ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
                selectedEntity = XMLTemplate.getInstance().getTemplateById(visualTemplate.id);
            }

            designer.openTemplate(selectedEntity);
            return;
        }catch (TemplateNotFoundException ex){
            result="Can't found "+visualTemplate.id+"";
            ex.printStackTrace();
        }catch (IllegalTemplateException ex){
            result="Template has invalid property";
            ex.printStackTrace();
        }catch (Exception ex){
            result="Open Failed";
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("TemplateDesigningView==>openTemplate :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(),"Open Template",result,null);
    }

    private Boolean updateTemplate(TemplateEntity templateEntity){
        String result="Update successfully";
        try {
            XMLTemplate.getInstance().updateTemplate(templateEntity);
            ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
            AlertDialogHelper.showInformationDialog(getContext(),"Save Template",result,null);
            return true;
        }catch (IllegalTemplateException ex) {
            ex.printStackTrace();
            result="Template has invalid property";
        } catch (Exception ex) {
            ex.printStackTrace();
            result="Update Failed";
            LogOperator.WriteLogfortxt("TemplateDesigningView==>updateTemplate :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(),"Save Template",result,null);
        return false;
    }

    private Boolean addTemplate(TemplateEntity templateEntity){
        String result="Update successfully";
        try {
            if(XMLTemplate.getInstance().addTemplate(templateEntity)){
                ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
                ((TemplateEditPreviewLayout)designer).saveTemplateResult(templateEntity);
                AlertDialogHelper.showInformationDialog(getContext(),"Add Template",result,null);
                return true;
            }
            result="Add failed";
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            result="Template can't find";
        }catch (IllegalTemplateException ex) {
            ex.printStackTrace();
            result="Template has invalid property";
        }catch (Exception ex) {
            ex.printStackTrace();
            result="Add failed";
            LogOperator.WriteLogfortxt("TemplateDesigningView==>addTemplate :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(),"Add Template",result,null);
        return false;
    }

    private Boolean renameTemplate(String oldName,String newName){
        String result="";
        try {
            XMLTemplate.getInstance().renameTemplate(oldName,newName);
            designer.renameTemplate(newName);
            return true;
        }catch (TemplateNotFoundException ex) {
            ex.printStackTrace();
            result="Template can't find";
        }catch (Exception ex) {
            ex.printStackTrace();
            result="Rename failed";
            LogOperator.WriteLogfortxt("TemplateDesigningView==>addTemplate :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(),"Rename Template",result,null);
        return false;
    }

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }

}
