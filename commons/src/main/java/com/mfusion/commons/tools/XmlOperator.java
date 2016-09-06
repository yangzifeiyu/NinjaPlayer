package com.mfusion.commons.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.R.xml;
/**
 * Created by guoyu on 7/14/2016.
 */
public class XmlOperator {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	
	public XmlOperator(){
		try {
			
			factory= DocumentBuilderFactory
					.newInstance();
			builder= factory.newDocumentBuilder();
			
		} catch (Exception e) {
			// TODO: handle exception
			LogOperator.WriteLogfortxt("XmlOperator==>"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Document createXmlDocument(){
		
		try {
			
			return builder.newDocument();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>createXmlDocument :"+e.getMessage());
		}
		
		return null;
	}
	
	public Document createXmlDocument(String rootNode){
		
		try {
			
			Document document=builder.newDocument();
			Element rootElement=document.createElement(rootNode);
			document.appendChild(rootElement);
			return document;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>createXmlDocument :"+e.getMessage());
		}
		
		return null;
	}
	
	public Document getXmlDocument(String xmlPath){
	
		try {
			
			File xmlFile=new File(xmlPath);
			if(xmlFile.isDirectory())
				return null;
			
			if(xmlFile.exists())
				return builder.parse("file://"+xmlPath);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>getXmlDocument :"+e.getMessage());
		}
		
		return null;
	}

	public Document getXmlDocument(File xml){

		try {

			return builder.parse(xml);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>getXmlDocument :"+e.getMessage());
		}

		return null;
	}

	public Document getXmlDocument(InputStream xmlStream){
	
		try {
			
			return builder.parse(xmlStream);
			
		} catch (Exception e) {
			// TODO: handle exception
			LogOperator.WriteLogfortxt("XmlOperator==>getXmlDocument :"+e.getMessage());
		}
		
		return null;
	}
	
	public Boolean saveXmlDocument(Document xmlDoc,String xmlPath){
		try
		{
			File oldFlie=new File(xmlPath);
			if(oldFlie.exists())
				oldFlie.renameTo(new File(xmlPath+".old"));
			
			File rootFile=oldFlie.getParentFile();
			if(!rootFile.exists())
				rootFile.mkdirs();

			DOMSource doms = new DOMSource(xmlDoc);  
	        File f = new File(xmlPath);  
	        StreamResult sr = new StreamResult(f.getAbsolutePath());
	        TransformerFactory tf = TransformerFactory.newInstance();  
            Transformer t = tf.newTransformer();
            Properties properties = t.getOutputProperties();  
            properties.setProperty(OutputKeys.ENCODING, "UTF-8");
            properties.setProperty(OutputKeys.VERSION, "1.1");
            t.setOutputProperties(properties);  
            t.transform(doms, sr);  
            
            return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>saveXmlDocument :"+e.getMessage());
			File oldFlie=new File(xmlPath+".old");
			if(oldFlie.exists())
				oldFlie.renameTo(new File(xmlPath));
			else
				FileOperator.deleteFile(oldFlie.getParent());
			return false;
		}
	}
	
	public ArrayList<Element> getAllTypeSubNodeList(Node parentNode){
		
		try {
			
			if(parentNode==null)
				return null;

			NodeList subNodes=parentNode.getChildNodes();
			
			ArrayList<Element> selectNodes=new ArrayList<Element>();
			for (int index=0;index< subNodes.getLength();index++) {
				selectNodes.add((Element)subNodes.item(index));
			}
			
			return selectNodes;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Element> getSubNodeList(Node parentNode,String nodeName){
		
		try {
			
			if(parentNode==null)
				return null;

			NodeList subNodes=null;
			if(nodeName==null||nodeName.isEmpty())
				subNodes=parentNode.getChildNodes();
			else 
				subNodes=((Element)parentNode).getElementsByTagName(nodeName);
			
			ArrayList<Element> selectNodes=new ArrayList<Element>();
			for (int index=0;index< subNodes.getLength();index++) {
				Node node=subNodes.item(index);
				if(node.getNodeType()==Node.ELEMENT_NODE)
					selectNodes.add((Element)subNodes.item(index));
			}
			
			return selectNodes;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>getSubNodeList :"+e.getMessage());
		}
		return null;
	}
	
	public Element getSubNode(Node parentNode,String nodeName){
		
		try {
			
			if(parentNode==null)
				return null;

			NodeList subNodes=null;
			if(nodeName==null||nodeName.isEmpty())
				subNodes=parentNode.getChildNodes();
			else 
				subNodes=((Element)parentNode).getElementsByTagName(nodeName);
			
			if(subNodes!=null&&subNodes.getLength()>0)
				return (Element)subNodes.item(0);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>getSubNode :"+e.getMessage());
		}
		return null;
	}

	public void copyNode(Document document,Element destElement,ArrayList<Element> sonNodeList){
		
		for (int i = 0; i < sonNodeList.size(); i++) {
			
			copyNode(document, destElement, sonNodeList.get(i));
		}
		
	}
	
	protected void copyNode(Document document,Element destElement,Node oldSon){
		try {
			Element newSon=null;
			if(oldSon.getNodeType()==Node.TEXT_NODE){
				if(destElement.getChildNodes().getLength()==0){
					destElement.setTextContent(oldSon.getTextContent());
					destElement.setNodeValue(oldSon.getNodeValue());
				}
				return;
			}
			
			newSon=document.createElement(oldSon.getNodeName());
			destElement.appendChild(newSon);
			
			if (oldSon.hasAttributes()) {  
	            NamedNodeMap attributes = oldSon.getAttributes();  
	            for (int j = 0; j < attributes.getLength(); j++) {  
	                String attribute_name = attributes.item(j).getNodeName();  
	                String attribute_value = attributes.item(j).getNodeValue();  
	                newSon.setAttribute(attribute_name, attribute_value);  
	            }  
	        } 
			
			if(oldSon.hasChildNodes()){
				Node subNode=null;
				NodeList subNodes=oldSon.getChildNodes();
				for (int index=0;index< subNodes.getLength();index++) {
					subNode=subNodes.item(index);
					if(subNode.getNodeType()==Node.TEXT_NODE||subNode.getNodeType()==Node.ELEMENT_NODE)
						copyNode(document, newSon, subNode);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogOperator.WriteLogfortxt("XmlOperator==>copyNode :"+e.getMessage());
		}
	}
}
