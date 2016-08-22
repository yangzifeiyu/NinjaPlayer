package com.mfusion.player.common.Entity;

import java.util.Date;

import com.mfusion.player.common.Enum.ExternalType;


public class DynamicResource
{
	
	public String ResourceKey;//URL
	public String ResourceDir;
	public String ResourceHash;
	public int RefreshInterval=20;
	public Date InvalidTime;
	public Date RefreshTime;
	public ExternalType type;
	public Date UpdateTime;
}

