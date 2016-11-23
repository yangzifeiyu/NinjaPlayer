package com.mfusion.commons.view;

import android.os.Handler;
import android.os.Message;

/**
 * Created by ThinkPad on 2016/9/6.
 */
public class SwitchCheckBoxAnimation {
    private static  final int MSG_ANIMATE = 1000;

    public static final int ANIMATION_FRAME_DURATION = 1000 / 60;

    private static final Handler mHandler = new AnimationHandler();

    public static void requestAnimationFrame(Runnable runnable) {
        Message message = new Message();
        message.what = MSG_ANIMATE;
        message.obj = runnable;
        mHandler.sendMessageDelayed(message, ANIMATION_FRAME_DURATION);
    }

    public static void requestFrameDelay(Runnable runnable, long delay) {
        Message message = new Message();
        message.what = MSG_ANIMATE;
        message.obj = runnable;
        mHandler.sendMessageDelayed(message, delay);
    }

    private static class AnimationHandler extends Handler {
        public void handleMessage(Message m) {
            switch (m.what) {
                case MSG_ANIMATE:
                    if (m.obj != null) {
                        ((Runnable) m.obj).run();
                    }
                    break;
            }
        }
    }
}
