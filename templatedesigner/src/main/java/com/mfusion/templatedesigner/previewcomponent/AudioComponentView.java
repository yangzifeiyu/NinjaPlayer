package com.mfusion.templatedesigner.previewcomponent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.HandleTimer;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.dialog.MediaSelectDialog;
import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;
import com.mfusion.commons.view.DropDownView;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.ScheduleMediaPlayMode;
import com.mfusion.templatedesigner.previewcomponent.values.ScheduleMediaType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class AudioComponentView extends BasicComponentView {

	private ScheduleMediaPlayMode c_play_mode=ScheduleMediaPlayMode.Sequence;

	private List<ScheduleMediaEntity> c_media_list=new ArrayList<ScheduleMediaEntity>();

	private StaticLayout text_paint_layout = null;

	private TextPaint no_value_paint;

	@Override
	protected int getDefaultColor(){
		return Color.CYAN;
	}

	public AudioComponentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_w=150;
		this.c_h=70;

		this.init();
	}

	public AudioComponentView(Context context, ComponentEntity componentEntity) {
		super(context, componentEntity);
		// TODO Auto-generated constructor stub
		
		try {
			Element element=null;
			ArrayList<Element> list=componentEntity.property;
			for (int i = 0; i < list.size(); i++) {
				element=list.get(i);
				String propertyName=element.getAttribute("name");
				if(propertyName.equals("PlayMode")){
					this.c_play_mode=ScheduleMediaPlayMode.valueOf(element.getTextContent());
					continue;
				}
				if(propertyName.equals("PlayList")){
					Element mediaElement=null;
					ScheduleMediaEntity mediaEntity=null;
					NodeList mediaList = element.getElementsByTagName("Media");
					for (int mi=0;mi< mediaList.getLength();mi++) {
						mediaElement=(Element)mediaList.item(mi);
						
						mediaEntity=new ScheduleMediaEntity();
						mediaEntity.mediaPath=mediaElement.getAttribute("MediaPath");
						mediaEntity.mediaName=mediaEntity.mediaPath.substring(mediaEntity.mediaPath.lastIndexOf("/")+1);
						mediaEntity.mediaType= ScheduleMediaType.valueOf(mediaElement.getAttribute("MediaType"));
						mediaEntity.setStringDuration(mediaElement.getAttribute("Duration"));
						mediaEntity.effect=mediaElement.getAttribute("Effect");
						
						this.c_media_list.add(mediaEntity);
					}
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
		
		this.c_type=ComponentType.AudioComponent;

		this.no_value_paint=new TextPaint();
		this.no_value_paint.setAntiAlias(true);
		this.no_value_paint.setTextSize(14);
		this.no_value_paint.setColor(Color.GRAY);
		this.no_value_paint.setStyle(Style.STROKE);
		
	}
	
	@Override
	public void render(){

		//this.timer.start(5000);
	}

	@Override
	public void stop(){
		this.timer.stop();
	}

	private HandleTimer timer= new HandleTimer() {
		@Override
		protected void onTime() {

		}
	};
	
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			//if(this.c_media_list.size()==0){
				ViewGroup.LayoutParams layoutParams=this.getLayoutParams();
				if(this.no_value_paint.measureText(this.c_name)>layoutParams.width)
					text_paint_layout = new StaticLayout(this.c_name,this.no_value_paint,layoutParams.width,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
				else
					text_paint_layout = new StaticLayout(this.c_name,this.no_value_paint,layoutParams.width,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
				
				float base_line_y=(layoutParams.height-text_paint_layout.getHeight())/2;
				canvas.translate(0,base_line_y); 
				text_paint_layout.draw(canvas);
				canvas.translate(0,-base_line_y); 
			//}
		}
    }

	@Override
	protected Boolean checkComponetValid() throws Exception{
		if(this.c_media_list==null||this.c_media_list.size()==0)
			throw new IllegalTemplateException("There is no media in "+this.c_name);
		return true;
	}

	@Override
	public ComponentEntity getComponentProperty(Document doc,TemplateEntity templateEntity) throws Exception  {
		
		ComponentEntity componentEntity=super.getComponentProperty(doc, templateEntity);
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "PlayMode", this.c_play_mode.toString()));
		
		Element playlistElement=PropertyValues.createElement(doc, "Property", "PlayList", null);
		for(ScheduleMediaEntity mediaEntity: this.c_media_list){
			playlistElement.appendChild(this.getMediaElement(doc, mediaEntity));
		}
		componentEntity.property.add(playlistElement);

		return componentEntity;
	}
	
	private Element getMediaElement(Document doc,ScheduleMediaEntity mediaEntity){
		Element element=doc.createElement("Media");
		element.setAttribute("MediaPath", mediaEntity.mediaPath);
		element.setAttribute("MediaType", mediaEntity.mediaType.toString());
		element.setAttribute("MediaSource", mediaEntity.mediaSource);
		element.setAttribute("Duration", mediaEntity.durationString);
		element.setAttribute("Effect", mediaEntity.effect);
		return element;
	}
	
	private View propertyView;
	private TextView m_mediainfo_text;
	private ImageButton m_media_edit_btn;
	private DropDownView m_playamode_ddv;
	@Override
	public View getPropertyView(ViewGroup rootViewGroup) {
		if(propertyView==null){
		
			propertyView=LayoutInflater.from(this.m_context).inflate(R.layout.comp_audio_property,  null,true);

			m_playamode_ddv=(DropDownView)propertyView.findViewById(R.id.comp_audio_playmode);
			m_playamode_ddv.setSelectList(PropertyValues.getPlayModeList());
			m_playamode_ddv.setOnChangeListener(new DropDownView.OnSelectTextChangedListener() {
				@Override
				public void onSelectTextChange(String selectText) {
					if(c_play_mode.toString().equalsIgnoreCase(selectText))
						return;
					componentPropertyChanged();
					c_play_mode=ScheduleMediaPlayMode.valueOf(selectText);
				}
			});

			m_mediainfo_text=(TextView)propertyView.findViewById(R.id.comp_audio_mediainfo);
			m_media_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_audio_mediaedit);
			m_media_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					(new MediaSelectDialog()).createDialog( m_context,new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
							componentPropertyChanged();
							getMediaListInfo();
							return;
		                }  
		            },c_media_list,"Sound");
				}
			});
			
		}

		getMediaListInfo();
		m_playamode_ddv.setText(c_play_mode.toString());
		
		return propertyView;
	}

	private void getMediaListInfo(){

		int totalDuration=0;
		for(ScheduleMediaEntity mediaEntity:c_media_list){
			totalDuration+=mediaEntity.duration;
		}
		m_mediainfo_text.setText(String.valueOf(c_media_list.size())+" ("+ScheduleMediaEntity.ConvertSecondsToStr(totalDuration)+")");
	}
}
