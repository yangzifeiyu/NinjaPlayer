package com.mfusion.scheduledesigner.values;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ThinkPad on 2016/7/29.
 */
public class ScreenAdjustHelper {

    public static float TemplateLayoutScale=0.115f;

    public static float BlockPropertyLayoutScale=0.125f;

    public static float getScaleValusByHeight(int originalHeight,int currentHeight,ViewGroup parentView){
        try{

            float scale=currentHeight*1.0f/originalHeight;
            ViewGroup.LayoutParams layoutParams=null;
            View scaleView=null;
            for(int i=0;i<parentView.getChildCount();i++){
                scaleView=parentView.getChildAt(i);
                layoutParams=scaleView.getLayoutParams();
                if(ViewGroup.LayoutParams.MATCH_PARENT!=layoutParams.width&&ViewGroup.LayoutParams.FILL_PARENT!=layoutParams.width&&ViewGroup.LayoutParams.WRAP_CONTENT!=layoutParams.width)
                    layoutParams.width=(int)(layoutParams.width*scale);
                if(ViewGroup.LayoutParams.MATCH_PARENT!=layoutParams.height&&ViewGroup.LayoutParams.FILL_PARENT!=layoutParams.height&&ViewGroup.LayoutParams.WRAP_CONTENT!=layoutParams.height)
                    layoutParams.height=(int)(layoutParams.height*scale);
                if(layoutParams.getClass()==RelativeLayout.LayoutParams.class){
                    RelativeLayout.LayoutParams relativeParames=(RelativeLayout.LayoutParams)layoutParams;
                    if(relativeParames.leftMargin!=0)
                        relativeParames.leftMargin=(int)(relativeParames.leftMargin*scale);
                    if(relativeParames.topMargin!=0)
                        relativeParames.topMargin=(int)(relativeParames.topMargin*scale);
                    if(relativeParames.rightMargin!=0)
                        relativeParames.rightMargin=(int)(relativeParames.rightMargin*scale);
                    if(relativeParames.bottomMargin!=0)
                        relativeParames.bottomMargin=(int)(relativeParames.bottomMargin*scale);
                }
                scaleView.setLayoutParams(layoutParams);

                if(ViewGroup.class.isAssignableFrom(scaleView.getClass())){
                    getScaleValusByHeight(originalHeight,currentHeight,(ViewGroup) scaleView);
                }
                if(scaleView.getClass()== TextView.class){
                    ((TextView)(scaleView)).setTextSize(((TextView)(scaleView)).getTextSize()*scale);
                }
                if(scaleView.getClass()== Button.class){
                    ((Button)(scaleView)).setTextSize(((Button)(scaleView)).getTextSize()*scale);
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return 1;
    }
}
