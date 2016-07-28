package com.mfusion.templatedesigner.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.tools.ColorConverter;
import com.mfusion.templatedesigner.component.BaseComponentView;

import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jimmy on 7/12/2016.
 */
public class DateTimeComponentView extends BaseComponentView {

    private String text;
    private Paint textPaint;

    public DateTimeComponentView(Context context) {
        super(context);

    }

    public DateTimeComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public void setDateTime(Date date){
//        textLayout=new LinearLayout(getContext());
//        dateTimeTextView=new TextView(getContext());
//        textLayout.addView(dateTimeTextView);
//        dateTimeTextView.setText(date.toString());
//
//    }

    @Override
    public void setComponentEntity(ComponentEntity entity) {
        super.setComponentEntity(entity);
        textPaint=new Paint();
        textPaint.setTextSize(50f);



        for(Element current:componentEntity.property){
            String attribute=current.getAttribute("name");
            if(attribute.equals("ForeColor")){

                String color=current.getTextContent();
                textPaint.setColor(ColorConverter.convertARGBStrToColorInt(color));
            }
            else if (attribute.equals("TimeFormat")){
                SimpleDateFormat format=new SimpleDateFormat(current.getTextContent());
                text=format.format(new Date());
            }
        }




        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(text,getWidth()/2-textPaint.measureText(text)/2,(int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),textPaint);
    }

}
