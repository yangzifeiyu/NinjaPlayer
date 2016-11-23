package com.mfusion.commons.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mfusion.commons.tools.ButtonHoverStyle;
import com.mfusion.commontools.R;

/**
 * Created by Guoyu on 2016/8/18.
 */
public class ImageTextVerticalView extends RelativeLayout{

    private Context m_context;

    private RelativeLayout m_view_layout;

    private ImageView m_image_view;

    private TextView m_text_view;

    public ImageTextVerticalView(Context context) {
        super(context);
        initView(context);
    }

    public ImageTextVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public ImageTextVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.m_context=context;

        m_view_layout =(RelativeLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.view_imagetext_vertical, this,true);

        m_view_layout.setBackground(m_context.getResources().getDrawable(R.drawable.button_style));

        m_image_view=(ImageView)m_view_layout.findViewById(R.id.view_imagetext_vertical_img);
        m_image_view.setVisibility(GONE);

        m_text_view=(TextView)m_view_layout.findViewById(R.id.view_imagetext_vertical_text);
        m_text_view.setText("");
        m_text_view.setVisibility(GONE);
        m_text_view.setTag(m_text_view.getCurrentTextColor());

        ButtonHoverStyle.bindingHoverEffect(m_view_layout,m_context.getResources());
    }

    public void setText(String text){
        m_text_view.setText(text);
        m_text_view.setVisibility(VISIBLE);
    }

    public void setImage(Bitmap image){
        m_image_view.setImageBitmap(image);
        m_image_view.setVisibility(VISIBLE);
    }

    public void setImage(int drawableId){
        m_image_view.setImageDrawable(m_context.getResources().getDrawable(drawableId));
        m_image_view.setVisibility(VISIBLE);
    }

    @Override
    public void setSelected(boolean selected){
        super.setSelected(selected);
        ButtonHoverStyle.bindingHoverEffect(m_view_layout,m_context.getResources());
    }
    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        if(enabled) {
            m_text_view.setTextColor((int) m_text_view.getTag());
            m_image_view.setAlpha(1.0f);
        }
        else{
            m_text_view.setTextColor(Color.GRAY);
            m_image_view.setAlpha(0.7f);
        }
    }
}
