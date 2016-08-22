package com.mfusion.player.library.Database.Xml;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.mfusion.player.library.Helper.FileHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.mfusion.player.common.Setting.Player.PlayerStoragePath;


public class XMLHelper {

	public String xmlStr="";
	public static String xmlName = "MFusion.xml";
	private File xmlFile = null;
	private Element root = null;
	private Document doc = null;
	public static int XPATH_TAG_MODE = 0;
	public static int XPATH_POSITION_MODE = 1;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;

	public XMLHelper() {
		try 
		{
			factory= DocumentBuilderFactory
					.newInstance();
			builder= factory.newDocumentBuilder();
			File file=new File(PlayerStoragePath.XMLStorage + xmlName);
			if(file.exists())
			{
				xmlFile = new File(PlayerStoragePath.XMLStorage + xmlName);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public XMLHelper(String xml)
	{
		try
		{
			this.xmlStr=xml;
			factory= DocumentBuilderFactory
					.newInstance();
			builder= factory.newDocumentBuilder();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void SaveXml(String xmlStr)
	{
		try
		{
			
			FileHelper.IsExistsAndDeleteAndCreate(PlayerStoragePath.XMLStorage + xmlName);

			RandomAccessFile raf = new RandomAccessFile(PlayerStoragePath.XMLStorage + xmlName, "rwd");
			raf.write(xmlStr.getBytes());
			raf.close();
			raf=null;

			//LoggerHelper.WriteLogfortxt("SaveXML==>end");

			xmlFile = new File(PlayerStoragePath.XMLStorage + xmlName);
			//LoggerHelper.WriteLogfortxt("SaveXML==>end1");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}



	private Document getDoc() {
		if (doc != null)
			return doc;

		try {

			if(xmlFile!=null)
			{
				doc = builder.parse(xmlFile);
			}
			else if(!this.xmlStr.equals("")&&!this.xmlStr.isEmpty())
			{
				InputSource source = null;  
				StringReader reader = null;  
				try
				{
					reader = new StringReader(xmlStr);  
					source = new InputSource(reader);//ʹ���ַ��������µ�����Դ  
					doc = builder.parse(source);  
				}catch(Exception ex)
				{

				}
				finally
				{
					if(reader != null){  
						reader.close();  
					}  
				}
			}

			return doc;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	// ��ȡtag������elements
	public Element[] getElementsByTag(String tag) {
		try
		{
			if (getDoc() == null) {
				return null;
			}
			ArrayList<Element> elements = new ArrayList<Element>();
			NodeList nodeList = this.getDoc().getElementsByTagName(tag);
			Element[] results = null;

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node instanceof Element) {
					elements.add((Element) node);
				}
			}
			results = new Element[elements.size()];

			return elements.toArray(results);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;}

	}

	// ��ȡelement�������е��ӽڵ�
	public Element[] getElementsByParentElement(Element parent) {
		try
		{
			ArrayList<Element> elements = new ArrayList<Element>();
			NodeList nodeList = parent.getChildNodes();
			Element[] results = null;

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node instanceof Element) {
					elements.add((Element) node);
				}
			}
			results = new Element[elements.size()];

			return elements.toArray(results);
		}catch(Exception e){
			e.printStackTrace();
			return null;}
	}

	public Element getRoot() {
		if (root != null) {
			return root;
		}

		if (getDoc() == null) {
			return null;
		}

		root = getDoc().getDocumentElement();

		return root;
	}

	public Element getElementByXPath(String xpath) {
		String[] items = xpath.split("\\\\");
		Element root = getRoot();

		if (root == null) {
			return null;
		}

		for (int i = 0; i < items.length; i++) {
			String item = items[i].trim();
			int index = 0;
			int counter = 0;
			boolean found = false;

			try {
				int p1 = item.lastIndexOf("(");
				int p2 = item.lastIndexOf(")");

				index = Integer.valueOf(item.substring(p1 + 1, p2));
				item = item.substring(0, p1).trim();
				System.out.println(item.substring(p1, p2));
			} catch (Exception e) {
				// just keep index to the default value 0
			}

			// root
			if (i == 0) {
				if (index != 0) {
					return null;
				}

				if (item.length() == 0 || root.getTagName().equals(item)) {
					continue;
				} else {
					return null;
				}
			}

			for (int j = 0; j < root.getChildNodes().getLength(); j++) {
				Node node = root.getChildNodes().item(j);
				if (node instanceof Element) {
					Element element = (Element) node;

					// notag_mode
					if (item.length() == 0) {
						if (counter == index) {
							root = element;
							found = true;
							break;
						}

						counter++;
					}
					// tag mode
					else if (element.getTagName().equals(item)) {
						if (counter == index) {
							root = element;
							found = true;
							break;
						} else {
							counter++;
						}
					}
				}
			}

			if (!found) {
				return null;
			}
		}

		return root;
	}

	private String findXpath(Element element, int mode) {
		Node parent = null;
		int index = 0;
		String tagName = element.getTagName();

		parent = element.getParentNode();

		if (parent != null && parent instanceof Element) {
			for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
				Node node = parent.getChildNodes().item(i);
				if (node instanceof Element) {
					if (mode == XMLHelper.XPATH_POSITION_MODE) {
						index++;
					}

					Element one = (Element) node;
					if (one.getTagName().equals(tagName)) {
						if (one.equals(element)) {
							break;
						}

						if (mode == XMLHelper.XPATH_TAG_MODE) {
							index++;
						}
					}
				}
			}

			String tmp = "";
			if (mode == XMLHelper.XPATH_POSITION_MODE) {
				tagName = "";
				index--;

				tmp = "(" + index + ")";
			} else {
				tmp = tagName + (index > 0 ? "(" + index + ")" : "");
			}

			return findXpath((Element) parent, mode) + "\\" + tmp;
		} else {
			if (mode == XMLHelper.XPATH_POSITION_MODE) {
				return "(0)";
			} else if (mode == XMLHelper.XPATH_TAG_MODE) {
				return tagName;
			}

			// default mode is XPATH_TAG_MODE
			return tagName;
		}
	}

	public String[] getXPaths(String tag) {
		ArrayList<String> xpaths = new ArrayList<String>();
		Element[] elements = getElementsByTag(tag);
		String[] results = null;

		for (Element element : elements) {
			xpaths.add(findXpath(element, XPATH_TAG_MODE));
		}
		results = new String[xpaths.size()];

		return xpaths.toArray(results);
	}





}
