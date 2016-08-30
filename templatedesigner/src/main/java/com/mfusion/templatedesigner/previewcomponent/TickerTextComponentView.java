package com.mfusion.templatedesigner.previewcomponent;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.templatedesigner.HandleTimer;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.dialog.ColorDialog;
import com.mfusion.templatedesigner.previewcomponent.subview.DropDownView;
import com.mfusion.templatedesigner.previewcomponent.dialog.FontDialog;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.ComponentFont;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.TemplateDesignerKeys;
import com.mfusion.templatedesigner.previewcomponent.values.TextSpeedType;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class TickerTextComponentView extends BasicComponentView {
	
	private String c_content="A Sample Text";
	
	private TextSpeedType c_speed=TextSpeedType.Medium;
	
	private ComponentFont c_font=new ComponentFont();
	
	private float m_content_lenght=0;
	
	private float m_content_baseline;

	private TextPaint paint_content;
	
	@Override
	protected int getDefaultColor(){
		return Color.CYAN;
	}
	
	public TickerTextComponentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_w=600;
		this.c_h=70;
		
		this.init();
	}
	
	public TickerTextComponentView(Context context, ComponentEntity componentEntity) {
		super(context, componentEntity);
		// TODO Auto-generated constructor stub
		
		try {
			int color=c_font.color;
			String fontString="";
			Element element=null;
			ArrayList<Element> list=componentEntity.property;
			for (int i = 0; i < list.size(); i++) {
				element=list.get(i);
				String propertyName=element.getAttribute("name");
				if(propertyName.equals("Font")){
					fontString=element.getTextContent();
					continue;
				}
				if(propertyName.equals("FontColor")){
					color=PropertyValues.convertColorStrToInt(element.getTextContent());
					continue;
				}
				if(propertyName.equals("TextString")){
					this.c_content=element.getTextContent();
					continue;
				}
				if(propertyName.equals("Speed")){
					this.c_speed=TextSpeedType.valueOf(Integer.parseInt(element.getTextContent()));
					continue;
				}
			}
			
			this.c_font.setFont(fontString, color);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		this.init();
		
	}
	
	private void init(){

		this.c_type=ComponentType.TickerText;

		this.paint_content=new TextPaint();
		this.paint_content.setAntiAlias(true);
		this.paint_content.setColor(this.c_font.color);
		this.paint_content.setStyle(Style.FILL);
		this.paint_content.setTextSize(c_font.size* TemplateDesignerKeys.temp_scale);
		this.paint_content.setTypeface(Typeface.create(this.c_font.family, this.c_font.style));
		
	}
	
	int left=0;
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			canvas.drawText(this.c_content, left, m_content_baseline, this.paint_content);
			left-=c_speed.value()/100;
			if((left+this.m_content_lenght)<=0)
				left=this.getLayoutParams().width;
		}
    }

	@Override
	public void refreshLayout(){
		super.refreshLayout();
		this.paint_content.setTextSize(c_font.size* TemplateDesignerKeys.temp_scale);
		m_content_baseline= getTextBaseline();
	}

	@Override
	public void render(){
		left=this.getLayoutParams().width;
		m_content_baseline= getTextBaseline();
		this.m_content_lenght=this.paint_content.measureText(this.c_content);
		
		this.timer.start(10);
	}

	@Override
	public void onLayoutChange(View view, int l, int t, int r,
							   int b, int oldl, int oldt, int oldr,int oldb) {
		super.onLayoutChange(view,l,t,r,b,oldl,oldt,oldr,oldb);

		if(oldt!=t||oldb!=b)
			m_content_baseline= getTextBaseline();
	}

	private float getTextBaseline(){
		FontMetricsInt fontMetrics = this.paint_content.getFontMetricsInt();
		return  (this.getLayoutParams().height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
	}

	@Override
	public void stop(){
		this.timer.stop();
	}

	private HandleTimer timer= new HandleTimer() {
		@Override
		protected void onTime() {
			invalidate();
		}
	};

	@Override
	protected Boolean checkComponetValid() throws Exception{
		if(this.c_content==null||this.c_content.isEmpty())
			throw new IllegalTemplateException("There is no display content in "+this.c_name);
		return true;
	}

	@Override
	public ComponentEntity getComponentProperty(Document doc,TemplateEntity templateEntity) throws Exception  {
		ComponentEntity componentEntity=super.getComponentProperty(doc, templateEntity);
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Font", c_font.toString()));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "ForeColor", PropertyValues.convertIntToColorStr(c_font.color)));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "TextString", this.c_content));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Speed", String.valueOf(this.c_speed.value())));

		return componentEntity;
	}
	
	private View propertyView;
	private TextView m_font_text;
	private Button m_fontcolor_edit_btn;
	private EditText m_content_editview;
	private DropDownView m_speed_ddv;
	@Override
	public View getPropertyView(ViewGroup rootViewGroup) {
		if(propertyView==null){
		
			propertyView=LayoutInflater.from(this.m_context).inflate(R.layout.comp_ticker_property, null,true); 
			m_font_text=(TextView)propertyView.findViewById(R.id.comp_ticker_font);
			ImageButton m_font_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_ticker_font_btn);
			m_font_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					(new FontDialog()).createDialog( m_context,c_font, new CallbackBundle() {  
		                @Override  
		                public void callback(Bundle bundle) {
		                	if(bundle==null)
		                		return;
	
		                	c_font.setFamilyString(bundle.getString("Family"));
		                	c_font.setStyleString(bundle.getString("Style"));
		                	c_font.size=bundle.getFloat("Size");

		            		m_font_text.setText(c_font.toString());
							paint_content.setTextSize(c_font.size * TemplateDesignerKeys.temp_scale);
		            		paint_content.setTypeface(Typeface.create(c_font.family, c_font.style));
		                }  
		            });
				}
			});
			
			m_fontcolor_edit_btn=(Button)propertyView.findViewById(R.id.comp_ticker_font_color_btn);
			m_fontcolor_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Dialog dialog =(new ColorDialog()).createDialog(0, m_context, new CallbackBundle() {  
		                @Override  
		                public void callback(Bundle bundle) {  
		                    String colorString = bundle.getString("color");  
		                    c_font.color=Integer.parseInt(colorString);
		                    paint_content.setColor(c_font.color);
							PropertyValues.bindingColorButton(m_fontcolor_edit_btn,c_font.color);
		                }  
		            }, c_font.color);
				}
			});
			
			m_content_editview=(EditText)propertyView.findViewById(R.id.comp_ticker_content);
			m_content_editview.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					c_content=arg0.toString();
					m_content_lenght=paint_content.measureText(c_content);
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
			
			m_speed_ddv=(DropDownView)propertyView.findViewById(R.id.comp_ticker_speed);
			m_speed_ddv.setSelectList(PropertyValues.getSpeedList());
			m_speed_ddv.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
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
		
		m_font_text.setText(c_font.toString());
		PropertyValues.bindingColorButton(m_fontcolor_edit_btn,c_font.color);
		m_content_editview.setText(c_content);
		m_speed_ddv.setText(c_speed.toString());
		
		return propertyView;
	}
}
