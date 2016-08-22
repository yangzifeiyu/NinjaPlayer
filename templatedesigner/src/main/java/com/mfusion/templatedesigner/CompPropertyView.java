package com.mfusion.templatedesigner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.BGComponentView;
import com.mfusion.templatedesigner.previewcomponent.BasicComponentView;
import com.mfusion.templatedesigner.previewcomponent.dialog.*;

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
	
	private LinearLayout otherPropertiesLayout=null;
	
	private View otherPropertiesView=null;
	
	public CompPropertyView(Context context) {
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
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Dialog dialog =(new ColorDialog()).createDialog(0, m_context, new CallbackBundle() {  
	                @Override  
	                public void callback(Bundle bundle) {  
	                    String colorString = bundle.getString("color");  
	                    m_color_edit_btn.setBackgroundColor(Integer.parseInt(colorString));
	                    m_color_edit_btn.setTag(colorString);
	                    if(m_color_edit_callback!=null)
	                    	m_color_edit_callback.callback(bundle);
	                }  
	            }, Integer.valueOf(m_color_edit_btn.getTag().toString()));

			}
		});
		this.m_zindex_up_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_zindex_edit_callback!=null){
					Bundle resultBundle=new Bundle();
					resultBundle.putInt("ZIndex", 1);
					m_zindex_edit_callback.callback(resultBundle);
				}
			}
		});
		this.m_zindex_down_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_zindex_edit_callback!=null){
					Bundle resultBundle=new Bundle();
					resultBundle.putInt("ZIndex", -1);
					m_zindex_edit_callback.callback(resultBundle);
				}
			}
		});
		
		m_bgimage_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_bgimage_edit_callback!=null){
					Dialog dialog = OpenFileDialog.createDialog(m_context, "Select a background image", new CallbackBundle() {
		                @Override  
		                public void callback(Bundle bundle) { 
		                    m_bgimage_edit_callback.callback(bundle);
		                }  
		            },   
		            "Image;",false);
				}
			}
		});
		
		otherPropertiesLayout=(LinearLayout)propertiesLayout.findViewById(R.id.temp_comp_other_properties);
	}
	
	public void bingingBasicProperties(BasicComponentView compView) {
		
		if(compView.getClass()==BGComponentView.class){
			this.m_location_layout.setVisibility(GONE);
			this.m_zindex_layout.setVisibility(GONE);
			this.m_bgimage_layout.setVisibility(VISIBLE);
			this.m_bgimage_edit_callback=((BGComponentView)compView).ImageChangedCallback;
		}else {
			this.m_location_layout.setVisibility(VISIBLE);
			this.m_zindex_layout.setVisibility(VISIBLE);
			this.m_bgimage_layout.setVisibility(GONE);
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
