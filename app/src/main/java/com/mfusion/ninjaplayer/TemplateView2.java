package com.mfusion.ninjaplayer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.view.TemplateGridViewAdapter;
import com.mfusion.templatedesigner.EventDispatcher;
import com.mfusion.templatedesigner.TemplateDesigner;

import java.util.ArrayList;

/**
 * Created by MIN RUI on 1/8/2016.
 */
public class TemplateView2 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TemplateView2";

    private GridView gridView;
    private TemplateDesigner designer;
    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        gridView = (GridView) findViewById(R.id.template_list_grid_view);
        designer = (TemplateDesigner) findViewById(R.id.template_designer);

        btnSave = (Button) findViewById(R.id.template_btn_save);
        btnCancel = (Button) findViewById(R.id.template_btn_cancel);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        try {
            ArrayList<VisualTemplate> visualTemplates= XMLTemplate.getInstance().getAllLocalSampleLayouts(getAssets(),"template");
            TemplateGridViewAdapter adapter=new TemplateGridViewAdapter(this,visualTemplates);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    designer.clearComponentOnScreen();
                    VisualTemplate visualTemplate=(VisualTemplate) gridView.getItemAtPosition(position);
                    TemplateEntity selectedEntity= XMLTemplate.getInstance().getSampleLayout(visualTemplate,getAssets());
                    ProgressDialog progressDialog=ProgressDialog.show(TemplateView2.this,null,"Loading...");
                    EventDispatcher.registerProgressDialog(progressDialog);
                    progressDialog.show();

                    designer.openTemplate(selectedEntity);
                    toggleListAndDesigner();



                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ",e );
        }


        designer.setVisibility(View.GONE);

    }

    private void toggleListAndDesigner(){
        if(designer.getVisibility()==View.GONE){
            designer.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
        else{
            designer.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.template_btn_new){
            toggleListAndDesigner();
            //TODO: SAVE COMPONENT
        }
        else if(v.getId()==R.id.template_btn_edit){
            toggleListAndDesigner();
        }
    }


}
