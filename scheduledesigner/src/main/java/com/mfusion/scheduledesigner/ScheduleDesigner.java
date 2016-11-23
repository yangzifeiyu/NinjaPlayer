package com.mfusion.scheduledesigner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.controllers.AbstractScheduleDesigner;
import com.mfusion.commons.controllers.KeyBoardCenter;
import com.mfusion.commons.entity.schedule.Schedule;
import com.mfusion.commons.entity.values.BlockType;
import com.mfusion.commons.entity.values.SchedulePlayType;
import com.mfusion.commons.tools.AlertDialogHelper;
import com.mfusion.commons.tools.DateConverter;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.view.DropDownView;
import com.mfusion.commons.view.ImageTextHorizontalView;
import com.mfusion.commons.view.SystemInfoDialog;
import com.mfusion.scheduledesigner.entity.BlockUIEntity;
import com.mfusion.scheduledesigner.entity.BlockUIItemEntity;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.scheduledesigner.entity.ScheduleDrawHelper;
import com.mfusion.scheduledesigner.entity.SelectedCompProperty;
import com.mfusion.scheduledesigner.subview.BlockDailyView;
import com.mfusion.scheduledesigner.subview.BlockPropertyEditView;
import com.mfusion.scheduledesigner.subview.BlockView;
import com.mfusion.scheduledesigner.subview.GraphicTemplateListLayout;
import com.mfusion.scheduledesigner.subview.ScheduleConflictView;
import com.mfusion.scheduledesigner.subview.ScheduleWeeklyView;
import com.mfusion.scheduledesigner.subview.SelectAreaRect;
import com.mfusion.scheduledesigner.subview.TimeRuleView;
import com.mfusion.scheduledesigner.subview.WeekSelectView;
import com.mfusion.scheduledesigner.values.CompOperateType;
import com.mfusion.scheduledesigner.values.ScheduleConflictChecker;
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

    LinearLayout property_layout;

    LinearLayout sche_select_temp_list_layout;

    BlockPropertyEditView property_view,property_dialog_view;

    SystemInfoDialog property_dialog;

    LinearLayout sche_pbu_layut=null;

    GraphicTemplateListLayout sche_pbu_view;

    RelativeLayout sche_ws_layout;

    RelativeLayout sche_calendar_layout;

    RelativeLayout sche_timeline_layout;

    ScheduleWeeklyView sche_weekly_view=null;

    DropDownView sche_idle_view,sche_selected_day_temp_view;
    Date sche_selected_day;
    TextView sche_selected_day_view;
    ImageTextHorizontalView sche_selected_block_edit_btn,sche_selected_block_delete_btn;

    Switch sche_playmode_view;

    //Button sche_idle_view;

    int screen_w,screen_h;
    int sche_container_w,sche_container_h;
    int sche_ws_w,sche_ws_h;
    int sche_ws_left,sche_ws_top;
    float per_mins_w;
    float pre_day_h;

    SelectedCompProperty selectedComp=new SelectedCompProperty();

    SelectAreaRect blank_select_area;
    
    Calendar calendar = Calendar.getInstance();

    WeekSelectView sche_week_selecter;

    String sche_name="";

    List<BlockUIEntity> block_list=new ArrayList<BlockUIEntity>();

    List<BlockUIEntity> block_list_in_date=new ArrayList<BlockUIEntity>();

    Schedule m_schedule_data =null;

    ScheduleConflictView m_conflict_view;

    HandleTimer m_time_refresh_timer;

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

        property_layout =(LinearLayout)scheduleLayout.findViewById(R.id.sche_property_layout);
        ViewGroup.LayoutParams propertyLayoutParams=property_layout.getLayoutParams();
        /*property_view=new BlockPropertyEditView(this.getContext(),new CallbackBundle() {

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
        property_layout.addView(property_view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));*/
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
        sche_pbu_view.bindingTemplates(templateLoadListener);
        sche_pbu_view.setEnabled(false);

        sche_ws_left=sche_pbu_layut.getLayoutParams().width;
        sche_ws_top=((LinearLayout)scheduleLayout.findViewById(R.id.sche_base_info)).getLayoutParams().height +sche_timeline_layout.getLayoutParams().height;

        android.view.ViewGroup.LayoutParams ws_layoutParams = sche_ws_layout.getLayoutParams();
        ws_layoutParams.height=sche_container_h-sche_ws_top-property_layout.getLayoutParams().height;
        sche_ws_layout.setLayoutParams(ws_layoutParams);

        sche_ws_layout.setClickable(true);
        sche_ws_layout.setOnTouchListener(workspaceTouchListener);

        sche_idle_view=(DropDownView)scheduleLayout.findViewById(R.id.sche_default_list);

        if(this.m_schedule_data!=null)
            sche_idle_view.setText(m_schedule_data.idleItem);

        sche_selected_day_temp_view=(DropDownView)scheduleLayout.findViewById(R.id.sche_selected_day_temp_list);
        sche_selected_day_temp_view.setOnChangeListener(new DropDownView.OnSelectTextChangedListener() {
            @Override
            public void onSelectTextChange(String selectText) {
                selectControl(selectText);
            }
        });
        sche_selected_day_view=(TextView) scheduleLayout.findViewById(R.id.sche_selected_day);
        sche_selected_block_edit_btn=(ImageTextHorizontalView) scheduleLayout.findViewById(R.id.sche_selected_block_edit);
        sche_selected_block_edit_btn.setImage(R.drawable.mf_edit);
        sche_selected_block_delete_btn=(ImageTextHorizontalView) scheduleLayout.findViewById(R.id.sche_selected_block_delete);
        sche_selected_block_delete_btn.setImage(R.drawable.mf_delete);//android.R.drawable.ic_menu_close_clear_cancel
        sche_selected_block_edit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockView selectedBlockView = getSelectedViewInDayByName(sche_selected_day_temp_view.getText().toString());
                editBlock(selectedBlockView,null);
            }
        });
        sche_selected_block_delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockView selectedBlockView = getSelectedViewInDayByName(sche_selected_day_temp_view.getText().toString());
                deleteBlock(selectedBlockView,null);
            }
        });

        sche_playmode_view=(Switch)findViewById(R.id.sche_playmode);
        sche_playmode_view.setSwitchTypeface(Typeface.DEFAULT,Typeface.ITALIC);

        sche_select_temp_list_layout=(LinearLayout)scheduleLayout.findViewById(R.id.sche_select_temp_layout);
        sche_select_temp_list_layout.setVisibility(INVISIBLE);
        sche_select_temp_list_layout.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int other_control_width = (int)sche_selected_day_view.getPaint().measureText(m_context.getResources().getString(R.string.sche_temp_of_date)+" "+InternalKeyWords.DefaultDisplayDateFormat)
                        +(int)m_context.getResources().getDimension(R.dimen.global_margin_medium)*3+(int)m_context.getResources().getDimension(R.dimen.sche_select_temp_icon_width)*2;
                sche_selected_day_temp_view.setMaxWidth((right-left)-other_control_width);
            }
        });

        this.initCalendar();

        final TextView systemTimeText=(TextView)scheduleLayout.findViewById(R.id.sche_system_time);
        this.m_time_refresh_timer=new HandleTimer() {
            @Override
            protected void onTime() {
                systemTimeText.setText(DateConverter.convertCurrentTimeToStr());
                if(m_conflict_view!=null)
                    m_conflict_view.refresh();
            }
        };

        this.m_time_refresh_timer.start(100,1000);

        KeyBoardCenter.deleteKeyCallbackList.add(deleteCompCallBack);
    }

    /**
     * add a new block by drag
     */
    private void insertBlock(String temp_name,int x,int y,ViewGroup container) {

        if(parentFragment!=null)
            parentFragment.setIsEditing(true);

        BlockUIEntity block_info=new BlockUIEntity();
        block_info.blockType= BlockType.Single;
        BlockUIItemEntity itemEntity=new BlockUIItemEntity();
        itemEntity.blockItemName=temp_name;
        block_info.itemList.add(itemEntity);
        ScheduleDrawHelper.initBlockByLocation(block_info,this.per_mins_w, x,pre_day_h, y,calendar);

        Boolean conflict = ScheduleConflictChecker.isBlockConflict(block_list,block_info);
        if(conflict) {
            showConflictView(x,y);
            return;
        }

        this.block_list.add(block_info);

        BlockView blockView=new BlockView(m_context,block_info,per_mins_w,pre_day_h,this.block_touch_listener,this.calendar);
        blockView.setOnBlockOperateMenuListener(new BlockView.BlockOperateMenuListener() {
            @Override
            public void requestBlockEdit(View blockView) {
                editBlock((BlockView) blockView,null);
            }

            @Override
            public void requestBlockDelete(View blockView) {
                deleteBlock(blockView,null);
            }
        });
        container.addView(blockView, new RelativeLayout.LayoutParams((int)Math.ceil(block_info.duration*this.per_mins_w),sche_ws_h));

        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)blockView.getLayoutParams();
        layoutParams.leftMargin=ScheduleDrawHelper.getBlockLeftMargin(block_info.startTime, per_mins_w);
        blockView.setLayoutParams(layoutParams);

        selectControl(blockView,null);

        showBlockInSpecificDate(y);
    }
    CallbackBundle deleteCompCallBack=new CallbackBundle() {
        @Override
        public void callback(Bundle bundle) {
            if(selectedComp.selectedView==null)
                return;
            deleteBlock(selectedComp.selectedView,null);;
        }
    };
    private void deleteBlock(final View deletedView, MotionEvent event) {

        if(deletedView==null)
            return;
        AlertDialogHelper.showAlertDialog(m_context, "Information", "Do you want to delete this template?", new CallbackBundle() {
            @Override
            public void callback(Bundle bundle) {
                try {

                    if(parentFragment!=null)
                        parentFragment.setIsEditing(true);

                    BlockView deletedBlock = (BlockView) deletedView;
                    block_list.remove(deletedBlock.block_info);
                    sche_ws_layout.removeView(deletedBlock);
                    sche_selected_day_temp_view.setText("");
                    selectedComp.selectedView=null;
                    refreshBlockInSpecificDate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, null);
    }

    private void editBlock(BlockView selectedView, MotionEvent event){

        if(selectedView==null)
            return;

        selectControl(selectedView,event);

        if(property_dialog_view==null){
            property_dialog_view=new BlockPropertyEditView(this.getContext(),block_list,new CallbackBundle() {

                @Override
                public void callback(Bundle bundle) {
                    // TODO Auto-generated method stub
                    if(selectedComp.selectedView!=null) {

                        if (parentFragment != null)
                            parentFragment.setIsEditing(true);
                        ((BlockView) selectedComp.selectedView).changeBlockProperty();
                        refreshBlockInSpecificDate();
                    }
                    property_dialog.dismiss();
                }
            });

            property_dialog_view.initPropertyView();
        }

        property_dialog_view.setPropertyEditability(false);
        property_dialog_view.bindingBlockInfo((BlockView)selectedComp.selectedView,true);

        if(property_dialog==null) {
            SystemInfoDialog.Builder builder = new SystemInfoDialog.Builder(this.m_context);
            builder.setContentView(property_dialog_view);
            builder.setIcon(R.drawable.mf_edit);
            builder.setTitle("EDIT PROPERTIES");

            property_dialog = builder.create();
        }
        property_dialog.show();

    }
    /**
     * search block in specified week
     */
    private void changeWeek() {

        clearBlockInSpecificDate();
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
            blockView.setOnBlockOperateMenuListener(new BlockView.BlockOperateMenuListener() {
                @Override
                public void requestBlockEdit(View blockView) {
                    editBlock((BlockView) blockView,null);
                }

                @Override
                public void requestBlockDelete(View blockView) {
                    deleteBlock(blockView,null);
                }
            });
            sche_ws_layout.addView(blockView, new RelativeLayout.LayoutParams((int)(Math.ceil(blockEntity.duration*this.per_mins_w)),sche_ws_h));

            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)blockView.getLayoutParams();
            layoutParams.leftMargin=ScheduleDrawHelper.getBlockLeftMargin(blockEntity.startTime, per_mins_w);
            blockView.setLayoutParams(layoutParams);
        }
    }
    private void clearBlockInSpecificDate() {
        sche_select_temp_list_layout.setVisibility(INVISIBLE);
        sche_selected_day=null;
        sche_selected_day_view.setText("");
        block_list_in_date.clear();
        sche_selected_day_temp_view.setText("");
        sche_selected_day_temp_view.setSelectList(null);
    }

    private void showBlockInSpecificDate(int location){
        System.out.println("Mouse Location :"+location);
        sche_select_temp_list_layout.setVisibility(VISIBLE);
        sche_selected_day = ScheduleDrawHelper.getDateByLocation(pre_day_h, location,calendar);
        sche_selected_day_view.setText(" "+DateConverter.convertToDisplayStr(sche_selected_day));
        if(block_list_in_date!=null) {
            block_list_in_date.clear();
            sche_selected_day_temp_view.setText("");
        }
        refreshBlockInSpecificDate();
    }

    private void refreshBlockInSpecificDate(){

        block_list_in_date=ScheduleDrawHelper.getBlockByDateRange(block_list, sche_selected_day, sche_selected_day);
        List<String> block_info_list=new ArrayList<>();
        String oldBlockInfo,currentBlockInfo,selectedBlockInfo="";
        for(BlockUIEntity blockUIEntity :block_list_in_date){
            int index=block_info_list.size()-1;
            currentBlockInfo=blockUIEntity.toString();
            for(;index>=0;index--){
                oldBlockInfo=block_info_list.get(index);
                if(currentBlockInfo.compareTo(oldBlockInfo)>=0){
                    break;
                }
            }

            if(index==(block_info_list.size()-1))
                block_info_list.add(currentBlockInfo);
            else block_info_list.add(index+1,currentBlockInfo);

            if(selectedComp.selectedView!=null&&((BlockView)selectedComp.selectedView).block_info==blockUIEntity)
                selectedBlockInfo=currentBlockInfo;
        }

        sche_selected_day_temp_view.setText(selectedBlockInfo);
        sche_selected_day_temp_view.setSelectList(block_info_list);
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

        }
    };

    CallbackBundle templateLoadListener=  new CallbackBundle() {
        @Override
        public void callback(Bundle bundle) {
            if(bundle==null)
                return;

            sche_idle_view.setSelectList(bundle.getStringArrayList("temp_list"));
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
     * listen block operate about move, drag
     */
    OnTouchListener block_touch_listener = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:{
                    RelativeLayout.LayoutParams layout_block_item=(RelativeLayout.LayoutParams)view.getLayoutParams();

                    showBlockInSpecificDate((int)(layout_block_item.topMargin+event.getY()));
                    int viewStatus=((BlockDailyView)view).checkViewStatus(event);
                    if(viewStatus!=BlockDailyView.None){
                        if(viewStatus==BlockDailyView.Delete){
                            deleteBlock((View)view.getParent(),event);
                        }else  if(viewStatus==BlockDailyView.Edit){
                            editBlock((BlockView) view.getParent(),event);
                        }
                        break;
                    }

                    selectControl((View)view.getParent(),event);

                    RelativeLayout.LayoutParams layout_block=(RelativeLayout.LayoutParams)selectedComp.selectedView.getLayoutParams();
                    selectedComp.left=layout_block.leftMargin;
                    selectedComp.width=layout_block.width;

                    selectedComp.top=layout_block_item.topMargin;
                    selectedComp.height=layout_block_item.height;

                    selectedComp.mouseX=sche_ws_left+selectedComp.left+event.getX();
                    selectedComp.mouseY=sche_ws_top+selectedComp.top+event.getY();

                    selectedComp.operateType=((BlockView)selectedComp.selectedView).getOperateTypeByLocation(layout_block, event);

                    break;
                }
                case MotionEvent.ACTION_UP:{

                    break;
                }
                case MotionEvent.ACTION_MOVE:{
                    break;
                }
            }
            return false;
        }
    };

    OnTouchListener workspaceTouchListener= new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_UP){
                //showBlockInSpecificDate((int)ev.getY());
            } if (ev.getAction() == MotionEvent.ACTION_DOWN){
                showBlockInSpecificDate((int)ev.getY());
            }
            /*if(selectedComp.operateType == CompOperateType.none){
                //mouse in blank area
                if (ev.getAction() == MotionEvent.ACTION_DOWN){
                    if(blank_select_area==null){
                        blank_select_area=new SelectAreaRect(m_context);
                        sche_ws_layout.addView(blank_select_area);
                    }
                    RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)blank_select_area.getLayoutParams();
                    layoutParams.leftMargin=(int)(ev.getX()-sche_ws_left);
                    layoutParams.topMargin=(int)(ev.getY()-sche_ws_top);
                    blank_select_area.setLayoutParams(layoutParams);
                    blank_select_area.setStartPoint(ev.getX(),ev.getY());
                    blank_select_area.setVisibility(GONE);
                }else if (ev.getAction() == MotionEvent.ACTION_MOVE){
                    if(blank_select_area!=null){
                        blank_select_area.setEndPoint(ev.getX(),ev.getY());
                        blank_select_area.setVisibility(VISIBLE);
                    }
                }else if (ev.getAction() == MotionEvent.ACTION_UP){
                    if(blank_select_area!=null){

                        blank_select_area.setVisibility(GONE);
                    }
                }
            }*/
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
                        parentFragment.setIsEditing(true);

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

                    selectedComp.mouseX=ev.getX();
                    selectedComp.mouseY=ev.getY();
                    selectedComp.selectedView.setLayoutParams(layout);
                }
            }else if (ev.getAction() == MotionEvent.ACTION_UP) {
                if(selectedComp.selectedView!=null){

                    if(((BlockView)selectedComp.selectedView).changeBlockPropertyWithConflictCheck(selectedComp.operateType==CompOperateType.move?(int)(ev.getY() - sche_ws_top):-1,block_list))
                        showConflictView((int)(ev.getX()-sche_ws_left),(int)(ev.getY()-sche_ws_top));

                    ((BlockView)selectedComp.selectedView).isLayoutChange=false;

                    //ShowBlockInfo(selectedComp.selectedView,true);
                    showBlockInSpecificDate((int)ev.getY()-sche_ws_top);
                    selectedComp.operateType=CompOperateType.none;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    /*private void ShowBlockInfo(View view,Boolean forceBinding){

        if(this.selectedComp==null||this.selectedComp.selectedView==null)
            return;

        property_view.bindingBlockInfo((BlockView)view,forceBinding);
    }*/

    private BlockView getSelectedViewInDayByName(String blockName){
        if(blockName.isEmpty())
            return null;
        if(block_list_in_date!=null){
            BlockUIEntity selectBlockEntity=null;
            for(int index=0;index<block_list_in_date.size();index++){
                BlockUIEntity blockUIEntity=block_list_in_date.get(index);
                if(blockUIEntity.toString().equals(blockName)){
                    selectBlockEntity=blockUIEntity;
                    break;
                }
            }

            if(selectBlockEntity!=null){
                View subView=null;
                for(int view_index=1;view_index<sche_ws_layout.getChildCount();view_index++) {
                    subView=sche_ws_layout.getChildAt(view_index);
                    if(subView.getClass()==BlockView.class) {
                        BlockView blockView = (BlockView) subView;
                        if (blockView.block_info == selectBlockEntity) {
                            return blockView;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void selectControl(String blockName){
        BlockView selectedBlockView = getSelectedViewInDayByName(blockName);
        if(selectedBlockView!=null)
            selectControl(selectedBlockView,null);
    }

    private void selectControl(View selectedBlock, MotionEvent event){

        if(selectedComp.selectedView==null||selectedComp.selectedView!=selectedBlock){

            if(selectedComp.selectedView!=null){

                ((BlockView)selectedComp.selectedView).onSelected(false,event);
                selectedComp.selectedView=null;
                //property_view.setPropertyEditability(false);
            }

            if(selectedBlock!=null){

                selectedComp.selectedView=selectedBlock;
                ((BlockView)selectedComp.selectedView).onSelected(true,event);

                sche_selected_day_temp_view.setText(((BlockView)selectedComp.selectedView).block_info.toString());
                //ShowBlockInfo((BlockView)selectedBlock,false);
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

            //property_view.initPropertyView();

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
                    sche_pbu_view.bindingTemplates(templateLoadListener);

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
        if(calendar.get(Calendar.DAY_OF_WEEK)==1){
            calendar.add(Calendar.DAY_OF_WEEK, -7);
        }else
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        DateConverter.clearCalendarNoneHHmmss(calendar);

        sche_week_selecter.initWeek(calendar);
    }

    private void showConflictView(int x,int y){

        if(m_conflict_view==null){
            m_conflict_view=new ScheduleConflictView(m_context);
        }

        if(m_conflict_view.getParent()!=sche_ws_layout)
            sche_ws_layout.addView(m_conflict_view);

        sche_ws_layout.bringChildToFront(m_conflict_view);
        m_conflict_view.show(x,y,this.sche_ws_w);
    }
}
