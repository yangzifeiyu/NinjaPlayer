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
import com.mfusion.player.common.Entity.Control.VideoControl;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.StreamingSetting;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;



public class StreamingComponent1 extends BasicComponent {

	public StreamingSetting setting;
	private VideoControl videoControl ;
	//private common.Entity.Control.ErrorControl errorControl ;
	@SuppressLint("SetJavaScriptEnabled")
	public StreamingComponent1(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		try
		{
			videoControl=new VideoControl(context);
			//errorControl=(common.Entity.Control.ErrorControl) Helper.ControlManager.GetControl(ControlType.Error);
			this.setCmpcontext(videoControl.Element);
			//this.setCmpcontext(errorControl.Element);
			
			//this.container.addView(this.errorControl.Element);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("StreamingComponent==>"+ex.getMessage());
		}
	}

	@Override
	public void End() {
		try
		{
			this.videoControl.Stop();
			// TODO Auto-generated method stub
			/*Helper.ControlManager.ReturnControl(this.videoControl);
			Helper.ControlManager.ReturnControl(this.errorControl);
			
			this.container.removeView(this.videoControl.Element);
			this.container.removeView(this.errorControl.Element);*/
			this.container.removeView(this.videoControl.Element);
		}
		catch(Exception ex)
		{
   
		}
	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		boolean result=false;
		try
		{
		
			this.AddView(this.videoControl.Element);
			MainActivity.Instance.PBUDispatcher.template.addView(container);	
			
			this.container.bringChildToFront(videoControl.Element);
			videoControl.Element.setVisibility(View.VISIBLE);
			
			if(this.setting!=null)
				this.videoControl.LoadVideo(setting.Url,setting.Mute);
			/*if(result==false)
			{
				this.container.bringChildToFront(errorControl.Element);
				errorControl.Element.setVisibility(View.VISIBLE);
			}*/
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("StreamingComponent Render==>"+ex.getMessage());
		}
	}

	@Override
	public void Init(Object o) {
		try
		{
			// TODO Auto-generated method stub
			if(o==null)
				return;
			this.setting=(StreamingSetting)o;
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("StreamingComponent Init==>"+ex.getMessage());
		}
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub

	}

}
