package com.mfusion.templatedesigner.previewcomponent;

import java.io.IOException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.R.integer;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnHoverListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.RelativeLayout;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.templatedesigner.R;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.CompOperateType;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;
import com.mfusion.templatedesigner.previewcomponent.values.TemplateDesignerKeys;
import com.mfusion.templatedesigner.previewcomponent.values.ViewOperateHelper;

public class BasicComponentView extends RelativeLayout implements OnLongClickListener,OnHoverListener,OnLayoutChangeListener{

	protected Context m_context;
	
	public Boolean isLayoutChange=false;

	public CallbackBundle changedListener;

	private Boolean isSelected=false;

	public int c_w=200,c_h=200,c_left,c_top;

	public int c_back_color=Color.GRAY;

	protected ComponentType c_type;
	
	public String c_name="Component 1";
	
	private BasicComponentView compView;
	
	protected TextPaint paint_border,paint_bg,paint_selectd;

	private DashPathEffect effects;

	int distance=15;

	protected int getDefaultColor(){
		return Color.WHITE;
	}
	
	public BasicComponentView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);

		this.compView=this;
		
		this.initComponent(context,this.getDefaultColor());
	}

	public BasicComponentView(Context context, ComponentEntity componentEntity){
		super(context);

		this.compView=this;
		
		this.c_name=componentEntity.componentName;
		
		this.c_left=componentEntity.left;
		
		this.c_top=componentEntity.top;
		
		this.c_w=componentEntity.width;
		
		this.c_h=componentEntity.height;
		
		this.initComponent(context, componentEntity.backColor);
	}
	
	private void initComponent(Context context,int backColor) {

		this.m_context=context;
		
		this.c_back_color=backColor;
		
		this.setAlpha(0.8f);
		this.setBackgroundResource(R.drawable.component_style);

		this.paint_border=new TextPaint();
		this.paint_border.setAntiAlias(true);
		this.paint_border.setColor(Color.RED);
		this.paint_border.setStyle(Style.STROKE);

		this.paint_bg=new TextPaint();
		this.paint_bg.setAntiAlias(true);
		this.paint_bg.setColor(this.c_back_color);
		this.paint_bg.setStyle(Style.FILL);

		this.paint_selectd=new TextPaint();
		this.paint_selectd.setAntiAlias(true);
		this.paint_selectd.setColor(Color.RED);
		this.paint_selectd.setStyle(Style.STROKE);

	    this.effects = new DashPathEffect(new float[] { 8, 8}, 1);
	    
		this.setOnHoverListener(this);
		this.setOnLongClickListener(this);  
		this.addOnLayoutChangeListener(this);
	}

	@Override  
    protected void onDraw(Canvas canvas)  
    {  
		super.onDraw(canvas);
		
		if(canvas!=null)
		{
			canvas.drawRect(0, 0, this.getLayoutParams().width,  this.getLayoutParams().height, paint_bg);

			if(isSelected) {
				//顶角的选择标志
				//左上
				canvas.drawRect(1, 1, distance - 1, distance - 1, paint_selectd);
				//左下
				canvas.drawRect(1, this.getLayoutParams().height - distance + 1, distance - 1, this.getLayoutParams().height - 1, paint_selectd);
				//右上
				canvas.drawRect(this.getLayoutParams().width - distance + 1, 1, this.getLayoutParams().width - 1, distance - 1, paint_selectd);
				//右下
				canvas.drawRect(this.getLayoutParams().width - distance + 1, this.getLayoutParams().height - distance + 1, this.getLayoutParams().width - 1, this.getLayoutParams().height - 1, paint_selectd);

				int half_distance = distance / 2;
				if (this.getLayoutParams().width > distance * 4) {
					int width_center = this.getLayoutParams().width / 2;
					//上下边的选择标志
					canvas.drawRect(width_center - half_distance, 1, width_center + half_distance, distance - 1, paint_selectd);
					canvas.drawRect(width_center - half_distance, this.getLayoutParams().height - distance + 1, width_center + half_distance, this.getLayoutParams().height - 1, paint_selectd);
				}
				if (this.getLayoutParams().height > distance * 4) {
					int height_center = this.getLayoutParams().height / 2;
					//左右边的选择标志
					canvas.drawRect(1, height_center - half_distance, distance - 1, height_center + half_distance, paint_selectd);
					canvas.drawRect(this.getLayoutParams().width - distance + 1, height_center - half_distance, this.getLayoutParams().width - 1, height_center + half_distance, paint_selectd);
				}
			}

			canvas.drawRect(1, 1, this.getLayoutParams().width-1,  this.getLayoutParams().height-1, paint_border);
			//canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_close_clear_cancel), null,new Rect(getLayoutParams().width-15-distance, distance, getLayoutParams().width-distance, distance+15), paint_border);
		}
    }
	
	@Override
	public void onLayoutChange(View view, int l, int t, int r,
			int b, int oldl, int oldt, int oldr,int oldb) {
		// TODO Auto-generated method stub

		if(oldl==l&&oldt==t&&oldr==r&&oldb==b)
			return;

		if(view!=null){
			c_left=getNewSize(l,c_left);
			c_top=getNewSize(t,c_top);
			c_w=getNewSize(r-l,c_w);
			c_h=getNewSize(b-t,c_h);
		}
	}

	/*
	界面像素对实际像素的影响超过1的，才能采用
	 */
	private int getNewSize(int displayData,int realData){
		int new_data=(int)(displayData/ TemplateDesignerKeys.temp_scale);
		if(Math.abs(new_data-realData)>2)
			realData=new_data;
		return realData;
	}

	protected Boolean checkComponetValid() throws Exception{
		return true;
	}
	
	public ComponentEntity getComponentProperty(Document doc,TemplateEntity templateEntity) throws Exception  {

		checkComponetValid();

		ComponentEntity componentEntity=new ComponentEntity();
		componentEntity.componentName=c_name;
		componentEntity.type=this.c_type;
		componentEntity.backColor=this.c_back_color;
		componentEntity.height=this.c_h;
		componentEntity.width=this.c_w;
		componentEntity.left=this.c_left;
		componentEntity.top=this.c_top;
		
		templateEntity.compList.add(componentEntity);
		
		return componentEntity;
	}
	
	public void render(){
	
	}

	public void refreshLayout(){
		RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)this.getLayoutParams();
		layoutParams.leftMargin=(int)(c_left*TemplateDesignerKeys.temp_scale);
		layoutParams.topMargin=(int)(c_top*TemplateDesignerKeys.temp_scale);
		layoutParams.width=(int)(c_w*TemplateDesignerKeys.temp_scale);
		layoutParams.height=(int)(c_h*TemplateDesignerKeys.temp_scale);
		this.setLayoutParams(layoutParams);
	}

	public void stop(){
		
	}

	@Override
	public boolean onHover(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_HOVER_ENTER:{
				System.out.println("Comp  :ACTION_HOVER_ENTER");
            	break;
			}
			case MotionEvent.ACTION_HOVER_MOVE:{
				
				LayoutParams layout=(LayoutParams)this.getLayoutParams();

				getOperateTypeByLocation(layout,event);
				
            	break;
			}
			case MotionEvent.ACTION_HOVER_EXIT:{
				System.out.println("Comp  :ACTION_HOVER_EXIT");
            	break;
			}
		}
		return false;
	}
	
	//get mouse location when mouse click
	public void onSelected(Boolean selected){
		if(selected)
            this.paint_border.setPathEffect(effects);
        else
            this.paint_border.setPathEffect(null);

		isSelected=selected;
		invalidate();
	}

	public Boolean isDeleteComponent(MotionEvent event) {
		/*
		if(event.getX()>(getLayoutParams().width-20-distance)&&event.getX()<(getLayoutParams().width-distance)&&event.getY()>5&&event.getY()<20)
			return true;
		*/
		return false;
	}
	
	public CompOperateType getOperateTypeByLocation(LayoutParams layout,MotionEvent event) {
		String operateString="";
		if(event.getY()<distance){
			operateString="n";
		}
		else if(event.getY()>(layout.height-distance)){
			operateString="s";
		}
		if(event.getX()<distance){
			operateString=operateString+"w";
		}
		else if(event.getX()>(layout.width-distance)){
			operateString=operateString+"e";
		}

		return CompOperateType.fromString(operateString);
	}

	public CallbackBundle SizeChangedCallback=new CallbackBundle() {
		
		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			updateViewLayout(bundle.getInt("W"),bundle.getInt("H"),c_left,c_top);
		}
	};
	public CallbackBundle LocationChangedCallback=new CallbackBundle() {
		
		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			updateViewLayout(c_w,c_h,bundle.getInt("L"),bundle.getInt("T"));
		}
	};
	public CallbackBundle ColorChangedCallback=new CallbackBundle() {
		
		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			componentPropertyChanged();

			c_back_color=Integer.valueOf(bundle.getString("color"));
			paint_bg.setColor(c_back_color);
			invalidate();
		}
	};
	public CallbackBundle ZindexChangedCallback=new CallbackBundle() {
		
		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			componentPropertyChanged();

			Boolean operation=bundle.getInt("ZIndex")==1;
			ViewOperateHelper.changeViewZindex((ViewGroup)getParent(), compView, operation);
		}
	};
	
	protected void updateViewLayout(Integer width,Integer height,Integer left,Integer top){
		componentPropertyChanged();

		c_w=width;
		c_h=height;
		c_left=left;
		c_top=top;
		
		LayoutParams layoutParams=new LayoutParams(width,height);
		layoutParams.leftMargin=c_left;
		layoutParams.topMargin=c_top;
		setLayoutParams(PropertyValues.convertToVirtualLayout(layoutParams));
	}

	protected void componentPropertyChanged(){
		if(changedListener!=null)
			changedListener.callback(null);

	}

	public View getPropertyView(ViewGroup rootViewGroup) {
		return null;
		
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
