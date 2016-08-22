package com.mfusion.player.common.XML.Parser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.R.bool;
import android.R.integer;
import android.graphics.Color;

import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Entity.Template;
import com.mfusion.player.common.Entity.Components.AudioComponent;
import com.mfusion.player.common.Entity.Components.BasicComponent;
import com.mfusion.player.common.Entity.Components.DateTimeComponent;
import com.mfusion.player.common.Entity.Components.InteractiveComponent;
import com.mfusion.player.common.Entity.Components.RSSComponent;
import com.mfusion.player.common.Entity.Components.ScheduleMediaComponent;
import com.mfusion.player.common.Entity.Components.TickerTextComponent;
import com.mfusion.player.common.Entity.Components.WeatherComponent;
import com.mfusion.player.common.Enum.ComponentType;
import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Enum.PlayMode;
import com.mfusion.player.common.Enum.TemperatureType;
import com.mfusion.player.common.Helper.ComponentPropertyHelper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.AudioComponentSetting;
import com.mfusion.player.common.Setting.Component.DateTimeSetting;
import com.mfusion.player.common.Setting.Component.InteractiveSetting;
import com.mfusion.player.common.Setting.Component.RSSSetting;
import com.mfusion.player.common.Setting.Component.ScheduleMediaSetting;
import com.mfusion.player.common.Setting.Component.TextPropertySetting;
import com.mfusion.player.common.Setting.Component.TickerTextSetting;
import com.mfusion.player.common.Setting.Component.WeatherSetting;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;
import com.mfusion.player.common.Storage.PBUStorage;

public class AndroidXmlParser extends BaseXmlParser {

	String rootPath=PlayerStoragePath.XMLStorage;

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;

