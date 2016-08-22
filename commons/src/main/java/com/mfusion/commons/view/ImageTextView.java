package com.mfusion.commons.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commontools.R;

/**
 * Created by Guoyu on 2016/8/18.
 */
public class ImageTextView extends LinearLayout{

    private Context m_context;

    private LinearLayout m_view_layout;

    private ImageView m_image_view;

    private TextView m_text_view;

    public ImageTextView(Context context) {
        super(context);
        initView(context);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.m_context=context;

        m_view_layout =(LinearLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_imagetext, this,true);

        m_view_layout.setBackground(m_context.getResources().getDrawable(R.drawable.button_style));

        m_image_view=(ImageView)m_view_layout.findViewById(R.id.customer_img);

        m_text_view=(TextView)m_view_layout.findViewById(R.id.customer_text);
        m_text_view.setText("");

        ButtonHoverStyle.bindingHoverEffectWithBorder(m_view_layout,m_context.getResources());
    }

    public void setText(String text){
        m_text_view.setText(text);
    }

    public void setImage(Bitmap image){
        m_image_view.setImageBitmap(image);
    }

    public void setImage(int drawableId){
        m_image_view.setImageDrawable(m_context.getResources().getDrawable(drawableId));
    }
}
