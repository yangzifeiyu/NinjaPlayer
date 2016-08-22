package com.mfusion.player.common.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.mfusion.player.library.Controller.WebServiceAccesser;
import com.mfusion.player.library.Helper.LoggerHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.R.integer;
import android.graphics.Color;
import android.net.rtp.RtpStream;

import com.mfusion.player.common.Entity.Weather.DateWeather;
import com.mfusion.player.common.Entity.Weather.LayoutOfDate;
import com.mfusion.player.common.Entity.Weather.LayoutOfTime;
import com.mfusion.player.common.Entity.Weather.LayoutTemplate;
import com.mfusion.player.common.Entity.Weather.PropertyLayout;
import com.mfusion.player.common.Entity.Weather.RealWeather;
import com.mfusion.player.common.Entity.Weather.WeatherData;
import com.mfusion.player.common.Entity.Weather.WeatherPropertyType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

public class WeatherDataHelper {
	
	DocumentBuilderFactory factory = null;
	
	DocumentBuilder builder = null;
	
	WebServiceAccesser webServiceHelper=null;
	
	private static HashMap<String, LayoutTemplate> XMLRecords=new HashMap<String, LayoutTemplate>();
	
	private static HashMap<String,WeatherData> ContentRecords=new HashMap<String, WeatherData>();
	
	public WeatherDataHelper(){
		try {
			factory = DocumentBuilderFactory.newInstance();
			
			builder = factory.newDocumentBuilder();
			
			webServiceHelper=new WebServiceAccesser();
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("WeatherDataHelper==>"+e.getMessage());
		}
	}
	
	public LayoutTemplate getLastLayoutTemplate(String xmlPath){
		if(XMLRecords.containsKey(xmlPath))
			return XMLRecords.get(xmlPath);
		return null;
	}
	
	public WeatherData getLastWeatherData(String city,String language,int dates){
		String contentkey=this.GetContentKeyWord(city, language, dates);
		if(ContentRecords.containsKey(contentkey))
			return ContentRecords.get(contentkey);
		return null;
	}
	
