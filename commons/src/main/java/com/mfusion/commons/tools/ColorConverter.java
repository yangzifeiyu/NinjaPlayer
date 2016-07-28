package com.mfusion.commons.tools;

import android.graphics.Color;


public class ColorConverter {
    public static int convertARGBStrToColorInt(String ARGBStr){
        String[] ary=ARGBStr.split(",");
        return Color.argb(Integer.valueOf(ary[0]),Integer.valueOf(ary[1]),Integer.valueOf(ary[2]),Integer.valueOf(ary[3]));
    }
    public static String convertIntColorToARGBStr(int color){
        int A=Color.alpha(color);
        int R=Color.red(color);
        int G=Color.green(color);
        int B=Color.green(color);
        return A+","+R+","+G+","+B;
    }
}
