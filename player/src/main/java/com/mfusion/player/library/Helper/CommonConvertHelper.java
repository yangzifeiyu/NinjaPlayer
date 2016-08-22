package com.mfusion.player.library.Helper;

public class CommonConvertHelper {
	public static int StringToInt(String value)
	{
		if(value.equals(""))
			return 0;
		try
		{
			return Integer.parseInt(value);
		}
		catch(Exception ex)
		{
			return 0;
		}
	}

   //fontsize
	public static float StringToFloat(String value)
	{
		if(value.equals(""))
			return 0;
		try
		{
			return Float.parseFloat(value)*4/3;
		}
		catch(Exception ex)
		{
			return 0;
		}
	}


	public static boolean parseIntToBoolean(String attribute) {
		// TODO Auto-generated method stub
		boolean flag=true;
		int value=1;
		if(!attribute.equals(""))
		{

			try
			{
				value= Integer.parseInt(attribute);
			}
			catch(Exception ex)
			{

			}

			if(value==0)
				flag= false;

		}
		return flag;

	}


	public static long StringToLong(String value) {
		// TODO Auto-generated method stub
		if(value.equals(""))
			return 0;
		try
		{
			return Long.parseLong(value);
		}
		catch(Exception ex)
		{
			return 0;
		}
	}
}
