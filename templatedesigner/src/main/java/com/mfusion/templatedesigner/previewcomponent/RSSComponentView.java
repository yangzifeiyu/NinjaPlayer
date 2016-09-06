package com.mfusion.templatedesigner.previewcomponent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.commons.tools.rss.RssFeed_SAXParser;
import com.mfusion.commons.tools.rss.RssFeed;
import com.mfusion.commons.tools.rss.RssItem;

import com.mfusion.templatedesigner.HandleTimer;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.dialog.ColorDialog;
import com.mfusion.commons.view.DropDownView;
import com.mfusion.templatedesigner.previewcomponent.dialog.FontDialog;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.ComponentFont;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.TemplateDesignerKeys;
import com.mfusion.templatedesigner.previewcomponent.values.TextSpeedType;

public class RSSComponentView extends BasicComponentView {

	private String c_rss_url="http://www.asiaone.com/a1mborss/A1RssMBOTicker.xml";
	
	private TextSpeedType c_speed= TextSpeedType.Medium;
	
	private ComponentFont c_title_font=new ComponentFont();

	private ComponentFont c_body_font=new ComponentFont();
	
	private TextPaint paint_title,paint_body;
	
	private RssFeed_SAXParser m_rss_parser;
	
	private RssLoadingAsyncTask m_loading_task;
	
	private List<RssPaintEntity> m_rss_contents;
	
	private float m_content_lenght=0;
	
	private float m_content_baseline=0;

	@Override
	protected int getDefaultColor(){
		return Color.YELLOW;
	}
	
	public RSSComponentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_w=600;
		this.c_h=70;
		
