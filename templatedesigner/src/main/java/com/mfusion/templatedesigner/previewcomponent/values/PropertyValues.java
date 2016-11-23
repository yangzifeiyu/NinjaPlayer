package com.mfusion.templatedesigner.previewcomponent.values;

import android.content.res.Resources;
import android.graphics.Color;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mfusion.templatedesigner.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertyValues {

	private static List<String> family_list;
	public static List<String> getFontFamilyList(){
		if(family_list==null){
			family_list=new ArrayList<String>();
			family_list.add("serif");
			family_list.add("sans");
			family_list.add("monospace");
		}
		return family_list;
	}
	
	private static List<String> style_list;
	public static List<String> getFontStyleList(){
		if(style_list==null){
			style_list=new ArrayList<String>();
			style_list.add("normal");
			style_list.add("italic");
			style_list.add("bold");
			style_list.add("italic-bold");
		}
		return style_list;
	}
	
	private static List<String> time_format_list;
	public static List<String> getTimeFormatList(){
		if(time_format_list==null){
			time_format_list=new ArrayList<String>();
			time_format_list.add("yyyy-MM-dd HH:mm:ss");
			time_format_list.add("yyyy-MM-dd");
			time_format_list.add("HH:mm:ss");
		}
		return time_format_list;
	}
	
	private static List<String> speed_list;
	public static List<String> getSpeedList(){
		if(speed_list==null){
			speed_list=new ArrayList<String>();
			speed_list.add(TextSpeedType.Slow.toString());
			speed_list.add(TextSpeedType.Medium.toString());
			speed_list.add(TextSpeedType.Fast.toString());
			speed_list.add(TextSpeedType.VeryFast.toString());
		}
		return speed_list;
	}
	
	private static List<String> playmode_list;
	public static List<String> getPlayModeList(){
		if(playmode_list==null){
			playmode_list=new ArrayList<String>();
			playmode_list.add(ScheduleMediaPlayMode.Sequence.toString());
			playmode_list.add(ScheduleMediaPlayMode.Random.toString());
		}
		return playmode_list;
	}

	static List<Integer> color_list=null;
	public static List<Integer> getColorList() {

		if(color_list==null){
			color_list = new ArrayList<Integer>();

			color_list.add(Color.rgb(255, 255, 255));
			color_list.add(Color.rgb(192, 192, 255));
			color_list.add(Color.rgb(192, 224, 255));
			color_list.add(Color.rgb(192, 255, 255));
			color_list.add(Color.rgb(192, 255, 192));
			color_list.add(Color.rgb(255, 255, 192));
			color_list.add(Color.rgb(255, 192, 192));
			color_list.add(Color.rgb(255, 192, 255));

			color_list.add(Color.rgb(224, 224, 224));
			color_list.add(Color.rgb(128, 128, 255));
			color_list.add(Color.rgb(128, 192, 255));
			color_list.add(Color.rgb(128, 255, 255));
			color_list.add(Color.rgb(128, 255, 128));
			color_list.add(Color.rgb(255, 255, 128));
			color_list.add(Color.rgb(255, 128, 128));
			color_list.add(Color.rgb(255, 128, 255));

			color_list.add(Color.rgb(192, 192, 192));
			color_list.add(Color.rgb(0, 0, 255));
			color_list.add(Color.rgb(0, 128, 255));
			color_list.add(Color.rgb(0, 255, 255));
			color_list.add(Color.rgb(0, 255, 0));
			color_list.add(Color.rgb(255, 255, 0));
			color_list.add(Color.rgb(255, 0, 0));
			color_list.add(Color.rgb(255, 0, 255));

			color_list.add(Color.rgb(128, 128, 128));
			color_list.add(Color.rgb(0, 0, 192));
			color_list.add(Color.rgb(0, 64, 192));
			color_list.add(Color.rgb(0, 192, 192));
			color_list.add(Color.rgb(0, 192, 0));
			color_list.add(Color.rgb(192, 192, 0));
			color_list.add(Color.rgb(192, 0, 0));
			color_list.add(Color.rgb(192, 0, 192));

			color_list.add(Color.rgb(64, 64, 64));
			color_list.add(Color.rgb(0, 0, 128));
			color_list.add(Color.rgb(0, 64, 128));
			color_list.add(Color.rgb(0, 128, 128));
			color_list.add(Color.rgb(0, 128, 0));
			color_list.add(Color.rgb(128, 128, 0));
			color_list.add(Color.rgb(128, 0, 0));
			color_list.add(Color.rgb(128, 0, 128));

			color_list.add(Color.rgb(0, 0, 0));
			color_list.add(Color.rgb(0, 0, 64));
			color_list.add(Color.rgb(64, 64, 128));
			color_list.add(Color.rgb(0, 64, 64));
			color_list.add(Color.rgb(0, 64, 0));
			color_list.add(Color.rgb(64, 64, 0));
			color_list.add(Color.rgb(64, 0, 0));
			color_list.add(Color.rgb(64, 0, 64));
		}

		return color_list;
	}

	public static RelativeLayout.LayoutParams convertToVirtualLayoutWithoutMargin(RelativeLayout.LayoutParams layoutParams){
		layoutParams.width=(int)(layoutParams.width*TemplateDesignerKeys.temp_scale);
		layoutParams.height=(int)(layoutParams.height*TemplateDesignerKeys.temp_scale);
		return layoutParams;
	}

	public static RelativeLayout.LayoutParams convertToVirtualLayout(RelativeLayout.LayoutParams layoutParams){
		layoutParams.width=(int)(layoutParams.width*TemplateDesignerKeys.temp_scale);
		layoutParams.height=(int)(layoutParams.height*TemplateDesignerKeys.temp_scale);
		layoutParams.leftMargin=(int)(layoutParams.leftMargin*TemplateDesignerKeys.temp_scale);
		layoutParams.topMargin=(int)(layoutParams.topMargin*TemplateDesignerKeys.temp_scale);
		return layoutParams;
	}

	public static String convertIntToColorStr(int color) {
		return Color.alpha(color)+","+Color.red(color)+","+Color.green(color)+","+Color.blue(color);
	}

	public static Integer convertColorStrToInt(String colorStr) {
		if(colorStr.isEmpty()==false){
			String[] colors=colorStr.split(",");
			if(colors!=null&&colors.length==4)
				return Color.argb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]), Integer.parseInt(colors[3]));
		}

		return Color.BLACK;
	}

	public static Element createElement(Document doc, String tag, String name, String value) throws IllegalArgumentException, IllegalStateException, IOException {
		Element element=doc.createElement(tag);
		element.setAttribute("name", name);
		if(value!=null)
			element.setTextContent(value);
		return element;
	}
	static List<String> comp_list=null;
	public static List<String> getComponentList(Resources resources) {

		if(comp_list==null){
			comp_list = new ArrayList<String>();

			comp_list.add(resources.getString(R.string.comp_type_datetime));
			comp_list.add(resources.getString(R.string.comp_type_schedulemedia));
			comp_list.add(resources.getString(R.string.comp_type_ticker));
			comp_list.add(resources.getString(R.string.comp_type_rss));
			comp_list.add(resources.getString(R.string.comp_type_web));
			comp_list.add(resources.getString(R.string.comp_type_audio));
			//comp_list.add("WeatherComponent");
		}

		return comp_list;
	}

	static HashMap<String, Integer> comp_image_list=null;
	public static int getImageFotComponent(Resources resources,String type) {
		if(comp_image_list==null){
			comp_image_list = new HashMap<String, Integer>();

			comp_image_list.put(resources.getString(R.string.comp_type_datetime),R.drawable.comp_date);
			comp_image_list.put(resources.getString(R.string.comp_type_ticker),R.drawable.comp_ticker);
			comp_image_list.put(resources.getString(R.string.comp_type_schedulemedia),R.drawable.comp_schedule);
			comp_image_list.put(resources.getString(R.string.comp_type_rss),R.drawable.comp_rss);
			comp_image_list.put(resources.getString(R.string.comp_type_web),R.drawable.comp_interactive);
			comp_image_list.put(resources.getString(R.string.comp_type_audio),R.drawable.comp_audio);
			comp_image_list.put(resources.getString(R.string.comp_type_weather),R.drawable.comp_weather);
		}

		return comp_image_list.get(type);
	}

	public static void bindingColorButton(Button colorBtn,int color){
		if(color==Color.WHITE)
			colorBtn.setBackgroundResource(R.drawable.button_style);
		else
			colorBtn.setBackgroundColor(color);
		colorBtn.setTag(String.valueOf(color));
	}
}
