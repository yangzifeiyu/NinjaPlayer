package com.mfusion.templatedesigner.olddesigner.component;

import android.content.Context;
import android.util.AttributeSet;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.tools.ColorConverter;

import org.w3c.dom.Element;

/**
 * Created by 1B15182 on 25/7/2016 0025.
 */
public class TickerTextComponentView extends TickerTextBaseView {
    private static final String TAG = "TickerTextComponentView";


    public TickerTextComponentView(Context context) {
        super(context);
    }

    public TickerTextComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);



    }

    @Override
    public void setComponentEntity(ComponentEntity entity) {
        super.setComponentEntity(entity);

        for(Element current:componentEntity.property){
            String attribute=current.getAttribute("name");


            if (attribute.equals("TextString")){
                text=current.getTextContent();
            }
            else if(attribute.equals("ForeColor")){
                tickerPaint.setColor(ColorConverter.convertARGBStrToColorInt(current.getTextContent()));
            }
        }

        invalidate();

    }


}
