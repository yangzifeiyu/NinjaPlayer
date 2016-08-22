package com.mfusion.player.common.Entity.Weather;

import java.util.List;

public class WeatherData {
	public String City;
	
	public String ErrorMessage;

    public String ImageRootPath;
    
    public List<String> IconSizes;
    
    public RealWeather current_condition;

    public List<DateWeather> weather;
}
