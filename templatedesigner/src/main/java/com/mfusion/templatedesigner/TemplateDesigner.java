package com.mfusion.templatedesigner;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mfusion.commons.controllers.AbstractTemplateDesigner;
import com.mfusion.commons.entity.exception.IllegalTemplateException;
import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.template.TemplateEntity;
import com.mfusion.commons.data.XMLTemplate;
import com.mfusion.commons.entity.template.VisualTemplate;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.templatedesigner.component.BaseComponentView;
import com.mfusion.templatedesigner.component.DateTimeComponentView;
import com.mfusion.templatedesigner.component.RssComponentView;
import com.mfusion.templatedesigner.component.ScheduleMediaComponentView;
import com.mfusion.templatedesigner.component.TickerTextBaseView;
import com.mfusion.templatedesigner.component.TickerTextComponentView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2016/7/13.
 */
public class TemplateDesigner extends AbstractTemplateDesigner {

    private static final String TAG = "TemplateDesigner";
    private static final int COLOR_READY_FOR_DROP=Color.parseColor("#2eb82e");

    private LinearLayout toolLayout;
    private FrameLayout designerLayout;

    private int designerWidth;
    private int designerHeight;

    private DefaultComponent defaultComponent;


    private TemplateEntity templateEntity;
    private ArrayList<ComponentEntity> componentEntities;

    private boolean drawn;

    public TemplateDesigner(Context context) {
        this(context,null);



    }

