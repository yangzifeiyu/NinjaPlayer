/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-29
 *
 *锟截硷拷锟斤拷锟斤拷
 */
package com.mfusion.player.common.Entity.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import android.view.View;
import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Player.MainActivity;

public class ControlManager {

	private List<ControlStatus> m_controls;
	private HashMap<String, ControlStatus> m_rss_controls;

	public ControlManager()
	{
		this.m_controls = new ArrayList<ControlStatus>();
		this.m_rss_controls = new HashMap<String, ControlStatus>();


	}



	public AControl GetControl(ControlType type)
	{
		return this.GetControl(type, "");
	}


	public AControl GetControl(ControlType type, String param)
	{

		if (type == ControlType.MarqueeRSSText)
		{
			if(this.m_rss_controls.containsKey(param))
				//锟斤拷锟斤拷锟酵拷锟経RL锟侥ｏ拷锟斤拷锟斤拷锟角匡拷锟叫的ｏ拷直锟接凤拷锟斤拷使锟矫★拷
				if (this.m_rss_controls.get(param).Used == false)
				{
					this.m_rss_controls.get(param).Used = true;
					this.m_rss_controls.get(param).LastTime = MainActivity.Instance.Clock.Now;
					return this.m_rss_controls.get(param).ControlObject;
				}

		}
		if (type != ControlType.MarqueeRSSText)
			for (ControlStatus control :this.m_controls)
			{
				if (!control.Used && control.Type == type)
				{
					control.Used = true;
					control.LastTime = MainActivity.Instance.Clock.Now;
					return control.ControlObject;
				}
			}

		ControlStatus newControl = new ControlStatus();
		newControl.Used= true;
		newControl.LastTime = MainActivity.Instance.Clock.Now;
		newControl.Type = type;


		//没锟斤拷锟揭碉拷锟斤拷锟酵达拷锟斤拷一锟斤拷锟铰碉拷
		switch (type)
		{
		case Video:
			newControl.ControlObject = new VideoControl1(MainActivity.Instance);
			break;
		case Image:
			if(param.equals(""))
			{
				newControl.ControlObject = new ImageControl(MainActivity.Instance);
			}
			else if(param.equals("Gif"))
			{
				newControl.ControlObject = new GifControl(MainActivity.Instance);
			}
			break;
		case MarqueeRSSText:
			newControl.ControlObject = new RSSControl(MainActivity.Instance);
			if (false == this.m_rss_controls.containsKey(param))
				this.m_rss_controls.put(param, newControl);
			break;
		case WebPage:
			newControl.ControlObject = new WebControl(MainActivity.Instance);
			break;
		case Audio:
			newControl.ControlObject=new AudioControl(MainActivity.Instance);
			break;
		case Error:
			newControl.ControlObject=new ErrorControl(MainActivity.Instance);
			break;
		case Table:
			newControl.ControlObject=new DataTableControl(MainActivity.Instance);
			break;
		case Weather:
			newControl.ControlObject=new WeatherControl(MainActivity.Instance);
			break;
		default:
			break;

		}
		this.m_controls.add(newControl);
		return newControl.ControlObject;


	}

	/// <summary>
	/// 锟斤拷锟斤拷使锟矫的讹拷锟斤拷
	/// </summary>
	/// <param name="r_component"></param>
	public void ReturnControl(AControl r_control)
	{
		
		for (ControlStatus control : this.m_controls)
		{
			if (control.ControlObject == r_control)
			{
				control.Used = false;
				control.LastTime = MainActivity.Instance.Clock.Now;
				control.ControlObject.Element.setVisibility(View.INVISIBLE);
				return;
			}
		}
		//锟斤拷锟矫伙拷锟斤拷业锟斤拷锟街憋拷锟斤拷头牛锟斤拷锟斤拷锟斤拷锟斤拷呒锟斤拷遣锟斤拷锟斤拷锟街达拷锟斤拷锟斤拷锟�
		try
		{
			r_control.Release();
		}
		catch(Exception ex) { }
		

	}

	public void releaseControls(){
		for (ControlStatus control : this.m_controls)
			if(control.ControlObject!=null)
				control.ControlObject.Release();
		this.m_controls.clear();
	}


}
