package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.R;
import com.mfusion.ninjaplayer.view.TemplateDesigningView;
import com.mfusion.ninjaplayer.view.TemplateFragmentListener;
import com.mfusion.ninjaplayer.view.TemplateSampleView;
import com.mfusion.ninjaplayer.view.TemplateUserCreatedView;

public class TemplateFragment extends Fragment{
    private static final String TAG = "TemplateView1";

    private FrameLayout placeHolder;

    private TemplateUserCreatedView templateUserCreatedView;
    private TemplateSampleView templateSampleView;
    private TemplateDesigningView templateDesigningView;

    private FlowListener flowListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        flowListener=new FlowListener();

        templateSampleView=new TemplateSampleView(getContext());
        templateSampleView.setListener(flowListener);
        templateUserCreatedView=new TemplateUserCreatedView(getContext());
        templateUserCreatedView.setListener(flowListener);
        templateDesigningView=new TemplateDesigningView(getContext());
        templateDesigningView.setListener(flowListener);

        View rootView = inflater.inflate(R.layout.fragment_template,container,false);
        placeHolder= (FrameLayout) rootView.findViewById(R.id.fragment_template_place_holder);
        placeHolder.addView(templateUserCreatedView);

        //View rootView=new TemplateSampleView(getContext());


//        gridView = (GridView) rootView.findViewById(R.id.template_list_grid_view);
//        designer = (TemplateDesigner) rootView.findViewById(R.id.template_designer);
//
//        btnSave = (Button) rootView.findViewById(R.id.template_btn_save);
//        btnCancel=(Button)rootView.findViewById(R.id.template_btn_cancel);
//
//        btnSave.setOnClickListener(this);
//        btnCancel.setOnClickListener(this);
//
//
//
//        try {
//            ArrayList<VisualTemplate> visualTemplates= XMLTemplate.getInstance().getAllLocalSampleLayouts(getActivity().getAssets(),"template");
//            TemplateGridViewAdapter adapter=new TemplateGridViewAdapter(getActivity(),visualTemplates);
//            gridView.setAdapter(adapter);
//            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    designer.clearComponentOnScreen();
//                    VisualTemplate visualTemplate=(VisualTemplate) gridView.getItemAtPosition(position);
//                    TemplateEntity selectedEntity= XMLTemplate.getInstance().getSampleLayout(visualTemplate,getActivity().getAssets());
//                    ProgressDialog progressDialog=ProgressDialog.show(getActivity(),null,"Loading...");
//                    EventDispatcher.registerProgressDialog(progressDialog);
//                    progressDialog.show();
//
//                    designer.openTemplate(selectedEntity);
//                    toggleListAndDesigner();
//
//
//
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "onCreate: ",e );
//        }
//
//
//        designer.setVisibility(View.GONE);



        return  rootView;
    }

    private class FlowListener implements TemplateFragmentListener{

        @Override
        public void goSampleView() {
            placeHolder.removeAllViews();
            placeHolder.addView(templateSampleView);
        }

        @Override
        public void goUserCreatedView() {
            placeHolder.removeAllViews();
            templateUserCreatedView.refresh();
            placeHolder.addView(templateUserCreatedView);
        }

        @Override
        public void goDesigner(VisualTemplate selectedVisualTemplate) {
            placeHolder.removeAllViews();
            placeHolder.addView(templateDesigningView);
            templateDesigningView.openTemplate(selectedVisualTemplate);
        }
    }

//    private void toggleListAndDesigner(){
//        if(designer.getVisibility()==View.GONE){
//            designer.setVisibility(View.VISIBLE);
//            gridView.setVisibility(View.GONE);
//        }
//        else{
//            designer.setVisibility(View.GONE);
//            gridView.setVisibility(View.VISIBLE);
//        }
//    }
//    @Override
//    public void onClick(View v) {
//        if(v.getId()==R.id.template_btn_save){
//            toggleListAndDesigner();
//            //TODO: SAVE COMPONENT
//        }
//        else if(v.getId()==R.id.template_btn_cancel){
//            toggleListAndDesigner();
//
//
//        }
//    }
}
