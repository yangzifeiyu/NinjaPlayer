package com.mfusion.templatedesigner;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.ComponentType;

import java.util.ArrayList;


public class DefaultComponent {
    private static final String TAG = "DefaultComponent";
    private Context context;
    private TemplateEntity entity0;
    private TemplateEntity entity1;

    public DefaultComponent(Context context) {
        this.context = context;
        try {
            ArrayList<VisualTemplate> visualTemplates= XMLTemplate.getInstance().getAllLocalSampleLayouts(context.getAssets(),"template");
            entity0 = XMLTemplate.getInstance().getSampleLayout(visualTemplates.get(0),context.getAssets());
            entity1=XMLTemplate.getInstance().getSampleLayout(visualTemplates.get(1),context.getAssets());
        } catch (Exception e) {
            Log.e(TAG, "getDefaultComponent: ",e );
        }
    }

    public ComponentEntity getDefaultComponent(ComponentType type){

        ComponentEntity dateTimeSample= entity0.compList.get(0);
        ComponentEntity mediaSample= entity0.compList.get(2);
        ComponentEntity tickerTextSample= entity0.compList.get(1);
        ComponentEntity rssSample=entity1.compList.get(1);


        switch (type){
            case DateTime:
                dateTimeSample.backColor=Color.GRAY;
                return dateTimeSample;
            case ScheduleMedia:
                mediaSample.width=500;
                mediaSample.height=500;
                mediaSample.backColor=Color.YELLOW;
                return mediaSample;
            case TickerText:
                tickerTextSample.backColor=Color.BLUE;
                return tickerTextSample;
            case RSSComponent:
                rssSample.backColor=Color.RED;
                return rssSample;

        }
        return null;
    }




}
