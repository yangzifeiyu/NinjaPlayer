package com.mfusion.ninjaplayer.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.ninjaplayer.R;
import com.mfusion.templatedesigner.TemplateEditPreviewLayout;
import com.mfusion.templatedesigner.olddesigner.EventDispatcher;
import com.mfusion.templatedesigner.olddesigner.TemplateDesigner;


public class TemplateDesigningView extends LinearLayout {
    private static final String TAG = "TemplateDesigningView";

    private Context context;
    private AbstractTemplateDesigner designer;
    private ImageTextView btnSave;
    private ImageTextView btnCancel;

    private TextView m_warning_text;

    private TemplateFragmentListener listener;

    public TemplateDesigningView(Context context) {
        this(context,null);
    }

    public TemplateDesigningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.fragment_template_designing_view,this,false);
        m_warning_text=(TextView)  view.findViewById(R.id.template_warning_view);
        designer= (TemplateEditPreviewLayout) view.findViewById(R.id.template_designer);
        //designer= (TemplateDesigner) view.findViewById(R.id.template_designer);
        btnSave= (ImageTextView) view.findViewById(R.id.template_designing_view_btn_save);
        btnSave.setText("Save");
        btnSave.setImage(R.drawable.mf_save);
        btnCancel=(ImageTextView)view.findViewById(R.id.template_designing_view_btn_cancel);
        btnCancel.setImage(R.drawable.mf_undo);
        btnCancel.setText("Back");

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((TemplateEditPreviewLayout)designer).parentFragment.isEditing){
                    listener.goUserCreatedView();
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Information");
                builder.setMessage("Do you want to save these modification ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        saveTemplate(true,null);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.goUserCreatedView();
                    }
                });
                builder.show();
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTemplate(false,null);
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

            final EditText etName=new EditText(getContext());
            etName.setHint("Please input name fot this template");
            AlertDialog.Builder builder =new AlertDialog.Builder(getContext()).setTitle("Create New Template")//���öԻ������
                    .setView(etName)
                    .setPositiveButton("Apply",null).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�
                            if(callbackBundle!=null)
                                callbackBundle.onCancel("");
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false);
            final AlertDialog dialog =builder.show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���
            Button negativeButton=((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            builder.setView(etName);
            negativeButton.requestFocus();
            negativeButton.setFocusable(true);

            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=etName.getText().toString();
                    if(name.isEmpty()) {
                        return;
                    }
                    savedEntity.id=name;
                    if(addTemplate(savedEntity)) {
                        if(isExit)
                            listener.goUserCreatedView();

                        if(callbackBundle!=null)
                            callbackBundle.onConfim("");
                        dialog.dismiss();
                    }
                }
            });
        }catch (IllegalTemplateException ex){
            ex.printStackTrace();
            m_warning_text.setText(ex.getMessage());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if(callbackBundle!=null)
            callbackBundle.onCancel("");
        return false;
    }

    public void openTemplate(VisualTemplate visualTemplate, AbstractFragment parentFragment) {
        //designer.clearComponentOnScreen();
        m_warning_text.setText("");
        designer.showLoadingPage();
        ((TemplateEditPreviewLayout)designer).parentFragment=parentFragment;
        TemplateEntity selectedEntity =null;
        try {
            if(visualTemplate.templateOriginal== ResourceSourceType.internal) {
                ((TemplateEditPreviewLayout)designer).parentFragment.isEditing=true;
                selectedEntity = XMLTemplate.getInstance().getSampleLayout(visualTemplate, getContext().getAssets());
            }
            else {
                ((TemplateEditPreviewLayout)designer).parentFragment.isEditing=false;
                selectedEntity = XMLTemplate.getInstance().getTemplateById(visualTemplate.id);
            }

            designer.openTemplate(selectedEntity);
        }catch (TemplateNotFoundException ex){
            m_warning_text.setText("Can't found "+visualTemplate.id+"");
            ex.printStackTrace();
        }catch (IllegalTemplateException ex){
            m_warning_text.setText("Template has invalid property");
            ex.printStackTrace();
        }catch (Exception ex){
            m_warning_text.setText("Open Failed");
            ex.printStackTrace();
            LogOperator.WriteLogfortxt("TemplateDesigningView==>openTemplate :"+ex.getMessage());
        }
    }

    private Boolean updateTemplate(TemplateEntity templateEntity){
        try {
            XMLTemplate.getInstance().updateTemplate(templateEntity);
            ((TemplateEditPreviewLayout)designer).parentFragment.isEditing=false;
            m_warning_text.setText("Update successfully");
            return true;
        }catch (IllegalTemplateException ex) {
            ex.printStackTrace();
            m_warning_text.setText("Template has invalid property");
        } catch (Exception ex) {
            ex.printStackTrace();
            m_warning_text.setText("Update Failed");
            LogOperator.WriteLogfortxt("TemplateDesigningView==>updateTemplate :"+ex.getMessage());
        }
        return false;
    }

    private Boolean addTemplate(TemplateEntity templateEntity){
        try {
            XMLTemplate.getInstance().addTemplate(templateEntity);
            ((TemplateEditPreviewLayout)designer).parentFragment.isEditing=false;
            m_warning_text.setText("Add Successfully");
            return true;
        } catch (IllegalTemplateException ex) {
            ex.printStackTrace();
            m_warning_text.setText("Template has invalid property");
        }catch (Exception ex) {
            ex.printStackTrace();
            m_warning_text.setText("Add failed");
            LogOperator.WriteLogfortxt("TemplateDesigningView==>addTemplate :"+ex.getMessage());
        }
        return false;
    }
    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }

}
