/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-29
 *
 *control״̬
 */
package com.mfusion.player.common.Entity.Control;

import java.util.Date;

import com.mfusion.player.common.Enum.ControlType;
import com.mfusion.player.common.Player.MainActivity;

public class ControlStatus {
	 public AControl ControlObject;
     public boolean Used = false;
     public Date LastTime = MainActivity.Instance.Clock.Now;
     public ControlType Type;
}
