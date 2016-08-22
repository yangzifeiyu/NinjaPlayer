package com.mfusion.player.common.Entity.Control;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class ErrorControl extends AControl{

	
	public ErrorControl(Context context)
	{
		this.CreateControl(context);
	}
	@Override
	public void Release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void CreateControl(Context context) {
		// TODO Auto-generated method stub
		TextView errorText=new TextView(context);
		errorText.setGravity(Gravity.CENTER);
		errorText.setTextColor(Color.WHITE);
		errorText.setText("Can't play the mediafile.");
		errorText.setVisibility(View.INVISIBLE);
		this.Element=errorText;
	}
	
	public void SetErrorMessage(String message){
		((TextView)this.Element).setText("Can't play the mediafile("+message+")");
	}
	
	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}

}
