package com.mfusion.ninjaplayer.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.FileSelectType;
import com.mfusion.commons.entity.values.ResourceSourceType;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.view.ImageTextVerticalView;
import com.mfusion.commons.view.adapter.TemplateInfoAdapter;
import com.mfusion.ninjaplayer.R;

import java.util.ArrayList;
import java.util.List;


public class TemplateSampleView extends LinearLayout {
    private static final String TAG = "TemplateSampleView";
    private Context context;
    private GridView gridView;
    private ImageTextVerticalView btnBack,btnEdit;

    private ArrayList<VisualTemplate> visualTemplates;

    private TemplateInfoAdapter templateAdapter;

    private TemplateFragmentListener listener;
    public TemplateSampleView(Context context) {
        this(context,null);
    }

    public TemplateSampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.fragment_template_sample_view,this,false);
        gridView= (GridView) view.findViewById(R.id.template_sample_grid_view);
        btnBack= (ImageTextVerticalView) view.findViewById(R.id.template_sample_view_btn_back);
        btnBack.setText("Back");
        btnBack.setImage(R.drawable.mf_undo);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goUserCreatedView();
            }
        });
        btnEdit= (ImageTextVerticalView) view.findViewById(R.id.template_sample_view_btn_edit);
        btnEdit.setText("Edit");
        btnEdit.setImage(R.drawable.mf_edit);
        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<VisualTemplate> selectedList= templateAdapter.getSelectTemplateEntities();
                if(selectedList!=null&&selectedList.size()>0)
                    listener.goDesigner(selectedList.get(0));
                if(templateAdapter!=null)
                    templateAdapter.clearImageResource();
            }
        });
        try {
            visualTemplates = XMLTemplate.getInstance().getAllLocalSampleLayouts(context.getAssets(), "template");
        } catch (Exception ex) {
            Log.e(TAG, "TemplateSampleView: ",ex );
            LogOperator.WriteLogfortxt("TemplateSampleView==>loadingTemplate :"+ex.getMessage());
        }

        createBlankTemplateSample();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VisualTemplate visualTemplate=(VisualTemplate) gridView.getItemAtPosition(position);
                listener.goDesigner(visualTemplate);
                if(templateAdapter!=null)
                    templateAdapter.clearImageResource();
            }
        });

        addView(view);
    }

    private void createBlankTemplateSample(){
        VisualTemplate blankTemplate=new VisualTemplate();
        blankTemplate.id="Blank Sample";
        blankTemplate.templateOriginal= ResourceSourceType.internal;
        blankTemplate.thumbImageBitmap= ImageHelper.createTemplateThumb(InternalKeyWords.TemplateDefaultWidth,InternalKeyWords.TemplateDefaultHeight, Color.BLACK);

        if(visualTemplates==null)
            visualTemplates=new ArrayList<VisualTemplate>();

        visualTemplates.add(0,blankTemplate);
    }

    public void reBindingData(){
        //TemplateGridViewAdapter adapter=new TemplateGridViewAdapter(context,visualTemplates);
        if(templateAdapter!=null)
            templateAdapter.clearImageResource();
        templateAdapter=new TemplateInfoAdapter(context, null, visualTemplates, null,null,null,null, FileSelectType.SingleSelect);
        templateAdapter.setOnSelectItemChangedListener(new TemplateInfoAdapter.OnSelectItemChangedListener() {
            @Override
            public void onSelectItemChange(String template, Boolean checked) {
                btnEdit.setEnabled(checked);
            }
        });
        gridView.setAdapter(templateAdapter);
        btnEdit.setEnabled(false);
    }

    CallbackBundle editNewTempCall =  new CallbackBundle() {
        @Override
        public void callback(Bundle bundle) {
            int position=bundle.getInt("position");
            VisualTemplate visualTemplate=(VisualTemplate) gridView.getItemAtPosition(position);
            listener.goDesigner(visualTemplate);
        }
    };

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }
}
