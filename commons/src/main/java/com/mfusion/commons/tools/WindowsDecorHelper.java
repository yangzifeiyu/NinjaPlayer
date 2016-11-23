package com.mfusion.commons.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by ThinkPad on 2016/10/18.
 */
public class WindowsDecorHelper {

    public static void hideBottomBar(final Window sourceWindow){

        final Runnable mHideRunnable = new Runnable() {
            @SuppressLint("InlinedApi")
            @Override
            public void run() {
                int flags;
                int curApiVersion = Build.VERSION.SDK_INT;
                // This work only for android 4.4+
                if(curApiVersion >= Build.VERSION_CODES.KITKAT){
                    // This work only for android 4.4+
                    // hide navigation bar permanently in android activity
                    // touch the screen, the navigation bar will not show
                    flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                    ;

                }else{
                    // touch the screen, the navigation bar will show
                    flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                }

                // must be executed in main thread :)
                sourceWindow.getDecorView().setSystemUiVisibility(flags);
            }
        };

        View decorView=sourceWindow.getDecorView();
        final Handler sHandler= new Handler();
        sHandler.post(mHideRunnable); // hide the navigation bar
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                sHandler.post(mHideRunnable); // hide the navigation bar
            }
        });
    }

    public static void hideSoftInputForEditText(EditText editText){

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int inType = ((EditText)view).getInputType(); // backup the input type
                ((EditText)view).setInputType(InputType.TYPE_NULL); // disable soft input
                ((EditText)view).onTouchEvent(event); // call native handler
                ((EditText)view).setInputType(inType); // restore input type

                view.setFocusableInTouchMode(true);
                return true;
            }
        });
    }

    public static void hideSoftInputInEditText(EditText editText){

        /*editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int inType = ((EditText)view).getInputType(); // backup the input type
                ((EditText)view).setInputType(InputType.TYPE_NULL); // disable soft input
                ((EditText)view).onTouchEvent(event); // call native handler
                ((EditText)view).setInputType(inType); // restore input type

                view.setFocusableInTouchMode(true);
                return true;
            }
        });*/
    }
}
