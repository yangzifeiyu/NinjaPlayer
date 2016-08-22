package com.mfusion.player.common.Entity.Weather;

import java.util.List;

import com.mfusion.player.common.Setting.Component.TextPropertySetting;

import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.CalendarContract.Colors;

public class LayoutTemplate {
	public int DateNum = 0;
	
	public int Width = 0;

    public int Height = 0;

    public String BackImage;

    public Integer BackColor;

    public TextPropertySetting Font;

    public LayoutOfTime CurrentWeather;

    public List<LayoutOfDate> DatesWeather;
}
