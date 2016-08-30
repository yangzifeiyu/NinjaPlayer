package com.mfusion.commons.tools;

/**
 * Created by ThinkPad on 2016/8/22.
 */
public interface OperateCallbackBundle {
    abstract void onConfim(String content);
    abstract void onCancel(String errorMsg);
}
