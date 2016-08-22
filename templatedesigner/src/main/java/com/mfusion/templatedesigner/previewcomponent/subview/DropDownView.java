package com.mfusion.templatedesigner.previewcomponent.subview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mfusion.templatedesigner.R;

public class DropDownView extends Button{

	private List<String> item_list;
	
	private ListView dropdown_view;
	
	private PopupWindow popup_view;
	
	private Context m_ccontext;
	
	public DropDownView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.init(context);
	}
	
	public DropDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.init(context);
	}
	
	public DropDownView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.init(context);
	}
	
	private void init(Context context){
		this.m_ccontext=context;
		this.setOnClickListener(dropListener);
	}
	
	public void setSelectList(List<String> selectList){
		this.item_list=selectList;

	}

	OnClickListener dropListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			popupSelectList();
		}
	};

	private void popupSelectList(){
		if(item_list==null||item_list.size()==0)
			return;
		
		if(popup_view!=null&&popup_view.isShowing()){
			popup_view.dismiss();
		}else {

			LinearLayout conatinerLayout=(LinearLayout)((Activity)this.m_ccontext).getLayoutInflater().inflate(R.layout.dropdown_list_view,null);

			dropdown_view= (ListView)conatinerLayout.findViewById(R.id.dropdown_listview);
			dropdown_view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Object selectedObject = dropdown_view.getAdapter().getItem(arg2);
					setText(selectedObject.toString());
					popup_view.dismiss();
				}
			});
			
			this.dropdown_view.setAdapter(new DropItemAdapter(this.m_ccontext, this.item_list));
			
			popup_view = new PopupWindow(conatinerLayout, this.getWidth(),LayoutParams.WRAP_CONTENT);

			ColorDrawable cd = new ColorDrawable();
			popup_view.setBackgroundDrawable(cd);
			popup_view.setAnimationStyle(R.style.popwin_anim_style);
			popup_view.update();
			popup_view.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popup_view.setTouchable(true); // ����popupwindow�ɵ��
			popup_view.setOutsideTouchable(true); // ����popupwindow�ⲿ�ɵ��
			popup_view.setFocusable(true); // ��ȡ����

			popup_view.showAsDropDown(this, 0,0);

			popup_view.setTouchInterceptor(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// �������popupwindow���ⲿ��popupwindowҲ����ʧ
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						popup_view.dismiss();
						return true;
					}
					return false;
				}
			});
		}
	}
	
	class DropItemAdapter extends BaseAdapter {

		Context context =null;
			
			List<String> data=null;
			
		    public DropItemAdapter(Context context,
					List<String> data) {
				
				this.context=context;
				this.data=data;
			}

			@Override  
		    public View getView(final int position, View convertView, ViewGroup parent) {  
		        String itemMap = this.getItem(position).toString();  
		        convertView = LayoutInflater.from(context).inflate(R.layout.dropdown_list_item_view, null);  
		        
		        ((TextView)convertView.findViewById(R.id.dropdown_item_text)).setText(itemMap);

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
			
			public List<String> getSourceData() {
				return this.data;
			}
		}
}
