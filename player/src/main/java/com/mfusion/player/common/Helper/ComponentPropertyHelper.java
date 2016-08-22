package com.mfusion.player.common.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.TextPropertySetting;

import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.AbsoluteSizeSpan;



public class ComponentPropertyHelper {

	/*
	 * ��ȡ��ɫ
	 */
	public static int GetColor(String color) {
		int colorval =Color.argb(255, 0,0, 0);
		try
		{
			if (color.contains(",")) {
				String[] argb = color.split(",");
				int a = Integer.parseInt(argb[0].trim());
				int r = Integer.parseInt(argb[1].trim());
				int g = Integer.parseInt(argb[2].trim());
				int b = Integer.parseInt(argb[3].trim());
				colorval = Color.argb(a, r, g, b);
			} else if (color.trim().equalsIgnoreCase("Black")) {
				colorval = Color.BLACK;
			} else if (color.trim().equalsIgnoreCase("Blue")) {
				colorval = Color.BLUE;
			} else if (color.trim().equalsIgnoreCase("CYAN")) {
				colorval = Color.CYAN;
			} else if (color.trim().equalsIgnoreCase("DKGRAY")) {
				return Color.DKGRAY;
			}
			else if (color.trim().equalsIgnoreCase("GRAY")) {
				colorval = Color.GRAY;
			} else if (color.trim().equalsIgnoreCase("GREEN")) {
				colorval = Color.GREEN;
			} else if (color.trim().equalsIgnoreCase("LTGRAY")) {
				colorval = Color.LTGRAY;
			} else if (color.trim().equalsIgnoreCase("MAGENTA")) {
				colorval = Color.MAGENTA;
			} else if (color.trim().equalsIgnoreCase("RED")) {
				colorval = Color.RED;
			} else if (color.trim().equalsIgnoreCase("TRANSPARENT")) {
				colorval = Color.TRANSPARENT;
			} else if (color.trim().equalsIgnoreCase("WHITE")) {
				colorval = Color.WHITE;
			} else if (color.trim().equalsIgnoreCase("YELLOW")) {
				colorval = Color.YELLOW;
			}
		}catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ComponentPropertyHelper GetColor==>"+ex.getMessage());
		}
		return colorval;

	}


	/*
	 * ������ʽ
	 */
	public static Typeface GetTypeface(String family,String style) {

		int Style=Typeface.NORMAL;
		Typeface Family=Typeface.DEFAULT;
		try
		{
			if (style.contains("italic") && !style.contains("italic")) {
				Style = Typeface.ITALIC;
			} else if (style.contains("bold") && !style.contains("italic")) {
				Style = Typeface.BOLD;
			} else if (style.contains("bold") && style.contains("italic")) {
				Style =Typeface.BOLD_ITALIC;
			} 

			//Family =Typeface.createFromAsset(MainActivity.Instance.getAssets(),"fonts/Arial.ttf");


			if(family.toLowerCase().contains("sans"))
				Family= Typeface.SANS_SERIF;
			else if(family.toLowerCase().contains("monospace"))
				Family= Typeface.MONOSPACE;
			else if(family.toLowerCase().contains("serif"))
				Family= Typeface.SERIF;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("ComponentPropertyHelper GetTypeface==>"+ex.getMessage());
		}

		return Typeface.create(Family, Style);
	}

	public static TextPropertySetting GetTextFont(String fontString,Integer foreColor){
		TextPropertySetting textproperty=new TextPropertySetting();
		fontString=fontString.replace(", ",  ",");
		String[] style = fontString.toLowerCase().split(",");
		if(style!=null&&style.length>=2)
		{
			textproperty.FontSize = CommonConvertHelper.StringToFloat(style[1].substring(0, style[1].length()-2));
			String weight="";
			if(style.length>=3)
			{
				weight=style[2];

			}
			textproperty.FontStyle = ComponentPropertyHelper
					.GetTypeface(style[0],weight);
		}
		
		textproperty.FontColor=foreColor;
		
		return textproperty;
	}
}
