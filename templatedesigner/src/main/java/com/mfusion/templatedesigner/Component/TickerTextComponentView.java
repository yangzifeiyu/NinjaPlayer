package com.mfusion.templatedesigner.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.tools.ColorConverter;

import org.w3c.dom.Element;


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