	public AndroidXmlParser(){

		try {

			factory= DocumentBuilderFactory
					.newInstance();
			builder= factory.newDocumentBuilder();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void parseXml() {
		// TODO Auto-generated method stub
		try {
			File rootFolder=new File(rootPath);
			if(!rootFolder.exists()||rootFolder.isFile())
				return ;

			File[] templateFolders=rootFolder.listFiles();
			for (File file : templateFolders) {
				if(file.isDirectory())
					this.parseTemplateXml(file);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void parseTemplateXml(File templateFolder){
		try {
			File templateXmlFile=new File(templateFolder.getAbsolutePath(),templateFolder.getName()+".xml");
			if(!templateXmlFile.exists())
				return;

			Document templateDocument = builder.parse(templateXmlFile);
			if(templateDocument==null){
				return;
			}

			Element rootElement=templateDocument.getDocumentElement();

			String templatePath=templateFolder.getAbsolutePath()+File.separator;

			PBU pbu=new PBU();
			pbu.ID=templateFolder.getName();
			pbu.Template=this.getTemplate(rootElement, templatePath);
			pbu.Components=this.getComponents(rootElement, templatePath);
			pbu.Duration=pbu.OriginalDuration=this.getPbuDuration(pbu.Components);

			//add to PBU List
			PBUStorage.pbumap.put(pbu.ID, pbu);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private Integer getPbuDuration(ArrayList<BasicComponent> compList){
		int duration = 0;
		int max=0;
		for (BasicComponent comp : compList) {
			if (comp.componentType.equals(ComponentType.ScheduleMedia))
			{
				ScheduleMediaComponent schedulemedia = (ScheduleMediaComponent) comp;
				Playlist playlist=schedulemedia.setting.Idleplaylist;
				duration=playlist.Duration;
				max=duration>max?duration:max;//��ȡ����duration
			}
		}

		max=max==0?30:max;
		return max;
	}

	private Template getTemplate(Element rootElement,String templatePath){

		Template template=new Template();
		Element sizeElement=this.getSubNode(rootElement, "Size");
		if(sizeElement!=null){
			template.Width=Integer.valueOf(sizeElement.getAttribute("Width"));
			template.Height=Integer.valueOf(sizeElement.getAttribute("Height"));
		}

		Element bgElement=this.getSubNode(rootElement, "Background");
		if(bgElement!=null){
			template.BackColor=Integer.valueOf(bgElement.getAttribute("Color"));
			String imagePath=bgElement.getAttribute("ImagePath");
			if(imagePath!=null&&!imagePath.isEmpty()){
				template.BackMediaFile=new MediaFile();
				template.BackMediaFile.MediaSource="local";
				template.BackMediaFile.FilePath=templatePath+imagePath;
			}
		}

		return template;
	}

	private ArrayList<BasicComponent> getComponents(Element rootElement,String templatePath){

		ArrayList<Element> compElememts=this.getSubNodeList(rootElement, "Component");
		if(compElememts==null){
			return null;
		}

		ArrayList<BasicComponent> componentlist = new ArrayList<BasicComponent>();
		ComponentType compType=ComponentType.Unkown;
		BasicComponent component=null;
		for (Element element : compElememts) {
			try {
				compType=ComponentType.fromString(element.getAttribute("Type"));
				switch (compType) {
					case DateTime:
						component = new DateTimeComponent(MainActivity.Instance);
						component.Init(this.parseDateTime(element));
						break;

					case TickerText:
						component = new TickerTextComponent(MainActivity.Instance);
						component.Init(this.parseTickerText(element));
						break;
					case ScheduleMedia:
						component = new ScheduleMediaComponent(MainActivity.Instance);
						component.Init(this.parseScheduleMedia(element));
						break;
					case Rss:
						component = new RSSComponent(MainActivity.Instance);
						component.Init(this.parseRssText(element));
						break;
					case AudioComponent:
						component = new AudioComponent(MainActivity.Instance);
						component.Init(this.parseAudioComponent(element));
						break;
					case Interactive:
						component = new InteractiveComponent(MainActivity.Instance);
						component.Init(this.parseInteractive(element));
						break;
					case WeatherComponent:
						component = new WeatherComponent(MainActivity.Instance);
						component.Init(this.parseWeather(element));
						break;
					default:
						component=null;
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				component=null;
			}

			if(component==null)
				continue;

			component.setCmpheight(CommonConvertHelper.StringToInt(element.getAttribute("Height")));
			component.setCmpwidth(CommonConvertHelper.StringToInt(element.getAttribute("Width")));
			component.setCmpleft(CommonConvertHelper.StringToInt(element.getAttribute("Left")));
			component.setCmptop(CommonConvertHelper.StringToInt(element.getAttribute("Top")));
			component.componentType=compType;

			componentlist.add(component);
		}

		return componentlist;
	}

	private DateTimeSetting parseDateTime(Element componentElement){
		DateTimeSetting setting = new DateTimeSetting();
		TextPropertySetting textproperty = new TextPropertySetting();
		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if(property_val==null||property_val.isEmpty())
				continue;

			if (property_name.equalsIgnoreCase("Font")) {
				this.getFontInfo(textproperty,property_val);
			} else if (property_name.equalsIgnoreCase("ForeColor")) {
				textproperty.FontColor = convertColorStrToInt(property_val);
			} else if (property_name.equalsIgnoreCase("TimeFormat")) {
				setting.Format = property_val;
			}
		}

		setting.TextProperty = textproperty;
		setting.BackColor = convertColorStrToInt(componentElement.getAttribute("BackColor"));;

		return setting;
	}

	private TickerTextSetting parseTickerText(Element componentElement){
		TickerTextSetting setting = new TickerTextSetting();
		TextPropertySetting textproperty = new TextPropertySetting();
		String text="";
		int speed=0;

		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if(property_val==null||property_val.isEmpty())
				continue;

			if (property_name.equalsIgnoreCase("Font")) {
				this.getFontInfo(textproperty,property_val);
			} else if (property_name.equalsIgnoreCase("ForeColor")) {
				textproperty.FontColor = convertColorStrToInt(property_val);
			} else if (property_name.equalsIgnoreCase("TextString")) {
				setting.Context = property_val;
			}else if(property_name
					.equalsIgnoreCase("Speed")){
				setting.Speed=CommonConvertHelper.StringToInt(property_val);
			}
		}

		setting.TextProperty = textproperty;
		setting.BackColor = convertColorStrToInt(componentElement.getAttribute("BackColor"));;

		return setting;
	}

	private RSSSetting parseRssText(Element componentElement){
		RSSSetting setting = new RSSSetting();
		TextPropertySetting subtextproperty = new TextPropertySetting();
		TextPropertySetting contexttextproperty = new TextPropertySetting();
		String text="";
		int speed=0;

		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if(property_val==null||property_val.isEmpty())
				continue;

			if (property_name.equalsIgnoreCase("SubjectFont")) {
				this.getFontInfo(subtextproperty,property_val);
			}
			else if (property_name.equalsIgnoreCase("SubjectForeColor")) {
				subtextproperty.FontColor = convertColorStrToInt(property_val);
			}
			else if (property_name.equalsIgnoreCase("BodyFont")) {
				this.getFontInfo(contexttextproperty,property_val);
			}
			else if (property_name.equalsIgnoreCase("SubjectForeColor")) {
				contexttextproperty.FontColor = convertColorStrToInt(property_val);
			}
			else if (property_name.equalsIgnoreCase("Address")) {
				setting.RSSURL = property_val;
			}else if(property_name.equalsIgnoreCase("Speed")){
				setting.Speed=CommonConvertHelper.StringToInt(property_val);
			}
		}

		setting.SubTextProperty=subtextproperty;
		setting.BodyTextProperty=contexttextproperty;
		setting.BackColor = convertColorStrToInt(componentElement.getAttribute("BackColor"));;

		return setting;
	}

	private InteractiveSetting parseInteractive(Element componentElement){
		InteractiveSetting setting = new InteractiveSetting();
		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if (property_name.equalsIgnoreCase("Address")) {
				setting.Web_Url=property_val;
			}
		}

		return setting;
	}

	private ScheduleMediaSetting parseScheduleMedia(Element componentElement){
		ScheduleMediaSetting setting = new ScheduleMediaSetting();
		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		Playlist playlist=new Playlist();
		playlist.Mode=PlayMode.sequence;
		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if (property_name.equalsIgnoreCase("Mute")) {
				setting.Mute=CommonConvertHelper.parseIntToBoolean(property_val);
			}
			else if (property_name.equalsIgnoreCase("PlayMode")) {
				playlist.Mode=PlayMode.fromString(property_val);
			}
			else if (property_name.equalsIgnoreCase("PlayList")) {
				playlist.Duration = this.getMediaList(element, playlist.Medias);
			}
		}

		setting.Idleplaylist=playlist;

		return setting;
	}

	private AudioComponentSetting parseAudioComponent(Element componentElement){
		AudioComponentSetting setting = new AudioComponentSetting();
		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		Playlist playlist=new Playlist();
		playlist.Mode=PlayMode.sequence;
		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if (property_name.equalsIgnoreCase("PlayMode")) {
				playlist.Mode=PlayMode.fromString(property_val);
			}
			else if (property_name.equalsIgnoreCase("PlayList")) {
				this.getMediaList(element, setting.AudioList);
			}
		}

		return setting;
	}

	private WeatherSetting parseWeather(Element componentElement){
		WeatherSetting setting = new WeatherSetting();
		ArrayList<Element> properties = this.getSubNodeList(componentElement, "Property");
		if(properties==null)
			return null;

		String property_name = "";
		String property_val = "";
		for (Element element : properties) {
			property_name = element.getAttribute("name");
			property_val = element.getTextContent();
			if(property_val==null||property_val.isEmpty())
				continue;

			if (property_name.equalsIgnoreCase("LangCode")) {
				setting.Language=property_val;
			}
			else if (property_name.equalsIgnoreCase("WeatherCity"))
			{
				setting.City=property_val;
			}
			else if (property_name.equalsIgnoreCase("LayoutXML"))
			{
				setting.LayoutXMLPath=property_val;
			}
			else if (property_name.equalsIgnoreCase("TemperatureType"))
			{
				setting.Type=TemperatureType.fromString(property_val);
			}
		}

		return setting;
	}

	private void getFontInfo(TextPropertySetting textproperty,String fontString){
		String[] style = fontString.split(",");
		if(style!=null&&style.length>=2)
		{

			textproperty.FontSize =Float.parseFloat(style[1].substring(0, style[1].length()-2));
			if(style[1].substring(style[1].length()-2).equalsIgnoreCase("pt"))
				textproperty.FontSize=textproperty.FontSize*4/3;
			String weight="";
			if(style.length>=3)
				weight=style[2];
			textproperty.FontStyle = ComponentPropertyHelper.GetTypeface(style[0],weight);
		}
	}
	
	private int getMediaList(Element playlistElement,List<MediaFile> medias){
		ArrayList<Element> mediaElements=this.getSubNodeList(playlistElement, "Media");
		if(mediaElements==null)
			return 0;
		
		Integer totalDuration=0;
		MediaFile mediaFile=null;
		for (Element element : mediaElements) {
			mediaFile=new MediaFile();
			mediaFile.FilePath=element.getAttribute("MediaPath");
			mediaFile.Type=FileType.fromString(element.getAttribute("MediaType"));
			mediaFile.MediaSource=element.getAttribute("MediaSource").toLowerCase();
			mediaFile.Duration=DateTimeHelper.ConvertToMin(element.getAttribute("Duration"));
			mediaFile.Effect=element.getAttribute("Effect");
			mediaFile.ExtName=mediaFile.FilePath.substring(mediaFile.FilePath.lastIndexOf("."));
			
			totalDuration+=mediaFile.Duration;
			medias.add(mediaFile);
		}
		
		return totalDuration;
	}
	
 	private ArrayList<Element> getSubNodeList(Node parentNode,String nodeName){
		
		try {
			
			if(parentNode==null)
				return null;

			NodeList subNodes=null;
			if(nodeName==null||nodeName.isEmpty())
				subNodes=parentNode.getChildNodes();
			else 
				subNodes=((Element)parentNode).getElementsByTagName(nodeName);
			
			ArrayList<Element> selectNodes=new ArrayList<Element>();
			for (int index=0;index< subNodes.getLength();index++) {
				Node node=subNodes.item(index);
				if(node.getNodeType()==Node.ELEMENT_NODE)
					selectNodes.add((Element)subNodes.item(index));
			}
			
			return selectNodes;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	private Element getSubNode(Node parentNode,String nodeName){
		
		try {
			
			if(parentNode==null)
				return null;

			NodeList subNodes=null;
			if(nodeName==null||nodeName.isEmpty())
				subNodes=parentNode.getChildNodes();
			else 
				subNodes=((Element)parentNode).getElementsByTagName(nodeName);
			
			if(subNodes!=null&&subNodes.getLength()>0)
				return (Element)subNodes.item(0);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	private Integer convertColorStrToInt(String colorStr) {
		if(colorStr.isEmpty()==false){
			String[] colors=colorStr.split(",");
			if(colors!=null){
				if(colors.length==4)
					return Color.argb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]), Integer.parseInt(colors[3]));
				if(colors.length==1)
					return Integer.valueOf(colorStr);
			}
		}
		
		return Color.BLACK;
	}

}
