package com.mfusion.templatedesigner.previewcomponent;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.templatedesigner.HandleTimer;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.dialog.ColorDialog;
import com.mfusion.commons.view.DropDownView;
import com.mfusion.templatedesigner.previewcomponent.dialog.FontDialog;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.dialog.TimeFormatDialog;
import com.mfusion.templatedesigner.previewcomponent.values.ComponentFont;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.TemplateDesignerKeys;

public class DateTimeComponentView extends BasicComponentView {
	
	private String c_format="yyyy-MM-dd HH:mm:ss";
	
	private ComponentFont c_font=new ComponentFont();
	
	private TextPaint paint_content;
	
	@Override
	protected int getDefaultColor(){
		return Color.BLUE;
	}
	
	public DateTimeComponentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_w=200;
		this.c_h=70;
		
		this.init();
	}
	
	public DateTimeComponentView(Context context, ComponentEntity componentEntity) {
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
					color= PropertyValues.convertColorStrToInt(element.getTextContent());
					continue;
				}
				if(propertyName.equals("TimeFormat")){
					this.c_format=element.getTextContent();
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
		
		this.c_type= ComponentType.DateTime;

		this.paint_content=new TextPaint();
		this.paint_content.setAntiAlias(true);
		this.paint_content.setColor(this.c_font.color);
		this.paint_content.setStyle(Style.FILL);
		this.paint_content.setTextSize(c_font.size* TemplateDesignerKeys.temp_scale);
		this.paint_content.setTypeface(Typeface.create(c_font.family, c_font.style));
	}
	
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			try {

				/*ViewGroup.LayoutParams layoutParams=this.getLayoutParams();
				StaticLayout text_paint_layout = new StaticLayout(DateConverter.convertCurrentDateToStr(this.c_format),this.paint_content,layoutParams.width,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
				
				float base_line_y=(layoutParams.height-text_paint_layout.getHeight())/2;
				canvas.translate(0,base_line_y); 
				text_paint_layout.draw(canvas);
				canvas.translate(0,-base_line_y);*/

				String content=DateConverter.convertCurrentDateToStr(this.c_format);
				float textLenght=this.paint_content.measureText(content);
				canvas.drawText(content,(this.getLayoutParams().width-textLenght)/2,getTextBaseline(),this.paint_content);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} 
		}
    }

	private float getTextBaseline(){
		Paint.FontMetricsInt fontMetrics = this.paint_content.getFontMetricsInt();
		return  (this.getLayoutParams().height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
	}

	@Override
	public void refreshLayout(){
		super.refreshLayout();
		this.paint_content.setTextSize(c_font.size* TemplateDesignerKeys.temp_scale);
	}

	@Override
	public void render(){
		this.timer.start(1000);
		
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
	public ComponentEntity getComponentProperty(Document doc,TemplateEntity templateEntity) throws Exception  {
		
		ComponentEntity componentEntity=super.getComponentProperty(doc, templateEntity);
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Font", c_font.toString()));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "ForeColor", PropertyValues.convertIntToColorStr(c_font.color)));
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "TimeFormat", this.c_format));

		return componentEntity;
	}
	
	private View propertyView;
	private TextView m_font_text;
	private Button m_fontcolor_edit_btn;
	private TextView m_format_ddv;
	@Override
	public View getPropertyView(ViewGroup rootViewGroup) {
		if(propertyView==null){
		
			propertyView=LayoutInflater.from(this.m_context).inflate(R.layout.comp_dt_property, null,true);
			m_font_text=(TextView)propertyView.findViewById(R.id.comp_dt_font);
			ImageButton m_font_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_dt_font_btn);
			m_font_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					(new FontDialog()).createDialog( m_context,c_font, new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
		                	if(bundle==null)
		                		return;

							componentPropertyChanged();
		                	c_font.setFamilyString(bundle.getString("Family"));
		                	c_font.setStyleString(bundle.getString("Style"));
		                	c_font.size=bundle.getFloat("Size");

		            		m_font_text.setText(c_font.toString());
							paint_content.setTextSize(c_font.size* TemplateDesignerKeys.temp_scale);
		            		paint_content.setTypeface(Typeface.create(c_font.family, c_font.style));
		                }  
		            });
				}
			});
			
			m_fontcolor_edit_btn=(Button)propertyView.findViewById(R.id.comp_dt_font_color_btn);
			m_fontcolor_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Dialog dialog =(new ColorDialog()).createDialog(0, m_context, new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
							componentPropertyChanged();
							String colorString = bundle.getString("color");
		                    c_font.color=Integer.parseInt(colorString);
		                    paint_content.setColor(c_font.color);
							PropertyValues.bindingColorButton(m_fontcolor_edit_btn,c_font.color);
		                }  
		            }, c_font.color);
				}
			});
			
			m_format_ddv=(TextView)propertyView.findViewById(R.id.comp_dt_format);
			ButtonHoverStyle.bindingHoverEffectWithBorder(m_format_ddv,m_context.getResources());
			m_format_ddv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Dialog dialog =(new TimeFormatDialog()).createDialog( m_context, c_format,new CallbackBundle() {
						@Override
						public void callback(Bundle bundle) {
							String format=bundle.getString("format");
							if(c_format.toString().equalsIgnoreCase(format.toString()))
								return;
							componentPropertyChanged();
							c_format=format;
							m_format_ddv.setText(c_format);
						}
					});
				}
			});
		}
		
		m_font_text.setText(c_font.toString());
		PropertyValues.bindingColorButton(m_fontcolor_edit_btn,c_font.color);
		m_format_ddv.setText(c_format);
		
		return propertyView;
	}
}
