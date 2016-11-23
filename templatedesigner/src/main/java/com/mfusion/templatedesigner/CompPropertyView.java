package com.mfusion.templatedesigner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.view.OpenFileDialog;
import com.mfusion.templatedesigner.previewcomponent.BGComponentView;
import com.mfusion.templatedesigner.previewcomponent.BasicComponentView;
import com.mfusion.templatedesigner.previewcomponent.dialog.*;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class CompPropertyView extends LinearLayout {

	private Context m_context;
	
	private LinearLayout propertiesLayout=null;
	
	private LinearLayout m_location_layout,m_zindex_layout,m_bgimage_layout;
	
	private TextView m_comp_name;
	
	private int width,height;
	private TextView m_size_text=null;
	private ImageButton m_size_edit_btn=null;
	private CallbackBundle m_size_edit_callback=null;

	private ImageButton m_bgimage_btn;
	private CallbackBundle m_bgimage_edit_callback=null;
	
	private int left,top;
	private TextView m_location_text=null;
	private ImageButton m_location_edit_btn=null;
	private CallbackBundle m_location_edit_callback=null;

	private Button m_color_edit_btn=null;
	private CallbackBundle m_color_edit_callback=null;

	private ImageButton m_zindex_up_btn=null;
	private ImageButton m_zindex_down_btn=null;
	private CallbackBundle m_zindex_edit_callback=null;

	private ImageButton m_delete_btn;

	private LinearLayout basicPropertiesLayout=null,otherPropertiesLayout=null;
	private ImageButton basicPropertiesExpandBtn,otherPropertiesExpandBtn;

	private View otherPropertiesView=null;
	
	public CompPropertyView(Context context, final CallbackBundle deleteCallback) {
		super(context);
		// TODO Auto-generated constructor stub
		this.m_context=context;
		
		propertiesLayout = (LinearLayout)LayoutInflater.from(this.getContext()).inflate(R.layout.com_basic_properties, this,true);
		
		this.m_comp_name=(TextView)propertiesLayout.findViewById(R.id.temp_comp_name);
		
		this.m_location_layout=(LinearLayout)propertiesLayout.findViewById(R.id.temp_comp_location_layout);
		this.m_zindex_layout=(LinearLayout)propertiesLayout.findViewById(R.id.temp_comp_zindex_layout);
		this.m_bgimage_layout=(LinearLayout)propertiesLayout.findViewById(R.id.temp_comp_bgimage_layout);
		
		this.m_bgimage_btn=(ImageButton)propertiesLayout.findViewById(R.id.temp_comp_bgimage_btn);
		this.m_size_text=(TextView)propertiesLayout.findViewById(R.id.temp_comp_size_text);
		this.m_size_edit_btn=(ImageButton)propertiesLayout.findViewById(R.id.temp_comp_size_btn);
		this.m_location_text=(TextView)propertiesLayout.findViewById(R.id.temp_comp_location_text);
		this.m_location_edit_btn=(ImageButton)propertiesLayout.findViewById(R.id.temp_comp_location_btn);
		this.m_color_edit_btn=(Button)propertiesLayout.findViewById(R.id.temp_comp_color_btn);
		this.m_color_edit_btn.setTag(Color.BLACK);
		this.m_zindex_up_btn=(ImageButton)propertiesLayout.findViewById(R.id.temp_comp_moveup_btn);
		this.m_zindex_down_btn=(ImageButton)propertiesLayout.findViewById(R.id.temp_comp_movedown_btn);

		this.m_size_edit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);
				if(m_size_edit_callback!=null){
					(new SizeDialog()).createDialog( m_context,width,height, new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) {
		                	if(bundle==null)
		                		return;
		                	width=bundle.getInt("W");
		                	height=bundle.getInt("H");
		                	m_size_text.setText(width+", "+height);
		                	m_size_edit_callback.callback(bundle);
		                }  
		            });
				}
			}
		});
		this.m_location_edit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);
				if(m_location_edit_callback!=null){
					(new LocationDialog()).createDialog( m_context,left,top, new CallbackBundle() {  
		                @Override  
		                public void callback(Bundle bundle) {
		                	if(bundle==null)
		                		return;
		                	left=bundle.getInt("L");
		                	top=bundle.getInt("T");
		                	m_location_text.setText(left+", "+top);
		                	m_location_edit_callback.callback(bundle);
		                }  
		            });
				}
			}
		});
		this.m_color_edit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);
				Dialog dialog =(new ColorDialog()).createDialog(0, m_context, new CallbackBundle() {  
	                @Override  
	                public void callback(Bundle bundle) {  
	                    String colorString = bundle.getString("color");
						int color=Integer.parseInt(colorString);
						PropertyValues.bindingColorButton(m_color_edit_btn,color);
	                    if(m_color_edit_callback!=null)
	                    	m_color_edit_callback.callback(bundle);
	                }  
	            }, Integer.valueOf(m_color_edit_btn.getTag().toString()));

			}
		});
		this.m_zindex_up_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);
				if(m_zindex_edit_callback!=null){
					Bundle resultBundle=new Bundle();
					resultBundle.putInt("ZIndex", 1);
					m_zindex_edit_callback.callback(resultBundle);
				}
			}
		});
		this.m_zindex_down_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);
				if(m_zindex_edit_callback!=null){
					Bundle resultBundle=new Bundle();
					resultBundle.putInt("ZIndex", -1);
					m_zindex_edit_callback.callback(resultBundle);
				}
			}
		});
		
		m_bgimage_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);
				if(m_bgimage_edit_callback!=null){
					Dialog dialog = OpenFileDialog.createDialog(m_context, "Select a background image", new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) { 
		                    m_bgimage_edit_callback.callback(bundle);
		                }  
		            },   
		            "Image;",false,true);
				}
			}
		});

		this.m_delete_btn=(ImageButton)propertiesLayout.findViewById(R.id.temp_delete_btn);
		this.m_delete_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(deleteCallback!=null)
					deleteCallback.callback(null);
			}
		});
		basicPropertiesLayout=(LinearLayout)propertiesLayout.findViewById(R.id.temp_comp_basic_properties);
		basicPropertiesExpandBtn=(ImageButton) propertiesLayout.findViewById(R.id.temp_basic_expan_btn);
		bindingExpandEvent(basicPropertiesExpandBtn,R.drawable.mf_toggle,basicPropertiesLayout);

		otherPropertiesLayout=(LinearLayout)propertiesLayout.findViewById(R.id.temp_comp_other_properties);
		otherPropertiesExpandBtn=(ImageButton) propertiesLayout.findViewById(R.id.temp_other_expan_btn);
		bindingExpandEvent(otherPropertiesExpandBtn,R.drawable.mf_toggle,otherPropertiesLayout);
	}

	private void bindingExpandEvent(final ImageButton button, int defaultDrawable, final LinearLayout relatedLayout){
		button.setImageDrawable(getResources().getDrawable(defaultDrawable));
		button.setTag(defaultDrawable);
		if(defaultDrawable==R.drawable.mf_toggle) {
			relatedLayout.setVisibility(VISIBLE);
			((View)button.getParent()).setBackground(getResources().getDrawable(R.drawable.mf_propertysubbar));
		}
		if(defaultDrawable==R.drawable.mf_toggle_expand) {
			relatedLayout.setVisibility(GONE);
			((View)button.getParent()).setBackground(getResources().getDrawable(R.drawable.mf_propertybar));
		}

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				((ViewGroup)propertiesLayout).requestChildFocus(view,view);

				int currentDrawable=(int)view.getTag();
				if(currentDrawable==R.drawable.mf_toggle){
					currentDrawable=R.drawable.mf_toggle_expand;
					relatedLayout.setVisibility(GONE);
					((View)button.getParent()).setBackground(getResources().getDrawable(R.drawable.mf_propertybar));
				}else if(currentDrawable==R.drawable.mf_toggle_expand){
					currentDrawable=R.drawable.mf_toggle;
					relatedLayout.setVisibility(VISIBLE);
					((View)button.getParent()).setBackground(getResources().getDrawable(R.drawable.mf_propertysubbar));
				}
				button.setImageDrawable(getResources().getDrawable(currentDrawable));
				button.setTag(currentDrawable);
			}
		});
	}
	
	public void bingingBasicProperties(BasicComponentView compView) {
		
		if(compView.getClass()==BGComponentView.class){
			this.m_location_layout.setVisibility(GONE);
			this.m_zindex_layout.setVisibility(GONE);
			this.m_bgimage_layout.setVisibility(VISIBLE);
			this.m_delete_btn.setVisibility(GONE);
			this.m_bgimage_edit_callback=((BGComponentView)compView).ImageChangedCallback;
		}else {
			this.m_location_layout.setVisibility(VISIBLE);
			this.m_zindex_layout.setVisibility(VISIBLE);
			this.m_bgimage_layout.setVisibility(GONE);
			this.m_delete_btn.setVisibility(VISIBLE);
		}
		
		this.m_comp_name.setText(compView.c_name);
		//binding new value
		width=compView.c_w;
		height=compView.c_h;
		left=compView.c_left;
		top=compView.c_top;
		this.m_size_text.setText(compView.c_w+", "+compView.c_h);
		this.m_location_text.setText(compView.c_left+", "+compView.c_top);
		this.m_color_edit_btn.setBackgroundColor(compView.c_back_color);
		this.m_color_edit_btn.setTag(compView.c_back_color);
		
		//binding event
		this.m_size_edit_callback=compView.SizeChangedCallback;
		this.m_location_edit_callback=compView.LocationChangedCallback;
		this.m_color_edit_callback=compView.ColorChangedCallback;
		this.m_zindex_edit_callback=compView.ZindexChangedCallback;
		
		if(otherPropertiesView!=null){
			otherPropertiesLayout.removeView(otherPropertiesView);
			otherPropertiesLayout.removeAllViews();
		}
		otherPropertiesView=compView.getPropertyView(otherPropertiesLayout);
		if(otherPropertiesView!=null)
			otherPropertiesLayout.addView(otherPropertiesView);
	}

	public void unBinding(){
		this.m_comp_name.setText("");
		this.m_delete_btn.setVisibility(GONE);
		
		this.m_size_text.setText("0, 0");
		this.m_location_text.setText("0, 0");
		this.m_color_edit_btn.setBackgroundColor(Color.BLACK);
		this.m_color_edit_btn.setTag(Color.BLACK);
		
		this.m_size_edit_callback=null;
		this.m_location_edit_callback=null;
		this.m_color_edit_callback=null;
		this.m_zindex_edit_callback=null;
		
		if(otherPropertiesView!=null){
			otherPropertiesLayout.removeView(otherPropertiesView);
			otherPropertiesLayout.removeAllViews();
		}
	}
}
