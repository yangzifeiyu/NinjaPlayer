package com.mfusion.templatedesigner.previewcomponent.values;

import android.R.integer;
import android.graphics.Color;
import android.graphics.Typeface;

import java.text.DecimalFormat;

public class ComponentFont {
	public Typeface family=Typeface.DEFAULT;
	public int style=Typeface.NORMAL;
	public float size=12;
	public int color=Color.GRAY;
	
	public void setFont(String fontStr,int color){
		this.color=color;

		if(fontStr==null||fontStr.isEmpty())
			return;
		
		String[] properties=fontStr.toLowerCase().split(",");
		if(properties!=null&&properties.length>=2){
			
			setFamilyString(properties[0]);

			setStyleString(properties[2]);

			this.size=Float.parseFloat(properties[1].substring(0,properties[1].length()-2));
			if(properties[1].substring(properties[1].length()-2).equalsIgnoreCase("pt"))
				this.size=this.size*4/3;

			this.size=Float.valueOf((new DecimalFormat("#0.00")).format(this.size));
		}
	}
	
	public void setFamilyString(String familyStr){
		if(familyStr.contains("sans"))
			family=Typeface.SANS_SERIF;
		else if(familyStr.contains("monospace"))
			family=Typeface.MONOSPACE;
		else
			family=Typeface.SERIF;
	}
	
	public void setStyleString(String styleStr){
		if (styleStr.contains("italic") && !styleStr.contains("bold")) {
			this.style=Typeface.ITALIC;
		} else if (styleStr.contains("bold") && !styleStr.contains("italic")) {
			this.style=Typeface.BOLD;
		} else if (styleStr.contains("bold") && styleStr.contains("italic")) {
			this.style=Typeface.BOLD_ITALIC;
		} else {
			this.style=Typeface.NORMAL;
		}
	}
	
	public String getFamilyString(){
		String familyString="sans";
		if(family==Typeface.SANS_SERIF)
			familyString= "sans";
		else if(family==Typeface.MONOSPACE)
			familyString= "monospace";
		else if(family==Typeface.SERIF)
			familyString= "serif";
		return familyString;
	}
	
	public String getStyleString(){
		String styleString="normal";
		if(style==Typeface.ITALIC)
			styleString= "italic";
		else if(style==Typeface.BOLD)
			styleString= "bold";
		else if(style==Typeface.BOLD_ITALIC)
			styleString= "bold_italic";
		return styleString;
	}
	
	@Override
	public String toString(){
		return getFamilyString()+","+(size)+"dp,style="+getStyleString();
	}
}
