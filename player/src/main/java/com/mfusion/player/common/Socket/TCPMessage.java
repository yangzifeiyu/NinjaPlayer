/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *Tcp Message
 */
package com.mfusion.player.common.Socket;
import java.io.StringWriter;

import com.mfusion.player.library.Helper.LoggerHelper;

import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

public class TCPMessage {
	private XmlSerializer Serializer;


	/*
	 * ���캯��
	 */
	public TCPMessage() {
		try
		{
			Serializer = Xml.newSerializer();
		}
		catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("TCPMessage==>"+ex.getMessage());
		}

	}

	/*
	 * дmacaddress �� schedule updatetime
	 */
	public String WriteMacAddrAndScheUpdate(String MacAddr,String Updattime) {

		String Xml="";
		try {
			StringWriter writer= new StringWriter();
			Serializer.setOutput(writer);
			Serializer.startTag("", "Messages");
			Serializer.attribute("", "PlayerLicense", MacAddr);
			Serializer.attribute("", "UpdateTime", Updattime);
			Serializer.endTag("", "Messages");
			Serializer.endDocument();
			Xml = writer.toString();


		} catch (Exception e) {
			LoggerHelper.WriteLogfortxt("TCPMessage WriteMacAddrAndScheUpdate==>"+e.getMessage());
		}
		return Xml;
	}


}
