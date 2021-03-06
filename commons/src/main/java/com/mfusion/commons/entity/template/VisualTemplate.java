package com.mfusion.commons.entity.template;

import android.graphics.Bitmap;

import com.mfusion.commons.entity.values.ResourceSourceType;

/**
 * Created by jimmy on 7/12/2016.
 *
 * A Visual description of a template
 *
 *It includes template name, size, and thumbnail image for preview.
 */
public class VisualTemplate {

    public String id;

    public ResourceSourceType templateOriginal= ResourceSourceType.local;

    public Bitmap thumbImageBitmap;

    public String path;

    public long lastModifyTime;
}
