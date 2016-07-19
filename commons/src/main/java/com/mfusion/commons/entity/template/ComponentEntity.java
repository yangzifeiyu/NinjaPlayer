package com.mfusion.commons.entity.template;

import android.graphics.Color;

import com.mfusion.commons.entity.values.ComponentType;

import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by jimmy on 7/12/2016.
 */
public class ComponentEntity {

    public ComponentType type=ComponentType.Unkown;

    public String componentName;

    public int backColor= Color.WHITE;

    public int left=0;

    public int top=0;

    public int width=0;

    public int height=0;

    /*
     * @Description XML nodes(node name is property)
     */
    public ArrayList<Element> property=new ArrayList<Element>();
}
