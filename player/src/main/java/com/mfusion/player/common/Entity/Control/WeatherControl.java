package com.mfusion.player.common.Entity.Control;

import com.mfusion.player.common.Entity.View.WeatherView;
import com.mfusion.player.common.Entity.Weather.LayoutTemplate;
import com.mfusion.player.common.Entity.Weather.WeatherData;
import com.mfusion.player.common.Enum.TemperatureType;

import android.content.Context;
import android.view.View;

public class WeatherControl extends AControl{

	WeatherView weatherView;
	public WeatherControl (Context context)
	{
		CreateControl(context);
	}
	
	@Override
	public void Release() {
		// TODO Auto-generated method stub

	}

	@Override
	public void CreateControl(Context context) {
		// TODO Auto-generated method stub
		weatherView=new WeatherView(context);
		
		weatherView.setVisibility(View.INVISIBLE);
		
		this.Element=weatherView;
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub
		weatherView.Stop();
	}

	public void ShowWeather(WeatherData content,LayoutTemplate template){
		weatherView.DisplayWeather(content,template);
	}
	
	public void SetWeatherTemplateType(TemperatureType Type){
		weatherView.SetWeatherTemplateType(Type);
	}
}
