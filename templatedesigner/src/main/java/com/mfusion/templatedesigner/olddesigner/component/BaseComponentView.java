package com.mfusion.templatedesigner.olddesigner.component;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.templatedesigner.olddesigner.EditPropertyDialog;
import com.mfusion.templatedesigner.olddesigner.EventDispatcher;
import com.mfusion.templatedesigner.R;

/**
 * Created by jimmy on 7/12/2016.
 */
public class BaseComponentView extends View{
    private static final String TAG = "BaseComponentView";
    private static final int EDITING_POINT_OFFSET=30;//in px
    private static final int EDITING_TOP_LEFT_POINT=99;
    private static final int EDITING_TOP_EDGE=98;
    private static final int EDITING_TOP_RIGHT_POINT=97;
    private static final int EDITING_RIGHT_EDGE=96;
    private static final int EDITING_RIGHT_BOTTOM_POINT=95;
    private static final int EDITING_BOTTOM_EDGE=94;
    private static final int EDITING_BOTTOM_LEFT=93;
    private static final int EDITING_LEFT_EDGE=92;
    private static final int EDITING_CENTER =-1;

    private static final int MIN_COMPONENT_SIZE=100;
    private static final int DOUBLE_CLICK_THRESHOLD=500;

    private static final int DELETE_BTN_CLICKED=50;
    private static final int EDIT_BTN_CLICKED=51;
    private static final int INVALID=999;


    protected ComponentEntity componentEntity;
    private FrameLayout.LayoutParams layoutParams;
    private FrameLayout parentDesigner;
    private int parentLeft;
    private int parentTop;
    private int parentRight;
    private int parentBottom;
    


    private boolean editing;
    private int resizingPoint;
    private boolean ignoreMoveFlag;
    private float dX;
    private float dY;


    protected Paint borderPaint;
    private Paint resizeIndicatorOuter;
    private static final int RESIZE_INDICATOR_OUTER_LENGTH=20;
    private Paint resizeIndicatorInner;
    private static final int RESIZE_INDICATOR_INNER_LENGTH=15;
    private Paint btnPaint;
    private Bitmap deleteBtnBmp;
    private Bitmap editBtnBmp;
    private Rect btnDeleteSizeRect;
    private Rect btnEditSizeRect;




    private static final float BTN_BMP_SIZE_RATIO =0.1f;
    private static final int BTN_BMP_MAX_SIZE=50;
    private static final int BTN_BMP_MIN_SIZE=30;

    private int oTopMargin;
    private int oHeight;
    private float oRawX;
    private float oRawY;
    private int oLeftMargin;
    private int oRightMargin;
    private int oBottomMargin;
    private int oWidth;
    private int bmpSize;

    public BaseComponentView(Context context) {
        this(context,null);
    }

    public BaseComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderPaint=new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(5.f);
        borderPaint.setStyle(Paint.Style.STROKE);

        resizeIndicatorOuter=new Paint();
        resizeIndicatorOuter.setColor(Color.WHITE);

        resizeIndicatorInner=new Paint();
        resizeIndicatorInner.setColor(Color.BLACK);

