/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-13
 *
 *TickerText
 */
package com.mfusion.player.common.Entity.Components;

import com.mfusion.player.library.Helper.LoggerHelper;
import android.content.Context;
import com.mfusion.player.common.Entity.View.MarqueeTextSurfaceView;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.TickerTextSetting;

public class TickerTextComponent extends BasicComponent {
	public TickerTextSetting setting;
	private MarqueeTextSurfaceView m_Ticker;


	public TickerTextComponent(Context context) {
		super(context);
		try
		{
			this.m_Ticker = new MarqueeTextSurfaceView(context);
			this.setCmpcontext(m_Ticker);
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TickerTextComponent==>"+ex.getMessage());
		}
	}

	@Override
	public void End() {
		// TODO Auto-generated method stub
		this.m_Ticker.stopScroll();
		MainActivity.Instance.PBUDispatcher.template.removeView(container);
		this.container.removeView(this.m_Ticker);
	
		
	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		try
		{
			this.AddView(this.m_Ticker);
			MainActivity.Instance.PBUDispatcher.template.addView(container);

			this.m_Ticker.setSetting(setting);
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TickerTextComponent Render==>"+ex.getMessage());
		}
	}

	
	@Override
	public void Init(Object arg) {
		// TODO Auto-generated method stub
		if(arg==null)
			return;
		try
		{
			this.setting = (TickerTextSetting) arg;
			int color = setting.BackColor;
			this.container.setBackgroundColor(color);
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TickerTextComponent Init==>"+ex.getMessage());
		}

	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		m_Ticker.setTop();
	}
}
