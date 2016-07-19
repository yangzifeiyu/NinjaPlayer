package com.mfusion.commons.entity.template;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.mfusion.commons.entity.inernalenum.ResourceSourceType;

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

    public int width=0;

    public int height=0;

    public ArrayList<ComponentEntity> compList=new ArrayList<ComponentEntity>();
}
