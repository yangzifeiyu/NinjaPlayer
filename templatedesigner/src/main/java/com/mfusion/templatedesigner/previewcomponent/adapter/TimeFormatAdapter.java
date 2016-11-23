package com.mfusion.templatedesigner.previewcomponent.adapter;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

import java.util.List;

public class TimeFormatAdapter extends BaseAdapter {

	Context context =null;

	String[] format_char_list,format_char_description_list,format_char_sample_list;

	int layout= R.layout.time_format_item_view;

    public TimeFormatAdapter(Context context) {
		
		this.context=context;

		format_char_list =context.getResources().getStringArray(R.array.time_format);
		format_char_description_list =context.getResources().getStringArray(R.array.time_format_description);
		format_char_sample_list =context.getResources().getStringArray(R.array.time_format_sample);
		
	}

	@Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  

        convertView = LayoutInflater.from(context).inflate(layout, null);

        ((TextView)convertView.findViewById(R.id.time_format_item_name)).setText(format_char_list[position]);
		((TextView)convertView.findViewById(R.id.time_format_item_descript)).setText(format_char_description_list[position]);
		((TextView)convertView.findViewById(R.id.time_format_item_sample)).setText(format_char_sample_list[position]);

        return convertView;  
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.format_char_list.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.format_char_list[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}  
}
