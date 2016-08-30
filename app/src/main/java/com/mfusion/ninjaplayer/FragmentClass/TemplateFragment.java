package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.ninjaplayer.R;
import com.mfusion.ninjaplayer.view.TemplateDesigningView;
import com.mfusion.ninjaplayer.view.TemplateFragmentListener;
import com.mfusion.ninjaplayer.view.TemplateSampleView;
import com.mfusion.ninjaplayer.view.TemplateUserCreatedView;

public class TemplateFragment extends AbstractFragment {

    private static final String TAG = "TemplateView1";

    private FrameLayout placeHolder;

    private TemplateUserCreatedView templateUserCreatedView;
    private TemplateSampleView templateSampleView;
    private TemplateDesigningView templateDesigningView;

    private FlowListener flowListener;

    private AbstractFragment instance;

    public TemplateFragment(){
        instance=this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView!=null){
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_template,container,false);
        placeHolder= (FrameLayout) rootView.findViewById(R.id.fragment_template_place_holder);

        flowListener=new FlowListener();

        //flowListener.goUserCreatedView();
        return  rootView;
    }

    @Override
    public void saveModification(OperateCallbackBundle callbackBundle) {
        if(isEditing)
            templateDesigningView.saveTemplate(true,callbackBundle);
    }

    @Override
    public void cancelSaveModification() {

    }

    @Override
    public void showFragment() {
        if(flowListener!=null){
            System.out.println(DateConverter.convertCurrentDateToStr("HH:mm:ss SSS")+" start show template");
            flowListener.goUserCreatedView();
        }
    }

    @Override
    public void hideFragment() {
        if(templateDesigningView!=null){
            templateDesigningView.clearUI();
        }
    }

    private class FlowListener implements TemplateFragmentListener{

        @Override
        public void goSampleView() {
            placeHolder.removeAllViews();
            if(templateSampleView==null){
                templateSampleView=new TemplateSampleView(getContext());
                templateSampleView.setListener(flowListener);
            }
            templateSampleView.reBindingData();
            placeHolder.addView(templateSampleView);
            isEditing=false;
        }

        @Override
        public void goUserCreatedView() {
            placeHolder.removeAllViews();
            if(templateUserCreatedView==null){
                templateUserCreatedView=new TemplateUserCreatedView(getContext());
                templateUserCreatedView.setListener(flowListener);
            }
            templateUserCreatedView.loadingDatas();
            System.out.println(DateConverter.convertCurrentDateToStr("HH:mm:ss SSS")+" end show template");
            placeHolder.addView(templateUserCreatedView);
            isEditing=false;
        }

        @Override
        public void goDesigner(VisualTemplate selectedVisualTemplate) {
            placeHolder.removeAllViews();
            if(templateDesigningView==null){
                templateDesigningView=new TemplateDesigningView(getContext());
                templateDesigningView.setListener(flowListener);
            }
            placeHolder.addView(templateDesigningView);
            templateDesigningView.openTemplate(selectedVisualTemplate,instance);
        }
    }

}