		this.init();
	}
	
	public RSSComponentView(Context context, ComponentEntity componentEntity) {
		super(context, componentEntity);
		// TODO Auto-generated constructor stub
		
		try {
			int color_title=c_title_font.color;
			int color_body=c_title_font.color;
			String font_title="",font_body="";
			Element element=null;
			ArrayList<Element> list=componentEntity.property;
			for (int i = 0; i < list.size(); i++) {
				element=list.get(i);
				String propertyName=element.getAttribute("name");
				if(propertyName.equals("SubjectFont")){
					font_title=element.getTextContent();
					continue;
				}
				if(propertyName.equals("SubjectForeColor")){
					color_title= PropertyValues.convertColorStrToInt(element.getTextContent());
					continue;
				}
				if(propertyName.equals("BodyFont")){
					font_body=element.getTextContent();
					continue;
				}
				if(propertyName.equals("BodyForeColor")){
					color_body=PropertyValues.convertColorStrToInt(element.getTextContent());
					continue;
				}
				if(propertyName.equals("Address")){
					this.c_rss_url=element.getTextContent();
					continue;
				}
				if(propertyName.equals("Speed")){
					this.c_speed=TextSpeedType.valueOf(Integer.parseInt(element.getTextContent()));
					continue;
				}
			}
			
			this.c_title_font.setFont(font_title, color_title);
			this.c_body_font.setFont(font_body, color_body);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		this.init();
		
	}
	
	private void init(){

		this.c_type= ComponentType.RSSComponent;
		
		try {
			this.m_rss_parser=new RssFeed_SAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.m_rss_contents=new ArrayList<RssPaintEntity>();
		
		this.paint_title=new TextPaint();
		this.paint_title.setAntiAlias(true);
		this.paint_title.setStyle(Style.FILL);
		this.paint_title.setTextSize(c_title_font.size * TemplateDesignerKeys.temp_scale);
		this.paint_title.setTypeface(Typeface.create(this.c_title_font.family, this.c_title_font.style));
		
		this.paint_body=new TextPaint();
		this.paint_body.setAntiAlias(true);
		this.paint_body.setStyle(Style.FILL);
		this.paint_body.setTextSize(c_body_font.size * TemplateDesignerKeys.temp_scale);
		this.paint_body.setTypeface(Typeface.create(this.c_body_font.family, this.c_body_font.style));
	}

	int left=0;
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			int paint_x=left;
			for(RssPaintEntity paintEntity : this.m_rss_contents){
				if((paint_x+paintEntity.textLenght)<=0){
					paint_x+=paintEntity.textLenght;
					continue;
				}
				
				if(paint_x>=this.getLayoutParams().width)
					break;
				
				canvas.drawText(paintEntity.title, paint_x, paintEntity.titleBaseLine, paintEntity.titlePaint);
				paint_x+=paintEntity.titleLenght;
				canvas.drawText(paintEntity.description, paint_x, paintEntity.descriptionBaseLine, paintEntity.descriptionPaint);
				paint_x+=paintEntity.descriptionLenght;
			}
			
			left-=c_speed.value()/100;
			if((left+this.m_content_lenght)<=0)
				left=this.getLayoutParams().width;
		}
    }

	@Override
	public void refreshLayout(){
		super.refreshLayout();
		this.paint_title.setTextSize(c_title_font.size* TemplateDesignerKeys.temp_scale);
		this.paint_body.setTextSize(c_body_font.size* TemplateDesignerKeys.temp_scale);
		refreshDisplayView();
	}

	@Override
	public void render(){
		loadingRssContent();
		this.timer.start(10);
	}

	@Override
	public void onLayoutChange(View view, int l, int t, int r,
							   int b, int oldl, int oldt, int oldr,int oldb) {
		super.onLayoutChange(view,l,t,r,b,oldl,oldt,oldr,oldb);

		if(oldt!=t||oldb!=b)
			refreshDisplayView();
	}

	@Override
	public void stop(){
		this.timer.stop();
		
		try {

			if(this.m_loading_task!=null)
				this.m_loading_task.cancel(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private HandleTimer timer= new HandleTimer() {
		@Override
		protected void onTime() {

			if(m_content_lenght==0)
				loadingRssContent();
			else
				invalidate();
		}
	};
	
	private void loadingRssContent(){
		if(this.m_loading_task==null||this.m_loading_task.isCancelled()){

			this.m_loading_task=new RssLoadingAsyncTask(m_context, c_rss_url);
			this.m_loading_task.execute("");
		}
	}

	@Override
	protected Boolean checkComponetValid() throws Exception{
		if(this.c_rss_url==null||this.c_rss_url.isEmpty())
			throw new IllegalTemplateException("There is no rss address in "+this.c_name);
		return true;
	}

	@Override
	public ComponentEntity getComponentProperty(Document doc, TemplateEntity templateEntity) throws Exception  {
		ComponentEntity componentEntity=super.getComponentProperty(doc, templateEntity);

		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "SubjectFont", c_title_font.toString()));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "SubjectForeColor", PropertyValues.convertIntToColorStr(c_title_font.color)));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "BodyFont", c_body_font.toString()));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "BodyForeColor", PropertyValues.convertIntToColorStr(c_body_font.color)));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Address", this.c_rss_url));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Speed", String.valueOf(this.c_speed.value())));

		return componentEntity;
		
	}
	
	private View propertyView;
	private TextView m_title_font_text;
	private Button m_title_color_edit_btn;
	private TextView m_body_font_text;
	private Button m_body_color_edit_btn;
	private EditText m_content_editview;
	private DropDownView m_speed_ddv;
	@Override
	public View getPropertyView(ViewGroup rootViewGroup) {
		if(propertyView==null){
		
			propertyView=LayoutInflater.from(this.m_context).inflate(R.layout.comp_rss_property, null,true); 
			m_title_font_text=(TextView)propertyView.findViewById(R.id.comp_rss_title_font);
			ImageButton m_font_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_rss_title_font_btn);
			m_font_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					(new FontDialog()).createDialog( m_context,c_title_font, new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
		                	if(bundle==null)
		                		return;
							componentPropertyChanged();

		                	c_title_font.setFamilyString(bundle.getString("Family"));
		                	c_title_font.setStyleString(bundle.getString("Style"));
		                	c_title_font.size=bundle.getFloat("Size");

		                	m_title_font_text.setText(c_title_font.toString());
							paint_title.setTextSize(c_title_font.size * TemplateDesignerKeys.temp_scale);
		                	paint_title.setTypeface(Typeface.create(c_title_font.family, c_title_font.style));
		                	refreshDisplayView();
		                }  
		            });
				}
			});
			
			m_title_color_edit_btn=(Button)propertyView.findViewById(R.id.comp_rss_title_font_color_btn);
			m_title_color_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Dialog dialog =(new ColorDialog()).createDialog(0, m_context, new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
							componentPropertyChanged();

							String colorString = bundle.getString("color");
		                    c_title_font.color=Integer.parseInt(colorString);
		                    paint_title.setColor(c_title_font.color);
							PropertyValues.bindingColorButton(m_title_color_edit_btn,c_title_font.color);
		                }  
		            }, c_title_font.color);
				}
			});
			
			m_body_font_text=(TextView)propertyView.findViewById(R.id.comp_rss_body_font);
			ImageButton m_body_font_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_rss_body_font_btn);
			m_body_font_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					(new FontDialog()).createDialog( m_context,c_title_font, new CallbackBundle() {  
		                @Override  
		                public void callback(Bundle bundle) {
		                	if(bundle==null)
		                		return;

							componentPropertyChanged();

							c_body_font.setFamilyString(bundle.getString("Family"));
		                	c_body_font.setStyleString(bundle.getString("Style"));
		                	c_body_font.size=bundle.getFloat("Size");

		                	m_body_font_text.setText(c_body_font.toString());
							paint_body.setTextSize(c_body_font.size * TemplateDesignerKeys.temp_scale);
		                	paint_body.setTypeface(Typeface.create(c_body_font.family, c_body_font.style));
		                	refreshDisplayView();
		                }  
		            });
				}
			});
			
			m_body_color_edit_btn=(Button)propertyView.findViewById(R.id.comp_rss_body_font_color_btn);
			m_body_color_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Dialog dialog =(new ColorDialog()).createDialog(0, m_context, new CallbackBundle() {  
		                @Override  
		                public void callback(Bundle bundle) {
							componentPropertyChanged();

							String colorString = bundle.getString("color");
		                    c_body_font.color=Integer.parseInt(colorString);
		                    paint_body.setColor(c_body_font.color);
							PropertyValues.bindingColorButton(m_body_color_edit_btn,c_body_font.color);
		                }  
		            }, c_body_font.color);
				}
			});
			
			m_content_editview=(EditText)propertyView.findViewById(R.id.comp_rss_address);
			m_content_editview.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					if(!c_rss_url.equalsIgnoreCase(arg0.toString())){
						componentPropertyChanged();

						c_rss_url=arg0.toString();
						m_content_lenght=0;
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			m_speed_ddv=(DropDownView)propertyView.findViewById(R.id.comp_rss_speed);
			m_speed_ddv.setSelectList(PropertyValues.getSpeedList());
			m_speed_ddv.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub

					if(c_speed.toString().equalsIgnoreCase(arg0.toString()))
						return;
					componentPropertyChanged();
					c_speed=TextSpeedType.valueOf(arg0.toString());
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					System.out.println();
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
				}
			});
		}
		
		m_title_font_text.setText(c_title_font.toString());
		PropertyValues.bindingColorButton(m_title_color_edit_btn,c_title_font.color);
		m_body_font_text.setText(c_body_font.toString());
		PropertyValues.bindingColorButton(m_body_color_edit_btn,c_body_font.color);
		m_content_editview.setText(c_rss_url);
		m_speed_ddv.setText(c_speed.toString());
		
		return propertyView;
	}

	private void refreshDisplayView(){
		if(this.m_rss_contents==null)
			return;
		
		int content_lenght=0;
		for(RssPaintEntity paintEntity:this.m_rss_contents){
			paintEntity.refreshPaintInfo();
			
			content_lenght+=paintEntity.textLenght;
		}
		
		this.m_content_lenght=content_lenght;
	}
	
	class RssLoadingAsyncTask extends AsyncTask<String, Integer, String> {
		
		Context context;
		
		String rssUrl;
		
		public RssLoadingAsyncTask(Context context,String url){
			this.context=context;
			this.rssUrl=url;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				
				RssFeed rssFeed = m_rss_parser.getFeed(rssUrl);
				if(rssFeed==null||rssFeed.getRssItems()==null)
					return null;
				
				List<RssPaintEntity> contents=new ArrayList<RssPaintEntity>();
				int total_lenght=0;
				RssPaintEntity paintEntity=null;
				for (RssItem item : rssFeed.getRssItems()) {
					paintEntity=new RssPaintEntity();
					paintEntity.title=item.getTitle()+"    ";
					paintEntity.description=item.getDescription()+"    ";
					paintEntity.titlePaint=paint_title;
					paintEntity.descriptionPaint=paint_body;
					paintEntity.refreshPaintInfo();
					
					total_lenght+=paintEntity.textLenght;
					contents.add(paintEntity);
				}
				
				m_rss_contents.clear();
				m_rss_contents.addAll(contents);
				contents.clear();
				m_content_lenght=total_lenght;
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}

		@Override  
	    protected void onPostExecute(String result) {  
	        try {
	        	
	        	super.cancel(true);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }  
	}
	
	class RssPaintEntity{
		public String title;
		public TextPaint titlePaint;
		public float titleBaseLine;
		public float titleLenght;
		public String description;
		public TextPaint descriptionPaint;
		public float descriptionBaseLine;
		public float descriptionLenght;
		public float textLenght;
		
		public void refreshPaintInfo(){
			if(titlePaint!=null&&descriptionPaint!=null){
				titleLenght=titlePaint.measureText(title);
				descriptionLenght=descriptionPaint.measureText(description);
				textLenght=titleLenght+descriptionLenght;

				FontMetricsInt fontMetrics = titlePaint.getFontMetricsInt();  
				titleBaseLine= (getLayoutParams().height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top; 
				fontMetrics = descriptionPaint.getFontMetricsInt();  
				descriptionBaseLine= (getLayoutParams().height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top; 
			}
		}
	}
}
