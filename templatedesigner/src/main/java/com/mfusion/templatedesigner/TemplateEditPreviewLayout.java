package com.mfusion.templatedesigner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.controllers.AbstractTemplateDesigner;
import com.mfusion.commons.controllers.KeyBoardCenter;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.XmlOperator;
import com.mfusion.templatedesigner.previewcomponent.BGComponentView;
import com.mfusion.templatedesigner.previewcomponent.BasicComponentView;
import com.mfusion.templatedesigner.previewcomponent.ComponenCenter;
import com.mfusion.templatedesigner.previewcomponent.adapter.ComponentListAdapter;
import com.mfusion.templatedesigner.previewcomponent.entity.SelectedCompProperty;
import com.mfusion.templatedesigner.previewcomponent.values.CompOperateType;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.TemplateDesignerKeys;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class TemplateEditPreviewLayout extends AbstractTemplateDesigner implements OnLayoutChangeListener{

	boolean isInitUI=false;

	boolean isOpening=false;

	RelativeLayout temp_container=null;
	
	RelativeLayout temp_workspace=null;
	
	LinearLayout temp_complist=null;
	
	LinearLayout comp_properties=null;
	
	CompPropertyView comp_properties_view=null;

	TextView m_temp_name_view;

	ComponenCenter m_component_center;

	private XmlOperator m_xml_helper;

	private int containerWidth,containerHeight;
	int temp_ws_absolute_h;
	int temp_container_w,temp_container_h;
	int temp_ws_w,temp_ws_h,temp_ws_left;
	int temp_cl_w,temp_cl_h;
	int temp_real_w,temp_real_h;
	
	SelectedCompProperty selectedComp=new SelectedCompProperty();

	MotionEvent temp_ws_mouse_event;

	Context m_context;

	LinearLayout templateLayout;

	TemplateEntity currentTemplate,watingTemplate;

	public AbstractFragment parentFragment;

	public TemplateEditPreviewLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.init(context);
	}

	public TemplateEditPreviewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	private void init(Context context){

		this.m_context=context;

		this.addOnLayoutChangeListener(this);

		this.m_component_center=new ComponenCenter();

		this.m_xml_helper=new XmlOperator();

		this.initUIControl();
	}

	private void initUIControl() {

		templateLayout=(LinearLayout)LayoutInflater.from(this.getContext()).inflate(R.layout.activity_template, this,true);

		this.m_temp_name_view=(TextView) templateLayout.findViewById(R.id.temp_title_view);

		this.temp_complist=(LinearLayout)templateLayout.findViewById(R.id.temp_comp);
		
		this.comp_properties=(LinearLayout)templateLayout.findViewById(R.id.temp_comp_property);
		this.comp_properties_view=new CompPropertyView(m_context, deleteCompCallBack);
		this.comp_properties.addView(comp_properties_view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		this.temp_container=(RelativeLayout)templateLayout.findViewById(R.id.temp_container);
		
		this.temp_workspace=(RelativeLayout)templateLayout.findViewById(R.id.temp_workspace);

		((ListView)templateLayout.findViewById(R.id.comp_list)).setAdapter(new ComponentListAdapter(this.m_context, PropertyValues.getComponentList()));

		this.temp_workspace.setOnDragListener(new OnDragListener() {
			
			@Override
			public boolean onDrag(View arg0, DragEvent event) {
				// TODO Auto-generated method stub
				final int action = event.getAction(); 
				switch (action) { 
				case DragEvent.ACTION_DRAG_STARTED:
					if (event.getClipDescription().hasMimeType( ClipDescription.MIMETYPE_TEXT_PLAIN)) { 
						return true;
					} 
					return false; 
				case DragEvent.ACTION_DRAG_ENTERED:
					return true; 
				case DragEvent.ACTION_DRAG_LOCATION:return true;
				case DragEvent.ACTION_DRAG_EXITED:
					return true; 
				case DragEvent.ACTION_DROP:
					ClipData.Item item = event.getClipData().getItemAt(0); 
					String dragData = item.getText().toString();
					CreateComponent(dragData,(int) event.getX(),(int) event.getY(),temp_workspace);
					
					return true; 
				case DragEvent.ACTION_DRAG_ENDED: 
					//�Ϸ��¼���� 
					return true;
				default: break; } 
				return false;
			}
		});
		
		this.temp_workspace.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					temp_ws_mouse_event=event;
				}
				return false;
			}
		});

		KeyBoardCenter.deleteKeyCallbackList.add(deleteCompCallBack);
	}

	CallbackBundle deleteCompCallBack=new CallbackBundle() {
		@Override
		public void callback(Bundle bundle) {
			if(selectedComp.selectedView==null)
				return;
			temp_workspace.removeView(selectedComp.selectedView);
			if(temp_workspace.getChildCount()>0)
				comp_properties_view.bingingBasicProperties((BasicComponentView) temp_workspace.getChildAt(0));
			else
				comp_properties_view.unBinding();
		}
	};

	private void initTemplate() {
		temp_workspace.removeAllViews();

		if(currentTemplate==null){
			currentTemplate=new TemplateEntity();
			currentTemplate.width=((Activity)m_context).getWindowManager().getDefaultDisplay().getWidth();
			currentTemplate.height=((Activity)m_context).getWindowManager().getDefaultDisplay().getHeight();
			currentTemplate.backColor=Color.BLACK;
		}

		String displayText="New Template";
		if(currentTemplate.id!=null&&!currentTemplate.id.isEmpty())
			displayText=currentTemplate.id;
		this.m_temp_name_view.setText(displayText+" ( "+currentTemplate.width+" X "+currentTemplate.height+" )");
		this.m_temp_name_view.setTag(displayText);

		getTemplateScaleData(currentTemplate.width,currentTemplate.height);
		
		BGComponentView bg_viewBg=new BGComponentView(this.m_context, this.temp_real_w,this.temp_real_h, currentTemplate.backColor,currentTemplate.backImageBitmap);
		bg_viewBg.TemplateSizeChangedCallback=TemplateSizeChangedCallback;
		bindCompEvent(temp_workspace,bg_viewBg,0,0,bg_viewBg.c_w,bg_viewBg.c_h);
		comp_properties_view.bingingBasicProperties(bg_viewBg);
		
		for (ComponentEntity entity : currentTemplate.compList) {
			BasicComponentView componentView=this.m_component_center.getComponent(m_context, entity);
			if(componentView!=null)
				bindCompEvent(temp_workspace,componentView,entity.left,entity.top,entity.width,entity.height);
		}

		invalidate();
	}

	private CallbackBundle TemplateSizeChangedCallback=new CallbackBundle() {

		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			if (bundle == null)
				return;
			getTemplateScaleData(bundle.getInt("w"),bundle.getInt("h"));
			for(int i=1;i<temp_workspace.getChildCount();i++)
				((BasicComponentView)temp_workspace.getChildAt(i)).refreshLayout();
			m_temp_name_view.setText(m_temp_name_view.getTag()+" ( "+temp_real_w+" X "+temp_real_h+" )");
		}
	};

	private void getTemplateScaleData(int realWidth,int realHeight){

		this.temp_real_w=realWidth;
		this.temp_real_h=realHeight;

		float scale_w=(this.temp_container_w*1.0f)/this.temp_real_w;
		float scale_h=(this.temp_container_h*1.0f)/this.temp_real_h;
		if(scale_w<scale_h){
			TemplateDesignerKeys.temp_scale=scale_w;
			this.temp_ws_w=this.temp_container_w;
			this.temp_ws_h=(int)(this.temp_real_h*scale_w);
		}
		else{
			TemplateDesignerKeys.temp_scale=scale_h;
			this.temp_ws_h=this.temp_container_h;
			this.temp_ws_w=(int)(this.temp_real_w*scale_h);
		}
		RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)this.temp_workspace.getLayoutParams();
		layoutParams.width=this.temp_ws_w;
		layoutParams.height=this.temp_ws_h;
		layoutParams.leftMargin=this.temp_ws_left=(this.temp_container_w-this.temp_ws_w)/2;
		layoutParams.topMargin=(this.temp_container_h-this.temp_ws_h)/2;
		this.temp_workspace.setLayoutParams(layoutParams);

		this.temp_ws_absolute_h=layoutParams.topMargin+((RelativeLayout)findViewById(R.id.temp_title_layout)).getLayoutParams().height;

	}

	private void CreateComponent(String type,int x,int y,RelativeLayout parentLayout){
		if(parentFragment!=null)
			parentFragment.isEditing=true;

		BasicComponentView componentView=this.m_component_center.CreateComponent(m_context, type);
		if(componentView!=null) {
			bindCompEvent(parentLayout, componentView, x, y, componentView.c_w, componentView.c_h);
			comp_properties_view.bingingBasicProperties(componentView);
		}
	}
	
	private void bindCompEvent(RelativeLayout parentLayout,BasicComponentView componentView,int x,int y,int width,int height){

		if(componentView==null)
			return;

		componentView.changedListener=componentEditListener;
		parentLayout.addView(componentView);
		
		RelativeLayout.LayoutParams componentLayout = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		componentLayout.leftMargin=x;
		componentLayout.topMargin=y;
		componentLayout.width=width;
		componentLayout.height=height;
		
		componentView.setLayoutParams(PropertyValues.convertToVirtualLayout(componentLayout));
		componentView.setClickable(true);
		
		componentView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:{

						if(selectedComp.selectedView!=null)
							((BasicComponentView)selectedComp.selectedView).onSelected(false);

						if(((BasicComponentView)view).isDeleteComponent(event)){

							temp_workspace.removeView(view);
							comp_properties_view.unBinding();
							return false;
						}
						
						selectedComp.selectedView=null;
						RelativeLayout.LayoutParams layout=(RelativeLayout.LayoutParams)view.getLayoutParams();
						CompOperateType operateType=((BasicComponentView)view).getOperateTypeByLocation(layout, event);
						if(operateType==CompOperateType.none)
							break;
						
						selectedComp.selectedView=(BasicComponentView)view;
						selectedComp.left=layout.leftMargin;
						selectedComp.top=layout.topMargin;
						selectedComp.width=layout.width;
						selectedComp.height=layout.height;
						selectedComp.mouseX=temp_cl_w+temp_ws_left+layout.leftMargin+event.getX();
						selectedComp.mouseY=temp_ws_absolute_h+layout.topMargin+event.getY();
						selectedComp.operateType=operateType;

						((BasicComponentView)selectedComp.selectedView).onSelected(true);
						comp_properties_view.bingingBasicProperties(((BasicComponentView)selectedComp.selectedView));
						System.out.println("Comp  :ACTION_DOWN" +" Type:  "+selectedComp.operateType);
						break;
	                }
	                case MotionEvent.ACTION_UP:{
	                	System.out.println("Comp  :ACTION_UP");
	                	break;
	                }
	                case MotionEvent.ACTION_MOVE:{
	                	System.out.println("Comp  :ACTION_MOVE");
	                	break;
	                }
				}
				return false;
			}
		});
		componentView.render();
	}
	CallbackBundle componentEditListener = new CallbackBundle() {
		@Override
		public void callback(Bundle bundle) {
			if(parentFragment!=null)
				parentFragment.isEditing=true;
		}
	};
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if(selectedComp.selectedView!=null&&selectedComp.operateType!=CompOperateType.none){

				if(parentFragment!=null)
					parentFragment.isEditing=true;

				RelativeLayout.LayoutParams layout=(RelativeLayout.LayoutParams)selectedComp.selectedView.getLayoutParams();
				((BasicComponentView)selectedComp.selectedView).isLayoutChange=true;
				if(selectedComp.operateType==CompOperateType.move){
					layout.leftMargin+=ev.getX()-selectedComp.mouseX;
					layout.topMargin+=ev.getY()-selectedComp.mouseY;
					if(layout.leftMargin<0)
						layout.leftMargin=0;
					else if(layout.leftMargin>(this.temp_ws_w-selectedComp.width))
						layout.leftMargin=this.temp_ws_w-selectedComp.width;
					if(layout.topMargin<0)
						layout.topMargin=0;
					else if(layout.topMargin>(this.temp_ws_h-selectedComp.height))
						layout.topMargin=this.temp_ws_h-selectedComp.height;

				}else{
					float mouseAddX=0,mouseAddY=0;
					mouseAddX=ev.getX()-selectedComp.mouseX;
					mouseAddY=ev.getY()-selectedComp.mouseY;
					
					String operateString=selectedComp.operateType.toString();
					if(operateString.contains("n")){
						layout.topMargin+=mouseAddY;
						if(layout.topMargin<0)
							layout.topMargin=0;
						else
							layout.height-=mouseAddY;
					}else if(operateString.contains("s")){
						layout.height+=mouseAddY;
						if(layout.topMargin>(this.temp_ws_h-layout.height))
							layout.height-=mouseAddY;
						
					}
					if(operateString.contains("w")){
						layout.leftMargin+=mouseAddX;
						if(layout.leftMargin<0)
							layout.leftMargin=0;
						else
							layout.width-=mouseAddX;
					}else if(operateString.contains("e")){
						layout.width+=mouseAddX;
						if(layout.leftMargin>(this.temp_ws_w-layout.width))
							layout.width-=mouseAddX;
					}
					selectedComp.height=layout.height;
					selectedComp.width=layout.width;
				}
				selectedComp.mouseX=ev.getX();
				selectedComp.mouseY=ev.getY();
				selectedComp.selectedView.setLayoutParams(layout);
			}
		}else if (ev.getAction() == MotionEvent.ACTION_UP) {
			if(selectedComp.selectedView!=null){
				((BasicComponentView)selectedComp.selectedView).isLayoutChange=false;
				if(selectedComp.operateType!=CompOperateType.none) {

					comp_properties_view.bingingBasicProperties(((BasicComponentView) selectedComp.selectedView));
				}
				selectedComp.operateType=CompOperateType.none;
        	}
		}

        return super.dispatchTouchEvent(ev);
    }

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		if(left!=oldLeft||right!=oldRight||top!=oldTop||bottom!=oldBottom) {

			containerWidth=right-left;
			containerHeight=bottom-top;

			Float comp_weight = ((LinearLayout.LayoutParams)this.temp_complist.getLayoutParams()).weight;
			Float property_weight = ((LinearLayout.LayoutParams)this.comp_properties.getLayoutParams()).weight;
			Float workspace_weight = ((LinearLayout.LayoutParams)this.temp_container.getLayoutParams()).weight;
			Float pre_weight_lenght=containerWidth/(comp_weight+property_weight+workspace_weight);
			this.temp_cl_w=(int)(pre_weight_lenght*comp_weight);
			this.temp_container_w=(int)(pre_weight_lenght*workspace_weight)-this.temp_container.getPaddingLeft()-this.temp_container.getPaddingRight();
			this.temp_container_h=containerHeight-((RelativeLayout)findViewById(R.id.temp_title_layout)).getLayoutParams().height-this.temp_container.getPaddingTop()-this.temp_container.getPaddingBottom();

			isInitUI=true;

			if(isOpening){
				Message msg=new Message();
				msg.what=1;
				handler.sendMessage(msg);
			}
		}
	}

	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what){
				case 0:
					temp_workspace.removeAllViews();
					selectedComp.selectedView=null;
					invalidate();
					break;
				case 1:
					initTemplate();
					isOpening=false;
					hiheLoadingPage();
					break;
				case 2:
					m_temp_name_view.setTag(currentTemplate.id);
					m_temp_name_view.setText(m_temp_name_view.getTag() + " ( " + temp_real_w + " X " + temp_real_h + " )");
					invalidate();
					break;
			}
		}
	};

	@Override
	public Boolean openTemplate(TemplateEntity template) {
		this.m_component_center.clear();

		this.isOpening=true;
		currentTemplate=template;

		if(isInitUI){
			Message msg=new Message();
			msg.what=1;
			handler.sendMessage(msg);
		}

		return true;
	}

	@Override
	public TemplateEntity saveTemplate() throws Exception{
		Document doc=this.m_xml_helper.createXmlDocument();
		TemplateEntity savaTemplate=new TemplateEntity();
		savaTemplate.id=currentTemplate.id;
		for(int index=0;index<temp_workspace.getChildCount();index++){
			((BasicComponentView)temp_workspace.getChildAt(index)).getComponentProperty(doc,savaTemplate);
		}
		return savaTemplate;
	}

	@Override
	public void saveTemplateResult(TemplateEntity templateEntity){
		if(templateEntity!=null) {
			currentTemplate.id=templateEntity.id;
			Message msg=new Message();
			msg.what=2;
			handler.sendMessage(msg);
		}
	}

	@Override
	public void closeTemplate() {
		Message msg=new Message();
		msg.what=0;
		handler.sendMessage(msg);
	}
}
