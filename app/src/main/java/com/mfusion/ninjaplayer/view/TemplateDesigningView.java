package com.mfusion.ninjaplayer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
                    clearUI();
                    return;
                }

                AlertDialogHelper.showAlertDialog(context, "Information", "Do you want to save these modification ?", new CallbackBundle() {
                    @Override
                    public void callback(Bundle bundle) {
                        saveTemplate(true, new OperateCallbackBundle() {
                            @Override
                            public void onConfim(String content) {
                                listener.goUserCreatedView();
                                clearUI();
                            }

                            @Override
                            public void onCancel(String errorMsg) {

                            }
                        });
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
                        renameTemplateTask=new AsyncTemplateRenameTask();
                        renameTemplateTask.setOperateObject(oldName,content);
                        renameTemplateTask.execute("");
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
        if(designer!=null) {
            designer.setVisibility(INVISIBLE);
            designer.closeTemplate();
        }
    }

    AsyncTemplateUpdateTask updateTemplateTask=null;
    AsyncTemplateAddTask addTemplateTask=null;
    AsyncTemplateRenameTask renameTemplateTask=null;
    public void saveTemplate(final Boolean isExit, final OperateCallbackBundle callbackBundle){

        String result="";
        try{
            final TemplateEntity savedEntity=designer.saveTemplate();

            if(savedEntity.id!=null&&!savedEntity.id.isEmpty()){
                updateTemplateTask=new AsyncTemplateUpdateTask();
                updateTemplateTask.setOperateObject(savedEntity,callbackBundle);
                updateTemplateTask.execute("");
                //updateTemplate(savedEntity,callbackBundle);
                return;
            }

            FileNameEditor.createDialog(context, "Create New Template", "Please input name for this template", new OperateCallbackBundle() {
                @Override
                public void onConfim(String content) {
                    savedEntity.id=content;
                    addTemplateTask=new AsyncTemplateAddTask();
                    addTemplateTask.setOperateObject(savedEntity,callbackBundle);
                    addTemplateTask.execute("");
                    //addTemplate(savedEntity,callbackBundle);
                }

                @Override
                public void onCancel(String errorMsg) {
                    if(callbackBundle!=null)
                        callbackBundle.onCancel("");
                }
            });
            return;
        }catch (IllegalTemplateException ex){
            ex.printStackTrace();
            result=ex.getMessage();
        }catch (Exception ex){
            ex.printStackTrace();
            result="Open failed";
        }

        AlertDialogHelper.showWarningDialog(getContext(), "Save Template", result, new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                if(callbackBundle!=null)
                    callbackBundle.onCancel("");
            }
        });

    }

    public void openTemplate(VisualTemplate visualTemplate, AbstractFragment parentFragment) {
        //designer.clearComponentOnScreen();
        String result="";
        designer.setVisibility(VISIBLE);
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

    private Boolean updateTemplate(TemplateEntity templateEntity,final OperateCallbackBundle callbackBundle){
        String result="Update successfully";
        try {
            XMLTemplate.getInstance().updateTemplate(templateEntity);
            ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
            AlertDialogHelper.showInformationDialog(getContext(), "Save Template", result, new CallbackBundle() {
                @Override
                public void callback(Bundle bundle) {
                    if(callbackBundle!=null)
                        callbackBundle.onConfim("");
                }
            });
            return true;
        }catch (IllegalTemplateException ex) {
            ex.printStackTrace();
            result="Template has invalid property";
        } catch (Exception ex) {
            ex.printStackTrace();
            result="Update Failed";
            LogOperator.WriteLogfortxt("TemplateDesigningView==>updateTemplate :"+ex.getMessage());
        }
        AlertDialogHelper.showWarningDialog(getContext(), "Save Template", result, new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                if(callbackBundle!=null)
                    callbackBundle.onCancel("");
            }
        });
        return false;
    }

    private Boolean addTemplate(TemplateEntity templateEntity,final OperateCallbackBundle callbackBundle){
        String result="Update successfully";
        try {
            if(XMLTemplate.getInstance().addTemplate(templateEntity)){
                ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
                ((TemplateEditPreviewLayout)designer).saveTemplateResult(templateEntity);
                AlertDialogHelper.showInformationDialog(getContext(), "Add Template", result, new CallbackBundle() {
                    @Override
                    public void callback(Bundle bundle) {
                        if(callbackBundle!=null)
                            callbackBundle.onConfim("");
                    }
                });
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
        AlertDialogHelper.showWarningDialog(getContext(), "Add Template", result, new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                if(callbackBundle!=null)
                    callbackBundle.onCancel("");
            }
        });
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

    protected class AsyncTemplateUpdateTask extends AsyncTask<String, Integer, String> {

        TemplateEntity edited_template;

        Boolean operate_result=false;

        OperateCallbackBundle result_callback_bundle;

        public void setOperateObject(TemplateEntity savedEntity,OperateCallbackBundle callbackBundle){
            operate_result=false;
            result_callback_bundle=callbackBundle;
            edited_template=savedEntity;
        }

        @Override
        protected String doInBackground(String... params) {
            String result="Update successfully";
            try {
                XMLTemplate.getInstance().updateTemplate(edited_template);
                ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
                operate_result = true;
            }catch (IllegalTemplateException ex) {
                ex.printStackTrace();
                result="Template has invalid property";
            } catch (Exception ex) {
                ex.printStackTrace();
                result="Update Failed";
                LogOperator.WriteLogfortxt("TemplateDesigningView==>updateTemplate :"+ex.getMessage());
            }

            return result;
        }
        @Override
        protected void onPostExecute(String result) {

            AlertDialogHelper.showAlertDialog(getContext(),operate_result? AlertDialogHelper.AlertDialogType.Information: AlertDialogHelper.AlertDialogType.Warning, "Save Template", result, new CallbackBundle() {
                @Override
                public void callback(Bundle bundle) {
                    if(result_callback_bundle!=null&&operate_result)
                        result_callback_bundle.onConfim("");
                    if(result_callback_bundle!=null&&!operate_result)
                        result_callback_bundle.onCancel("");
                }
            });
        }
    }

    protected class AsyncTemplateAddTask extends AsyncTask<String, Integer, String> {

        TemplateEntity edited_template;

        Boolean operate_result=false;

        OperateCallbackBundle result_callback_bundle;

        public void setOperateObject(TemplateEntity savedEntity,OperateCallbackBundle callbackBundle){
            operate_result=false;
            result_callback_bundle=callbackBundle;
            edited_template=savedEntity;
        }

        @Override
        protected String doInBackground(String... params) {
            String result="Update successfully";
            try {
                if(XMLTemplate.getInstance().addTemplate(edited_template)){
                    ((TemplateEditPreviewLayout)designer).parentFragment.setIsEditing(false);
                    operate_result=true;
                    return result;
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

            return result;
        }
        @Override
        protected void onPostExecute(String result) {

            AlertDialogHelper.showAlertDialog(getContext(),operate_result? AlertDialogHelper.AlertDialogType.Information: AlertDialogHelper.AlertDialogType.Warning, "Add Template", result, new CallbackBundle() {
                @Override
                public void callback(Bundle bundle) {
                    if(operate_result) {
                        ((TemplateEditPreviewLayout)designer).saveTemplateResult(edited_template);
                        if (result_callback_bundle != null && operate_result)
                            result_callback_bundle.onConfim("");
                    }
                    if(result_callback_bundle!=null&&!operate_result)
                        result_callback_bundle.onCancel("");
                }
            });
        }
    }

    protected class AsyncTemplateRenameTask extends AsyncTask<String, Integer, String> {

        String oldName,newName;

        Boolean operate_result=false;

        public void setOperateObject(String oldName,String newName){
            this.oldName=oldName;
            this.newName=newName;
            operate_result=false;
        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            try {
                XMLTemplate.getInstance().renameTemplate(oldName,newName);
                operate_result=true;
            }catch (TemplateNotFoundException ex) {
                ex.printStackTrace();
                result="Template can't find";
            }catch (Exception ex) {
                ex.printStackTrace();
                result="Rename failed";
                LogOperator.WriteLogfortxt("TemplateDesigningView==>renameTemplate :"+ex.getMessage());
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {

            if(operate_result)
                designer.renameTemplate(newName);
            else
                AlertDialogHelper.showWarningDialog(getContext(), "Rename Template", result, null);
        }
    }
}
