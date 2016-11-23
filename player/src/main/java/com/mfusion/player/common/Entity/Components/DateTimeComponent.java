/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-13
 *
 *Datetime
 */
package com.mfusion.player.common.Entity.Components;

import java.util.Date;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.DateTimeSetting;

public class DateTimeComponent extends BasicComponent {
	public DateTimeSetting setting;
	private TextView m_Date;
	private int interval = 1000;
	private HandleTimer timer= new HandleTimer() {
		@Override
		protected void onTime() {
			FormatDateTime(setting.Format);
		}
	};

	@SuppressLint("InlinedApi")
	public DateTimeComponent(Context context) {
		super(context);
		try
		{
			this.m_Date= new TextView(context);
			this.m_Date.setGravity(Gravity.CENTER);
			this.m_Date.setSingleLine(true);
			this.m_Date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			this.setCmpcontext(m_Date);
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DateTimeComponent Init==>"+ex.getMessage());
		}

	}

	private void FormatDateTime(String format) {

		try
		{
			
			Date date = MainActivity.Instance.Clock.Now;
			String datestring = DateTimeHelper.ConvertToString(date, format);
			this.m_Date.setText(datestring);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DateTimeComponent FormatDateTime==>"+ex.getMessage());
		}
	}

	@Override
	public void End() {
		try
		{
			
			this.timer.stop();
			MainActivity.Instance.PBUDispatcher.template.removeView(container);
			this.container.removeView(m_Date);
			
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DateTimeComponent Stop==>"+ex.getMessage());
		}

	}

	@Override
	public void Render() {
		
		
		try
		{	
			this.AddView(m_Date);
			MainActivity.Instance.PBUDispatcher.template.addView(container);
			this.timer.start(interval);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DateTimeComponent Render==>"+ex.getMessage());
		}
	}

	@Override
	public void Init(Object o) {
		// TODO Auto-generated method stub
		if(o==null)
			return;
		try
		{
			this.setting = (DateTimeSetting) o;
			int color = setting.BackColor;
			this.container.setBackgroundColor(color);
			this.m_Date.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.setting.TextProperty.FontSize);//��ptΪ��λ
			this.m_Date.setTypeface(this.setting.TextProperty.FontStyle);
			this.m_Date.setTextColor(this.setting.TextProperty.FontColor);
		
			
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DateTimeComponent Init==>"+ex.getMessage());
		}

	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}
}
