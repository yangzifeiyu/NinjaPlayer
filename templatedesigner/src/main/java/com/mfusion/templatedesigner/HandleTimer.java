
package com.mfusion.templatedesigner;

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
		if (mRunnable != null) {
			return;
		}
		mRunnable = new Runnable() {
			public void run() {
				mHandler.postDelayed(this, period);
				onTime();
			}
		};
		mHandler.postDelayed(mRunnable, delay);
	}

	/**
	 * Stop timer.
	 */
	public synchronized void stop() {
		if (mRunnable != null) {
			mHandler.removeCallbacks(mRunnable);
			mRunnable = null;
		}
	}

	/**
	 * Call back function on time.
	 */
	protected abstract void onTime();
}