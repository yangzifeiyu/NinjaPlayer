package com.mfusion.player.common.Entity.Control;



import com.mfusion.player.common.Entity.View.CustomerVideoSurfaceView;


import android.content.Context;
import android.view.View;



public class VideoControl1 extends AControl{


	public VideoControl1(Context context)
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
		
		CustomerVideoSurfaceView view =new CustomerVideoSurfaceView(context);
		view.setVisibility(View.INVISIBLE);
		
		this.Element=view;
	}
	
	public boolean LoadVideo(String videopath,boolean mute)
	{
		boolean result=((CustomerVideoSurfaceView)this.Element).setVideo(videopath,mute);
		return result;
		
	}
	public void Stop()
	{
		((CustomerVideoSurfaceView)this.Element).stop();
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		((CustomerVideoSurfaceView)this.Element).setTop();
	}
	
	
}
