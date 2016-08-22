/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *BasicComponent
 */
package com.mfusion.player.common.Entity.Components;


import com.mfusion.player.library.Helper.LoggerHelper;
import com.mfusion.player.common.Enum.ComponentType;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

public abstract class BasicComponent extends View implements Comparable<BasicComponent>{

	private RelativeLayout.LayoutParams layoutParams1;
	private RelativeLayout.LayoutParams layoutParams2;
	protected RelativeLayout container;

	public void setCmpcontext(View cmpcontext) {
		try
		{
			cmpcontext.setLayoutParams(layoutParams2);
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("BasicComponent setCmpcontext==>"+ex.getMessage());
		}
	}

	public ComponentType componentType;

	private int cmpwidth;
	public int getCmpwidth() {
		return cmpwidth;
	}

	public void setCmpwidth(int cmpwidth) {
		this.cmpwidth = cmpwidth;
		this.layoutParams2.width=cmpwidth;
		this.layoutParams1.width=cmpwidth;
	}


	private int cmpheight;
	public int getCmpheight() {
		return cmpheight;
	}

	public void setCmpheight(int cmpheight) {
		this.cmpheight = cmpheight;
		this.layoutParams2.height=cmpheight;
		this.layoutParams1.height=cmpheight;
	}


	private int cmptop;
	public int getCmptop() {
		return cmptop;
	}

	public void setCmptop(int cmptop) {
		this.cmptop = cmptop;
		layoutParams1.topMargin =cmptop;

	}


	private int cmpleft;
	public int getCmpleft() {
		return cmpleft;
	}



	public void setCmpleft(int cmpleft) {
		this.cmpleft = cmpleft;
		layoutParams1.leftMargin =cmpleft;
	}


	public Integer index;


	public BasicComponent(Context context) {
		super(context);
		this.layoutParams1= new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.container = new RelativeLayout(context);
		this.container.setLayoutParams(layoutParams1);
		this.layoutParams2= new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	}



	@Override
	public int compareTo(BasicComponent arg0) {
		return this.index.compareTo(arg0.index);
	}


	public void AddView(View view)
	{
		if(this.container!=null&&view!=null)
		{
			ViewGroup parent = (ViewGroup)view.getParent();
			if (parent != null) {
				parent.removeAllViews();
			} 
			this.container.addView(view);
		}
	}

	public abstract void End();

	public abstract void Render();

	public abstract void Init(Object o);

	public abstract void SetTop();






}
