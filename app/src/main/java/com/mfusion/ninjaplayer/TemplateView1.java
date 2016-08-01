package com.mfusion.ninjaplayer;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class TemplateView1 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TemplateView1";

    private GridView gridView;
    private TemplateDesigner designer;
    private Button btnNew;
    private Button btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.template_list_grid_view);
        designer = (TemplateDesigner) findViewById(R.id.template_designer);

        btnEdit = (Button) findViewById(R.id.template_btn_edit);


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
                    ProgressDialog progressDialog=ProgressDialog.show(TemplateView1.this,null,"Loading...");
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

        btnNew = (Button) findViewById(R.id.template_btn_new);
        btnNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(TemplateView1.this,TemplateView2.class);
                startActivity(intent);
                //finish();
            }
        });

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
