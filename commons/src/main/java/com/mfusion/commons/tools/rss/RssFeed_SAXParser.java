package com.mfusion.commons.tools.rss;

import com.mfusion.commons.tools.LogOperator;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



public class RssFeed_SAXParser {
	private CodepageDetectorProxy detector;
	private SAXParserFactory saxParserFactory;
	private  SAXParser saxParser;
	private XMLReader xmlReader ;
	public RssFeed_SAXParser() throws ParserConfigurationException, SAXException
	{
		detector = CodepageDetectorProxy.getInstance();
		// �����������̽����
		detector.add(JChardetFacade.getInstance());

		saxParserFactory = SAXParserFactory.newInstance(); // ����SAX��������
		saxParser = saxParserFactory.newSAXParser(); // ����������������
		xmlReader = saxParser.getXMLReader(); // ͨ��saxParser����xmlReader�Ķ���
	}

	public RssFeed getFeed(String urlStr) throws ParserConfigurationException,
	SAXException, IOException {
		URL url = new URL(urlStr);

		RssHandler rssHandler = new RssHandler();
		xmlReader.setContentHandler(rssHandler);
		Charset charset = detector.detectCodepage(url);
		// �õ��������
		String encodingName = charset.name();
		System.out.println(encodingName);

		InputSource inputSource = null;
		InputStream stream = null;

		if ("GBK".equals(encodingName)) {
			stream = url.openStream();
			// ͨ��InputStreamReader�趨���뷽ʽ
			InputStreamReader streamReader = new InputStreamReader(stream,
					encodingName);
			inputSource = new InputSource(streamReader);
			xmlReader.parse(inputSource);
			return rssHandler.getRssFeed();
		} else {
			// ��utf-8����
			inputSource = new InputSource(url.openStream());
			inputSource.setEncoding("UTF-8");
			xmlReader.parse(inputSource);
			return rssHandler.getRssFeed();
		}
	}

	/**
	 * ���Զ��URL�ļ��ı����ʽ
	 */
	public static String getReomoteURLFileEncode(URL url) {

		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		detector.add(new ParsingDetector(false));
		detector.add(JChardetFacade.getInstance());
		detector.add(ASCIIDetector.getInstance());
		detector.add(UnicodeDetector.getInstance());
		Charset charset = null;
		try {
			System.out.println(url);
			charset = detector.detectCodepage(url);
		} catch (Exception ex) {
			ex.printStackTrace();
			LogOperator.WriteLogfortxt("RssFeed_SAXParser==>getReomoteURLFileEncode :"+ex.getMessage());
		}
		if (charset != null) {
			return charset.name();
		} else {
			return "utf-8";
		}
	}
}
