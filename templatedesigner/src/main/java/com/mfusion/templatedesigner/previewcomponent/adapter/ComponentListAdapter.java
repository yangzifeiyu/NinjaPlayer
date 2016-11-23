package com.mfusion.templatedesigner.previewcomponent.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class ComponentListAdapter  extends BaseAdapter {

	Context context =null;
	
	List<String> comp_list=null;
	
	int layout= R.layout.comp_list_item;
	
    public ComponentListAdapter(Context context,List<String> components) {
		
		this.context=context;
		
		this.comp_list=components;
		
	}

	@Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
		String comp_name = (String)this.getItem(position);  
        convertView = LayoutInflater.from(context).inflate(layout, null);
        convertView.setTag(comp_name);
        
        ((ImageView)convertView.findViewById(R.id.comp_type_img)).setImageResource(PropertyValues.getImageFotComponent(this.context.getResources(),comp_name));
        
        ((TextView)convertView.findViewById(R.id.comp_type)).setText(comp_name);

        convertView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:

            		ClipData.Item item = new ClipData.Item(view.getTag().toString());	
    				
    				ClipData data = new ClipData("move", new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN }, item);
    				
    				//����startDrag�������ڶ�������Ϊ�����Ϸ���Ӱ 
    				view.startDrag(data, new View.DragShadowBuilder(view), null, 0);
    				
                    return true;
				}
				return false;
			}
		});
        return convertView;  
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.comp_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.comp_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}  
}
