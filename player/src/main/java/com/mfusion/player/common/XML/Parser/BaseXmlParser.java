package com.mfusion.player.common.XML.Parser;

import com.mfusion.player.common.XML.Parser.*;
public class BaseXmlParser {

	private BaseXmlParser m_current_xmlparser=null;
	
	public BaseXmlParser(){}
	
	public BaseXmlParser(String xmlVresion){
		if(xmlVresion!=null&&!xmlVresion.isEmpty())
			this.m_current_xmlparser=new AndroidXmlParser();
		else
			this.m_current_xmlparser=new WindowsXmlParser();
	}
	
	public void initPlayingContent(){
		this.m_current_xmlparser.parseXml();
	}
	
	protected void parseXml(){}
}
