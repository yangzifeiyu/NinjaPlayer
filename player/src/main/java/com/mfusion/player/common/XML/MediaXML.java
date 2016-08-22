/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *����Medias
 */
package com.mfusion.player.common.XML;
import java.util.Date;
import java.util.Hashtable;


import org.w3c.dom.Element;

import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Enum.FileType;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Storage.MediaStorageHelper;

import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class MediaXML {
	private XMLHelper xmlHelper;
	private Hashtable<Integer,MediaFile> mediamap;
	private Date currentTime;
	private String currentdate;
	private MediaStorageHelper mediahelper;
	/*
	 * ���캯��
	 */
	public MediaXML() {
		try
		{
			this.xmlHelper = new XMLHelper();
			this.mediamap = new Hashtable<Integer,MediaFile>();
			this.currentTime = MainActivity.Instance.Clock.Now;
			this.currentdate=DateTimeHelper.ConvertToString(currentTime, "yyyy,MM,dd");
			this.mediahelper=new MediaStorageHelper(MainActivity.Instance);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			LoggerHelper.WriteLogfortxt("MediaXml==>"+ex.getMessage());
		}

	}

	/*
	 * ��ȡmedia list
	 */
	public Hashtable<Integer,MediaFile> GetMediaMap() {
		try {

			if (xmlHelper != null) {
				// medias
				Element display_media_element = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\Medias");
				if(display_media_element==null)
					return this.mediamap;
				Element[] medias = xmlHelper
						.getElementsByParentElement(display_media_element);
				int len= medias.length;
				for (int i = 0; i <len; i++) {
					MediaFile media = new MediaFile();
					Element element = medias[i];
					String mediaid = element.getAttribute("Id");
					String startdate = element.getAttribute("StartDate");
					String expirydate = element.getAttribute("EndDate");
					String mediasource = element.getAttribute("MediaSource");
					FileType mediatype = FileType.fromString(element.getAttribute("MediaType"));
					String mediapath = element.getAttribute("MediaPath");
					String medianame = mediapath.substring(
							mediapath.lastIndexOf("/")+1,
							mediapath.length());
					String mediaLength=element.getAttribute("MediaLength");
							
					String mediaext="";
					
					if(mediatype.equals(FileType.Image)||mediatype.equals(FileType.Video))
						mediaext=medianame.substring(medianame.indexOf("."));

					//�ж��ļ��Ƿ�����Ч�ڷ�Χ��
					//if((startdate.equals("")||DateTimeHelper.CompareTime(startdate, this.currentTime, "yyyy,MM,dd")>=0)&&(expirydate.equals("")||DateTimeHelper.CompareTime(expirydate, this.currentTime, "yyyy,MM,dd")<=0))
					if((startdate.equals("")||DateTimeHelper.CompareTime(startdate, this.currentTime, "yyyy,MM,dd")<=0)&&(expirydate.equals("")||DateTimeHelper.CompareTime(expirydate, this.currentTime, "yyyy,MM,dd")>=0))
					{
						media.MediaId = CommonConvertHelper.StringToInt(mediaid);
						media.StartDate = startdate;
						media.EndDate = expirydate;
						media.MediaSource = mediasource;
						media.Type = mediatype;
						media.FilePath = mediapath;
						media.FileName=medianame;
						media.ExtName=mediaext;
						media.mediaLength=CommonConvertHelper.StringToLong(mediaLength);
						if(!mediamap.contains(Integer.parseInt(mediaid)))
							mediamap.put(Integer.parseInt(mediaid),media);
						mediahelper.UpdateMediaInfo(mediapath, this.currentdate, mediatype.getValue());
					}
				}
			}

		} catch (Exception ex) {
			LoggerHelper.WriteLogfortxt("MediaXml GetMediaList==>"+ex.getMessage());
		}
		return this.mediamap;

	}
}
