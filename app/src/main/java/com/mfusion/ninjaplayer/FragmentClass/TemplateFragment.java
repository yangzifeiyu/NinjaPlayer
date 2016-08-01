package com.mfusion.ninjaplayer.FragmentClass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.R;
import com.mfusion.ninjaplayer.TemplateView2;
import com.mfusion.ninjaplayer.view.TemplateGridViewAdapter;
import com.mfusion.templatedesigner.EventDispatcher;
import com.mfusion.templatedesigner.TemplateDesigner;

import java.util.ArrayList;

public class TemplateFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TemplateView1";

    private GridView gridView;
    private TemplateDesigner designer;
    private Button btnNew;
    private Button btnEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_template, container, false);

        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        gridView = (GridView) rootView.findViewById(R.id.template_list_grid_view);
        designer = (TemplateDesigner) rootView.findViewById(R.id.template_designer);

        btnEdit = (Button) rootView.findViewById(R.id.template_btn_edit);



        try {
            ArrayList<VisualTemplate> visualTemplates= XMLTemplate.getInstance().getAllLocalSampleLayouts(getActivity().getAssets(),"template");
            TemplateGridViewAdapter adapter=new TemplateGridViewAdapter(getActivity(),visualTemplates);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    designer.clearComponentOnScreen();
                    VisualTemplate visualTemplate=(VisualTemplate) gridView.getItemAtPosition(position);
                    TemplateEntity selectedEntity= XMLTemplate.getInstance().getSampleLayout(visualTemplate,getActivity().getAssets());
                    ProgressDialog progressDialog=ProgressDialog.show(getActivity(),null,"Loading...");
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

        btnNew = (Button) rootView.findViewById(R.id.template_btn_new);
        btnNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(),TemplateView2.class);
                startActivity(intent);
                //finish();
            }
        });

        return  rootView;
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
