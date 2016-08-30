package com.mfusion.templatedesigner.previewcomponent;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.text.Editable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.Layout.Alignment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.subview.UserWebView;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class InteractiveComponentView  extends BasicComponentView {
	
	private String c_address="";

	private StaticLayout text_paint_layout = null;

	private TextPaint no_value_paint;
	
	private UserWebView m_web_view;
	
	@Override
	protected int getDefaultColor(){
		return Color.DKGRAY;
	}
	
	public InteractiveComponentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_w=400;
		this.c_h=500;
		
		this.init();
	}
	
	public InteractiveComponentView(Context context, ComponentEntity componentEntity) {
		super(context, componentEntity);
		// TODO Auto-generated constructor stub
		
		try {
			Element element=null;
			ArrayList<Element> list=componentEntity.property;
			for (int i = 0; i < list.size(); i++) {
				element=list.get(i);
				String propertyName=element.getAttribute("name");
				if(propertyName.equals("Address")){
					this.c_address=element.getTextContent();
					continue;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		this.init();
		
	}
	
	private void init(){
		
		this.c_type= ComponentType.Interactive;

		this.no_value_paint=new TextPaint();
		this.no_value_paint.setAntiAlias(true);
		this.no_value_paint.setTextSize(14);
		this.no_value_paint.setColor(Color.GRAY);
		this.no_value_paint.setStyle(Style.STROKE);
	}
	
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			if(this.c_address==null||this.c_address.isEmpty()){
				ViewGroup.LayoutParams layoutParams=this.getLayoutParams();
				if(this.no_value_paint.measureText(this.c_type.toString())>layoutParams.width)
					text_paint_layout = new StaticLayout(this.c_name,this.no_value_paint,layoutParams.width,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
				else
					text_paint_layout = new StaticLayout(this.c_name,this.no_value_paint,layoutParams.width,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
				
				float base_line_y=(layoutParams.height-text_paint_layout.getHeight())/2;
				canvas.translate(0,base_line_y); 
				text_paint_layout.draw(canvas);
				canvas.translate(0,-base_line_y); 
			}
		}
    }
	
	@Override
	public void render(){
		if(this.c_address==null||this.c_address.isEmpty()){
			return;
		}
		
		if(this.m_web_view==null){
			m_web_view=new UserWebView(this.m_context);
			m_web_view.setLongClickable(false);
			m_web_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			m_web_view.setBackgroundColor(Color.argb(100, Color.red(c_back_color), Color.green(c_back_color), Color.blue(c_back_color)));
			m_web_view.setWebViewClient(new WebViewClient(){       
				public boolean shouldOverrideUrlLoading(WebView view, String url) {       
					view.loadUrl(url);       
					return true;       
				}       
			});   
			
			WebSettings webSettings = m_web_view.getSettings();    
			webSettings.setJavaScriptEnabled(true); 
			webSettings.setBuiltInZoomControls(false);
			this.addView(m_web_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		}
		
		this.m_web_view.loadUrl(c_address);
	}

	@Override
	public void stop(){
		if(this.m_web_view!=null){
			this.m_web_view.clearCache(true);
			this.m_web_view.clearHistory();
			this.m_web_view.removeAllViews();
		}
	}

	@Override
	protected Boolean checkComponetValid() throws Exception{
		if(this.c_address==null||this.c_address.isEmpty())
			throw new IllegalTemplateException("There is no web url in "+this.c_name);
		return true;
	}

	@Override
	public ComponentEntity getComponentProperty(Document doc,TemplateEntity templateEntity) throws Exception  {
		
		ComponentEntity componentEntity=super.getComponentProperty(doc, templateEntity);
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Address", this.c_address));

		return componentEntity;
	}
	
	private View propertyView;
	private EditText m_content_editview;
	@Override
	public View getPropertyView(ViewGroup rootViewGroup) {
		if(propertyView==null){
		
			propertyView=LayoutInflater.from(this.m_context).inflate(R.layout.comp_interactive_property, null,true);
			m_content_editview=(EditText)propertyView.findViewById(R.id.comp_interactive_address);
			m_content_editview.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					if(!c_address.equalsIgnoreCase(arg0.toString())){
						c_address=arg0.toString();
						invalidate();
						render();
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
		}
		
		m_content_editview.setText(c_address);
		
		return propertyView;
	}
}
