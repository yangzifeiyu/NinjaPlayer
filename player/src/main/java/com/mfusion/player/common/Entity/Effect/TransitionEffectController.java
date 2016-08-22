/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *TransitionEffectController
 */
package com.mfusion.player.common.Entity.Effect;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class TransitionEffectController
{
	public final int rela1 = Animation.RELATIVE_TO_SELF;
	public final int rela2 = Animation.RELATIVE_TO_PARENT;
	public final int Default = -1;
	public final int Linear = 0;
	public final int Accelerate = 1;
	public final int Decelerate = 2;
	public final int AccelerateDecelerate = 3;
	public final int Bounce = 4;
	public final int Overshoot = 5;
	public final int Anticipate = 6;
	public final int AnticipateOvershoot = 7;

	

	public TransitionEffectController()
	{
	}

	private class MyAnimationListener implements AnimationListener
	{
		
		
		private View m_preview;

		public MyAnimationListener(View view,View preview)
		{
			
			this.m_preview=preview;
		}

		

		@Override
		public void onAnimationEnd(Animation animation)
		{
			this.m_preview.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{
		}



		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

	}

	private void setEffect(Animation animation, int interpolatorType, long durationMillis, long delayMillis)
	{
		switch (interpolatorType)
		{
		case 0:
			animation.setInterpolator(new LinearInterpolator());
			break;
		case 1:
			animation.setInterpolator(new AccelerateInterpolator());
			break;
		case 2:
			animation.setInterpolator(new DecelerateInterpolator());
			break;
		case 3:
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			break;
		case 4:
			animation.setInterpolator(new BounceInterpolator());
			break;
		case 5:
			animation.setInterpolator(new OvershootInterpolator());
			break;
		case 6:
			animation.setInterpolator(new AnticipateInterpolator());
			break;
		case 7:
			animation.setInterpolator(new AnticipateOvershootInterpolator());
			break;
		default:
			break;
		}
		animation.setDuration(durationMillis);
		animation.setStartOffset(delayMillis);
	}

	private void baseIn(View view,View peview, Animation animation, long durationMillis, long delayMillis)
	{
		setEffect(animation, Default, durationMillis, delayMillis);
		animation.setAnimationListener(new MyAnimationListener(view,peview));
		view.startAnimation(animation);
	}

	private void baseOut(View view,View peview, Animation animation, long durationMillis, long delayMillis)
	{
		setEffect(animation, Default, durationMillis, delayMillis);
		animation.setAnimationListener(new MyAnimationListener(view,peview));
		view.startAnimation(animation);
	}
	
	public void show(View view)
	{
		view.setVisibility(View.VISIBLE);
	}
	public void hide(View view)
	{
		view.setVisibility(View.GONE);
	}
	public void transparent(View view)
	{
		view.setVisibility(View.INVISIBLE);
	}
	
	public void fadeIn(View view,View peview, long durationMillis, long delayMillis)
	{
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		baseIn(view,peview, animation, durationMillis, delayMillis);
	}

	public void fadeOut(View view,View peview, long durationMillis, long delayMillis)
	{
		AlphaAnimation animation = new AlphaAnimation(1, 0);
		baseOut(view,peview,animation, durationMillis, delayMillis);
	}

	public void slideIn(View view,View peview, long durationMillis, long delayMillis)
	{
		TranslateAnimation animation = new TranslateAnimation(rela2, 1, rela2, 0, rela2, 0, rela2, 0);
		baseIn(view,peview, animation, durationMillis, delayMillis);
	}

	public void slideOut(View view,View peview, long durationMillis, long delayMillis)
	{
		TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, -1, rela2, 0, rela2, 0);
		baseOut(view,peview, animation, durationMillis, delayMillis);
	}

	public void scaleIn(View view,View peview, long durationMillis, long delayMillis)
	{
		ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, rela2, 0.5f, rela2, 0.5f);
		baseIn(view,peview, animation, durationMillis, delayMillis);
	}

	public void scaleOut(View view,View peview, long durationMillis, long delayMillis)
	{
		ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, rela2, 0.5f, rela2, 0.5f);
		baseOut(view,peview, animation, durationMillis, delayMillis);
	}

	public void rotateIn(View view,View peview, long durationMillis, long delayMillis)
	{
		RotateAnimation animation = new RotateAnimation(-90, 0, rela1, 0, rela1, 1);
		baseIn(view, peview,animation, durationMillis, delayMillis);
	}

	public void rotateOut(View view, View peview,long durationMillis, long delayMillis)
	{
		RotateAnimation animation = new RotateAnimation(0, 90, rela1, 0, rela1, 1);
		baseOut(view,peview, animation, durationMillis, delayMillis);
	}

	public void scaleRotateIn(View view,View peview, long durationMillis, long delayMillis)
	{
		ScaleAnimation animation1 = new ScaleAnimation(0, 1, 0, 1, rela1, 0.5f, rela1, 0.5f);
		RotateAnimation animation2 = new RotateAnimation(0, 360, rela1, 0.5f, rela1, 0.5f);
		AnimationSet animation = new AnimationSet(false);
		animation.addAnimation(animation1);
		animation.addAnimation(animation2);
		baseIn(view,peview, animation, durationMillis, delayMillis);
	}

	public void scaleRotateOut(View view,View peview, long durationMillis, long delayMillis)
	{
		ScaleAnimation animation1 = new ScaleAnimation(1, 0, 1, 0, rela1, 0.5f, rela1, 0.5f);
		RotateAnimation animation2 = new RotateAnimation(0, 360, rela1, 0.5f, rela1, 0.5f);
		AnimationSet animation = new AnimationSet(false);
		animation.addAnimation(animation1);
		animation.addAnimation(animation2);
		baseOut(view, peview,animation, durationMillis, delayMillis);
	}

	public void slideFadeIn(View view,View peview, long durationMillis, long delayMillis)
	{
		TranslateAnimation animation1 = new TranslateAnimation(rela2, 1, rela2, 0, rela2, 0, rela2, 0);
		AlphaAnimation animation2 = new AlphaAnimation(0, 1);
		AnimationSet animation = new AnimationSet(false);
		animation.addAnimation(animation1);
		animation.addAnimation(animation2);
		baseIn(view,peview, animation, durationMillis, delayMillis);
	}

	public void slideFadeOut(View view, View peview,long durationMillis, long delayMillis)
	{
		TranslateAnimation animation1 = new TranslateAnimation(rela2, 0, rela2, -1, rela2, 0, rela2, 0);
		AlphaAnimation animation2 = new AlphaAnimation(1, 0);
		AnimationSet animation = new AnimationSet(false);
		animation.addAnimation(animation1);
		animation.addAnimation(animation2);
		baseOut(view,peview, animation, durationMillis, delayMillis);
	}
}
