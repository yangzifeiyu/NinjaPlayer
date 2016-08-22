package com.mfusion.player.common.Floatwindows;

public enum FocusKeyWord {
	page_setting(0),
	page_screencontrol(1),
	page_download(2),
	page_storage(3),
	page_log(4),
	item_ip(0),
	item_port(0),
	item_mport(0),
	item_apply(0),
	item_setip(0),
	item_refresh(0);
	
	public int num;
	FocusKeyWord(int num) {
		this.num = num;
	}
}

