package com.mfusion.ninjaplayer.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.ResourceSourceType;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.LogOperator;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.ninjaplayer.R;
import com.mfusion.ninjaplayer.adapter.TemplateGridViewAdapter;

import java.util.ArrayList;


public class TemplateSampleView extends LinearLayout {
    private static final String TAG = "TemplateSampleView";
    private Context context;
    private GridView gridView;
    private ImageTextView btnBack;

    private ArrayList<VisualTemplate> visualTemplates;

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
        btnBack= (ImageTextView) view.findViewById(R.id.template_sample_view_btn_back);
        btnBack.setText("Back");
        btnBack.setImage(R.drawable.mf_undo);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goUserCreatedView();
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
            }
        });

        addView(view);
    }

    private void createBlankTemplateSample(){
        VisualTemplate blankTemplate=new VisualTemplate();
        blankTemplate.id="Blank Sample";
        blankTemplate.templateOriginal= ResourceSourceType.internal;
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        blankTemplate.thumbImageBitmap= ImageHelper.createTemplateThumb(dm2.widthPixels,dm2.heightPixels, Color.BLACK);

        if(visualTemplates==null)
            visualTemplates=new ArrayList<VisualTemplate>();

        visualTemplates.add(0,blankTemplate);
    }

    public void reBindingData(){
        TemplateGridViewAdapter adapter=new TemplateGridViewAdapter(context,visualTemplates);
        gridView.setAdapter(adapter);
    }

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }
}
