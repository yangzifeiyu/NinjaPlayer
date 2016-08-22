package com.mfusion.scheduledesigner;

import android.app.Activity;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.controllers.AbstractScheduleDesigner;
import com.mfusion.commons.entity.schedule.Schedule;
import com.mfusion.commons.entity.values.BlockType;
import com.mfusion.commons.entity.values.SchedulePlayType;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;
import com.mfusion.scheduledesigner.entity.BlockUIItemEntity;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.scheduledesigner.entity.ScheduleDrawHelper;
import com.mfusion.scheduledesigner.entity.SelectedCompProperty;
import com.mfusion.scheduledesigner.subview.BlockDailyView;
import com.mfusion.scheduledesigner.subview.BlockPropertyEditView;
import com.mfusion.scheduledesigner.subview.BlockView;
import com.mfusion.scheduledesigner.subview.GraphicTemplateListLayout;
import com.mfusion.scheduledesigner.subview.ScheduleWeeklyView;
import com.mfusion.scheduledesigner.subview.TimeRuleView;
import com.mfusion.scheduledesigner.subview.WeekSelectView;
import com.mfusion.scheduledesigner.values.CompOperateType;
import com.mfusion.scheduledesigner.values.ScheduleDataConverter;
import com.mfusion.scheduledesigner.values.ScreenAdjustHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Guoyu on 2016/7/13.
 */
public class ScheduleDesigner extends AbstractScheduleDesigner implements View.OnLayoutChangeListener{

    public ScheduleDesigner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.m_context=context;

