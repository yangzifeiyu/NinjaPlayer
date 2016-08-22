package com.mfusion.player.common.Entity.View;

import com.mfusion.player.common.Enum.ServerConnectStatus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MFConnectionView extends View{

	private Paint paint=null;  
	private int col=Color.GREEN;

	
	public MFConnectionView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.Init();
	}
	public MFConnectionView(Context context, AttributeSet attrs) 
	{
		super(context,attrs);
		// TODO Auto-generated constructor stub
		this.Init();
	}
	
	public MFConnectionView(Context context, AttributeSet attrs,int defStyle)  
	{
		super(context,attrs,defStyle);
		// TODO Auto-generated constructor stub
		this.Init();
	}

	private void Init() {
		// TODO Auto-generated method stub
		this.paint = new Paint();  
		this.paint.setAntiAlias(true); //�����  
		this.paint.setStyle(Paint.Style.FILL); //���ƿ���Բ   
	}
	@Override  
	protected void onDraw(Canvas canvas) {  
		// TODO Auto-generated method stub  
		int center = getWidth()/2;  
		int ringWidth =5; 
		this.paint.setColor(col);
		this.paint.setStrokeWidth(ringWidth);  
		canvas.drawCircle(center,center, ringWidth/2, this.paint);  
		super.onDraw(canvas);  
	}  
	
	public void ChangeColor(ServerConnectStatus connection)
	{
		int color=Color.GREEN;
		if(connection.equals(ServerConnectStatus.OverLimit))
		{
			color=Color.RED;
		}
		else if(connection.equals(ServerConnectStatus.Unconnection))
		{
			color=Color.GREEN;	
		}
		if(color!=col)
		{
			col=color;
			this.invalidate();
			
		}
		
	}

}
