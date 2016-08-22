package com.mfusion.commons.tools.rss;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RssHandler extends DefaultHandler {

	RssFeed rssFeed;
	RssItem rssItem;

	String lastElementName = "";// ��Ǳ��������ڱ���ڽ�����������ǹ��ĵļ�����ǩ�����������ǹ��ĵı�ǩ����0

	final int RSS_TITLE = 1;// ���� title ��ǩ������1��ע��������title�������Ƕ�������item�ĳ�Ա������
	final int RSS_LINK = 2;// ���� link ��ǩ������2
	final int RSS_AUTHOR = 3;
	final int RSS_CATEGORY = 4;// ����category��ǩ,���� 4
	final int RSS_PUBDATE = 5; // ����pubdate��ǩ,����5,ע��������pubdate,�����Ƕ�������item��pubdate��Ա������
	final int RSS_COMMENTS = 6;
	final int RSS_DESCRIPTION = 7;// ���� description ��ǩ������3

	int currentFlag = 0;
	private List<String> delete;//RSSContent���List�е��ַ����ַ�">"֮��������ַ����

	public RssHandler() {

		this.delete = new ArrayList<String>();
		/*this.delete.add("<p");
		this.delete.add("</p");
		this.delete.add("<a");
		this.delete.add("</a");*/
		this.delete.add("<br");
		//this.delete.add("<img");
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		rssFeed = new RssFeed();
		rssItem = new RssItem();

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		// ��ȡ�ַ�
		String text = new String(ch, start, length);
		text=text.replace("\n", " ");
		text=text.replace("\r", " ");
		text=text.replace("\t", " ");
		text=ContentFilter(text);

		switch (currentFlag) {
		case RSS_TITLE:
			rssItem.setTitle(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		case RSS_PUBDATE:
			rssItem.setPubdate(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		case RSS_CATEGORY:
			rssItem.setCategory(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		case RSS_LINK:
			rssItem.setLink(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		case RSS_AUTHOR:
			rssItem.setAuthor(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		case RSS_DESCRIPTION:
			rssItem.setDescription(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		case RSS_COMMENTS:
			rssItem.setComments(text);
			currentFlag = 0;// �����������Ϊ��ʼ״̬
			break;
		default:
			break;
		}
	}



	private String ContentFilter(String content)
	{
		try
		{
			int indexFir = -1;
			int indexEnd = -1;
			for (String item :delete)
			{
				while (true)
				{
					indexFir = content.indexOf(item);
					if (indexFir > -1)
					{
						indexEnd = content.indexOf(">", indexFir);
						if (indexEnd > -1)
						{
							content = content.replace(content.substring(indexFir, indexEnd+1), " ");
						}
						else
						{
							indexFir = indexEnd = -1;
							break;
						}
					}
					else
					{
						indexFir = indexEnd = -1;
						break;
					}
				}
			}
		}
		catch (Exception ex) { 
			ex.printStackTrace();
		}
		return content;
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if ("chanel".equals(localName)) {
			// �����ǩ��û�����ǹ��ĵ����ݣ����Բ������?currentFlag=0
			currentFlag = 0;
			return;
		}
		if ("item".equals(localName)) {
			rssItem = new RssItem();
			return;
		}
		if ("title".equals(localName)) {
			currentFlag = RSS_TITLE;
			return;
		}
		if ("description".equals(localName)) {
			currentFlag = RSS_DESCRIPTION;
			return;
		}
		if ("link".equals(localName)) {
			currentFlag = RSS_LINK;
			return;
		}
		if ("pubDate".equals(localName)) {
			currentFlag = RSS_PUBDATE;
			return;
		}
		if ("category".equals(localName)) {
			currentFlag = RSS_CATEGORY;
			return;
		}

		if ("author".equals(localName)) {
			currentFlag = RSS_AUTHOR;
			return;
		}

		if ("comments".equals(localName)) {
			currentFlag = RSS_COMMENTS;
			return;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		// ������һ��item�ڵ����ͽ�rssItem��ӵ�rssFeed�С�
		if ("item".equals(localName)) {

			rssFeed.addItem(rssItem);
			return;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	public RssFeed getRssFeed() {
		return rssFeed;
	}


}