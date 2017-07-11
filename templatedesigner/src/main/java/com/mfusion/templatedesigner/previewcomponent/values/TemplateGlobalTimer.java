package com.mfusion.templatedesigner.previewcomponent.values;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.HandleTimer;
import com.mfusion.templatedesigner.previewcomponent.BasicComponentView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Created by ThinkPad on 2016/11/25.
 */
public class TemplateGlobalTimer {

    private HandleTimer m_refresh_timer;

    private int m_timer_interval=10;

    private Set<BasicComponentView> m_execute_process;

    public  TemplateGlobalTimer(){

        this.m_execute_process= Collections.synchronizedSet(new HashSet());

    }

    public void addTimerProcess(BasicComponentView componentView){
        synchronized (this.m_execute_process) {
            this.m_execute_process.add(componentView);
        }
    }

    public void removeTimerProcess(BasicComponentView componentView){
        synchronized (this.m_execute_process) {
            this.m_execute_process.remove(componentView);
        }
    }

    public void restartTimer(){
        if(m_refresh_timer==null)
            m_refresh_timer=new HandleTimer() {
                @Override
                protected void onTime() {
                    executeTimerProcess();
                }
            };
        m_refresh_timer.restart(100,m_timer_interval);
    }

    public void stopTimer(){
        if(m_refresh_timer!=null)
            m_refresh_timer.stop();
    }

    public void releaseTimer(){
        if(m_refresh_timer!=null) {
            m_execute_process.clear();
            m_refresh_timer.release();
            m_refresh_timer = null;
        }
    }

    private void executeTimerProcess(){

        try {

            BasicComponentView componentView=null;
            synchronized (this.m_execute_process) {

                Iterator<BasicComponentView> iterator = this.m_execute_process.iterator();
                while (iterator.hasNext()) {
                    componentView = iterator.next();
                    componentView.executeTimer();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
