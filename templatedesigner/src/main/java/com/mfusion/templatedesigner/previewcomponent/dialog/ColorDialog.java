package com.mfusion.templatedesigner.previewcomponent.dialog;

import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.WindowsDecorHelper;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.templatedesigner.R;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.templatedesigner.previewcomponent.values.PropertyValues;

public class ColorDialog {
	
	Button selelctButton=null;
	
	CheckBox transCheckBox=null;
	
	int color_r=0,color_g=0,color_b=0;
	
	EditText r_editer,g_editer,b_editer;
	
	Boolean change_by_btn=false;
	
	public  Dialog createDialog(int id, final Context context, final CallbackBundle callback, int color){

		LinearLayout dialogContent = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.activity_color_selecter, null);

        selelctButton=(Button)dialogContent.findViewById(R.id.color_selected);

        transCheckBox=(CheckBox)dialogContent.findViewById(R.id.color_trans);
        
        r_editer=(EditText)dialogContent.findViewById(R.id.color_red);
		WindowsDecorHelper.hideSoftInputInEditText(r_editer);
        g_editer=(EditText)dialogContent.findViewById(R.id.color_green);
		WindowsDecorHelper.hideSoftInputInEditText(g_editer);
        b_editer=(EditText)dialogContent.findViewById(R.id.color_blue);
		WindowsDecorHelper.hideSoftInputInEditText(b_editer);
        
        refreshRGB(color);

        r_editer.addTextChangedListener(new ColorTextWatcher(r_editer, color_change));
        g_editer.addTextChangedListener(new ColorTextWatcher(g_editer, color_change));
        b_editer.addTextChangedListener(new ColorTextWatcher(b_editer, color_change));
        
        if(Color.alpha(color)!=255)
        	transCheckBox.setChecked(true);

		final RelativeLayout colorLayout=(RelativeLayout)dialogContent.findViewById(R.id.color_list_view);
		colorLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if(left!=oldLeft||right!=oldRight) {
					int width=right-left-colorLayout.getPaddingLeft()-colorLayout.getPaddingRight();
					int x_space=15,y_space=5;
					int color_block_width=(width-7*x_space)/8;
					if(color_block_width<0)
						return;

					int margin_left=0,margin_top=0;int line_count=0;
					for (Integer colorValue : PropertyValues.getColorList()) {
						Button colorBtn=new Button(context);
						bindColorBtnProperty(colorBtn,colorValue);

						RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(color_block_width,color_block_width);
						layoutParams.leftMargin=margin_left;
						layoutParams.topMargin=margin_top;

						line_count++;
						if(line_count>=8){
							margin_top+=y_space+color_block_width;
							margin_left=0;
							line_count=0;
						}else
							margin_left+=x_space+color_block_width;

						colorLayout.addView(colorBtn, layoutParams);
					}
				}
			}
		});

		SystemInfoDialog.Builder builder =new  SystemInfoDialog.Builder(context)
				.setTitle("Color Selector").setIcon(R.drawable.mf_edit)
				.setContentView(dialogContent, Gravity.CENTER_HORIZONTAL)
				.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Bundle bundle=new Bundle();
						int color_trans=255;
						if(transCheckBox.isChecked())
							color_trans=0;

						bundle.putString("color",String.valueOf(Color.argb(color_trans, color_r, color_g, color_b)));
						callback.callback(bundle);

						dialog.dismiss();
					}
				}).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});;
        Dialog dialog = builder.create();

		dialog.show();

		/*WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = context.getResources().getDisplayMetrics().widthPixels*1/3;
		params.height=context.getResources().getDisplayMetrics().heightPixels*3/5;
		dialog.getWindow().setAttributes(params);*/

        return dialog;  
    } 
	
	private void bindColorBtnProperty(Button btn,int color) {
		btn.setBackgroundColor(color);
		btn.setTag(color);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View colorView) {
				// TODO Auto-generated method stub
				int color=Integer.parseInt(colorView.getTag().toString());
				change_by_btn=true;
				refreshRGB(color);
				change_by_btn=false;
			}
		});
	}
	
	private void refreshRGB(int color) {
		
		color_r=Color.red(color);
        color_g=Color.green(color);
        color_b=Color.blue(color);
        
        r_editer.setText(String.valueOf(color_r));
        g_editer.setText(String.valueOf(color_g));
        b_editer.setText(String.valueOf(color_b));
        
        this.refreshColorDisplay();
	}
	
    CallbackBundle color_change=new CallbackBundle() {
		
		@Override
		public void callback(Bundle bundle) {
			// TODO Auto-generated method stub
			if(change_by_btn)
				return;
			
			if(r_editer.getText().toString().isEmpty()==false)
				color_r=Integer.parseInt(r_editer.getText().toString());
			if(g_editer.getText().toString().isEmpty()==false)
				color_g=Integer.parseInt(g_editer.getText().toString());
			if(b_editer.getText().toString().isEmpty()==false)
				color_b=Integer.parseInt(b_editer.getText().toString());
			
			refreshColorDisplay();
		}
	};
    
    
    private void refreshColorDisplay() {
        try {

            int rgb=Color.argb(255, color_r, color_g, color_b);

            selelctButton.setBackgroundColor(rgb);
            
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}  
    
    class ColorTextWatcher implements TextWatcher{

    	CallbackBundle edited_call;
    	
    	EditText inpuText;
    	
    	String old_value="";
    	public ColorTextWatcher(EditText view,CallbackBundle call){
    		this.edited_call=call;
    		
    		this.inpuText=view;
    	}
    	
		@Override
		public void afterTextChanged(Editable editable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			this.old_value=arg0.toString();
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			// TODO Auto-generated method stub
			try {

				if(charSequence.toString().isEmpty()){
					return;
					
				}else{
					int color=Integer.parseInt(charSequence.toString());
					if(color>255){
						int index =this.inpuText.getSelectionStart();
						this.inpuText.setText(this.old_value);
						this.inpuText.setSelection(index-count);
						
						return;
					}
				}
				
				this.edited_call.callback(null);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
    	
    }
}
