package com.mfusion.templatedesigner.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.tools.ColorConverter;
import com.mfusion.templatedesigner.RssParser;

import org.w3c.dom.Element;

public class RssComponentView extends TickerTextBaseView {
    private String address;
    public RssComponentView(Context context) {
        super(context);
    }

    public RssComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setComponentEntity(ComponentEntity entity) {
        super.setComponentEntity(entity);
        for(Element current:componentEntity.property){
            String attribute=current.getAttribute("name");


            if (attribute.equals("Address")){
                address=current.getTextContent();
            }
            else if(attribute.equals("BodyForeColor")){
                tickerPaint.setColor(ColorConverter.convertARGBStrToColorInt(current.getTextContent()));
            }
        }

        text="Fetching RSS Content, Please Wait......";
        new RssParser(address, new RssParser.RssParserListener() {
            @Override
            public void onSuccess(String result) {
                text=result;
                invalidate();
            }

            @Override
            public void onFailure() {
                text="RSS feed can't be loaded";
                invalidate();
            }
        }).startFetching();

        invalidate();
    }
}