        deleteBtnBmp= BitmapFactory.decodeResource(getResources(), R.drawable.btn_close);
        //deleteBtnBmp=Bitmap.createScaledBitmap(deleteBtnBmp, BTN_BMP_SIZE, BTN_BMP_SIZE,false);
        editBtnBmp=BitmapFactory.decodeResource(getResources(),R.drawable.btn_edit);
        //editBtnBmp=Bitmap.createScaledBitmap(editBtnBmp,BTN_BMP_SIZE,BTN_BMP_SIZE,false);
        btnDeleteSizeRect=new Rect();
        btnEditSizeRect=new Rect();
        btnPaint =new Paint();


    }
    public void setComponentEntity(ComponentEntity entity){

        this.componentEntity=entity;
        setBackgroundColor(componentEntity.backColor);
        this.setAlpha(0.5f);
        setMeasuredDimension(componentEntity.width,componentEntity.height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: fired");
        Log.i(TAG, "onDraw: editing="+ editing);
        if(editing)
        {
            //borderPaint.setColor(Color.RED);
            canvas.drawRect(getRectFByCenterLocation(0,0,RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(0,0,RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(0,getHeight()/2,RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(0,getHeight()/2,RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(0,getHeight(),RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(0,getHeight(),RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(getWidth()/2,0,RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(getWidth()/2,0,RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(getWidth()/2,getHeight(),RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(getWidth()/2,getHeight(),RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(getWidth(),0,RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(getWidth(),0,RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(getWidth(),getHeight()/2,RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(getWidth(),getHeight()/2,RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            canvas.drawRect(getRectFByCenterLocation(getWidth(),getHeight(),RESIZE_INDICATOR_OUTER_LENGTH),resizeIndicatorOuter);
            canvas.drawRect(getRectFByCenterLocation(getWidth(),getHeight(),RESIZE_INDICATOR_INNER_LENGTH),resizeIndicatorInner);
            //draw delete and edit btn
            bmpSize = (int) ((getWidth()<getHeight()?getWidth():getHeight())*BTN_BMP_SIZE_RATIO);
            bmpSize=bmpSize>BTN_BMP_MAX_SIZE?BTN_BMP_MAX_SIZE:bmpSize;
            bmpSize=bmpSize<BTN_BMP_MIN_SIZE?BTN_BMP_MIN_SIZE:bmpSize;
            btnDeleteSizeRect.left= getWidth()-bmpSize;
            btnDeleteSizeRect.top=0;
            btnDeleteSizeRect.right=getWidth();
            btnDeleteSizeRect.bottom= bmpSize;
            btnEditSizeRect.left=getWidth()-bmpSize;
            btnEditSizeRect.top= getHeight()-bmpSize;
            btnEditSizeRect.right=getWidth();
            btnEditSizeRect.bottom=getHeight();
            canvas.drawBitmap(deleteBtnBmp,null,btnDeleteSizeRect, btnPaint);
            canvas.drawBitmap(editBtnBmp,null,btnEditSizeRect,btnPaint);
        }
//        else
//        {
//            borderPaint.setColor(Color.BLACK);
//            borderPaint.setStrokeWidth(5.f);
//        }
        canvas.drawRect(0,0,getWidth(),getHeight(),borderPaint);


    }

    /**
     *
     * @param x center x
     * @param y center y
     * @param size length of one side
     * @return a rectf with (x,y) as center point
     */
    private RectF getRectFByCenterLocation(float x,float y,float size){
        return new RectF(x-size/2,y-size/2,x+size/2,y+size/2);
    }
    private void clearSelection(){
        Log.i(TAG, "clearSelection: child count="+((FrameLayout)getParent()).getChildCount());
        FrameLayout designer=(FrameLayout)getParent();
        for(int i=0;i<designer.getChildCount();i++){
            BaseComponentView view= (BaseComponentView) designer.getChildAt(i);
            view.setEditing(false);
        }
    }

    /**
     * d
     * @param x touch x
     * @param y touch y
     * @return a int represents the editing edge or point
     */
    private int whichEditingPoint(float x,float y){
        int result= EDITING_CENTER;
        if(x<EDITING_POINT_OFFSET){
            if(y<EDITING_POINT_OFFSET)
                result=EDITING_TOP_LEFT_POINT;
            else if(y>getHeight()-EDITING_POINT_OFFSET)
                result=EDITING_BOTTOM_LEFT;
            else
                result=EDITING_LEFT_EDGE;
        }
        else if(x>EDITING_POINT_OFFSET&&x<getWidth()-EDITING_POINT_OFFSET){
            if(y<EDITING_POINT_OFFSET)
                result=EDITING_TOP_EDGE;
            else if(y>getHeight()-EDITING_POINT_OFFSET)
                result=EDITING_BOTTOM_EDGE;
        }
        else if(x>getWidth()-EDITING_POINT_OFFSET){
            if(y<EDITING_POINT_OFFSET)
                result=EDITING_TOP_RIGHT_POINT;
            else if(y>getHeight()-EDITING_POINT_OFFSET)
                result=EDITING_RIGHT_BOTTOM_POINT;
            else
                result=EDITING_RIGHT_EDGE;
        }
        Log.i(TAG, "whichEditingPoint: result="+result);
        return result;
    }
    private int checkBtnClicked(float x,float y){
        if(x<getWidth()&&x>getWidth()- bmpSize)
        {
            if(y>0&&y< bmpSize)
                return DELETE_BTN_CLICKED;
            else if(y>getHeight()-bmpSize&&y<getHeight())
                return EDIT_BTN_CLICKED;
        }
        return INVALID;

    }
    public void setEditing(boolean selected){
        this.editing =selected;
        invalidate();
    }

//    @Override
//    public boolean onGenericMotionEvent(MotionEvent event) {
//        if(whichEditingPoint(event.getX(),event.getY())!=EDITING_CENTER&&editing){
//            borderPaint.setStrokeWidth(EDITING_POINT_OFFSET);
//            invalidate();
//        }
//        else{
//            borderPaint.setStrokeWidth(5.f);
//            invalidate();
//        }
//        if(event.getAction()==MotionEvent.ACTION_HOVER_EXIT){
//            borderPaint.setStrokeWidth(5.f);
//            invalidate();
//        }
//
//
//        return super.onGenericMotionEvent(event);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i(TAG, "onTouchEvent: fired");
        Log.i(TAG, "onTouchEvent: event raw x="+event.getRawX()+" event raw y="+event.getRawY());
        Log.i(TAG, "onTouchEvent: even x="+event.getX()+" event y="+event.getY());

        int action=event.getAction();
        switch (action){

            case MotionEvent.ACTION_DOWN:
//                if(System.currentTimeMillis()-actionDownTimeStamp<DOUBLE_CLICK_THRESHOLD)
//                {
//                    Log.i(TAG, "onTouchEvent: double click triggered");
//                    try {
//                        EventDispatcher.dispatchSaveEvent();
//                    } catch (Exception e) {
//                        Log.e(TAG, "onTouchEvent: ",e );
//                    }
//                    EditPropertyDialog.newInstance(componentEntity).show(((Activity)getContext()).getFragmentManager(),"a");
//                }
//                actionDownTimeStamp=System.currentTimeMillis();

                int btnClicked=checkBtnClicked(event.getX(),event.getY());
                if(btnClicked==EDIT_BTN_CLICKED){
                    try {
                        EventDispatcher.dispatchSaveEvent();
                    } catch (Exception e) {
                        Log.e(TAG, "onTouchEvent: ",e );
                    }
                    EditPropertyDialog.newInstance(componentEntity).show(((Activity)getContext()).getFragmentManager(),"a");
                }
                else if(btnClicked==DELETE_BTN_CLICKED){
                    try {
                        EventDispatcher.dispatchDeleteEvent(componentEntity);
                        return false;
                    } catch (Exception e) {
                        Log.e(TAG, "onTouchEvent: ",e );
                    }
                }
                
                //record value for moving component
                dX = this.getX() - event.getRawX();
                dY = this.getY() - event.getRawY();

                oRawX=event.getRawX();
                oRawY=event.getRawY();
                resizingPoint=whichEditingPoint(event.getX(),event.getY());
                ignoreMoveFlag=false;
                //storing old params for resizing
                layoutParams=(FrameLayout.LayoutParams)this.getLayoutParams();
                oTopMargin = layoutParams.topMargin;
                oLeftMargin=layoutParams.leftMargin;
                oRightMargin=layoutParams.rightMargin;
                oBottomMargin=layoutParams.bottomMargin;
                oHeight = layoutParams.height;
                oWidth=layoutParams.width;

                //storing parent border for detecting if user moves component outside the designer
                parentDesigner=(FrameLayout)getParent();
                parentLeft=parentDesigner.getLeft();
                parentTop=parentDesigner.getTop();
                parentRight=parentDesigner.getRight();
                parentBottom=parentDesigner.getBottom();

                break;

            case MotionEvent.ACTION_MOVE:
                //handle resize and shift
            if(editing&&!ignoreMoveFlag){
                float deltaX=event.getRawX()-oRawX;
                float deltaY=event.getRawY()-oRawY;

                switch (resizingPoint){
                    case EDITING_TOP_EDGE:
                        layoutParams.topMargin=(int)(oTopMargin +deltaY);
                        layoutParams.height=(int)(oHeight-deltaY);
                        break;
                    case EDITING_BOTTOM_EDGE:
                        
                        layoutParams.bottomMargin=(int)(oBottomMargin-deltaY);
                        layoutParams.height=(int)(oHeight+deltaY);
                        break;
                    case EDITING_RIGHT_EDGE:
                        layoutParams.rightMargin=(int)(oRightMargin-deltaX);
                        layoutParams.width=(int)(oWidth+deltaX);
                        break;
                    case EDITING_LEFT_EDGE:
                        layoutParams.leftMargin=(int)(oLeftMargin+deltaX);
                        layoutParams.width=(int)(oWidth-deltaX);
                        break;
                    case EDITING_TOP_LEFT_POINT:
                        layoutParams.topMargin=(int)(oTopMargin +deltaY);
                        layoutParams.height=(int)(oHeight-deltaY);
                        layoutParams.leftMargin=(int)(oLeftMargin+deltaX);
                        layoutParams.width=(int)(oWidth-deltaX);
                        break;
                    case EDITING_TOP_RIGHT_POINT:
                        layoutParams.topMargin=(int)(oTopMargin +deltaY);
                        layoutParams.height=(int)(oHeight-deltaY);
                        layoutParams.rightMargin=(int)(oRightMargin-deltaX);
                        layoutParams.width=(int)(oWidth+deltaX);
                        break;
                    case EDITING_RIGHT_BOTTOM_POINT:
                        layoutParams.bottomMargin=(int)(oBottomMargin-deltaY);
                        layoutParams.height=(int)(oHeight+deltaY);
                        layoutParams.rightMargin=(int)(oRightMargin-deltaX);
                        layoutParams.width=(int)(oWidth+deltaX);
                        break;
                    case EDITING_BOTTOM_LEFT:
                        layoutParams.leftMargin=(int)(oLeftMargin+deltaX);
                        layoutParams.width=(int)(oWidth-deltaX);
                        layoutParams.bottomMargin=(int)(oBottomMargin-deltaY);
                        layoutParams.height=(int)(oHeight+deltaY);
                        break;

                    case EDITING_CENTER:
                        this.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                }


                //prevent user make component too small or disappear
                if(layoutParams.height<MIN_COMPONENT_SIZE)
                {
                    ignoreMoveFlag=true;
                    layoutParams.height=MIN_COMPONENT_SIZE+1;
                }
                if(layoutParams.width<MIN_COMPONENT_SIZE){
                    ignoreMoveFlag=true;
                    layoutParams.width=MIN_COMPONENT_SIZE+1;
                }
                //prevent user move component outside designer
                if(event.getRawX()<parentLeft||event.getRawX()>parentRight||event.getRawY()<parentTop||event.getRawY()>parentBottom)
                    ignoreMoveFlag=true;

                //apply editing params
                Log.i(TAG, "onTouchEvent: layout param height="+layoutParams.height);
                this.setLayoutParams(layoutParams);
            }

                break;
            case MotionEvent.ACTION_UP:
                
                
                clearSelection();
                editing =true;
                break;



        }


        Log.i(TAG, "onTouchEvent: end");

        invalidate();
        return true;
    }


}
