
package com.mfusion.player.common.Entity.Effect;



import com.mfusion.player.common.Enum.TransitionEffectType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;


public class TransitionEffectHelper {


	long durationMillis = 2000, delayMillis = 0;

	private TransitionEffectController transitionController;
	public TransitionEffectHelper()
	{
		transitionController=new TransitionEffectController();
	}

	public  Bitmap convertViewToBitmap(View view,int width,int height)
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));
		return bitmap;
	}
	public void SetAnimation(TransitionEffectType type, View view,View peview) {
		// TODO Auto-generated method stub

		switch (type) 
		{
		case fadein:
			transitionController.fadeIn(view,peview, durationMillis, delayMillis);
			break;

		case fadeout:
			transitionController.fadeOut(view,peview, durationMillis,delayMillis);
		break;


		case slidein:
			transitionController.slideIn(view,peview, durationMillis, delayMillis);		
			break;


		case slideout:
			transitionController.slideOut(view,peview, durationMillis,delayMillis);
			break;


		case scalein: 
			transitionController.scaleIn(view,peview, durationMillis,delayMillis);
			break;
			
		case scaleout: 
			transitionController.scaleOut(view,peview, durationMillis,delayMillis);
			break;


		case rotatein:
			transitionController.rotateIn(view,peview,  durationMillis, delayMillis);
			break;


		case rotateout: 
			transitionController.rotateOut(view, peview,durationMillis,delayMillis); break;


		case scalerotatein:
			transitionController.scaleRotateIn(view,peview,  durationMillis, delayMillis);
			break;


		case scalerotateout: 
			transitionController.scaleRotateOut(view,peview,durationMillis, delayMillis);
			break;

		case slidefadeout: 
			transitionController.slideFadeOut(view,peview,durationMillis, delayMillis);
			break;
			
		case slidefadein:
			transitionController.slideFadeIn(view,peview,  durationMillis, delayMillis);
			break;
		case none:	
		case plnone:
			peview.setVisibility(View.INVISIBLE);
			break;

		default:
			break;



		}
	}
}
