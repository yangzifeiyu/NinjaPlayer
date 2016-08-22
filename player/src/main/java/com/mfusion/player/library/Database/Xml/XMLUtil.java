package com.mfusion.player.library.Database.Xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The XMLUtil helps you deal with xml easy.<br>
 * The class is so tiny.It's an opensource api and tested by junit.<br> 
 * @author ShiMingHua,
 * @version 2009.7.30
 */
public class XMLUtil
{
	private File xmlFile=null;
	private Element root=null;
	private Document doc=null;
	public static int XPATH_TAG_MODE=0;
	public static int XPATH_POSITION_MODE=1;
	
	/**
	 * Set the xml file for the xml util. The xml util will read xml from this file.<br>
	 * If the file is not valid,getRoot() will be null.<br>
	 * 
	 * @param file java.io.File as a xml file 
	 */
	public XMLUtil(File file)
	{
		if (file==null)
		{
			System.out.println("The input param file is null.");
			return;
		}
		
		xmlFile=file;
	}
	
	/**
	 * Set the xml file for the xml util. The xml util will read xml from this file.<br>
	 * If the file is not valid,getRoot() will be null.<br>
	 * 
	 * @param filename a string presents the name of the xml file.
	 */
	public XMLUtil(String filename)
	{
		try
		{
			xmlFile=new File(filename);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the xml document.
	 * 
	 * @param doc the xml document
	 */
	public XMLUtil(Document doc)
	{
		this.doc=doc;
	}
	
	/*
	 * Get the doc element in the xml file.
	 * @return element
	 *  the root element
	 */
	private Document getDoc()
	{
		if (doc!=null)
		{
			return doc;
		}
		
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(xmlFile);
			
			return doc;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the root element of the xml <br>
	 * Note: If the input xml file is invalid, getRoot() will be null.<br>
	 * 
	 * @return the root element
	 */
	public Element getRoot()
	{
		if (root!=null)
		{
			return root;
		}

		if (getDoc()==null)
		{
			return null;
		}
		
		root=getDoc().getDocumentElement();
		
		return root;
	}
	
	/* 
	 * Example: support xpath 2 modes
	 * abc\aaa     tag_mode
	 * abc\aaa(0)  tag_mode
	 * (0)\(0)     position_mode
	 * @param element
	 * @return
	 */
	private String findXpath(Element element,int mode)
	{
		Node parent=null;
		int index=0;
		String tagName=element.getTagName();
		
		parent=element.getParentNode();
		
		if (parent!=null&&parent instanceof Element)
		{
			for (int i=0;i<parent.getChildNodes().getLength();i++)
			{
				Node node=parent.getChildNodes().item(i);
				if (node instanceof Element)
				{
					if (mode==XMLUtil.XPATH_POSITION_MODE)
					{
						index++;
					}
					
					Element one=(Element)node;
					if (one.getTagName().equals(tagName))
					{
						if (one.equals(element))
						{
							break;
						}
						
						if (mode==XMLUtil.XPATH_TAG_MODE)
						{
							index++;
						}					
					} 
				}
			}
			
			String tmp="";
			if (mode==XMLUtil.XPATH_POSITION_MODE)
			{
				tagName="";
				index--;
				
				tmp="("+index+")";
			}
			else
			{
				tmp=tagName+(index>0?"("+index+")":"");
			}
			
			return findXpath((Element)parent,mode)+"\\"+tmp;
		}
		else
		{
			if (mode==XMLUtil.XPATH_POSITION_MODE)
			{
				return "(0)";
			}
			else if (mode==XMLUtil.XPATH_TAG_MODE)
			{
				return tagName;
			}
			
			//default mode is XPATH_TAG_MODE
			return tagName;
		}
	}
	
	/**
	 * Get the element by given its xpath.<br>
	 * What's the xpath? <br>
	 * There are 2 modes in XMLUtil: XPATH_TAG_MODE and XPATH_POSITION_MODE<br>
	 * The param xpath support both 2 modes<br>
	 * <br>
	 * In the sample test1.xml,<br>
	 * <br>
	 * for XPATH_TAG_MODE:<br>
	 *  the xpath of the first tag "ccc" is abc\ccc or abc\ccc(0),here ccc(0) means the first "ccc" tag.<br>
	 *  the xpath of the second tag "ccc" is abc\ccc(1),ccc(1) means the second "ccc" tag.<br>
	 *  <br>
	 * for XPATH_POSITION_MODE:<br>
	 *  the xpath of the first tag "ccc" is (0)\(1). <br>
	 *  (0) means the first tag.So its the root tag.<br>
	 *  (1) means the second tag.So its the second child tag in the root.<br>
	 *  (0)\(1) has the same meaning as abc\(1),abc(0)\ccc,abc\ccc(0)<br>
	 *  <br>
	 *  the xpath of the second tag "ccc" is (0)\(2).<br>
	 *  (0) means the root tag.<br>
	 *  (2) means the third tag.So its the third child tag in the root.<br>
	 *  (0)\(2) has the same meaning as abc(0)\(2),abc\ccc(1),abc(0)\ccc(1)<br>
	 *  <br>
	 * Note:If the xpath is invalid or no element pointed by the xpath, it will return null.<br>
	 * The xpath accepts the mix of XPATH_TAG_MODE and XPATH_POSITION_MODE,such as abc(0)\(2).<br>
	 * 
	 * @param xpath the xpath 
	 * @return the element pointed by the xpath
	 */
	public Element getElementByXPath(String xpath)
	{
		String[] items=xpath.split("\\\\");
		Element root=getRoot();
		
		if (root==null)
		{
			return null;
		}
		
		for (int i=0;i<items.length;i++)
		{
			String item=items[i].trim();
			int index=0;
			int counter=0;
			boolean found=false;
			
			try
			{
				int p1=item.lastIndexOf("(");
				int p2=item.lastIndexOf(")");
				
				index=Integer.valueOf(item.substring(p1+1, p2));
				item=item.substring(0, p1).trim();
				System.out.println(item.substring(p1, p2));
			}
			catch(Exception e)
			{
				//just keep index to the default value 0 
			}
			
			//root
			if (i==0)
			{
				if (index!=0)
				{
					return null;
				}
				
				if (item.length()==0||root.getTagName().equals(item))
				{
					continue;					
				}
				else
				{
					return null;
				}
			}
			
			for (int j=0;j<root.getChildNodes().getLength();j++)
			{
				Node node=root.getChildNodes().item(j);
				if (node instanceof Element)
				{
					Element element=(Element)node;
					
					//notag_mode
					if (item.length()==0)
					{
						if (counter==index)
						{
							root=element;
							found=true;
							break;
						}
						
						counter++;
					}
					//tag mode
					else if (element.getTagName().equals(item))
					{
						if (counter==index)
						{
							root=element;
							found=true;
							break;
						}
						else
						{
							counter++;
						}
					}
				}
			}
			
			if (!found)
			{
				return null;
			}
		}
		
		return root;
	}
	
	/**
	 * Get a xpath as a specified mode.
	 * Current version support XPATH_TAG_MODE and XPATH_POSITION_MODE.
	 * 
	 * @param xpath the xpath to be converted
	 * @param mode the xpath mode
	 * @return the converted xpath
	 */
	public String getXPathByMode(String xpath,int mode)
	{
		if (mode!=XPATH_TAG_MODE&&mode!=XPATH_POSITION_MODE)
		{
			return "";
		}
		
		Element element=getElementByXPath(xpath);
		
		if (element==null)
		{
			return "";
		}
		
		return findXpath(element, mode);
	}
	
	/**
	 * Get all attribute values by the tag name and attribute name.<br>
	 *  
	 * @param tag the specified tag
	 * @param attr the sepcified attribute
	 * @return A String array contains attribute values found.
	 */
	public String[] getAttrValues(String tag,String attr)
	{
		ArrayList<String> attrValues=new ArrayList<String>();
		Element[] elements=getElementsByTag(tag);
		String[] results=null;
		
		for (Element element:elements)
		{
			if (element.hasAttribute(attr))
			{
				attrValues.add(element.getAttribute(attr));
			}
		}
		results=new String[attrValues.size()];
		
		return attrValues.toArray(results);
	}
	
	/**
	 * Get all contents by the tag name.<br>
	 *  
	 * @param tag the specified tag
	 * @return A String array contains contents found.
	 */
	public String[] getContents(String tag)
	{
		ArrayList<String> contents=new ArrayList<String>();
		Element[] elements=getElementsByTag(tag);
		String[] results=null;
		
		for (Element element:elements)
		{
			contents.add(element.getTextContent());
		}
		results=new String[contents.size()];
		
		return contents.toArray(results);
	}
	
	/**
	 * Get all xpaths by the tag name.<br>
	 * The xpaths mode is XPATH_TAG_MODE.<br>
	 * You can convert xpath to other mode by calling getXPathByMode()<br>
	 * 
	 * @param tag the specified tag
	 * @return A String array contains xpaths found.
	 */
	public String[] getXPaths(String tag)
	{
		ArrayList<String> xpaths=new ArrayList<String>();
		Element[] elements=getElementsByTag(tag);
		String[] results=null;
		
		for (Element element:elements)
		{
			xpaths.add(findXpath(element,XPATH_TAG_MODE));
		}
		results=new String[xpaths.size()];
		
		return xpaths.toArray(results);
	}
	
	/**
	 * Get elements whose tag name equals the param.
	 * @param tag the tag name of elements
	 * @return element found by the tag
	 */
	public Element[] getElementsByTag(String tag)
	{
		ArrayList<Element> elements=new ArrayList<Element>();
		NodeList nodeList=this.getDoc().getElementsByTagName(tag);
		Element[] results=null;
		
		for (int i=0;i<nodeList.getLength();i++)
		{
			Node node=nodeList.item(i);
			
			if (node instanceof Element)
			{
				elements.add((Element)node);
			}
		}
		results=new Element[elements.size()];
		
		return elements.toArray(results);
	}
	
	/**
	 * find the attribute value in the nth specified tag.
	 * @param tag the specified tag
	 * @param attr the specified attriubte  
	 * @return the attribute value in the nth specified tag
	 */
	public String getAttrValue(String tag,String attr,int n)
	{
		String[] results=getAttrValues(tag,attr);
		
		if (results.length==0 || n<0 || n>=results.length)
		{
			return "";
		}
		else
		{
			return results[n];
		}
	}
	
	/**
	 * find the attr value in the first specified tag.
	 * @param tag the specified tag
	 * @param attr the specified attriubte 
	 * @return the attribute value in the first specified tag
	 */
	public String getAttrValue(String tag,String attr)
	{
		return getAttrValue(tag,attr,0);
	}
	
	/**
	 * find the content in the nth specified tag
	 * @param tag the specified tag
	 * @return the content in the nth specified tag
	 */
	public String getContent(String tag,int n)
	{
		String[] results=getContents(tag);
		
		if (results.length==0 || n<0 || n>=results.length)
		{
			return "";
		}
		else
		{
			return results[n];
		}
	}
	
	/**
	 * find the content in the first specified tag
	 * @param tag the specified tag
	 * @return the content in the first specified tag
	 */
	public String getContent(String tag)
	{
		return getContent(tag,0);
	}
	
	/**
	 * find the xpath in the nth specified tag
	 * @param tag the specified tag
	 * @return the xpath of the nth specified tag
	 */
	public String getXPath(String tag,int n)
	{
		String[] results=getXPaths(tag);
		
		if (results.length==0 || n<0 || n>=results.length)
		{
			return "";
		}
		else
		{
			return results[n];
		}
	}
	
	/**
	 * find the xpath in the first specified tag
	 * @param tag the specified tag
	 * @return the xpath of the first specified tag
	 */
	public String getXPath(String tag)
	{
		return getXPath(tag,0);
	}
	
	/**
	 * Add a new element as the last child of the parent.
	 *  
	 * @param parent the parent element
	 * @param tagname the tag name of the new element
	 * @return the new element added.
	 */
	public Element addElement(Element parent,String tagname)
	{
		Element elment=getDoc().createElement(tagname);
		
		if (parent==null)
		{
			elment.appendChild(parent);
		}
		else
		{
			parent.appendChild(elment);
		}
		
		return elment;
	}
	
	/**
	 * Insert a new element before an existed element.
	 * @param element the specified existed element 
	 * @param tagname the tag name of the new element.
	 * @return the new element
	 */
	public Element insertBefore(Element element,String tagname)
	{
		Element newElment=getDoc().createElement(tagname);
		Element parent=null;
		
		if (element==null)
		{
			return null;
		}
		else
		{
			parent=(Element)element.getParentNode();
			
			if (parent==null||!(parent instanceof Element))
			{
				return null;
			}
			
			return (Element)parent.insertBefore(newElment,element);
		}
	}
	
	/**
	 * Modify the tag name of an existed element.
	 * 
	 * @param element the specified element whose tag name will be modified.
	 * @param tagname the new tag name of the specified element
	 * @return the specified element
	 * 
	 *  Note:the root element can't change its tag name.
	 */
	public Element setElement(Element element,String tagname)
	{
		if (element==null)
		{
			return null;
		}
		
		Node parent=element.getParentNode();
		
		if (parent==null||!(parent instanceof Element))
		{
			return null;
		}
		
		Element newElement=getDoc().createElement(tagname);
		
		//copy all 
		while(element.getChildNodes().getLength()>0)
		{
			Node childNode=element.getFirstChild();
			newElement.appendChild(childNode);
		}
		
		//copy all attributes
		for (int i=0;i<element.getAttributes().getLength();i++)
		{
			Attr attr=(Attr)element.getAttributes().item(i);
			setAttrValue(newElement,attr.getName(),attr.getValue());
		}
		
		((Element)parent).insertBefore(newElement,element);
		((Element)parent).removeChild(element);
		
		return newElement;
	}
	
	/**
	 * Remove an existed element.
	 * 
	 * @param element the element to be removed.
	 * @return true if success. false if failed.
	 */
	public boolean removeElement(Element element)
	{
		if (element==null)
		{
			return false;
		}
		
		Element parent=(Element)element.getParentNode();
		
		if (parent==null)
		{
			return false;
		}
		
		parent.removeChild(element);
		
		return true;
	}
	
	/**
	 * Add or modify an element's attribute value 
	 * @param element the specified element
	 * @param attr the attribute to be added or modified
	 * @param value the value of the attribute
	 * @return true if success, false if failed.
	 */
	public boolean setAttrValue(Element element,String attr,String value)
	{
		if (element==null)
		{
			return false;
		}
		
		if (element.hasAttribute(attr))
		{
			element.setAttribute(attr,value);
		}
		else
		{
			Attr newAttr=getDoc().createAttribute(attr);
			newAttr.setValue(value);
			element.setAttributeNode(newAttr);
		}
		
		return true;
	}
	
	/**
	 * Remove a attribute in the specified element.
	 * @param element the specified element 
	 * @param attr the attribute anme
	 * @return true if success,false if failed.
	 */
	public boolean removeAttrValue(Element element,String attr)
	{
		if (element==null)
		{
			return false;
		}
		
		if (element.hasAttribute(attr))
		{
			element.removeAttribute(attr);
		}
		else
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Set the content of the specified element.
	 * @param element the specified element.
	 * @param content the new content value
	 * @return true if success,false if failed.
	 */
	public boolean setContent(Element element,String content)
	{
		if (element==null)
		{
			return false;
		} 
		
		element.setTextContent(content);
		
		return true;
	}
	
	/**
	 * Update the current xml into the file.(Write xml)
	 * @param file A Java.io.File presents the file to be written.
	 * @return true if success,false if failed.
	 * 
	 * Note:If the file is invalid,it will return false.
	 */
	public boolean update(File file)
	{
		if (file==null)
		{
			return false;
		}
		
		if (!file.exists()||!file.isFile())
		{
			try
			{
				if (!file.createNewFile())
				{
					return false;
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		try
		{
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * Update the current xml into the file.(Write xml)
	 * @param filename A String presents the file to be written.
	 * @return true if success,false if failed.
	 * 
	 * Note:If the file is invalid,it will return false.
	 */
	public boolean update(String filename)
	{	
		return update(new File(filename));
	}
	
	/**
	 * Update the current xml into the current file.<br>
	 * For exmaple,you new XMLUtil with "test1.xml", <br>
	 * all xml modifications you done will be updated to the "test1.xml".<br>
	 * 
	 * @return true if success,false if failed.
	 */
	public boolean update()
	{
		if (xmlFile==null)
		{
			return false;
		}
		
		return update(xmlFile);
	}
	
	public static void main(String[] args)
	{
		XMLUtil xmlUtil=new XMLUtil("h:\\test1.xml");		
		
		Element element=xmlUtil.getElementByXPath("abc\\(0)\\(0)");
		
		Element newElement=xmlUtil.addElement(element,"helloworld");//xmlUtil.insertBefore(element, "helloworld");
		xmlUtil.setAttrValue(newElement, "first", "1");
		xmlUtil.setAttrValue(newElement, "second", "2");
		xmlUtil.setAttrValue(newElement, "third", "3");
//
//		String xpath=xmlUtil.getXPath("helloworld");
//		element=xmlUtil.getElement(xpath);
//		System.out.println("tag name:"+element.getTagName()+",xpath="+xpath+",second="+element.getAttribute("second"));
//		
//		xmlUtil.removeAttrValue(element, "second");
//		System.out.println("tag name:"+element.getTagName()+",xpath="+xpath+",second="+element.getAttribute("second"));
//		xmlUtil.removeAttrValue(element, "second");
//		
//		xmlUtil.setContent(element, "hello world");
//		System.out.println("tag name:"+element.getTagName()+",xpath="+xpath+",content="+xmlUtil.getContent("helloworld"));
//		
//		xmlUtil.setElement(element, "hellosmh");
//		xpath=xmlUtil.getXPath("hellosmh");
//		element=xmlUtil.getElement(xpath);
//		
//		if (element!=null)
//		{
//			System.out.println();
//			System.out.println("tag name:"+element.getTagName()+",xpath="+xpath+",first="+element.getAttribute("first"));
//			System.out.println("tag name:"+element.getTagName()+",xpath="+xpath+",content="+xmlUtil.getContent("helloworld"));
//			System.out.println("tag name:"+element.getTagName()+",xpath="+xpath+",content="+xmlUtil.getContent("hellosmh"));
//		}
//		
//		element=xmlUtil.getElement("abc\\aaa");
//		xmlUtil.setElement(element, "aaaa");
//		
//        element=xmlUtil.getElement("abc\\ccc");
//		xmlUtil.removeElement(element);
		
		System.out.println("update to new file:"+xmlUtil.update("h:\\test2.xml"));
		
	}
}
