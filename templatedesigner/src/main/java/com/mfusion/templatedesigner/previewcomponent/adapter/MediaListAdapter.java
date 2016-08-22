package com.mfusion.templatedesigner.previewcomponent.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mfusion.templatedesigner.R;
import com.mfusion.templatedesigner.previewcomponent.entity.ScheduleMediaEntity;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class MediaListAdapter  extends BaseAdapter {

	Context context =null;
	
	List<ScheduleMediaEntity> data=null;
	
	int layout= R.layout.temp_comp_media_item;
	
	OnClickListener deleteListener;
	
    public MediaListAdapter(Context context,List<ScheduleMediaEntity> data,OnClickListener deleteListener) {
		
		this.context=context;
		this.data=data;
		
		this.deleteListener=deleteListener;
	}

	@Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
		ScheduleMediaEntity item = (ScheduleMediaEntity)this.getItem(position);  
        convertView = LayoutInflater.from(context).inflate(layout, null);
		convertView.setFocusable(true);
		convertView.setFocusableInTouchMode(true);

        ImageView typeImage=(ImageView)convertView.findViewById(R.id.pm_type);
        typeImage.setImageResource(PropertyValues.convertTypeToImage(item.mediaType.toString()));
        
        ((TextView)convertView.findViewById(R.id.pm_name)).setText(item.mediaName+" ("+item.durationString+")");
        ((TextView)convertView.findViewById(R.id.pm_path)).setText(item.mediaPath);
        
        EditText duration=(EditText)convertView.findViewById(R.id.pm_duration);
        duration.setText(item.durationString);
        duration.addTextChangedListener(new EditChangedListener(duration,item));
        
        ImageButton deleteButton=(ImageButton)convertView.findViewById(R.id.pm_delete);
        deleteButton.setTag(position);
		deleteButton.setFocusable(true);
        deleteButton.setOnClickListener(this.deleteListener);
          
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
	
	public List<ScheduleMediaEntity> getSourceData() {
		return this.data;
	}
	
	class EditChangedListener implements TextWatcher {  
		
		EditText inpuText;
    	
    	String old_value="";
    	
    	ScheduleMediaEntity mediaEntity;
    	
    	public EditChangedListener(EditText view,ScheduleMediaEntity item){
    		this.inpuText=view;
    		this.mediaEntity=item;
    	}
       @Override  
       public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
    	   this.old_value=s.toString();
       }  
  
       @Override  
       public void onTextChanged(CharSequence s, int start, int before, int count) {
    	   try {
    		   if(count<1)
    			   return;

        	   if(s.toString().length()>8){
        		   int selectIndex=this.inpuText.getSelectionStart();
        		   this.inpuText.setText(old_value);
       			   this.inpuText.setSelection(selectIndex-1);
       			   return;
        	   }
        	   
    		   String add_value=s.toString().substring(start,start+count).toLowerCase();
        	   if(add_value.equals("a")||add_value.equals("p")||add_value.equals("m")){
        		   int selectIndex=this.inpuText.getSelectionStart();
       			   this.inpuText.setText(old_value);
       			   this.inpuText.setSelection(selectIndex-count);
       			   return;
        	   }
        	   if(add_value.equals(":")){
        		   if (start==0||s.toString().substring(start-1,start).equals(":")) {
        			   int selectIndex=this.inpuText.getSelectionStart();
           			   this.inpuText.setText(old_value);
           			   this.inpuText.setSelection(selectIndex-count);
           			   return;
        		   }
        	   }


        	   String beforeString=s.toString().substring(0,start+1);
        	   int index=beforeString.lastIndexOf(":");
        	   if((!isSplitCharEnough(s.toString()))&&((index<0&&start==1)||(index>=0&&(start-index)==2))){
        		   this.inpuText.setText(s.toString()+":");
       			   this.inpuText.setSelection(s.toString().length()+1);
       			   return;
        	   }
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
       }  
  
       @Override  
       public void afterTextChanged(Editable s) {  
           this.mediaEntity.durationString=s.toString();
       }  
       
       private Boolean isSplitCharEnough(String source) {
    	   String[] subStr = source.split(":");
    	   if(subStr.length==3)
    		   return true;
    	   
    	   return false;
       }
   };  
}
