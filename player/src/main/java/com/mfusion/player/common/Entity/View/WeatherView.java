package com.mfusion.player.common.Entity.View;

import java.util.ArrayList;
import java.util.List;

import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.ImageHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Weather.LayoutOfDate;
import com.mfusion.player.common.Entity.Weather.LayoutTemplate;
import com.mfusion.player.common.Entity.Weather.PropertyLayout;
import com.mfusion.player.common.Entity.Weather.WeatherData;
import com.mfusion.player.common.Entity.Weather.WeatherPropertyType;
import com.mfusion.player.common.Enum.TemperatureType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.TextPropertySetting;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class WeatherView extends View {

	private Paint mPaint;  
    
	TextPaint textPaint = null;
	
    private Rect mBounds; 
    
    private TemperatureType m_temperatureType=TemperatureType.Celsius;

	private WeatherData m_content;
	
	private LayoutTemplate m_template;
	
	private double m_scale_w;
	
	private double m_scale_H;
	
	private TextPropertySetting m_default_textproperty;
	
	private List<Bitmap> m_used_images=new ArrayList<Bitmap>();
	
	public WeatherView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		mPaint.setAntiAlias(true);   
		
		textPaint = new TextPaint();  
        textPaint.setAntiAlias(true);  
        
        mBounds = new Rect();  
        
        m_default_textproperty=new TextPropertySetting();
        m_default_textproperty.FontColor=Color.BLACK;
        m_default_textproperty.FontSize=18;
        m_default_textproperty.FontStyle=Typeface.DEFAULT;
	}

	public WeatherView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void DisplayWeather(WeatherData content,LayoutTemplate template){
		this.m_content=content;
		this.m_template=template;
		this.postInvalidate();
	}
	
	public void SetWeatherTemplateType(TemperatureType Type){
		this.m_temperatureType=Type;
	}
	
	public void Stop(){
		
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);   

        if(this.m_template==null){
        	this.DrawWarningText(canvas, "Loading XML for Weather");
        	return;
        }else if(this.m_content==null){
        	this.DrawWarningText(canvas, "Loading data for Weather");
        	return;
        }
        
        //canvas.drawColor(Color.TRANSPARENT); 
        
        try {
        	if(m_used_images.size()>0){
        		for (Bitmap image:m_used_images) {
    				image.recycle();
    			}
        	}
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        try {
            m_scale_w=getWidth()*1.0/this.m_template.Width;
            m_scale_H=getHeight()*1.0/this.m_template.Height;
            double currentScale=m_scale_w;
            if(currentScale>m_scale_H)
            	currentScale=m_scale_H;
            
            if(this.m_template.BackImage.equalsIgnoreCase("")==false){
            	this.DrawImage(canvas,m_template.BackImage, 0, 0, getWidth(), getHeight());
            }else{
            	this.DrawBackColor(canvas,m_template.BackColor);
            }
            
            if(this.m_template.CurrentWeather!=null&&this.m_template.CurrentWeather.SubItem!=null){
            	for (PropertyLayout layout : this.m_template.CurrentWeather.SubItem) {
    				if(layout.Type==WeatherPropertyType.Image){
    					String imageUrlString="";
    					int imageWidth = (int)(layout.Width * currentScale);
    					
                        if (this.m_content.current_condition!=null){
                        	imageUrlString=this.m_content.current_condition.weatherIconUrl;
                        }
                        
                        String imagePath = this.GetWeatherIconPath(imageUrlString, imageWidth);
                        
                        this.DrawImage(canvas, imagePath, (int)((layout.Left + this.m_template.CurrentWeather.Left) * currentScale), (int)((layout.Top + this.m_template.CurrentWeather.Top) * currentScale), imageWidth, (int)(layout.Height * currentScale));
                        
    				}else {
    					String propertyValue=this.GetCurrentWeatherContent(layout.Name, layout.Formate);
    					this.DrawText(canvas, propertyValue, layout.Font, (int)((layout.Left + this.m_template.CurrentWeather.Left) * currentScale), (int)((layout.Top + this.m_template.CurrentWeather.Top) * currentScale), (int)(layout.Width * currentScale), (int)(layout.Height * currentScale),currentScale);
    				}
    			}
            }
            
            if (this.m_template.DatesWeather != null && this.m_template.DatesWeather.size() > 0)
            {
            	LayoutOfDate layoutofDate = null;
                for (int i = 0; i < m_template.DatesWeather.size();i++ )
                {
                	layoutofDate = m_template.DatesWeather.get(i);
                    for (int date = layoutofDate.StartDate; date < layoutofDate.NumOfDates; date++)
                    {
                    	for (PropertyLayout layout : layoutofDate.SubItem)
                        {
                            if (layout.Type == WeatherPropertyType.Image)
                            {
                            	String imageUrlString="";
            					int imageWidth = (int)(layout.Width * currentScale);
            					
                                if (this.m_content.weather!=null&&this.m_content.weather.size()>date&&this.m_content.weather.get(date)!=null){
                                	imageUrlString=this.m_content.weather.get(date).weatherIconUrl;
                                }
                                
                                String imagePath = this.GetWeatherIconPath(imageUrlString, imageWidth);
                                
                                this.DrawImage(canvas, imagePath, (int)((layout.Left + layoutofDate.Left) * currentScale), (int)((layout.Top + layoutofDate.Top) * currentScale), imageWidth, (int)(layout.Height * currentScale));
                            }
                            else{
                            	String propertyValue=this.GetDateWeatherContent(layoutofDate,date, layout.Name, layout.Formate);
            					this.DrawText(canvas, propertyValue, layout.Font, (int)((layout.Left + layoutofDate.Left) * currentScale), (int)((layout.Top + layoutofDate.Top) * currentScale), (int)(layout.Width * currentScale), (int)(layout.Height * currentScale),currentScale);
                            }
                        }
                    }
                }
            }
		} catch (Exception e) {
			LoggerHelper.WriteLogfortxt("WeatherView==>"+e.getMessage());
			// TODO: handle exception
		}
    }  
	
	private String GetWeatherIconPath(String imageUrl,Integer imageWidth){
		try {
			String imagePath = "";
			String imageName = "wsymbol_0999_unknown";
			String imageExt=".png";
	        if (imageUrl.isEmpty()==false){
	        	imageName = imageUrl.substring(0, imageUrl.lastIndexOf("."));
	            imageExt = imageUrl.substring(imageUrl.lastIndexOf("."));
	        }
	        String imageSize=this.m_content.IconSizes.get(this.m_content.IconSizes.size()-1);
	        for (String iconSize : this.m_content.IconSizes) {
	        	if (imageWidth <= Integer.parseInt(iconSize))
	            {
	        		imageSize = iconSize;
	                break;
	            }
			}
	        imagePath = imageName+imageSize + imageExt;
	        return imagePath;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return imageUrl;
	}
	
	private void DrawWarningText(Canvas canvas,String text){
		try {
			canvas.drawColor(Color.WHITE); 
			textPaint.setColor(m_default_textproperty.FontColor);  
			textPaint.setTextSize(m_default_textproperty.FontSize);  
			textPaint.setTypeface(m_default_textproperty.FontStyle);
			textPaint.getTextBounds(text, 0, text.length()-1, mBounds);
			
			Integer top =(getHeight()-(mBounds.bottom-mBounds.top))/2;
			Integer left=(getWidth()-(mBounds.right-mBounds.left))/2;
			this.DrawText(canvas, text, null, left, top, getWidth(), getHeight(),1);
		} catch (Exception e) {
			// TODO: handle exception
			LoggerHelper.WriteLogfortxt("WeatherView DrawWarningText==>"+e.getMessage());
		}
		
	}
	private void DrawText(Canvas canvas,String text,TextPropertySetting font,Integer left,Integer top,Integer outWidth,Integer outHeight,double scale){
		if(font!=null){
			textPaint.setColor(font.FontColor);  
			textPaint.setTextSize((float)(font.FontSize*scale));  
			textPaint.setTypeface(font.FontStyle);
		}

		StaticLayout staticLayout = new StaticLayout(text, textPaint, outWidth, Alignment.ALIGN_NORMAL, 1, 0, false); 

		if(staticLayout.getLineCount()>1){
			if(staticLayout.getLineCount()>2){
				textPaint.setTextSize(font.FontSize*2/staticLayout.getLineCount());
				staticLayout = new StaticLayout(text, textPaint, outWidth, Alignment.ALIGN_NORMAL, 1, 0, false); 
			}
			textPaint.getTextBounds(text, 0, 1, mBounds);
			top=top-(mBounds.bottom-mBounds.top);
		}
		
		canvas.translate(left, top); 
		staticLayout.draw(canvas);  
		canvas.translate(-left, -top);  
	}
	
	private void DrawImage(Canvas canvas,String imagePath,Integer left,Integer top,Integer outWidth,Integer outHeight){
		try {
			Bitmap imageBitmap = ImageHelper.bitmapZoomBySize(PlayerStoragePath.WeatherIconStorage + imagePath, outWidth, outHeight);
			m_used_images.add(imageBitmap);
			canvas.drawBitmap(imageBitmap, left, top, null); 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void DrawBackColor(Canvas canvas,Integer color){
		mPaint.setColor(color);   
		mPaint.setStyle(Style.FILL);   
        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), mPaint);
	}
	
	private String GetCurrentWeatherContent(String propertyName,String format)
    {
		if (this.m_content.current_condition==null)
			return "N/A";
		if (propertyName.equalsIgnoreCase("city"))
        {
            return String.format(format, this.m_content.City);
        }
        else if (propertyName.equalsIgnoreCase("weatherstatus"))
        {
            return String.format(format, this.m_content.current_condition.weatherDesc);
        }
        else if (propertyName.equalsIgnoreCase("temprature"))
        {
            if (this.m_temperatureType == TemperatureType.Fahrenheit)
                return String.format(format, this.m_content.current_condition.temp_F, "F");
            else
                return String.format(format, this.m_content.current_condition.temp_C,"C");
        }
        else if (propertyName.equalsIgnoreCase("psi")){
        	return String.format(format, this.m_content.current_condition.PSI);
        }
        else if (propertyName.equalsIgnoreCase("psirange")){
        	return String.format(format, this.m_content.current_condition.minPSI, this.m_content.current_condition.maxPSI);
        }
        return propertyName;
    }
	
	private String GetDateWeatherContent(LayoutOfDate layoutOfDate,Integer index, String propertyName,String format){
		if(this.m_content.weather==null||this.m_content.weather.size()-1<index)
			return "N/A";
		
		if (propertyName.equalsIgnoreCase("city"))
        {
            return String.format(format, this.m_content.City);
        }
        else if (propertyName.equalsIgnoreCase("weatherstatus"))
        {
            return String.format(format, this.m_content.weather.get(index).weatherDesc);
        }
        else if (propertyName.equalsIgnoreCase("tempraturerange"))
        {
            if (this.m_temperatureType == TemperatureType.Fahrenheit)
                return String.format(format, this.m_content.weather.get(index).mintempF, "F",this.m_content.weather.get(index).maxtempF, "F");
            else
                return String.format(format, this.m_content.weather.get(index).mintempC,"C",this.m_content.weather.get(index).maxtempC, "C");
        }
        else if (propertyName.equalsIgnoreCase("date"))
        {
        	if (layoutOfDate.DateDescribe == null || layoutOfDate.DateDescribe.size() - 1 < index || layoutOfDate.DateDescribe.get(index).isEmpty())
                return DateTimeHelper.ConvertToString(DateTimeHelper.GetAddedDateFromDay(MainActivity.Instance.Clock.Now,index),format);
            else
                return layoutOfDate.DateDescribe.get(index);
        }
        else if (propertyName.equalsIgnoreCase("psirange")){
        	return String.format(format, this.m_content.weather.get(index).minPSI, this.m_content.weather.get(index).maxPSI);
        }
        return propertyName;
	}
	
}
