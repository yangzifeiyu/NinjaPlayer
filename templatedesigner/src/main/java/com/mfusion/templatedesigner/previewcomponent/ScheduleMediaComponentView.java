package com.mfusion.templatedesigner.previewcomponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.commons.tools.FileOperator;
import com.mfusion.commons.tools.ImageHelper;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.templatedesigner.R;
import com.mfusion.commons.view.DropDownView;
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
	
	private CustomerVideoSurfaceView m_video_view;

	private Bitmap m_used_bitmap;

	private int m_media_index=-1;
	
	private View m_pre_view=null,m_current_view=null;

	ScheduleMediaComponentView m_component_view;

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

		this.m_component_view=this;

	}
	
	@Override
	public void render(){

		//this.timer.start(1000,100);
		
	}

	ReentrantLock m_media_play_lock=new ReentrantLock();
	Date media_expiry_time;
	@Override
	public void executeTimer(){

		m_media_play_lock.lock();
		try {
			if (c_media_list.size() == 0) {

				setAlpha(0.8f);
				setBackgroundResource(R.drawable.component_style);

				if (m_video_view != null)
					m_video_view.stop();

				if (m_image_view != null)
					m_image_view.setVisibility(INVISIBLE);
				if (m_video_view != null)
					m_video_view.setVisibility(INVISIBLE);
				media_expiry_time = null;
				return;
			}

			Calendar current_date = Calendar.getInstance();
			if (media_expiry_time != null && media_expiry_time.compareTo(current_date.getTime()) >= 0)
				return;

			setAlpha(1f);
			setBackgroundResource(0);
			try {
				if (m_video_view != null)
					m_video_view.stop();

				refreshCurrentIndex();

				ScheduleMediaEntity currentMedia = c_media_list.get(m_media_index);
				if (!(new File((currentMedia.mediaPath)).exists())) {
					return;
				}

				current_date.add(Calendar.SECOND, currentMedia.duration);
				media_expiry_time = current_date.getTime();

				m_pre_view = m_current_view;
				if (m_pre_view != null)
					m_pre_view.setVisibility(INVISIBLE);

				Bitmap un_used_bitmap=m_used_bitmap;
				if (currentMedia.mediaType == ScheduleMediaType.Video && FileOperator.existFile(currentMedia.mediaPath)) {
					if (m_video_view == null) {
						m_video_view = new CustomerVideoSurfaceView(m_context);
						m_video_view.setVisibility(INVISIBLE);
						addView(m_video_view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					}
					m_video_view.setVideo(currentMedia.mediaPath, c_mute);
					m_current_view = m_video_view;
				} else {
					if (m_image_view == null) {
						m_image_view = new ImageView(m_context);
						m_image_view.setVisibility(VISIBLE);
						addView(m_image_view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					}
					if (currentMedia.mediaType == ScheduleMediaType.Image && FileOperator.existFile(currentMedia.mediaPath)) {
						m_image_view.setScaleType(ImageView.ScaleType.FIT_XY);
						m_used_bitmap=ImageHelper.getBitmap(currentMedia.mediaPath);
						m_image_view.setImageBitmap(m_used_bitmap);
					} else {
						m_image_view.setScaleType(ImageView.ScaleType.CENTER);
						m_image_view.setImageDrawable(m_context.getResources().getDrawable(currentMedia.mediaType == ScheduleMediaType.Image ? R.drawable.filedialog_image : R.drawable.filedialog_wavfile));
					}
					m_current_view = m_image_view;
				}

				if(un_used_bitmap!=null)
					ImageHelper.recycleBitmap(un_used_bitmap);

				m_component_view.bringChildToFront(m_current_view);
				m_current_view.setVisibility(VISIBLE);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			m_media_play_lock.unlock();
		}
	}

	@Override
	public void stop(){
		try {
			this.timer.stop();
			if(m_video_view!=null)
				m_video_view.stop();
			ImageHelper.recycleBitmap(m_used_bitmap);
			this.removeAllViews();

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/*private HandleTimer timer = new HandleTimer() {
		@Override
		protected void onTime() {

			if(c_media_list.size()==0){
				if(m_current_view!=null)
					m_current_view.setVisibility(GONE);
				return;
			}

			timer.stop();

			int timer_interval=100;
			try {
				refreshCurrentIndex();

				ScheduleMediaEntity currentMedia=c_media_list.get(m_media_index);
				if(!(new File((currentMedia.mediaPath)).exists())){
					timer.start(10);
					return;
				}

				if(currentMedia.mediaType==ScheduleMediaType.Image||currentMedia.mediaType==ScheduleMediaType.Video){
					if(m_image_view==null){
						m_image_view=new ImageView(m_context);
						m_image_view.setVisibility(VISIBLE);
						addView(m_image_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					}

					String imagePath=currentMedia.mediaPath;
					if(currentMedia.mediaType==ScheduleMediaType.Video) {
						imagePath = InternalKeyWords.VideoThumbPath + currentMedia.mediaName + ".jpg";
					}

					if(!FileOperator.existFile(imagePath)) {
						m_image_view.setScaleType(ImageView.ScaleType.CENTER);
						m_image_view.setImageDrawable(m_context.getResources().getDrawable(currentMedia.mediaType==ScheduleMediaType.Image?R.drawable.filedialog_image:R.drawable.filedialog_wavfile));
					}else{
						m_image_view.setScaleType(ImageView.ScaleType.FIT_XY);
						m_image_view.setImageBitmap(currentMedia.mediaType==ScheduleMediaType.Image?ImageHelper.getBitmap(imagePath):ImageHelper.getBitmap(imagePath,1));
					}

					m_current_view=m_image_view;
				}

				m_current_view.setVisibility(VISIBLE);
				timer_interval=currentMedia.duration*1000;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			finally {
				timer.start(timer_interval,timer_interval);
			}
		}
	};*/

	private HandleTimer timer= new HandleTimer() {
		@Override
		protected void onTime() {

			timer.stop();

			if(m_video_view!=null)
				m_video_view.stop();

			if(c_media_list.size()==0){

				setAlpha(0.8f);
				setBackgroundResource(R.drawable.component_style);

				if(m_image_view!=null)
					m_image_view.setVisibility(INVISIBLE);
				if(m_video_view!=null)
					m_video_view.setVisibility(INVISIBLE);
				timer.start(1000,100);
				return;
			}

			setAlpha(1f);
			setBackgroundResource(0);
			int timer_interval=100;
			try {
				refreshCurrentIndex();
				
				ScheduleMediaEntity currentMedia=c_media_list.get(m_media_index);
				if(!(new File((currentMedia.mediaPath)).exists())){
					timer.start(10);
					return;
				}

				m_pre_view=m_current_view;
				if(m_pre_view!=null)
					m_pre_view.setVisibility(INVISIBLE);

				if(currentMedia.mediaType==ScheduleMediaType.Video&&FileOperator.existFile(currentMedia.mediaPath)){
					if(m_video_view==null){
						m_video_view=new CustomerVideoSurfaceView(m_context);
						m_video_view.setVisibility(INVISIBLE);
						addView(m_video_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					}
					m_video_view.setVideo(currentMedia.mediaPath,c_mute);
					m_current_view=m_video_view;
				}else{
					if(m_image_view==null){
						m_image_view=new ImageView(m_context);
						m_image_view.setVisibility(VISIBLE);
						addView(m_image_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					}
					if(currentMedia.mediaType==ScheduleMediaType.Image&&FileOperator.existFile(currentMedia.mediaPath)){
						m_image_view.setScaleType(ImageView.ScaleType.FIT_XY);
						m_image_view.setImageBitmap(ImageHelper.getBitmap(currentMedia.mediaPath));
					}else{
						m_image_view.setScaleType(ImageView.ScaleType.CENTER);
						m_image_view.setImageDrawable(m_context.getResources().getDrawable(currentMedia.mediaType==ScheduleMediaType.Image?R.drawable.filedialog_image:R.drawable.filedialog_wavfile));
					}
					m_current_view=m_image_view;
				}

				m_component_view.bringChildToFront(m_current_view);
				m_current_view.setVisibility(VISIBLE);
				System.out.println("ScheduleMedia");
				for(int i=0;i<m_component_view.getChildCount();i++)
					System.out.println(m_component_view.getChildAt(i).getClass().toString());
				timer_interval=currentMedia.duration*1000;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			finally {
				timer.start(timer_interval,timer_interval);
			}
		}
	};
	
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			if(this.c_media_list.size()==0){

				ViewGroup.LayoutParams layoutParams=this.getLayoutParams();

				if(this.no_value_paint.measureText(this.c_name)>layoutParams.width)
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
			m_mute_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					c_mute=isChecked;

					if(m_video_view!=null)
						m_video_view.setMute(c_mute);
				}
			});
			
			m_playamode_ddv=(DropDownView)propertyView.findViewById(R.id.comp_sm_playmode);
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

			m_mediainfo_text=(TextView)propertyView.findViewById(R.id.comp_sm_mediainfo);
			m_media_edit_btn=(ImageButton)propertyView.findViewById(R.id.comp_sm_mediaedit);
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
		            },c_media_list,"Video|Image;");
				}
			});
			
		}

		getMediaListInfo();
		m_mute_cb.setChecked(c_mute);
		m_playamode_ddv.setText(c_play_mode.toString());
		
		return propertyView;
	}

	private void getMediaListInfo(){

		int totalDuration=0;
		for(ScheduleMediaEntity mediaEntity:c_media_list){
			totalDuration+=mediaEntity.duration;
		}
		m_mediainfo_text.setText(String.valueOf(c_media_list.size())+" ("+ScheduleMediaEntity.ConvertSecondsToStr(totalDuration)+")");

		if(c_media_list.size()==0){
			System.out.println("No media");
			//this.timer.restart(1000,1000);
		}
	}
}
