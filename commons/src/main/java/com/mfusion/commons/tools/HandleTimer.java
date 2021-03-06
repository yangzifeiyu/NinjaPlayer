/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *�Զ���timer
 */
package com.mfusion.commons.tools;

import android.os.Handler;
import android.os.Looper;

public abstract class HandleTimer {
	private Handler mHandler = new Handler(Looper.getMainLooper());

	private Runnable mRunnable;

	/**
	 * Create a new HandleTimer for update UI thread.
	 */
	public HandleTimer() {
	}

	/**
	 * Start timer. <br>
	 * If timer has been started, stop first. <br>
	 * 
	 * @param period
	 *            period time
	 */
	public synchronized void restart(final long period) {
		restart(0, period);
	}

	/**
	 * Start timer. <br>
	 * If timer has been started, stop first. <br>
	 * 
	 * @param delay
	 *            delay time
	 * @param period
	 *            period time
	 */
	public synchronized void restart(long delay, final long period) {
		stop();
		start(delay, period);
	}

	/**
	 * Start timer. <br>
	 * If timer has been started, do nothing.
	 * 
	 * @param period
	 *            period time
	 */
	public synchronized void start(final long period) {
		start(0, period);
	}

	/**
	 * Start timer. <br>
	 * If timer has been started, do nothing.
	 * 
	 * @param delay
	 *            delay time
	 * @param period
	 *            period time
	 */
	public synchronized void start(long delay, final long period) {
		if (mRunnable != null||mHandler==null) {
			return;
		}
		try {

			mRunnable = new Runnable() {
				public void run() {
					if(mHandler!=null)
						mHandler.postDelayed(this, period);
					onTime();
				}
			};
			mHandler.postDelayed(mRunnable, delay);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Stop timer.
	 */
	public synchronized void stop() {
		try{

			if (mRunnable != null) {
				mHandler.removeCallbacks(mRunnable);
				mRunnable = null;
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public synchronized void release() {
		stop();
		mHandler=null;
	}
	/**
	 * Call back function on time.
	 */
	protected abstract void onTime();
}