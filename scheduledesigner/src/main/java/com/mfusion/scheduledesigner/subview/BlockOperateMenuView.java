package com.mfusion.scheduledesigner.subview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.scheduledesigner.R;

/**
 * Created by ThinkPad on 2016/11/14.
 */
public class BlockOperateMenuView extends LinearLayout {

    Context m_context;

    LinearLayout m_operator_layout;

    ImageView m_edit_btn,m_delete_btn;

    BlockView.BlockOperateMenuListener blockOperateMenuListener;

    public BlockOperateMenuView(Context context) {
        super(context);
        init(context);
    }

    public BlockOperateMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BlockOperateMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){

        this.m_context=context;

        m_operator_layout = (LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_block_operator, this,true);

        m_edit_btn=(ImageView)m_operator_layout.findViewById(R.id.sche_block_edit_btn);
        m_edit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blockOperateMenuListener!=null)
                    blockOperateMenuListener.requestBlockEdit((View) getParent());
            }
        });
        m_delete_btn=(ImageView)m_operator_layout.findViewById(R.id.sche_block_delete_btn);
        m_delete_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(blockOperateMenuListener!=null)
                    blockOperateMenuListener.requestBlockDelete((View) getParent());
            }
        });

        ButtonHoverStyle.bindingHoverEffect(m_edit_btn,getResources());
        ButtonHoverStyle.bindingHoverEffect(m_delete_btn,getResources());
    }

    public void setOnBlockOperateMenuListener(BlockView.BlockOperateMenuListener listener){
        blockOperateMenuListener=listener;
    }

}
