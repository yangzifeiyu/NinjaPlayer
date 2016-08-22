package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnHoverListener;

import com.mfusion.commons.tools.DateConverter;
import com.mfusion.scheduledesigner.R;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class BlockDailyView extends View implements OnHoverListener,OnLayoutChangeListener{

    public static int None=0;

    public static int Delete=1;

    public BlockUIEntity block_info;

    TextPaint paint_title,paint_body,paint_item,paint_border;

    PathEffect effects =null;

    int width,height,title_h=20;

    Boolean block_selected=false;

    Context m_context=null;

    public BlockDailyView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.m_context=context;
        this.initView();
    }

    public BlockDailyView(Context context, BlockUIEntity block, Boolean selected) {
        super(context);
        // TODO Auto-generated constructor stub
        this.block_info=block;
        this.block_selected=selected;

        this.m_context=context;
        this.initView();
    }

    private void initView() {

        this.paint_title=new TextPaint();
        this.paint_title.setAntiAlias(true);
        paint_title.setColor(this.m_context.getResources().getColor(R.color.colorBlockTitle));
        this.paint_title.setStyle(Style.FILL);

        this.paint_body=new TextPaint();
        this.paint_body.setAntiAlias(true);
        this.paint_body.setTextSize(m_context.getResources().getDimension(R.dimen.textSizeSmall));
        this.paint_body.setColor(Color.DKGRAY);
        this.paint_body.setStyle(Style.STROKE);

        this.paint_border=new TextPaint();
        this.paint_border.setAntiAlias(true);
        this.paint_border.setColor(Color.DKGRAY);
        this.paint_border.setStyle(Style.STROKE);

        this.paint_item=new TextPaint();
        this.paint_item.setAntiAlias(true);
        this.paint_item.setColor(this.m_context.getResources().getColor(R.color.colorBlockItem));
        this.paint_item.setStyle(Style.FILL);

        this.effects = new DashPathEffect(new float[] { 8, 8}, 1);

        this.setOnHoverListener(this);
        this.addOnLayoutChangeListener(this);

        this.onSelected(block_selected, false);
    }

    public void onSelected(Boolean selected,Boolean isRefresh){

        this.block_selected=selected;
        //this.paint_body.setColor(selected?Color.RED:Color.DKGRAY);
        if(selected)
            this.paint_border.setPathEffect(effects);
        else
            this.paint_border.setPathEffect(null);
        if(isRefresh)
            refresh();
    }

    public void refresh(){
        invalidate();
    }

    public int checkViewStatus(MotionEvent event) {

        if(event.getX()>(width-20)&&event.getY()<20)
            return BlockDailyView.Delete;

        return BlockDailyView.None;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if(left!=oldLeft||right!=oldRight||top!=oldTop||bottom!=oldBottom){
            width=right-left;
            height=bottom-top;
            title_h=height/3;
            //invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(canvas!=null)
        {
            //all block
            canvas.drawRect(0, 0, width, title_h, this.paint_title);
            canvas.drawRect(1, 1, width-1, height-1, this.paint_border);
            //title
            canvas.drawText(DateConverter.convertShortTimeToStr(this.block_info.startTime)+"-"+DateConverter.convertShortTimeToStr(this.block_info.endTime), 5, 16, this.paint_body);
            canvas.drawLine(0, title_h, width, title_h, this.paint_border);
            //item list
            canvas.drawRect(0, title_h, width, height, this.paint_item);

            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_close_clear_cancel), null,new Rect(width-15, 5, width, 20), this.paint_body);

            //this.paint_body.setTextSize(10);
            if(this.block_info!=null&&this.block_info.itemList.size()>0){
                int item_count=this.block_info.itemList.size();
                StaticLayout item_layout=null;
                int block_item_width=this.width/item_count;
                int item_name_location_x,item_name_location_y;
                for (int i = 0; i < item_count; i++) {
                    item_layout = new StaticLayout(this.block_info.itemList.get(i).blockItemName,this.paint_body,block_item_width,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
                    item_name_location_x=i*block_item_width;
                    item_name_location_y=(height-item_layout.getHeight()+title_h)/2;
                    canvas.translate(item_name_location_x,item_name_location_y);
                    item_layout.draw(canvas);
                    canvas.translate(-item_name_location_x,-item_name_location_y);

                    if(item_count>(i+1)){
                        canvas.drawLine(block_item_width*(i+1), title_h, block_item_width*(i+1), height, this.paint_body);
                    }
                }
            }
            //this.paint_body.setTextSize(12);

        }
    }

    @Override
    public boolean onHover(View arg0, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_HOVER_ENTER:{
                System.out.println("Comp  :ACTION_HOVER_ENTER");
                break;
            }
            case MotionEvent.ACTION_HOVER_MOVE:{

                break;
            }
            case MotionEvent.ACTION_HOVER_EXIT:{
                System.out.println("Comp  :ACTION_HOVER_EXIT");
                break;
            }
        }
        return false;
    }
}
