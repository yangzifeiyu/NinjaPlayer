/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-20
 *
 *���ڶ�������
 */
package com.mfusion.player.common.Entity.Control;

import android.content.Context;
import android.view.View;

public abstract class AControl {
	public abstract void Release();
	public abstract void CreateControl(Context context);
	public abstract void SetTop();
	public View Element;
}
