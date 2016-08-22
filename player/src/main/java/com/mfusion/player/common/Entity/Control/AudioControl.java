package com.mfusion.player.common.Entity.Control;

import com.mfusion.player.library.Callback.Caller;
import com.mfusion.player.common.Entity.View.MyAudioView;

import android.content.Context;

public class AudioControl extends AControl{

	
	public AudioControl(Context context)
	{
		this.CreateControl(context);
	}
	@Override
	public void Release() {
		// TODO Auto-generated method stub
		this.Element=null;
	}

	@Override
	public void CreateControl(Context context) {
		// TODO Auto-generated method stub
		MyAudioView audioview=new MyAudioView(context);
		this.Element=audioview;
	}
	public void Stop() {
		// TODO Auto-generated method stub
	
		((MyAudioView)this.Element).Stop();
	}
	public void SetCallback(Caller caller) {
		// TODO Auto-generated method stub
		((MyAudioView)this.Element).caller=caller;
	}
	public void PlayFile(String mediaSourcePath) {
		// TODO Auto-generated method stub
		((MyAudioView)this.Element).PlayFile(mediaSourcePath);
	}
	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		
	}

}
