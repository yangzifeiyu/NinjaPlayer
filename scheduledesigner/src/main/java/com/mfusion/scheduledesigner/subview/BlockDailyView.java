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
public class BlockDailyView extends View implements OnLayoutChangeListener{

    public static int None=0;

    public static int Delete=1;

    public static int Edit=2;

    public BlockUIEntity block_info;

    TextPaint paint_title,paint_body,paint_item,paint_border;

    PathEffect effects =null;

    int width,height,title_h=20,btn_size=20,btn_margin=5,btn_margin_top=0;

    Float default_font_size,default_title_length,default_body_length;

    Float title_scale=1.0f,body_scale=1.0f;

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

        this.default_font_size=m_context.getResources().getDimension(R.dimen.textSizeSmall);
        this.paint_title=new TextPaint();
        this.paint_title.setAntiAlias(true);
        this.paint_title.setColor(this.m_context.getResources().getColor(R.color.colorBlockTitle));
        this.paint_title.setStyle(Style.FILL);

        this.paint_body=new TextPaint();
        this.paint_body.setAntiAlias(true);
        this.paint_body.setTextSize(this.default_font_size);
        this.paint_body.setColor(Color.DKGRAY);
        this.paint_body.setStyle(Style.STROKE);

        this.paint_border=new TextPaint();
        this.paint_border.setAntiAlias(true);
        this.paint_border.setColor(this.m_context.getResources().getColor(R.color.colorBlockBorder));
        this.paint_border.setStyle(Style.STROKE);

        this.paint_item=new TextPaint();
        this.paint_item.setAntiAlias(true);
        this.paint_item.setColor(this.m_context.getResources().getColor(R.color.colorBlockItem));
        this.paint_item.setStyle(Style.FILL);

        this.effects = new DashPathEffect(new float[] { 8, 8}, 1);

        this.addOnLayoutChangeListener(this);

        this.onSelected(block_selected, false);
    }

    public void onSelected(Boolean selected,Boolean isRefresh){

        this.block_selected=selected;
        //this.paint_body.setColor(selected?Color.RED:Color.DKGRAY);
        if(selected){
            this.paint_border.setPathEffect(effects);
            this.paint_title.setColor(this.m_context.getResources().getColor(R.color.colorBlockTitleSelect));
            this.paint_body.setColor(this.m_context.getResources().getColor(R.color.colorBlockItemSelect));
        }
        else{
            this.paint_border.setPathEffect(null);
            this.paint_title.setColor(this.m_context.getResources().getColor(R.color.colorBlockTitle));
            this.paint_body.setColor(Color.DKGRAY);
        }
        if(isRefresh)
            refresh();
    }

    public void refresh(){
        invalidate();
    }

    public int checkViewStatus(MotionEvent event) {

        if((btn_size*2+btn_margin)<width) {
            if (event.getX() > (width - btn_size) && event.getY() < btn_size+btn_margin_top)
                return BlockDailyView.Delete;
            if (event.getX() > (width - btn_size * 2) && event.getX() < (width - btn_size) && event.getY() < btn_size+btn_margin_top)
                return BlockDailyView.Edit;
        }
        return BlockDailyView.None;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if(left!=oldLeft||right!=oldRight||top!=oldTop||bottom!=oldBottom){
            width=right-left;
            height=bottom-top;
            title_h=btn_size=height/3;
            this.paint_body.setTextSize(title_h*3/5);
            default_title_length=this.paint_body.measureText(DateConverter.convertShortTimeToStr(this.block_info.startTime)+"-"+DateConverter.convertShortTimeToStr(this.block_info.endTime))+(btn_size+btn_margin)*2;
            default_body_length=0f;
            for (int i = 0; i < this.block_info.itemList.size(); i++) {
                default_body_length+=this.paint_body.measureText(this.block_info.itemList.get(i).blockItemName);
            }
            title_scale=width>default_title_length?1.0f:width/default_title_length;
            body_scale=width>default_body_length?1.0f:width/default_body_length;
            btn_size=(int)(btn_size*title_scale);
            btn_margin=(int)(btn_margin*title_scale);
            btn_margin_top=(title_h-btn_size)/2;
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
            this.paint_body.setTextSize(default_font_size*title_scale);
            this.paint_body.setColor(Color.WHITE);
            canvas.drawText(DateConverter.convertShortTimeToStr(this.block_info.startTime)+"-"+DateConverter.convertShortTimeToStr(this.block_info.endTime), 5, title_h-(title_h-this.paint_body.getTextSize())/2, this.paint_body);
            canvas.drawLine(0, title_h, width, title_h, this.paint_border);
            //item list
            canvas.drawRect(0, title_h, width, height, this.paint_item);

            if((btn_size*2+btn_margin)<width) {
                canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mf_edit), null, new Rect(width - btn_size * 2 + btn_margin , btn_margin_top, width - btn_size + btn_margin, btn_size +btn_margin_top), this.paint_body);
                canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mf_delete), null, new Rect(width - btn_size , btn_margin_top, width, btn_size+btn_margin_top), this.paint_body);
                //android.R.drawable.ic_menu_close_clear_cancel
            }

            this.paint_body.setColor(Color.DKGRAY);
            //this.paint_body.setTextSize(10);
            if(this.block_info!=null&&this.block_info.itemList.size()>0){
                int item_count=this.block_info.itemList.size();
                StaticLayout item_layout=null;
                int block_item_width=this.width/item_count;
                int item_name_location_x,item_name_location_y;
                String draw_text="";
                this.paint_body.setTextSize(default_font_size*body_scale);
                for (int i = 0; i < item_count; i++) {
                    /*item_layout = new StaticLayout(this.block_info.itemList.get(i).blockItemName,this.paint_body,block_item_width,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
                    item_name_location_x=i*block_item_width;
                    item_name_location_y=(height-item_layout.getHeight()+title_h)/2;
                    canvas.translate(item_name_location_x,item_name_location_y);
                    item_layout.draw(canvas);
                    canvas.translate(-item_name_location_x,-item_name_location_y);*/
                    draw_text=this.block_info.itemList.get(i).blockItemName;
                    float textLenght=this.paint_body.measureText(draw_text);
                    /*System.out.println("width "+textLenght+" font "+paint_body.getTextSize());
                    if ( textLenght> width) {
                        int subIndex = this.paint_body.breakText(draw_text, 0, draw_text.length(), true, width, null);
                        if(subIndex>=1)
                            draw_text = draw_text.substring(0, subIndex-1)+"...";
                    }*/
                    item_name_location_x=textLenght> width?0:(int)(width-textLenght)/2;
                    item_name_location_y=(int)getTextBaseline();
                    canvas.drawText(draw_text,item_name_location_x,item_name_location_y,this.paint_body);

                    if(item_count>(i+1)){
                        canvas.drawLine(block_item_width*(i+1), title_h, block_item_width*(i+1), height, this.paint_body);
                    }
                }
            }
            //this.paint_body.setTextSize(12);

        }
    }

    private float getTextBaseline(){
        Paint.FontMetricsInt fontMetrics = this.paint_body.getFontMetricsInt();
        return  (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top+title_h/2;
    }
}
