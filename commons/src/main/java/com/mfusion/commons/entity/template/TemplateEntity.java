package com.mfusion.commons.entity.template;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.mfusion.commons.entity.values.ResourceSourceType;
import com.mfusion.commons.tools.InternalKeyWords;

import java.util.ArrayList;

/**
 * Created by jimmy on 7/12/2016.
 */
public class TemplateEntity {
    /**
     * Created by jimmy on 7/12/2016.
     *
     * All components to be inherited from this base class
     */
    public String id;

    public ResourceSourceType templateOriginal= ResourceSourceType.local;

    public Bitmap thumbImageBitmap;

    public Integer backColor= Color.BLACK;

    public String backImagePath="";

    public Bitmap backImageBitmap;

    public int width= InternalKeyWords.TemplateDefaultWidth;

    public int height=InternalKeyWords.TemplateDefaultHeight;

    public ArrayList<ComponentEntity> compList=new ArrayList<ComponentEntity>();
}
