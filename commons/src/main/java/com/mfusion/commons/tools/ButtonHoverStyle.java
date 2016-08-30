package com.mfusion.commons.tools;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.mfusion.commontools.R;


/**
 * Created by ThinkPad on 2016/7/25.
 */
public class ButtonHoverStyle {
    public static void bindingHoverEffect(View view, final Resources resources){
        view.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                int what = event.getAction();
                switch(what){
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.setBackgroundColor(resources.getColor(R.color.selected));
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        break;
                }
                return false;
            }
        });
    }

    public static void bindingHoverEffectWithBorder(View view, Resources resources){
        bindingHoverEffectWithBorder(view,false,resources);
    }

    public static void bindingHoverEffectWithBorder(View view, final Boolean isSelected, final Resources resources){
        if(isSelected)
            view.setBackground(resources.getDrawable(R.drawable.button_select_style));
        else
            view.setBackground(resources.getDrawable(R.drawable.button_style));

        view.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                int what = event.getAction();
                switch(what){
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.setBackground(resources.getDrawable(R.drawable.button_hover_style));
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        if(isSelected)
                            v.setBackground(resources.getDrawable(R.drawable.button_select_style));
                        else
                            v.setBackground(resources.getDrawable(R.drawable.button_style));
                        break;
                }
                return false;
            }
        });
    }
}
