package com.mfusion.player.common.Helper.RSS;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.mfusion.commons.tools.rss.RssFeed;
import com.mfusion.commons.tools.rss.RssFeed_SAXParser;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.library.Helper.DateTimeHelper;

public class RSSContentHelper {
	private int interval = 1000;
	private boolean RUNNING=false;

	private Thread m_thread= new Thread(new Runnable() {

		public void run() {
			// TODO Auto-generated method stub

			while (RUNNING) {
				m_checker_Elapsed();
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}});

	private HashMap<String, RssFeed> m_contents;
	private RssFeed_SAXParser rssDownloader;

	public RSSContentHelper() throws ParserConfigurationException, SAXException
	{
		this.m_contents = new HashMap<String, RssFeed>();
		this.rssDownloader=new RssFeed_SAXParser();

	}

	public void Register(String URL)
	{

		if (this.m_contents.containsKey(URL)) return;
		else
		{
			this.m_contents.put(URL, null);
			RUNNING=true;
			m_thread.start();
		}

	}

	public void Unregister(String url)
	{

		this.m_contents.remove(url);

		if (this.m_contents.size() == 0)
			this.RUNNING=false;

	}
	private void m_checker_Elapsed()
	{

		try
		{
			this.UpdateRSSContents();
		}
		catch(Exception ex) 
		{ 

		}



	}

	private void UpdateRSSContents()
	{

		for (Iterator<String> itr = this.m_contents.keySet().iterator(); itr.hasNext();)
		{
			try
			{
				String url = (String) itr.next();
				RssFeed value = (RssFeed) this.m_contents.get(url);
				if (value==null||value.UpdateTime==null||DateTimeHelper.CompareTime(this.m_contents.get(url).UpdateTime,DateTimeHelper.GetAddedDate(MainActivity.Instance.Clock.Now,-60,MainActivity.Instance.PlayerSetting.Timezone)) <= 0)
				{

					RssFeed content=this.rssDownloader.getFeed(url);
					if(content!=null)
					{
						content.UpdateTime = MainActivity.Instance.Clock.Now;
						this.m_contents.put(url, content);
					}
				}
			}
			catch (Exception ex)
			{
				//LoggerHelper.WriteLogfortxt("RSSContentHelper UpdateRSSContents==> " + ex.getMessage()); 
			}
		}
	}
	public RssFeed RSSContent(String URL)
	{
		RssFeed feed=null;
		try
		{
			MainActivity.Instance.ResourceCenter.RegeditControl(URL);
			feed= this.m_contents.get(URL);
		}
		catch(Exception ex)
		{

		}
		return feed;

	}
}