    public TemplateDesigner(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultComponent=new DefaultComponent(getContext());
        this.setWeightSum(4);
        LinearLayout.LayoutParams toolParams = new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT, 1.0f);
        LinearLayout.LayoutParams designerParams=new LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,3.0f);

        toolLayout=new LinearLayout(context);
        toolLayout.setBackgroundColor(Color.parseColor("#7e97e7"));


        designerLayout=new FrameLayout(context);
        designerLayout.setOnDragListener(new DesignerDragListener());
        setUpToolBar();
        addView(toolLayout,toolParams);
        addView(designerLayout,designerParams);

        EventDispatcher.registerDesigner(this);
    }



    @Override
    public Boolean openTemplate(TemplateEntity template) {
        templateEntity=template;
        componentEntities=template.compList;
        //work around to avoid width and height have not been calculated during initialization progress
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==0)
                    showTemplateOnScreen();
            }};

            new Thread(){
            @Override
            public void run() {

                try {
                    sleep(1000);
                    EventDispatcher.dispatchDisposeProgressDialog();
                } catch (Exception e) {
                    Log.e(TAG, "run: ",e );
                }
                Message msg=handler.obtainMessage();
                msg.arg1=0;
                handler.sendMessage(msg);

            }
        }.start();

        return templateEntity != null;
    }

    @Override
    public TemplateEntity saveTemplate() {
        return getTemplateInfoFromDesigner();
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //draw components on screen once the view is inflated and drawn
        if(!drawn&&templateEntity!=null){
            showTemplateOnScreen();
            drawn=true;
        }

        Log.i(TAG, "onWindowFocusChanged: designer layout width="+designerLayout.getWidth()+" height="+designerLayout.getHeight());
        Log.i(TAG, "onWindowFocusChanged: designer class variable width="+designerWidth+" height="+designerHeight);
    }



    public boolean openAssetsTemplate(String name){
        boolean finished=false;
        try {
            ArrayList<VisualTemplate> visualTemplates=XMLTemplate.getInstance().getAllLocalSampleLayouts(getContext().getAssets(),"template");
            for(VisualTemplate current:visualTemplates){
                if(current.id.equals(name))
                {
                    templateEntity=XMLTemplate.getInstance().getSampleLayout(current,getContext().getAssets());
                    componentEntities=templateEntity.compList;
                    finished=true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "openAssetsTemplate: ",e );
        }

        return finished;
    }

    private void setUpToolBar(){
        ListView toolList=new ListView(getContext());
        LinearLayout.LayoutParams toolListParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        toolList.setLayoutParams(toolListParams);
        List<ComponentListItem> items=new ArrayList<>();
        items.add(new ComponentListItem(R.drawable.date_time,"Date Time",ComponentType.DateTime));
        items.add(new ComponentListItem(R.drawable.schedule_media,"Media",ComponentType.ScheduleMedia));
        items.add(new ComponentListItem(R.drawable.ticker,"Ticker Text",ComponentType.TickerText));
        items.add(new ComponentListItem(R.drawable.rss,"RSS",ComponentType.RSSComponent));
        items.add(new ComponentListItem(R.drawable.weather,"Weather",ComponentType.WeatherComponent));
        items.add(new ComponentListItem(R.drawable.audio,"Audio",ComponentType.AudioComponent));
        ComponentListAdapter adapter=new ComponentListAdapter(getContext(),items);
        toolList.setAdapter(adapter);
        toolLayout.addView(toolList);
    }
    public void showTemplateOnScreen(){
        designerLayout.setBackgroundColor(templateEntity.backColor);
        designerLayout.removeAllViews();
        for(ComponentEntity componentEntity:componentEntities){
            addComponentToDesigner(componentEntity);
        }
        invalidate();
    }

    /**
     * to be fixed
     * @param entity
     */
    public void bringToFront(ComponentEntity entity){
        saveComponentEntityInfoFromDesigner();
        componentEntities.remove(entity);
        componentEntities.add(entity);
        showTemplateOnScreen();
    }

    private void addComponentToDesigner(ComponentEntity entity){
        BaseComponentView view=null;
        switch (entity.type){
            case DateTime:
                view=new DateTimeComponentView(getContext());
                break;
            case TickerText:
                view=new TickerTextComponentView(getContext());

                break;
            case ScheduleMedia:
                view=new ScheduleMediaComponentView(getContext());
                break;
            case RSSComponent:
                view=new RssComponentView(getContext());
                break;

        }
        if(view!=null){
            view.setComponentEntity(entity);

            designerLayout.addView(view,getLayoutParamsFromComponent(entity));
        }
    }
    public void saveComponentEntityInfoFromDesigner(){
        for(int i=0;i<designerLayout.getChildCount();i++){
            BaseComponentView currentView= (BaseComponentView) designerLayout.getChildAt(i);
            setComponentParamsFromView(currentView.getX(),currentView.getY(),currentView.getWidth(),currentView.getHeight(),componentEntities.get(i));
        }
    }
    public void deleteComponent(ComponentEntity entity){
        saveComponentEntityInfoFromDesigner();
        int index=componentEntities.indexOf(entity);
        componentEntities.remove(index);
        showTemplateOnScreen();

    }
    public void clearComponentOnScreen(){
        designerLayout.removeAllViews();
    }

    private FrameLayout.LayoutParams getLayoutParamsFromComponent(ComponentEntity componentEntity){
        Log.i(TAG, "getLayoutParamsFromComponent: fired");
        float onScreenWidth=1.0f*componentEntity.width/templateEntity.width*designerLayout.getWidth();
        float onScreenHeight=1.0f*componentEntity.height/templateEntity.height*designerLayout.getHeight();
        float onScreenLeft=1.0f*componentEntity.left/templateEntity.width*designerLayout.getWidth();
        float onScreenTop=1.0f*componentEntity.top/templateEntity.height*designerLayout.getHeight();
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams((int)onScreenWidth,(int)onScreenHeight);


        params.setMargins((int)onScreenLeft,(int)onScreenTop,0,0);
        return params;
    }


    private void setComponentParamsFromView(float left,float top,float width,float height,ComponentEntity entity){
        int rLeft=(int)(left/designerLayout.getWidth()*templateEntity.width);
        int rTop=(int)(top/designerLayout.getHeight()*templateEntity.height);
        int rWidth=(int)(width/designerLayout.getWidth()*templateEntity.width);
        int rHeight=(int)(height/designerLayout.getHeight()*templateEntity.height);
        entity.top=rTop;
        entity.left=rLeft;
        entity.width=rWidth;
        entity.height=rHeight;

    }



    private TemplateEntity getTemplateInfoFromDesigner() {
        // TODO Auto-generated method stub
        saveComponentEntityInfoFromDesigner();

        return templateEntity;
    }

    private Boolean saveTemplateInfo(){
        try {

            XMLTemplate.getInstance().updateTemplate(this.getTemplateInfoFromDesigner());
            return true;
        }catch (IllegalTemplateException ex){

        }catch (Exception ex){

        }
        return false;
    }

    private TemplateEntity getTemplateInfo(String templateName){
        try {

            return XMLTemplate.getInstance().getTemplateById(templateName);

        } catch (Exception e) {

        }
        return  null;
    }



    private class DesignerDragListener implements OnDragListener{

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action=event.getAction();
            //ClipData.Item item=event.getClipData().getItemAt(0);

            switch (action){
                case DragEvent.ACTION_DRAG_STARTED:
                    designerLayout.setBackgroundColor(Color.YELLOW);
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    designerLayout.setBackgroundColor(COLOR_READY_FOR_DROP);
                    break;
                case DragEvent.ACTION_DROP:

                    ClipData.Item item=event.getClipData().getItemAt(0);
                    int realLeft=(int)(event.getX()/getWidth()*templateEntity.width);
                    int realTop=(int)(event.getY()/getHeight()*templateEntity.height);

                    ComponentEntity addedEntity= defaultComponent.getDefaultComponent(ComponentType.fromString((String)item.getText()));
                    if(addedEntity!=null){
                        addedEntity.top= realTop;
                        addedEntity.left=realLeft;

                        componentEntities.add(addedEntity);
                        addComponentToDesigner(addedEntity);
                    }

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    designerLayout.setBackgroundColor(Color.YELLOW);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    designerLayout.setBackgroundColor(templateEntity.backColor);
                    break;
            }
            return true;
        }
    }
}
