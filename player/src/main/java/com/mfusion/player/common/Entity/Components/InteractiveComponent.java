/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-13
 *
 *Interactive Component
 */
package com.mfusion.player.common.Entity.Components;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.InteractiveSetting;
import com.mfusion.player.common.Entity.Control.WebControl;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;



public class InteractiveComponent extends BasicComponent {

	public InteractiveSetting setting;
	private WebControl WebControl ;
	@SuppressLint("SetJavaScriptEnabled")
	public InteractiveComponent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		try
		{
			WebControl=(WebControl) Helper.ControlManager.GetControl(ControlType.WebPage);
			this.setCmpcontext(WebControl.Element);
			//this.container.addView(WebControl.Element);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("InteractiveComponent==>"+ex.getMessage());
		}
	}

	@Override
	public void End() {
		// TODO Auto-generated method stub
		try
		{
			WebControl.Stop();
			Helper.ControlManager.ReturnControl(this.WebControl);
			MainActivity.Instance.PBUDispatcher.template.removeView(container);
			this.container.removeView(this.WebControl.Element);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("InteractiveComponent End==>"+ex.getStackTrace());
		}

	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		try
		{
			this.WebControl.Element.setVisibility(View.VISIBLE);
			this.AddView(this.WebControl.Element);
			MainActivity.Instance.PBUDispatcher.template.addView(container);
			if(this.setting!=null)
				this.WebControl.LoadUrl(setting.Web_Url);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("InteractiveComponent Render==>"+ex.getMessage());
		}
	}

	@Override
	public void Init(Object o) {
		try
		{
			// TODO Auto-generated method stub
			if(o==null)
				return;
			this.setting=(InteractiveSetting)o;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("InteractvieComponent Init==>"+ex.getMessage());
		}
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub

	}

}
