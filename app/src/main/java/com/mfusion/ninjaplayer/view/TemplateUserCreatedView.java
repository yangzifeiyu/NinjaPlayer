package com.mfusion.ninjaplayer.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.R;
import com.mfusion.templatedesigner.EventDispatcher;
import com.mfusion.templatedesigner.TemplateDesigner;

import java.util.ArrayList;



public class TemplateUserCreatedView extends LinearLayout{
    private static final String TAG = "TemplateUserCreatedView";
    
    private GridView gridView;

    private Button btnNew;

    private View userCreatedView;
    private Context context;
    private TemplateFragmentListener listener;

    public TemplateUserCreatedView(Context context) {
        this(context,null);
    }

    public TemplateUserCreatedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userCreatedView = inflater.inflate(R.layout.fragment_template_user_created_view,this,false);
        gridView = (GridView) userCreatedView.findViewById(R.id.template_list_user_created_grid_view);
        btnNew=(Button)userCreatedView.findViewById(R.id.template_btn_new);
        btnNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goSampleView();
            }
        });




            refresh();


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    VisualTemplate visualTemplate = (VisualTemplate) gridView.getItemAtPosition(position);
                    listener.goDesigner(visualTemplate);
//                    TemplateEntity selectedEntity = XMLTemplate.getInstance().getSampleLayout(visualTemplate, TemplateUserCreatedView.this.context.getAssets());
//                    ProgressDialog progressDialog = ProgressDialog.show(TemplateUserCreatedView.this.context, null, "Loading...");
//                    EventDispatcher.registerProgressDialog(progressDialog);
//                    progressDialog.show();
//
//                    designer.openTemplate(selectedEntity);

                    


                }
            });




        addView(userCreatedView);
    }
    public void refresh(){
        ArrayList<VisualTemplate> visualTemplates = null;
        try {
            visualTemplates = XMLTemplate.getInstance().getAllTemplates();
            TemplateGridViewAdapter adapter = new TemplateGridViewAdapter(context, visualTemplates);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "refresh: ",e );
        }

    }

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }
}
