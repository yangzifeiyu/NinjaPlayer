package com.mfusion.ninjaplayer.view;

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
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.ninjaplayer.R;

import java.util.ArrayList;


public class TemplateSampleView extends LinearLayout {
    private static final String TAG = "TemplateSampleView";
    private Context context;
    private GridView gridView;
    private Button btnBack;

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
        btnBack= (Button) view.findViewById(R.id.template_sample_view_btn_back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.goUserCreatedView();
            }
        });

        try {
            ArrayList<VisualTemplate> visualTemplates = XMLTemplate.getInstance().getAllLocalSampleLayouts(context.getAssets(), "template");
            TemplateGridViewAdapter adapter=new TemplateGridViewAdapter(context,visualTemplates);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    VisualTemplate visualTemplate=(VisualTemplate) gridView.getItemAtPosition(position);
                    listener.goDesigner(visualTemplate);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "TemplateSampleView: ",e );
        }


        addView(view);
    }

    public void setListener(TemplateFragmentListener listener) {
        this.listener = listener;
    }
}
