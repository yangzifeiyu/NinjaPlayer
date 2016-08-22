/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *�Զ���ص�����
 */
package com.mfusion.player.library.Callback;

public class Caller {

	private MyCallInterface mc;

	public Caller(){}

	/*
	 * ���ûص����ʺ���
	 */
	public void setI(MyCallInterface mc){

		this.mc=mc;

	}

	/*
	 * ���ûص�����
	 */
	public Object  call(Object Paras)
	{
		return mc.fuc(Paras);
	}//Caller�ĵ��÷���
}

