package com.mfusion.ninjaplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.templatedesigner.DesignerActivity;
import com.mfusion.templatedesigner.TemplateDesigner;

import java.util.ArrayList;

public class TemplateGridView extends GridView {
    public TemplateGridView(Context context) {
        this(context,null);
    }

    public TemplateGridView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TemplateGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setNumColumns(3);
        setOnItemClickListener(new OnTemplateGVClickListener());

    }

    public void setTemplates(ArrayList<VisualTemplate> templates){
        setAdapter(new TemplateGridViewAdapter(getContext(),templates));
        requestLayout();
        invalidate();
    }

    private class OnTemplateGVClickListener implements GridView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            VisualTemplate entity=(VisualTemplate) TemplateGridView.this.getItemAtPosition(position);
//            TemplateEntity selected=XMLTemplate.getInstance().getSampleLayout(entity,getContext().getAssets());
//            Intent goDesigner=new Intent(getContext(), DesignerActivity.class);
//            goDesigner.putExtra("selected",selected);
//            getContext().startActivity(goDesigner);
        }
    }


}
