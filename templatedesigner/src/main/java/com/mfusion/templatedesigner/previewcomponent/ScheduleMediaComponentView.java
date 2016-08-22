package com.mfusion.templatedesigner.previewcomponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.text.Editable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.templatedesigner.HandleTimer;
import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.subview.DropDownView;
import com.mfusion.templatedesigner.previewcomponent.dialog.MediaSelectDialog;
import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;
import com.mfusion.templatedesigner.previewcomponent.subview.CustomerVideoSurfaceView;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.ScheduleMediaPlayMode;
import com.mfusion.templatedesigner.previewcomponent.values.ScheduleMediaType;

public class ScheduleMediaComponentView extends BasicComponentView {
	
	private Boolean c_mute=false;
	
	private ScheduleMediaPlayMode c_play_mode=ScheduleMediaPlayMode.Sequence;
	
	private List<ScheduleMediaEntity> c_media_list=new ArrayList<ScheduleMediaEntity>();
	
	private StaticLayout text_paint_layout = null;

	private TextPaint no_value_paint;
	
	private ImageView m_image_view;
	
	private CustomerVideoSurfaceView m_video_surfaceview;
	
	private VideoView m_video_view;
	
	private int m_media_index=-1;
	
	private View m_pre_view=null,m_current_view=null;

	@Override
	protected int getDefaultColor(){
		return Color.LTGRAY;
	}
	
	public ScheduleMediaComponentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.c_w=400;
		this.c_h=400;
		
		this.init();
	}
	
	public ScheduleMediaComponentView(Context context, ComponentEntity componentEntity) {
		super(context, componentEntity);
		// TODO Auto-generated constructor stub
		
		try {
			Element element=null;
			ArrayList<Element> list=componentEntity.property;
			for (int i = 0; i < list.size(); i++) {
				element=list.get(i);
				String propertyName=element.getAttribute("name");
				if(propertyName.equals("Mute")){
					this.c_mute=Boolean.valueOf(element.getTextContent());
					continue;
				}
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
		
		this.c_type=ComponentType.ScheduleMedia;

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
			
			if(c_media_list.size()==0){
				if(m_current_view!=null)
					m_current_view.setVisibility(GONE);
				return;
			}
			
			timer.stop();
			
			/*final VideoView videoView=new VideoView(m_context);
			addView(videoView, new LayoutParams(getLayoutParams().width,getLayoutParams().height));
			MediaController mediaController = new MediaController(m_context); 
			videoView.setMediaController(mediaController); 
			videoView.setOnInfoListener(new OnInfoListener() {
				
				@Override
				public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					return false;
				}
			});
				videoView.setVideoPath("/sdcard/Storage/Video/0/android/TSL - Laos 30s HD.mp4");
			videoView.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
			
			         videoView.start(); 
				}
			});*/
	        
			/*try {
				refreshCurrentIndex();
				
				ScheduleMediaEntity currentMedia=c_media_list.get(m_media_index);
				if(!(new File((currentMedia.mediaPath)).exists())){
					timer.start(10);
					return;
				}
				
				if(m_current_view!=null)
					m_current_view.setVisibility(GONE);
				
				if(currentMedia.mediaType==ScheduleMediaType.Image){
					if(m_image_view==null){
						m_image_view=new ImageView(m_context);
						m_image_view.setVisibility(VISIBLE);
						m_image_view.setScaleType(ScaleType.FIT_XY);
						addView(m_image_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					}
					m_image_view.setImageBitmap(ImageHelper.getBitmap(currentMedia.mediaPath));
					m_current_view=m_image_view;
				}
				if(currentMedia.mediaType==ScheduleMediaType.Video){
					if(m_video_surfaceview==null){
						m_video_surfaceview=new CustomerVideoSurfaceView(m_context);
						m_video_surfaceview.setVisibility(VISIBLE);
						addView(m_video_surfaceview, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					}
					m_video_surfaceview.setVideo(currentMedia.mediaPath, true);
					m_current_view=m_video_surfaceview;
				}
				
				m_current_view.setVisibility(VISIBLE);
				timer.start(currentMedia.duration*1000,currentMedia.duration*1000);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}*/
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
				if(this.no_value_paint.measureText(this.c_type.toString())>layoutParams.width)
					text_paint_layout = new StaticLayout(this.c_type.toString(),this.no_value_paint,layoutParams.width,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
				else
					text_paint_layout = new StaticLayout(this.c_type.toString(),this.no_value_paint,layoutParams.width,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
				
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
		componentEntity.property.add(PropertyValues.createElement(doc, "Property", "Mute", String.valueOf(this.c_mute)));
		
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
	
	private void refreshCurrentIndex(){
		if(this.c_media_list.size()>0)
			this.m_media_index=(this.m_media_index+1)%this.c_media_list.size();
	}
	
	private View propertyView;
	private CheckBox m_mute_cb;
	private TextView m_mediainfo_text;
	private ImageButton m_media_edit_btn;
	private DropDownView m_playamode_ddv;
	@Override
	public View getPropertyView(ViewGroup rootViewGroup) {
		if(propertyView==null){
		
			propertyView=LayoutInflater.from(this.m_context).inflate(R.layout.comp_sm_property,  null,true); 
			
			m_mute_cb=(CheckBox)propertyView.findViewById(R.id.comp_sm_mute);
			
			m_playamode_ddv=(DropDownView)propertyView.findViewById(R.id.comp_sm_playmode);
			m_playamode_ddv.setSelectList(PropertyValues.getPlayModeList());
			m_playamode_ddv.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					c_play_mode=ScheduleMediaPlayMode.valueOf(arg0.toString());
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

			m_mediainfo_text=(TextView)propertyView.findViewById(R.id.comp_sm_mediainfo);
			m_media_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_sm_mediaedit);
			m_media_edit_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					(new MediaSelectDialog()).createDialog( m_context,new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
							getMediaListInfo();
							return;
		                }  
		            },c_media_list);
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
