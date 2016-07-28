package com.mfusion.scheduledesigner.subview;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.mfusion.commons.entity.values.BlockType;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;
import com.mfusion.scheduledesigner.entity.BlockUIItemEntity;
import com.mfusion.scheduledesigner.entity.CallbackBundle;
import com.mfusion.scheduledesigner.entity.ScheduleDrawHelper;
import com.mfusion.scheduledesigner.values.CompOperateType;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guoyu on 2016/7/20.
 */
public class BlockView extends RelativeLayout{

    public BlockUIEntity block_info;

    public Boolean isLayoutChange=false;

    public int topMargin=0;

    private Calendar calendar;

    OnTouchListener block_touch_listener;

    Boolean block_selected=false;

    float per_mins_w;
    int pre_day_h;

    public BlockView(Context context,float mins_w,int day_h,int x,int y,OnTouchListener block_touch_listener) {
        super(context);
        // TODO Auto-generated constructor stub

        this.per_mins_w=mins_w;
        this.pre_day_h=day_h;

        this.block_touch_listener=block_touch_listener;

        this.refreshBlockView();
    }

    public BlockView(Context context, BlockUIEntity block, float mins_w, int day_h, OnTouchListener block_touch_listener, Calendar calendar) {
        super(context);
        // TODO Auto-generated constructor stub
        this.block_info=block;

        this.calendar=calendar;

        this.per_mins_w=mins_w;
        this.pre_day_h=day_h;

        this.block_touch_listener=block_touch_listener;

        this.refreshBlockView();
    }

    public void refreshBlockView() {

        if(this.block_info==null||this.block_info.recurrence.isEmpty())
            return;

        this.removeAllViews();

        Calendar cal = (Calendar)(calendar.clone());
        Date current_line_date=null;
        for (int i = 0; i < this.block_info.recurrence.length(); i++) {

            cal.add(Calendar.DAY_OF_WEEK, 1);
            current_line_date=cal.getTime();
            if(block_info.startDate.compareTo(current_line_date)>0||(block_info.endDate!=null&&block_info.endDate.compareTo(current_line_date)<0))
                continue;

            if(ScheduleDrawHelper.checkBlockRecurrence(block_info, i)){

                BlockDailyView itemView=new BlockDailyView(getContext(),this.block_info,block_selected);
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, this.pre_day_h);

                this.addView(itemView,layoutParams);

                layoutParams=(RelativeLayout.LayoutParams)itemView.getLayoutParams();
                this.topMargin=layoutParams.topMargin=i*this.pre_day_h;
                itemView.setLayoutParams(layoutParams);

                itemView.setClickable(true);
                itemView.setOnTouchListener(this.block_touch_listener);

                if(this.block_info.blockType== BlockType.Single)
                    continue;
                itemView.setOnDragListener(new OnDragListener() {

                    @Override
                    public boolean onDrag(View arg0, DragEvent event) {
                        // TODO Auto-generated method stub
                        final int action = event.getAction();
                        switch (action) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                //拖拽开始事件
                                if (event.getClipDescription().hasMimeType( ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                    return true;
                                }
                                return false;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                //被拖放View进入目标View ;
                                return true;
                            case DragEvent.ACTION_DRAG_LOCATION:return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                //被拖放View离开目标View
                                return true;
                            case DragEvent.ACTION_DROP:
                                //释放拖放阴影，并获取移动数据
                                ClipData.Item item = event.getClipData().getItemAt(0);
                                String dragData = item.getText().toString();

                                initBlockItem(dragData);
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                //拖放事件完成
                                return true;
                            default: break; }
                        return false;
                    }
                });
            }
        }

    }

    private void initBlockItem(String temp_name) {

        BlockUIItemEntity itemEntity=new BlockUIItemEntity();
        itemEntity.blockItemName=temp_name;
        this.block_info.itemList.add(itemEntity);

        for (int i = 0; i < this.getChildCount(); i++) {
            ((BlockDailyView)this.getChildAt(i)).refresh();
        }
    }

    public void changeBlockProperty(int location) {

        RelativeLayout.LayoutParams layout=(RelativeLayout.LayoutParams)this.getLayoutParams();

        System.out.println(layout.leftMargin+"  "+layout.topMargin);
        if(this.block_info.isRecurrence==false&&location>0){

            int oldDateIndex=this.block_info.recurrence.indexOf("1");

            this.block_info.recurrence=ScheduleDrawHelper.getRecurrenceBylocation(this.pre_day_h, location);

            layout.topMargin=0;

            this.setLayoutParams(layout);

            int newDateIndex=this.block_info.recurrence.indexOf("1");

            this.block_info.startDate.setDate(this.block_info.startDate.getDate()+(newDateIndex-oldDateIndex));
            this.block_info.endDate=this.block_info.startDate;
        }

        this.block_info.duration=(int) (layout.width/this.per_mins_w);

        ScheduleDrawHelper.getBlockTime(block_info, per_mins_w, layout.leftMargin);

        this.refreshBlockView();
    }

    public void changeBlockProperty() {

        RelativeLayout.LayoutParams layout=(RelativeLayout.LayoutParams)this.getLayoutParams();
        layout.leftMargin=ScheduleDrawHelper.getBlockLeftMargin(block_info.startTime, per_mins_w);
        layout.width=(int)(block_info.duration*this.per_mins_w);
        this.setLayoutParams(layout);

        this.refreshBlockView();
    }

    public void onSelected(Boolean selected, MotionEvent event){

        block_selected=selected;
        for(int index=0;index<this.getChildCount();index++){
            ((BlockDailyView)this.getChildAt(index)).onSelected(selected,true);
        }
    }

    int diatance=15;
    public CompOperateType getOperateTypeByLocation(RelativeLayout.LayoutParams layout, MotionEvent event) {
        String operateString="move";
        if(event.getX()<diatance){
            operateString="w";
        }
        else if(event.getX()>(layout.width-diatance)){
            operateString="e";
        }else{
            if(this.block_info.isRecurrence)
                operateString="moveH";
        }

        return CompOperateType.fromString(operateString);
    }

    CallbackBundle editResponse=new CallbackBundle() {

        @Override
        public void callback(Bundle bundle) {
            // TODO Auto-generated method stub
            RelativeLayout.LayoutParams layout=(RelativeLayout.LayoutParams)getLayoutParams();

            layout.width=(int) (block_info.duration*per_mins_w);
            layout.leftMargin=ScheduleDrawHelper.getBlockLeftMargin(block_info.startTime, per_mins_w);

            setLayoutParams(layout);

            refreshBlockView();
        }
    };
}
