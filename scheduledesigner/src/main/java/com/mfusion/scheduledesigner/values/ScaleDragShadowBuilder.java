package com.mfusion.scheduledesigner.values;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by ThinkPad on 2016/7/27.
 */
public class ScaleDragShadowBuilder extends View.DragShadowBuilder {
    // 拖动阴影的图像， 作为一个drawable来定义
    private Drawable shadow;

    int width;
    int height;
    // 构造函数
    public ScaleDragShadowBuilder(View v) {
        // 通过myDragShadowBuilder存储View参数
        super(v);
        // 创建一个可拖拽的图像，此图像可以通过系统的Canvas来填充
        shadow = new ColorDrawable(Color.LTGRAY);
    }

    // 定义一个回调方法，将阴影的维度和触摸点返回给系统
    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        // 定义当地的变量
        // 设置阴影的宽度为视图一半
        width = getView().getWidth() / 2;
        // 设置阴影的高度为视图一半
        height = getView().getHeight() / 2;

        super.onProvideShadowMetrics(size,touch);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.translate(width / 2, height / 2);
        canvas.scale(0.5f,0.5f);
        super.onDrawShadow(canvas);
    }
}