        this.addOnLayoutChangeListener(this);
    }
    public ScheduleDesigner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.m_context=context;
        this.addOnLayoutChangeListener(this);
    }

    public ScheduleDesigner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.m_context=context;
        this.addOnLayoutChangeListener(this);
    }
    Context m_context;

    boolean isInitUI=false;

    View scheduleLayout;

    RelativeLayout property_layout;

    BlockPropertyEditView property_view;

    LinearLayout sche_pbu_layut=null;

    GraphicTemplateListLayout sche_pbu_view;

    RelativeLayout sche_ws_layout;

    RelativeLayout sche_calendar_layout;

    RelativeLayout sche_timeline_layout;

    ScheduleWeeklyView sche_weekly_view=null;

    Switch sche_playmode_view;

    Button sche_idle_view;

    int screen_w,screen_h;
    int sche_container_w,sche_container_h;
    int sche_ws_w,sche_ws_h;
    int sche_ws_left,sche_ws_top;
    float per_mins_w;
    float pre_day_h;

    SelectedCompProperty selectedComp=new SelectedCompProperty();

    Calendar calendar = Calendar.getInstance();

    WeekSelectView sche_week_selecter;

    String sche_name="";

    List<BlockUIEntity> block_list=new ArrayList<BlockUIEntity>();

    Schedule m_schedule_data =null;

    public AbstractFragment parentFragment;
    /**
     * init all subview in schedule designer
     */
    public void Init() {

        if(this.isInitUI)
            return;

        screen_w=((Activity)this.m_context).getWindowManager().getDefaultDisplay().getWidth();
        screen_h=((Activity)this.m_context).getWindowManager().getDefaultDisplay().getHeight();

        scheduleLayout=LayoutInflater.from(this.getContext()).inflate(R.layout.view_schedule_designer, this,true);

        property_layout =(RelativeLayout)scheduleLayout.findViewById(R.id.sche_property_layout);
        ViewGroup.LayoutParams propertyLayoutParams=property_layout.getLayoutParams();
        property_view=new BlockPropertyEditView(this.getContext(),propertyLayoutParams.width,propertyLayoutParams.height,new CallbackBundle() {

            @Override
            public void callback(Bundle bundle) {
                // TODO Auto-generated method stub
                if(selectedComp.selectedView==null)
                    return;

                if(parentFragment!=null)
                    parentFragment.isEditing=true;
                ((BlockView)selectedComp.selectedView).changeBlockProperty();
            }
        });
        property_layout.addView(property_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        propertyLayoutParams.height =(int)(screen_h*ScreenAdjustHelper.BlockPropertyLayoutScale);
        property_layout.setLayoutParams(propertyLayoutParams);

        sche_ws_layout=(RelativeLayout)scheduleLayout.findViewById(R.id.sche_workspace);
        sche_ws_layout.addOnLayoutChangeListener(workspace_layoutChangeListener);

        sche_ws_layout.setOnDragListener(temp_dragListener);

        sche_calendar_layout=(RelativeLayout)scheduleLayout.findViewById(R.id.sche_calendar);
        sche_calendar_layout.removeAllViews();
        sche_week_selecter=new WeekSelectView(m_context,new CallbackBundle() {

            @Override
            public void callback(Bundle bundle) {
                // TODO Auto-generated method stub
                int dates=bundle.getInt("dates");

                calendar.add(Calendar.DAY_OF_WEEK, dates);

                changeWeek();
            }
        });
        sche_calendar_layout.addView(sche_week_selecter, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        sche_timeline_layout=(RelativeLayout)scheduleLayout.findViewById(R.id.sche_timeline);
        sche_timeline_layout.removeAllViews();
        sche_timeline_layout.addView(new TimeRuleView(m_context), new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        sche_pbu_layut=(LinearLayout) scheduleLayout.findViewById(R.id.sche_pbu);
        ViewGroup.LayoutParams pbuLayoutParams=sche_pbu_layut.getLayoutParams();
        pbuLayoutParams.width =(int)(screen_w*ScreenAdjustHelper.TemplateLayoutScale);
        sche_pbu_layut.setLayoutParams(pbuLayoutParams);

        sche_pbu_view=(GraphicTemplateListLayout)scheduleLayout.findViewById(R.id.sche_pbu_list);
        sche_pbu_view.bindingTemplates();
        sche_pbu_view.setEnabled(false);

        sche_ws_left=sche_pbu_layut.getLayoutParams().width;
        sche_ws_top=((LinearLayout)scheduleLayout.findViewById(R.id.sche_base_info)).getLayoutParams().height +sche_timeline_layout.getLayoutParams().height;

        android.view.ViewGroup.LayoutParams ws_layoutParams = sche_ws_layout.getLayoutParams();
        ws_layoutParams.height=sche_container_h-sche_ws_top-property_layout.getLayoutParams().height;
        sche_ws_layout.setLayoutParams(ws_layoutParams);

        sche_idle_view=(Button)scheduleLayout.findViewById(R.id.sche_default);
        sche_idle_view.setOnDragListener(idle_dragListener);
        sche_idle_view.setPadding(5,0,5,0);
        if(this.m_schedule_data!=null)
            sche_idle_view.setText(m_schedule_data.idleItem);

        sche_playmode_view=(Switch)findViewById(R.id.sche_playmode);
        sche_playmode_view.setSwitchTypeface(Typeface.DEFAULT,Typeface.ITALIC);
        this.initCalendar();
    }

    /**
     * add a new block by drag
     */
    private void insertBlock(String temp_name,int x,int y,ViewGroup container) {

        if(parentFragment!=null)
            parentFragment.isEditing=true;

        BlockUIEntity block_info=new BlockUIEntity();
        block_info.blockType= BlockType.Single;
        BlockUIItemEntity itemEntity=new BlockUIItemEntity();
        itemEntity.blockItemName=temp_name;
        block_info.itemList.add(itemEntity);

        this.block_list.add(block_info);

        ScheduleDrawHelper.initBlockBylocation(block_info,this.per_mins_w, x,pre_day_h, y,(Calendar)calendar.clone());

        BlockView blockView=new BlockView(m_context,block_info,per_mins_w,pre_day_h,this.block_touch_listener,this.calendar);
        container.addView(blockView, new RelativeLayout.LayoutParams((int)Math.ceil(block_info.duration*this.per_mins_w),sche_ws_h));

        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)blockView.getLayoutParams();
        layoutParams.leftMargin=ScheduleDrawHelper.getBlockLeftMargin(block_info.startTime, per_mins_w);
        blockView.setLayoutParams(layoutParams);

        selectControl(blockView,null);
    }

    private void deleteBlock(View deletedView) {
        if(parentFragment!=null)
            parentFragment.isEditing=true;

        BlockView deletedBlock = (BlockView) deletedView;
        block_list.remove(deletedBlock.block_info);
        sche_ws_layout.removeView(deletedBlock);
        property_view.setPropertyEditability(false);
    }

    /**
     * search block in specified week
     */
    private void changeWeek() {

        this.sche_ws_layout.removeAllViews();

        if(sche_weekly_view!=null){

            sche_ws_layout.addView(sche_weekly_view);
            sche_weekly_view.refreshFirstWeek(calendar);
        }

        Calendar cal=(Calendar)this.calendar.clone();
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date startDate=cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate=cal.getTime();
        List<BlockUIEntity> blockInWeek=ScheduleDrawHelper.getBlockByDateRange(block_list, startDate, endDate);

        for (BlockUIEntity blockEntity : blockInWeek) {

            BlockView blockView=new BlockView(m_context,blockEntity,per_mins_w,pre_day_h,this.block_touch_listener,this.calendar);
            sche_ws_layout.addView(blockView, new RelativeLayout.LayoutParams((int)(Math.ceil(blockEntity.duration*this.per_mins_w)),sche_ws_h));

            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)blockView.getLayoutParams();
            layoutParams.leftMargin=ScheduleDrawHelper.getBlockLeftMargin(blockEntity.startTime, per_mins_w);
            blockView.setLayoutParams(layoutParams);
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){

                sche_ws_layout.removeOnLayoutChangeListener(workspace_layoutChangeListener);

                changeWeek();
            }
        }
    };

    OnLayoutChangeListener workspace_layoutChangeListener = new OnLayoutChangeListener() {

        @Override
        public void onLayoutChange(View view, int l, int t, int r,
                                   int b, int oldl, int oldt, int oldr,int oldb) {
            // TODO Auto-generated method stub
           /* if(l==oldl&&t==oldt&&r==oldr&&b==oldb)
                return;*/
            sche_ws_w=r-l;
            sche_ws_h=b-t;
            per_mins_w=sche_ws_w/(24*60.0f);
            pre_day_h=sche_ws_h/7.0f;

            if(sche_weekly_view==null){

                sche_weekly_view=new ScheduleWeeklyView(m_context);
                sche_ws_layout.addView(sche_weekly_view, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            }
            sche_weekly_view.refreshFirstWeek(calendar);

            mHandler.sendEmptyMessage(0);

            System.out.println("layout change");
        }
    };

    /**
     * receive dragged template, and insert into schedule
     */
    OnDragListener temp_dragListener = new OnDragListener() {

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

                    insertBlock(dragData,(int) event.getX(),(int) event.getY(),sche_ws_layout);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    //拖放事件完成
                    return true;
                default: break; }
            return false;
        }
    };
    /**
     * receive dragged template, and create as default template for schedule
     */
    OnDragListener idle_dragListener = new OnDragListener() {

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

                    sche_idle_view.setText(dragData);
                    if(parentFragment!=null)
                        parentFragment.isEditing=true;

                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    //拖放事件完成
                    return true;
                default: break; }
            return false;
        }
    };
    /**
     * listen block operate about move, drag
     */
    OnTouchListener block_touch_listener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:{

                    int viewStatus=((BlockDailyView)view).checkViewStatus(event);
                    if(viewStatus==BlockDailyView.Delete){

                        deleteBlock((View)view.getParent());
                        break;
                    }

                    selectControl((View)view.getParent(),event);

                    RelativeLayout.LayoutParams layout_block=(RelativeLayout.LayoutParams)selectedComp.selectedView.getLayoutParams();
                    selectedComp.left=layout_block.leftMargin;
                    selectedComp.width=layout_block.width;

                    RelativeLayout.LayoutParams layout_block_item=(RelativeLayout.LayoutParams)view.getLayoutParams();
                    selectedComp.top=layout_block_item.topMargin;
                    selectedComp.height=layout_block_item.height;

                    selectedComp.mouseX=sche_ws_left+selectedComp.left+event.getX();
                    selectedComp.mouseY=sche_ws_top+selectedComp.top+event.getY();

                    selectedComp.operateType=((BlockView)selectedComp.selectedView).getOperateTypeByLocation(layout_block, event);

                    System.out.println("Comp  :ACTION_DOWN" +" Type:  "+selectedComp.operateType);
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    System.out.println("Comp  :ACTION_UP");

                    break;
                }
                case MotionEvent.ACTION_MOVE:{
                    break;
                }
            }
            return false;
        }
    };
    /**
     * move template or,change it size
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(selectedComp.operateType!= CompOperateType.none){
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if(selectedComp.selectedView!=null){
                    if(parentFragment!=null)
                        parentFragment.isEditing=true;

                    ((BlockView)selectedComp.selectedView).isLayoutChange=true;
                    RelativeLayout.LayoutParams layout=(RelativeLayout.LayoutParams)selectedComp.selectedView.getLayoutParams();
                    if(selectedComp.operateType==CompOperateType.move||selectedComp.operateType==CompOperateType.moveH){
                        layout.leftMargin+=ev.getX()-selectedComp.mouseX;
                        if(layout.leftMargin<0)
                            layout.leftMargin=0;
                        else if(layout.leftMargin>(this.sche_ws_w-selectedComp.width))
                            layout.leftMargin=this.sche_ws_w-selectedComp.width;

                        if(selectedComp.operateType!=CompOperateType.moveH){
                            layout.topMargin+=ev.getY()-selectedComp.mouseY;
                            int layout_item_top=((BlockView)selectedComp.selectedView).topMargin+layout.topMargin;
                            if(layout_item_top<0)
                                layout.topMargin=-((BlockView)selectedComp.selectedView).topMargin;
                            else if(layout_item_top>(this.sche_ws_h-this.pre_day_h))
                                layout.topMargin=this.sche_ws_h-(int)this.pre_day_h-((BlockView)selectedComp.selectedView).topMargin;
                        }
                    }else{
                        float mouseAddX=0,mouseAddY=0;
                        mouseAddX=ev.getX()-selectedComp.mouseX;
                        mouseAddY=ev.getY()-selectedComp.mouseY;

                        String operateString=selectedComp.operateType.toString();
                        if(operateString.contains("w")){
                            layout.leftMargin+=mouseAddX;
                            if(layout.leftMargin<0)
                                layout.leftMargin=0;
                            else
                                layout.width-=mouseAddX;
                            if(layout.width<=0){
                                layout.width=(int)Math.ceil(per_mins_w);
                                layout.leftMargin-=layout.width;
                            }
                        }else if(operateString.contains("e")){
                            layout.width+=mouseAddX;
                            if(layout.leftMargin>(this.sche_ws_w-layout.width))
                                layout.width-=mouseAddX;
                            if(layout.width<=0){
                                layout.width=(int)Math.ceil(per_mins_w);
                            }
                        }
                        selectedComp.height=layout.height;
                        selectedComp.width=layout.width;
                    }
                    System.out.println(layout.leftMargin);
                    selectedComp.mouseX=ev.getX();
                    selectedComp.mouseY=ev.getY();
                    selectedComp.selectedView.setLayoutParams(layout);
                }
            }else if (ev.getAction() == MotionEvent.ACTION_UP) {
                if(selectedComp.selectedView!=null){

                    ((BlockView)selectedComp.selectedView).changeBlockProperty(selectedComp.operateType==CompOperateType.move?(int)(ev.getY() - sche_ws_top):-1);
                    ((BlockView)selectedComp.selectedView).isLayoutChange=false;

                    ShowBlockInfo(selectedComp.selectedView,true);

                    selectedComp.operateType=CompOperateType.none;
                }
            }

        }

        return super.dispatchTouchEvent(ev);
    }

    private void ShowBlockInfo(View view,Boolean forceBinding){

        if(this.selectedComp==null||this.selectedComp.selectedView==null)
            return;

        property_view.bindingBlockInfo((BlockView)view,forceBinding);
    }

    private void selectControl(View selectedBlock, MotionEvent event){

        if(selectedComp.selectedView==null||selectedComp.selectedView!=selectedBlock){

            if(selectedComp.selectedView!=null){

                ((BlockView)selectedComp.selectedView).onSelected(false,event);
                selectedComp.selectedView=null;
                property_view.setPropertyEditability(false);
            }

            if(selectedBlock!=null){

                selectedComp.selectedView=selectedBlock;
                ((BlockView)selectedComp.selectedView).onSelected(true,event);

                ShowBlockInfo((BlockView)selectedBlock,false);
            }
        }
    }

    @Override
    public Schedule saveSchedule(){
        Schedule schedule=new Schedule();
        schedule.id=m_schedule_data.id;
        schedule.idleItem=sche_idle_view.getText().toString();
        if(schedule.idleItem.isEmpty()||schedule.idleItem.equals(getResources().getString(R.string.sche_idle_template_tip)))
            schedule.idleItem="";
        schedule.playType=sche_playmode_view.isChecked()?SchedulePlayType.Sequence:SchedulePlayType.TimeLine;
        ScheduleDataConverter.convertToSave(schedule, block_list);

        m_schedule_data=schedule;
        return m_schedule_data;
    }

    @Override
    public Boolean openSchedule(Schedule schedule) {
        // TODO Auto-generated method stub

        m_schedule_data = schedule;
        ScheduleDataConverter.convertToDisplay(m_schedule_data,block_list);

        if(this.isInitUI){

            this.initCalendar();

            changeWeek();

            property_view.initPropertyView();

            Message msg=new Message();
            msg.what=0;
            handler.sendMessage(msg);

        }
        return true;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(left!=oldLeft||right!=oldRight||top!=oldTop||bottom!=oldBottom){
            sche_container_w=right-left;
            sche_container_h=bottom-top;

            Message msg=new Message();
            msg.what=1;
            handler.sendMessage(msg);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0:
                    sche_pbu_view.bindingTemplates();

                    break;
                case 1:
                    Init();

                    isInitUI=true;

                    break;
            }

            if(m_schedule_data.idleItem==null||m_schedule_data.idleItem.isEmpty())
                sche_idle_view.setText(getResources().getString(R.string.sche_idle_template_tip));
            else
                sche_idle_view.setText(m_schedule_data.idleItem);
            sche_playmode_view.setChecked(m_schedule_data.playType== SchedulePlayType.Sequence);

            sche_pbu_view.setEnabled(true);

            invalidate();
        }
    };

    private void initCalendar(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        sche_week_selecter.initWeek(calendar);
    }
}
