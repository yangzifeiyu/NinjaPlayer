package com.mfusion.player.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mfusion.player.common.Player.MainActivity;

public class BootReceiver extends BroadcastReceiver {   

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		// TODO Auto-generated method stub
		Intent intent2 = new Intent(ctx, MainActivity.class);
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent2);


	}   
}  