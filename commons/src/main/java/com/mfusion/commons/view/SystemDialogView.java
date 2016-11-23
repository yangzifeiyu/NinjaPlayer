package com.mfusion.commons.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commontools.R;

/**
 * Created by ThinkPad on 2016/9/13.
 */
public class SystemDialogView extends LinearLayout {
    
    private Context m_context;
    private LinearLayout m_dialog_layout;
    
    private ImageView m_dialog_icon_view;
    private TextView m_dialog_title_view;
    private Button m_dialog_close_view;
    private LinearLayout m_dialog_content_layout;

    private CallbackBundle m_close_event;
    
    public SystemDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDialogStyle(context);
    }

    public SystemDialogView(Context context) {
        super(context);
        initDialogStyle(context);
    }

    public SystemDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDialogStyle(context);
    }
    
    private void initDialogStyle(Context context){
        m_context=context;
        m_dialog_layout =(LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_dialog_sample, this,true);
        m_dialog_icon_view=(ImageView)m_dialog_layout.findViewById(R.id.system_dialog_icon);
        m_dialog_title_view=(TextView) m_dialog_layout.findViewById(R.id.system_dialog_title);
        m_dialog_close_view=(Button)m_dialog_layout.findViewById(R.id.system_dialog_close);
        m_dialog_close_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_close_event!=null){
                    m_close_event.callback(null);
                }
            }
        });
        m_dialog_content_layout=(LinearLayout)m_dialog_layout.findViewById(R.id.system_dialog_content_layout);
    }

    public void initDialogContent(String title, int icon, View content, CallbackBundle closeCallBack){
        m_dialog_title_view.setText(title);
        m_dialog_icon_view.setImageDrawable(this.m_context.getResources().getDrawable(icon));
        if(content!=null)
            m_dialog_content_layout.addView(content,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        m_close_event=closeCallBack;
    }
}
