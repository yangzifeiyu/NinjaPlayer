package com.mfusion.ninjaplayer.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.R;
import com.mfusion.templatedesigner.EventDispatcher;
import com.mfusion.templatedesigner.TemplateDesigner;


public class TemplateDesigningView extends LinearLayout {
    private static final String TAG = "TemplateDesigningView";

    private Context context;
    private TemplateDesigner designer;
    private Button btnSave;
    private Button btnCancel;

    private TemplateFragmentListener listener;

    public TemplateDesigningView(Context context) {
        this(context,null);
    }

    public TemplateDesigningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.fragment_template_designing_view,this,false);
        designer= (TemplateDesigner) view.findViewById(R.id.template_designer);
        btnSave= (Button) view.findViewById(R.id.template_designing_view_btn_save);
        btnCancel=(Button)view.findViewById(R.id.template_designing_view_btn_cancel);

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure to quit without saving any changes ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.goUserCreatedView();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final TemplateEntity savedEntity=designer.saveTemplate();
                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Name your template");
                final EditText etName=new EditText(getContext());
                builder.setView(etName);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=etName.getText().toString();
                        savedEntity.id=name;
                        try {
                            XMLTemplate.getInstance().updateTemplate(savedEntity);
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: ",e );
                        }
                        listener.goUserCreatedView();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        addView(view);


    }

    public void openTemplate(VisualTemplate visualTemplate) {
        designer.clearComponentOnScreen();

        TemplateEntity selectedEntity = XMLTemplate.getInstance().getSampleLayout(visualTemplate, getContext().getAssets());
        if(selectedEntity==null){
            try {
                selectedEntity=XMLTemplate.getInstance().getTemplateById(visualTemplate.id);
            } catch (Exception e) {
                Log.e(TAG, "openTemplate: ",e );
            }
        }


        ProgressDialog progressDialog = ProgressDialog.show(getContext(), null, "Loading...");
        EventDispatcher.registerProgressDialog(progressDialog);
        progressDialog.show();

        designer.openTemplate(selectedEntity);
    }

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }
}