	public LayoutTemplate DownloadWeatherTemplate(String xmlPath) {
		try {
			Document document = builder.parse(xmlPath);
			
			Element root = document.getDocumentElement();
			
			LayoutTemplate template=new LayoutTemplate();
			template.DateNum=Integer.parseInt( this.GetNodeValueByTag(root,"DateNum"));
			template.Width=Integer.parseInt( this.GetNodeValueByTag(root,"Width"));
			template.Height=Integer.parseInt(this.GetNodeValueByTag(root,"Height"));
			template.BackImage=this.GetNodeValueByTag(root,"BackImage");
			template.BackColor=this.ConvertToColor(root.getElementsByTagName("BackImage").item(0).getChildNodes());
			Integer fontColor=this.ConvertToColor(root.getElementsByTagName("FontColor").item(0).getChildNodes());
			template.Font=ComponentPropertyHelper.GetTextFont(this.GetNodeValueByTag(root,"Font"),fontColor);
			
			NodeList currentList=root.getElementsByTagName("CurrentWeather");
			if(currentList!=null&&currentList.getLength()>0){
				Element currentWeatherNode=(Element)currentList.item(0);
				LayoutOfTime currentWeather=new LayoutOfTime();
				currentWeather.Width=Integer.parseInt(this.GetNodeValueByTag(currentWeatherNode,"Width"));
				currentWeather.Height=Integer.parseInt(this.GetNodeValueByTag(currentWeatherNode,"Height"));
				currentWeather.Left=Integer.parseInt(this.GetNodeValueByTag(currentWeatherNode,"Left"));
				currentWeather.Top=Integer.parseInt(this.GetNodeValueByTag(currentWeatherNode,"Top"));
				currentWeather.SubItem=getSubLayouts(currentWeatherNode);
				template.CurrentWeather=currentWeather;
			}
			
			template.DatesWeather=new ArrayList<LayoutOfDate>();
			NodeList DatesWeather=root.getElementsByTagName("DatesWeather");
			if(DatesWeather!=null&&DatesWeather.getLength()>0){
				NodeList weather_dateList=((Element)(DatesWeather.item(0))).getElementsByTagName("LayoutOfDate");
				LayoutOfDate weather_dateLayout=null;
				Element weather=null;
				for (int i = 0; i < weather_dateList.getLength(); i++) {
					weather=(Element)weather_dateList.item(i);
					weather_dateLayout=new LayoutOfDate();
					weather_dateLayout.Width=Integer.parseInt(this.GetNodeValueByTag(weather,"Width"));
					weather_dateLayout.Height=Integer.parseInt(this.GetNodeValueByTag(weather,"Height"));
					weather_dateLayout.Left=Integer.parseInt(this.GetNodeValueByTag(weather,"Left"));
					weather_dateLayout.Top=Integer.parseInt(this.GetNodeValueByTag(weather,"Top"));
					weather_dateLayout.NumOfDates=Integer.parseInt(this.GetNodeValueByTag(weather,"NumOfDates"));
					weather_dateLayout.StartDate=Integer.parseInt(this.GetNodeValueByTag(weather,"StartDate"));
					
					weather_dateLayout.DateDescribe=new ArrayList<String>();
					NodeList dateDescrideList=weather.getElementsByTagName("DateDescribe");
					if(dateDescrideList!=null&&dateDescrideList.getLength()>0){
						NodeList describeNodes=((Element)dateDescrideList.item(0)).getElementsByTagName("string");
						for (int sub = 0; sub < describeNodes.getLength(); sub++) {
							weather_dateLayout.DateDescribe.add(describeNodes.item(sub).getTextContent());
						}
					}
					
					weather_dateLayout.SubItem=getSubLayouts(weather);
					template.DatesWeather.add(weather_dateLayout);
				}
			}
			
			XMLRecords.put(xmlPath, template);
			
			return template;
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("WeatherDataHelper DownloadWeatherTemplate==>"+e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	private List<PropertyLayout> getSubLayouts(Element parent){
		List<PropertyLayout> SubItem=new ArrayList<PropertyLayout>();
		NodeList currentWeatherPropertysNode=((Element)(parent.getElementsByTagName("SubItem").item(0))).getElementsByTagName("PropertyLayout");
		PropertyLayout property=null;
		Element propertyNode=null;
		Integer fontColor=0;
		for (int i = 0; i < currentWeatherPropertysNode.getLength(); i++) {
			propertyNode=(Element)currentWeatherPropertysNode.item(i);
			property=new PropertyLayout();
			property.Name=this.GetNodeValueByTag(propertyNode,"Name");
			property.Type=WeatherPropertyType.fromString(this.GetNodeValueByTag(propertyNode,"Type"));
			property.Width=Integer.parseInt(this.GetNodeValueByTag(propertyNode,"Width"));
			property.Height=Integer.parseInt(this.GetNodeValueByTag(propertyNode,"Height"));
			property.Left=Integer.parseInt(this.GetNodeValueByTag(propertyNode,"Left"));
			property.Top=Integer.parseInt(this.GetNodeValueByTag(propertyNode,"Top"));
			if(property.Type==WeatherPropertyType.Text){
				fontColor=this.ConvertToColor(propertyNode.getElementsByTagName("FontColor").item(0).getChildNodes());
				property.Font=ComponentPropertyHelper.GetTextFont(this.GetNodeValueByTag(propertyNode,"Font"),fontColor);
				property.Formate=this.GetJavaStringFormat(this.GetNodeValueByTag(propertyNode,"Formate"));
			}
			SubItem.add(property);
		}
		return SubItem;
	}
	
	private String GetJavaStringFormat(String donetFormat){
		try {
			while (donetFormat.contains("{")) {
				Integer startInteger=donetFormat.indexOf("{");
				Integer endInteger=donetFormat.indexOf("}",startInteger);
				if(endInteger>=0)
					donetFormat=donetFormat.replace(donetFormat.substring(startInteger, endInteger+1), "%s");
			}
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("WeatherDataHelper GetJavaStringFormat==>"+e.getMessage());
			e.printStackTrace();
		}
		return donetFormat;
	}
	
	private String GetNodeValueByTag(Element root,String tagName){
		return root.getElementsByTagName(tagName).item(0).getTextContent();
	}
	
	public WeatherData DownloadWeatherContent(String city,String language,int dates,HashSet<String> imageList) {
		
		JSONObject contentJsonObject=this.webServiceHelper.GetWeatherDataFromService(city,language,dates);
		
		WeatherData content=new WeatherData();
		
		if(contentJsonObject==null)
			content.ErrorMessage="Try to reload weather data";
		else {
			try {
				content.ErrorMessage=contentJsonObject.getString("ErrorMessage");
				
				if(content.ErrorMessage!=null&&content.ErrorMessage.equals("")==false)
					return content;
				
				content.City=city;
				
				content.ImageRootPath=contentJsonObject.getString("ImageRootPath");
				
				content.IconSizes=new ArrayList<String>();
				JSONArray iconSizes = contentJsonObject.getJSONArray("IconSizes");
				for (int i = 0; i < iconSizes.length(); i++) 
					content.IconSizes.add(iconSizes.getString(i));

				RealWeather currnetWeather=new RealWeather();
				JSONObject currentWeatherJson=contentJsonObject.getJSONObject("current_condition");
				currnetWeather.temp_C=currentWeatherJson.getString("temp_C");
				currnetWeather.temp_F=currentWeatherJson.getString("temp_F");
				currnetWeather.PSI=currentWeatherJson.getString("PSI");
				currnetWeather.minPSI=currentWeatherJson.getString("minPSI");
				currnetWeather.maxPSI=currentWeatherJson.getString("maxPSI");
				currnetWeather.humidity=currentWeatherJson.getString("humidity");
				currnetWeather.weatherCode=currentWeatherJson.getString("weatherCode");
				currnetWeather.weatherDesc=currentWeatherJson.getString("weatherDesc");
				currnetWeather.weatherIconUrl=currentWeatherJson.getString("weatherIconUrl");
				if(imageList.contains(currnetWeather.weatherIconUrl)==false)
					imageList.add(currnetWeather.weatherIconUrl);
				currnetWeather.winddir16Point=currentWeatherJson.getString("winddir16Point");
				currnetWeather.windspeedKmph=currentWeatherJson.getString("windspeedKmph");

				content.current_condition=currnetWeather;
				
				JSONArray datesWeatherJson=contentJsonObject.getJSONArray("weather");
				if(datesWeatherJson!=null){
					content.weather=new ArrayList<DateWeather>();
					DateWeather dateWeather=null;
					JSONObject dateWeatherJson=null;
					for (int i = 0; i < datesWeatherJson.length(); i++) {
						dateWeather=new DateWeather();
						dateWeatherJson = datesWeatherJson.getJSONObject(i);
						dateWeather.minPSI=dateWeatherJson.getString("minPSI");
						dateWeather.maxPSI=dateWeatherJson.getString("maxPSI");
						dateWeather.maxtempC=dateWeatherJson.getString("maxtempC");
						dateWeather.mintempC=dateWeatherJson.getString("mintempC");
						dateWeather.maxtempF=dateWeatherJson.getString("maxtempF");
						dateWeather.mintempF=dateWeatherJson.getString("mintempF");
						dateWeather.humidity=dateWeatherJson.getString("humidity");
						dateWeather.weatherCode=dateWeatherJson.getString("weatherCode");
						dateWeather.weatherDesc=dateWeatherJson.getString("weatherDesc");
						dateWeather.weatherIconUrl=dateWeatherJson.getString("weatherIconUrl");
						if(imageList.contains(dateWeather.weatherIconUrl)==false)
							imageList.add(dateWeather.weatherIconUrl);
						dateWeather.winddir16Point=dateWeatherJson.getString("winddir16Point");
						dateWeather.windspeedKmph=dateWeatherJson.getString("windspeedKmph");
						content.weather.add(dateWeather);
					}
				}

				imageList.add("wsymbol_0999_unknown.png");
				
				ContentRecords.put(this.GetContentKeyWord(city, language, dates), content);

			} catch (Exception e) {
				// TODO: handle exception
				LoggerHelper.WriteLogfortxt("WeatherDataHelper DownloadWeatherContent==>"+e.getMessage());
				content.ErrorMessage="Decode Failed";
			}
		}
		return content;
	}
	
	public void DownloadWeatherBackImage(String xmlUrl, String imageName) {
		new File(PlayerStoragePath.WeatherIconStorage).mkdirs();
		
		this.DownloadFile(PlayerStoragePath.WeatherIconStorage+imageName, xmlUrl.substring(0,xmlUrl.lastIndexOf("/")+1)+imageName);
	}
	
	public Boolean DownloadWeatherIcons(String imagePath,List<String> IconSizes,String rootUrl) {
		try {  
			
			if(IconSizes==null)
				return true;

			new File(PlayerStoragePath.WeatherIconStorage).mkdirs();
			
			String imageName=imagePath.substring(0,imagePath.lastIndexOf("."));
			String imageExt=imagePath.substring(imagePath.lastIndexOf("."));
			for (String size : IconSizes) 	
				this.DownloadFile(PlayerStoragePath.WeatherIconStorage+imageName+size+imageExt, rootUrl+size+"/"+imagePath);
            
			return true;
        } catch(Exception e){  
            e.printStackTrace();  
            LoggerHelper.WriteLogfortxt("WeatherDataHelper DownloadWeatherIcons==>"+e.getMessage());
            return false;
        }  
	}
	
	private void DownloadFile(String destPath,String fileUrl){
		try {
			File imageFile = new File(destPath);
	        if(imageFile.exists())
	        	return;
	        
	        URL url = new URL(fileUrl);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
	        conn.connect();  
	        int length = conn.getContentLength();  
	        InputStream is = conn.getInputStream();
	        
	        Boolean canceled=false;
	        FileOutputStream fos = new FileOutputStream(imageFile);  
	        byte buf[] = new byte[512];       
	        do{  
	            int numread = is.read(buf);  
	            if(numread <= 0){      
	                break;  
	            }  
	            fos.write(buf,0,numread);  
	        }while(!canceled);  

	        fos.close();  
	        fos=null;
	        is.close(); 
	        is=null;
	        conn.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("Download Weather Icon == > "+fileUrl+ " "+e.getMessage());
		}
	}
	
	private Integer ConvertToColor(NodeList colorList){
		Integer A=255,R=255,G=255,B=255;
		String tagName="";
		Node subColor;
		for(Integer i=0;i<colorList.getLength();i++){
			subColor=colorList.item(i);
			tagName=subColor.getNodeName();
			if(tagName.equalsIgnoreCase("a"))
				A=Integer.parseInt(subColor.getTextContent());
			else if(tagName.equalsIgnoreCase("r"))
				R=Integer.parseInt(subColor.getTextContent());
			else if(tagName.equalsIgnoreCase("g"))
				G=Integer.parseInt(subColor.getTextContent());
			else if(tagName.equalsIgnoreCase("b"))
				B=Integer.parseInt(subColor.getTextContent());
		}
		
		return Color.argb(A, R, G, B);
	}
	
	private String GetContentKeyWord(String city, String lang, int dates)
    {
        return city + "_" + lang + "_" + dates;
    }
}
