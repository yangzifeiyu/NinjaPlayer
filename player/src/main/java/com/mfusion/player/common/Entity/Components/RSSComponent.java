/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-13
 *
 *RSSComponent
 */
package com.mfusion.player.common.Entity.Components;

import com.mfusion.player.library.Helper.LoggerHelper;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.mfusion.player.common.Entity.Control.RSSControl;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.RSSSetting;


public class RSSComponent extends BasicComponent{
	public RSSSetting setting;
	private RSSControl m_rss_control;



	public RSSComponent(Context context) {
		super(context);

	}

	@Override
	public void End() {
		// TODO Auto-generated method stub
		try
		{
			if(m_rss_control!=null)
			{
				Helper.ControlManager.ReturnControl(this.m_rss_control);
				MainActivity.Instance.PBUDispatcher.template.removeView(container);
				if(m_rss_control.Element!=null)
				{
					this.container.removeView(m_rss_control.Element);
				}
				
				
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("RSSComponent End==>"+ex.getMessage());
		}



	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		try
		{
			this.m_rss_control =(RSSControl) Helper.ControlManager.GetControl(ControlType.MarqueeRSSText, this.setting.RSSURL);
			this.m_rss_control.Element.setVisibility(View.VISIBLE);
			this.setCmpcontext(m_rss_control.Element);
			
			
			
			this.AddView(m_rss_control.Element);
			this.m_rss_control.setSetting(setting);
			MainActivity.Instance.PBUDispatcher.template.addView(container);
			this.m_rss_control.Play();
		}
		catch(Exception ex)

		{
			LoggerHelper.WriteLogfortxt("RSSComponent Render==>"+ex.getMessage());
		}


	}


	@Override
	public void Init(Object arg) {
		// TODO Auto-generated method stub
		if(arg==null)
			return;
		try
		{
			this.setting = (RSSSetting) arg;
			int color = setting.BackColor;
			this.container.setBackgroundColor(color);

			Helper.RSS.Register(setting.RSSURL);

		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TickerTextComponent Init==>"+ex.getMessage());
		}

	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}


}
