/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-13
 *
 *Interactive Component
 */
package com.mfusion.player.common.Entity.Components;
import com.mfusion.player.common.Entity.Control.ErrorControl;
import com.mfusion.player.common.Entity.Control.VideoControl1;
import com.mfusion.player.library.Controller.HandleTimer;
import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.StreamingSetting;
import android.content.Context;
import android.view.View;



public class StreamingComponent extends BasicComponent {

	public StreamingSetting setting;
	private VideoControl1 videoControl ;
	private ErrorControl errorControl ;
	private HandleTimer timer;    
	public StreamingComponent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		try
		{
			videoControl=(VideoControl1) Helper.ControlManager.GetControl(ControlType.Video);
			errorControl=(ErrorControl) Helper.ControlManager.GetControl(ControlType.Error);
			this.setCmpcontext(videoControl.Element);
			this.setCmpcontext(errorControl.Element);

			
			timer = new HandleTimer() {
				@Override
				protected void onTime() {
					playTimer_Tick();
				}
			};
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("StreamingComponent==>"+ex.getMessage());
		}
	}


	protected void playTimer_Tick() {
		// TODO Auto-generated method stub
		timer.stop();
		boolean result=this.videoControl.LoadVideo(setting.Url,setting.Mute);
		if(result==false)
		{
			this.container.bringChildToFront(errorControl.Element);
			errorControl.Element.setVisibility(View.VISIBLE);
		}

	}
	@Override
	public void End() {
		try
		{
			this.videoControl.Stop();
			// TODO Auto-generated method stub
			Helper.ControlManager.ReturnControl(this.videoControl);
			Helper.ControlManager.ReturnControl(this.errorControl);

			MainActivity.Instance.PBUDispatcher.template.removeView(container);
			this.container.removeView(this.videoControl.Element);
			this.container.removeView(this.errorControl.Element);
			
		
			
		}
		catch(Exception ex)
		{

		}
	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
	
		try
		{
			this.AddView(this.errorControl.Element);
			this.AddView(this.videoControl.Element);
			MainActivity.Instance.PBUDispatcher.template.addView(container);	

			this.container.bringChildToFront(videoControl.Element);
			videoControl.Element.setVisibility(View.VISIBLE);

			if(this.setting!=null)
				this.timer.start(0);
				
			
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
