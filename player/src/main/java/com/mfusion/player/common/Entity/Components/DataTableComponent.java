package com.mfusion.player.common.Entity.Components;

import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Entity.Control.DataTableControl;
import com.mfusion.player.common.Entity.Control.RSSControl;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Helper.Helper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.DataTableSetting;
import com.mfusion.player.common.Setting.Component.RSSSetting;

import android.content.Context;
import android.view.View;

public class DataTableComponent extends BasicComponent {

	DataTableControl m_datatable_control;
	
	DataTableSetting setting;
	public DataTableComponent(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void End() {
		// TODO Auto-generated method stub
		try
		{
			if(m_datatable_control!=null)
			{
				Helper.ControlManager.ReturnControl(this.m_datatable_control);
				MainActivity.Instance.PBUDispatcher.template.removeView(container);
				if(m_datatable_control.Element!=null)
				{
					this.container.removeView(m_datatable_control.Element);
				}
			}
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DataTableComponent End==>"+ex.getMessage());
		}
	}

	@Override
	public void Render() {
		// TODO Auto-generated method stub
		try
		{
			this.AddView(m_datatable_control.Element);
			this.m_datatable_control.setSetting(setting);
			MainActivity.Instance.PBUDispatcher.template.addView(container);
			this.m_datatable_control.Play();
		}
		catch(Exception ex)

		{
			LoggerHelper.WriteLogfortxt("DataTableComponent Render==>"+ex.getMessage());
		}
	}

	@Override
	public void Init(Object arg) {
		// TODO Auto-generated method stub
		if(arg==null)
			return;
		try
		{
			this.setting = (DataTableSetting) arg;
			int color = setting.BackColor;
			this.container.setBackgroundColor(color);

			this.m_datatable_control =(DataTableControl) Helper.ControlManager.GetControl(ControlType.Table, "");
			this.m_datatable_control.Element.setVisibility(View.VISIBLE);
			this.setCmpcontext(m_datatable_control.Element);
			
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("DataTableComponent Init==>"+ex.getMessage());
		}
	}

	@Override
	public void SetTop() {
		// TODO Auto-generated method stub

	}

}
