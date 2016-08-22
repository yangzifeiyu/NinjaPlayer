/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-08
 *
 * ����android player��¼��־
 */
package com.mfusion.player.library.Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;


import com.mfusion.commons.tools.LogOperator;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;


public class LoggerHelper {

	public static void WriteLogfortxt(String logMsg)
	{
		LogOperator.WriteLogfortxt(logMsg,MainActivity.Instance.Clock.Now);
	}
}




