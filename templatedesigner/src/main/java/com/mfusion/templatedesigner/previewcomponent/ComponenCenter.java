package com.mfusion.templatedesigner.previewcomponent;

import java.util.Hashtable;

import android.content.Context;

import com.mfusion.commons.entity.template.ComponentEntity;
import com.mfusion.commons.entity.values.ComponentType;
import com.mfusion.templatedesigner.R;

public class ComponenCenter {

	private Hashtable<String, Integer> m_comp_index_datas;
	
	public ComponenCenter(){
		this.m_comp_index_datas=new Hashtable<String, Integer>();
	}
	
	public void clear(){
		this.m_comp_index_datas.clear();
	}
	
	public BasicComponentView CreateComponent(Context context, String type){
		BasicComponentView componentView=null;

		if(type.equals(context.getResources().getString(R.string.comp_type_datetime))){
			componentView=new DateTimeComponentView(context);
		}
		else if(type.equals(context.getResources().getString(R.string.comp_type_ticker))){
			componentView=new TickerTextComponentView(context);
		}
		else if(type.equals(context.getResources().getString(R.string.comp_type_schedulemedia))){
			componentView=new ScheduleMediaComponentView(context);
		}
		else if(type.equals(context.getResources().getString(R.string.comp_type_web))){
			componentView=new InteractiveComponentView(context);
		}
		else if(type.equals(context.getResources().getString(R.string.comp_type_rss))){
			componentView=new RSSComponentView(context);
		}
		else if(type.equals(context.getResources().getString(R.string.comp_type_audio))){
			componentView=new AudioComponentView(context);
		}

		if(componentView==null)
			return null;
		
		Integer comp_index=1;
		if(this.m_comp_index_datas.containsKey(componentView.c_type.toString())){
			comp_index=this.m_comp_index_datas.get(componentView.c_type.toString());
			comp_index++;
		}
		this.m_comp_index_datas.put(componentView.c_type.toString(),comp_index);
		componentView.c_index=comp_index;
		componentView.c_name=componentView.c_type.toString()+" "+comp_index;
		return componentView;
	}
	
	public BasicComponentView getComponent(Context context,ComponentEntity entity){
		BasicComponentView componentView=null;
		try {
			if(entity.type== ComponentType.DateTime){
				componentView=new DateTimeComponentView(context,entity);
			}
			else if(entity.type==ComponentType.TickerText){
				componentView=new TickerTextComponentView(context, entity);
			}
			else if(entity.type==ComponentType.ScheduleMedia){
				componentView=new ScheduleMediaComponentView(context, entity);
			}
			else if(entity.type==ComponentType.WebPage){
				componentView=new InteractiveComponentView(context, entity);
			}
			else if(entity.type==ComponentType.RSSComponent){
				componentView=new RSSComponentView(context, entity);
			}
			else if(entity.type==ComponentType.AudioComponent){
				componentView=new AudioComponentView(context, entity);
			}

			if(componentView==null)
				return null;

			Integer comp_index=1;
			if(entity.componentName!=null&&!entity.componentName.isEmpty()){
				Integer index=entity.componentName.lastIndexOf(" ");
				if(index>=0){
					comp_index=Integer.parseInt(entity.componentName.substring(index+1));
					if(this.m_comp_index_datas.containsKey(componentView.c_type.toString())){
						if(comp_index<=this.m_comp_index_datas.get(componentView.c_type.toString())) {
							return componentView;
						}
					}
				}
			}

			if(this.m_comp_index_datas.containsKey(componentView.c_type.toString())){
				comp_index=this.m_comp_index_datas.get(componentView.c_type.toString());
				comp_index++;
			}
			componentView.c_name=componentView.c_type.toString()+" "+comp_index;
			componentView.c_index=comp_index;
			this.m_comp_index_datas.put(componentView.c_type.toString(),comp_index);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return componentView;
	}
}
