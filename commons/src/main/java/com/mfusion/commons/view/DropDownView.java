package com.mfusion.commons.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commontools.R;

public class DropDownView extends Button{

	public enum DropDownLocationType{
		left,
		top,
		right,
		bottom
	}
	private List<String> item_list;
	
	private ListView dropdown_view;
	
	private PopupWindow popup_view;

	private DropDownLocationType popup_location_type=DropDownLocationType.bottom;

	private Paint paint=new Paint();

	private Context m_context;

	private int max_height_popup_window=0;

	private OnSelectTextChangedListener textChangedListener;

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
		this.m_context=context;
		this.setGravity(Gravity.LEFT|Gravity.CENTER);
		this.setPadding((int)getResources().getDimension(R.dimen.global_margin_medium),0,(int)getResources().getDimension(R.dimen.global_margin_medium),0);
		this.setOnClickListener(dropListener);

		this.max_height_popup_window=context.getResources().getDisplayMetrics().heightPixels*2/3;
	}

	public void setOnChangeListener(OnSelectTextChangedListener listener) {
		textChangedListener = listener;
	}

	public void setSelectList(List<String> selectList){
		this.item_list=selectList;

	}

	public void setSelectList(List<String> selectList,DropDownLocationType dropDownType){
		this.item_list=selectList;
		this.popup_location_type=dropDownType;
	}

	OnClickListener dropListener=new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			getParent().requestChildFocus(DropDownView.this,DropDownView.this);
			popupSelectList();
		}
	};

	private void popupSelectList(){
		if(item_list==null||item_list.size()==0)
			return;
		
		if(popup_view!=null&&popup_view.isShowing()){
			popup_view.dismiss();
		}else {

			LinearLayout containerLayout=(LinearLayout)((Activity)this.m_context).getLayoutInflater().inflate(R.layout.dropdown_list_view,null);

			dropdown_view= (ListView)containerLayout.findViewById(R.id.dropdown_listview);
			dropdown_view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Object selectedObject = dropdown_view.getAdapter().getItem(arg2);
					setText(selectedObject.toString());
					if(textChangedListener!=null)
						textChangedListener.onSelectTextChange(selectedObject.toString());
					popup_view.dismiss();
				}
			});
			this.dropdown_view.setAdapter(new DropItemAdapter(this.m_context, this.item_list));

			int popup_width=(int)getPopupWindowWith();
			int popup_height=(int)(this.item_list.size()*(paint.getFontMetrics().bottom-paint.getFontMetrics().top+getResources().getDimension(R.dimen.list_view_border)*2 +getResources().getDimension(R.dimen.dropdown_margin)*2)+getResources().getDimension(R.dimen.border_padding)*2);
			popup_height=this.max_height_popup_window>popup_height?popup_height:this.max_height_popup_window;
			popup_view = new PopupWindow(containerLayout, popup_width,popup_height);
			//popup_view = new PopupWindow(containerLayout, popup_width,LayoutParams.WRAP_CONTENT);

			ColorDrawable cd = new ColorDrawable();
			popup_view.setBackgroundDrawable(cd);
			popup_view.setAnimationStyle(R.style.popwin_anim_style);
			popup_view.update();
			popup_view.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			popup_view.setTouchable(true); // ����popupwindow�ɵ��
			popup_view.setOutsideTouchable(true); // ����popupwindow�ⲿ�ɵ��
			popup_view.setFocusable(true); // ��ȡ����

			if(popup_location_type==DropDownLocationType.bottom){
				popup_view.showAsDropDown(this, 0, 0);
			}else {
				int[] location = new int[2];
				this.getLocationOnScreen(location);
				if (popup_location_type == DropDownLocationType.top)
					popup_view.showAsDropDown(this, 0, -popup_height-this.getHeight());
					//popup_view.showAtLocation(this, Gravity.NO_GRAVITY, location[0], location[1] - popup_height);
				else if (popup_location_type == DropDownLocationType.left)
					popup_view.showAtLocation(this, Gravity.NO_GRAVITY, location[0] - popup_width, location[1]);
				else if (popup_location_type == DropDownLocationType.right)
					popup_view.showAtLocation(this, Gravity.NO_GRAVITY, location[0] + this.getWidth(), location[1]);
			}

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

	private float getPopupWindowWith(){
		float popup_width=0;

		paint.setTextSize(this.getTextSize());
		for(String item :this.item_list){
			float current_length=paint.measureText(item);
			popup_width=popup_width>current_length?popup_width:current_length;
		}
		popup_width+=+(getResources().getDimension(R.dimen.border_padding))*2+this.getPaddingLeft()+this.getPaddingRight()+(getResources().getDimension(R.dimen.dropdown_margin))*2;
		popup_width = popup_width>getWidth()?popup_width:getWidth();
		/*LayoutParams layoutParams=this.getLayoutParams();
		layoutParams.width=(int)popup_width;
		this.setLayoutParams(layoutParams);*/
		return popup_width;
	}

	public interface OnSelectTextChangedListener{
		public void onSelectTextChange(String selectText);
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
