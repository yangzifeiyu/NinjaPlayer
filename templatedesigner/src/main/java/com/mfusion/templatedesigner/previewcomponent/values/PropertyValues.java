package com.mfusion.templatedesigner.previewcomponent.values;

import android.graphics.Color;
import android.widget.RelativeLayout;

import com.mfusion.templatedesigner.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertyValues {

	static HashMap<String, Integer> image_list=null;
	public static int convertTypeToImage(String type) {
		if(image_list==null){
			image_list = new HashMap<String, Integer>();

			image_list.put("Unkown", R.drawable.filedialog_root);
			image_list.put("/", R.drawable.filedialog_root);
			image_list.put("...", R.drawable.filedialog_folder_up);
			image_list.put(".", R.drawable.filedialog_folder);
			image_list.put("Video", R.drawable.filedialog_wavfile);
			image_list.put("Sound", R.drawable.filedialog_audio);
			image_list.put("Image", R.drawable.filedialog_image);
		}
		if(image_list.containsKey(type))
			return image_list.get(type);

		return image_list.get("Unkown");
	}

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

	static List<String> audio_ext_list=null;
	public static List<String> getAudioExtList() {

		if(audio_ext_list==null){
			audio_ext_list = new ArrayList<String>();

			audio_ext_list.add("mp3");
			audio_ext_list.add("m4a");
			audio_ext_list.add("wav");
			audio_ext_list.add("amr");
			audio_ext_list.add("awb");
			audio_ext_list.add("wma");
			audio_ext_list.add("mid");
			audio_ext_list.add("xmf");
			audio_ext_list.add("rtttl");
			audio_ext_list.add("smf");
			audio_ext_list.add("imy");
			audio_ext_list.add("m3u");
			audio_ext_list.add("pls");
			audio_ext_list.add("ogg");
			audio_ext_list.add("wpl");
		}

		return audio_ext_list;
	}

	static List<String> image_ext_list=null;
	public static List<String> getImageExtList() {

		if(image_ext_list==null){
			image_ext_list = new ArrayList<String>();

			image_ext_list.add("jpg");
			image_ext_list.add("jpeg");
			image_ext_list.add("gif");
			image_ext_list.add("png");
			image_ext_list.add("bmp");
			image_ext_list.add("wbmp");
		}

		return image_ext_list;
	}

	static List<String> video_ext_list=null;
	public static List<String> getVideoExtList() {

		if(video_ext_list==null){
			video_ext_list = new ArrayList<String>();

			video_ext_list.add("mp4");
			video_ext_list.add("m4v");
			video_ext_list.add("3gp");
			video_ext_list.add("3gpp");
			video_ext_list.add("3g2");
			video_ext_list.add("3gpp2");
			video_ext_list.add("wmv");
		}

		return video_ext_list;
	}

	public static String getMeidaType(String mediaPath){
		int splitIndex=mediaPath.lastIndexOf(".");
		if(splitIndex<0){
			return "Unkown";
		}

		String extName=mediaPath.substring(splitIndex+1);
		for (String extString : getAudioExtList()) {
			if(extString.equals(extName))
				return "Sound";
		}
		for (String extString : getVideoExtList()) {
			if(extString.equals(extName))
				return "Video";
		}
		for (String extString : getImageExtList()) {
			if(extString.equals(extName))
				return "Image";
		}

		return "Unkown";
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
	public static List<String> getComponentList() {

		if(comp_list==null){
			comp_list = new ArrayList<String>();

			comp_list.add("DateTime");
			comp_list.add("TickerText");
			comp_list.add("ScheduleMedia");
			comp_list.add("RSSComponent");
			comp_list.add("InteractiveComponent");
			comp_list.add("AudioComponent");
			comp_list.add("WeatherComponent");
		}

		return comp_list;
	}

	static HashMap<String, Integer> comp_image_list=null;
	public static int getImageFotComponent(String type) {
		if(comp_image_list==null){
			comp_image_list = new HashMap<String, Integer>();

			comp_image_list.put("DateTime",R.drawable.comp_date);
			comp_image_list.put("TickerText",R.drawable.comp_ticker);
			comp_image_list.put("ScheduleMedia",R.drawable.comp_schedule);
			comp_image_list.put("RSSComponent",R.drawable.comp_rss);
			comp_image_list.put("InteractiveComponent",R.drawable.comp_interactive);
			comp_image_list.put("AudioComponent",R.drawable.comp_audio);
			comp_image_list.put("WeatherComponent",R.drawable.comp_weather);
		}

		return comp_image_list.get(type);
	}

}
