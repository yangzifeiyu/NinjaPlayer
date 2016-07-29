package com.mfusion.templatedesigner;

import android.content.Context;
import android.graphics.Bitmap;

import com.mfusion.commons.entity.values.ComponentType;

import java.util.List;

/**
 * Created by 1B15182 on 22/7/2016 0022.
 */
public class ComponentListItem {
    private int drawableId;
    private String name;
    private ComponentType type;

    public ComponentListItem(int drawableId, String name, ComponentType type) {
        this.drawableId=drawableId;
        this.name = name;
        this.type = type;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }
}
