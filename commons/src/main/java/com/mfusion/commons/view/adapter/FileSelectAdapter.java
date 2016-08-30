package com.mfusion.commons.view.adapter;

import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.nfc.cardemulation.OffHostApduService;
import android.opengl.Visibility;
import android.provider.ContactsContract.Contacts.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FileSelectAdapter extends BaseAdapter {

	Context context =null;
	
	List<? extends Map<String, ?>> data=null;
	
	int layout;
	
	String[] fromStrings;
	
	int[] toControlId;
	
	boolean mutilSelect=false;
	
    public FileSelectAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to,boolean mutilSelect) {
		
		this.context=context;
		this.data=data;
		this.layout=resource;
		this.fromStrings=from;
		this.toControlId=to;
		
		this.mutilSelect=mutilSelect;
	}

	@Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        Map itemMap = (Map)this.getItem(position);  
        convertView = LayoutInflater.from(context).inflate(layout, null);  
        
        for (int index=0;index<this.toControlId.length;index++) {
			View controlView=convertView.findViewById(this.toControlId[index]);
			if(controlView.getClass()==CheckBox.class){
				CheckBox checkBox =((CheckBox)controlView);
				
				if(mutilSelect==false){
					checkBox.setVisibility(View.GONE);
					continue;
				}
				
				Boolean isFolder=Boolean.parseBoolean(itemMap.get("isFolder").toString());
				if(isFolder){
					checkBox.setVisibility(View.GONE);
					continue;
				}
				
				checkBox.setTag(position);
				checkBox.setContentDescription(this.fromStrings[index]);
				
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton checkButton, boolean checked) {
						// TODO Auto-generated method stub
						try {
							((Map)getItem(Integer.parseInt(checkButton.getTag().toString()))).put(checkButton.getContentDescription().toString(), checked);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				});
			}else if(controlView.getClass()==TextView.class){
				((TextView)controlView).setText(itemMap.get(this.fromStrings[index].toString()).toString());
			}else if(controlView.getClass()==ImageView.class){
				((ImageView)controlView).setImageResource(Integer.parseInt(itemMap.get(this.fromStrings[index]).toString()));
			}
		}
          
        return convertView;  
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}  
	
	public List<? extends Map<String, ?>> getSourceData() {
		return this.data;
	}
}
