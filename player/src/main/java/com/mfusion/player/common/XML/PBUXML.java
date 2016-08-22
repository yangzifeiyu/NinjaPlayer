/**
 * 
 * @author Xinmiao
 * 
 * @version 2015-04-10
 *
 *����pbu
 */
package com.mfusion.player.common.XML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import org.w3c.dom.Element;
import android.content.Context;
import com.mfusion.player.common.Entity.MediaFile;
import com.mfusion.player.common.Entity.PBU;
import com.mfusion.player.common.Entity.Playlist;
import com.mfusion.player.common.Entity.Template;
import com.mfusion.player.common.Entity.TimelinePlaylist;
import com.mfusion.player.common.Entity.Components.AudioComponent;
import com.mfusion.player.common.Entity.Components.BasicComponent;
import com.mfusion.player.common.Entity.Components.DateTimeComponent;
import com.mfusion.player.common.Entity.Components.InteractiveComponent;
import com.mfusion.player.common.Entity.Components.RSSComponent;
import com.mfusion.player.common.Entity.Components.ScheduleMediaComponent;
import com.mfusion.player.common.Entity.Components.StreamingComponent;
import com.mfusion.player.common.Entity.Components.TickerTextComponent;
import com.mfusion.player.common.Entity.Components.WeatherComponent;
import com.mfusion.player.common.Enum.ComponentType;
import com.mfusion.player.common.Enum.PlayMode;
import com.mfusion.player.common.Enum.TemperatureType;
import com.mfusion.player.common.Enum.TriggerActionType;
import com.mfusion.player.common.Enum.TriggerExitType;
import com.mfusion.player.common.Enum.TriggerOpenType;
import com.mfusion.player.common.Enum.TriggerType;
import com.mfusion.player.common.Helper.ComponentPropertyHelper;
import com.mfusion.player.common.MathPlaylist.MergePlaylistHelper;
import com.mfusion.player.common.Player.MainActivity;
import com.mfusion.player.common.Setting.Component.AudioComponentSetting;
import com.mfusion.player.common.Setting.Component.DateTimeSetting;
import com.mfusion.player.common.Setting.Component.InteractiveSetting;
import com.mfusion.player.common.Setting.Component.RSSSetting;
import com.mfusion.player.common.Setting.Component.ScheduleMediaSetting;
import com.mfusion.player.common.Setting.Component.StreamingSetting;
import com.mfusion.player.common.Setting.Component.TextPropertySetting;
import com.mfusion.player.common.Setting.Component.TickerTextSetting;
import com.mfusion.player.common.Setting.Component.WeatherSetting;
import com.mfusion.player.common.Storage.MediaStorage;
import com.mfusion.player.common.Storage.PBUStorage;
import com.mfusion.player.common.Trigger.MouseTrigger;
import com.mfusion.player.common.Trigger.TCPTrigger;
import com.mfusion.player.common.Trigger.VirtualTrigger;
import com.mfusion.player.library.Database.Xml.XMLHelper;
import com.mfusion.player.library.Helper.CommonConvertHelper;
import com.mfusion.player.library.Helper.DateTimeHelper;
import com.mfusion.player.library.Helper.LoggerHelper;

public class PBUXML {
	private XMLHelper xmlHelper;
	public Hashtable<String, PBU> pbumap;
	public PBUXML() {
		try
		{
			this.xmlHelper = new XMLHelper();
			this.pbumap = new Hashtable<String, PBU>();
			this.m_playlist_id_list=new ArrayList<Integer>();
			this.m_playlistCollection=new Stack<Playlist>();


		}catch(Exception ex)
		{
			LoggerHelper.WriteLogfortxt("PBUXML==>"+ex.getMessage());
		}


	}

	public Hashtable<String, PBU> GetPBUList() {
		try {

			if (xmlHelper != null) {
				// pbus
				Element display_pbus = xmlHelper
						.getElementByXPath("MfusionPlayer\\Data\\Contents\\Display\\PBUs");
				if(display_pbus==null)
					return pbumap;
				Element[] pbus = xmlHelper
						.getElementsByParentElement(display_pbus);
				if(pbus==null||pbus.length<=0)
					return pbumap;
				Context context = MainActivity.Instance;
				for (Element element:pbus) {
					PBU pbu = new PBU();
					String pbuid = "";
					String duration = "";
					String trigger="";
					duration = element.getAttribute("Duration");
					trigger = element.getAttribute("Trigger");

					pbuid = element.getAttribute("Id");
					pbu.ID = pbuid;
					Element[] elements= xmlHelper.getElementsByParentElement(element);

					ArrayList<BasicComponent> componentlist = new ArrayList<BasicComponent>();
					if(elements!=null&&elements.length>0)
					{
						for (int j = 0; j < elements.length; j++) {
							Element e = elements[j];
							String tag = "";
							tag = e.getTagName();
							//����template
							if (tag.equalsIgnoreCase("template")) {
								Template template = new Template();
								String templateBackcolor = e
										.getAttribute("BackColor");
								String templateHeight = e.getAttribute("Height");
								String templateWidth = e.getAttribute("Width");
								String templateBackMediaId = "";
								Element[] backmedia = xmlHelper
										.getElementsByParentElement(e);
								if (backmedia.length != 0) {
									Element back = backmedia[0];
									if (back != null)
										templateBackMediaId = back
										.getAttribute("MediaId");
									template.BackMediaId =CommonConvertHelper.StringToInt(templateBackMediaId);

									template.BackMediaFile=MediaStorage.GetMediaFile(template.BackMediaId);
								}

								template.Width=CommonConvertHelper.StringToInt(templateWidth);
								template.Height=CommonConvertHelper.StringToInt(templateHeight);
								template.BackColor = ComponentPropertyHelper
										.GetColor(templateBackcolor.trim());
								pbu.Template = template;

							} else {


								BasicComponent component = null;
								String componentIndex = e.getAttribute("Index");
								String componentBackcolor = e
										.getAttribute("BackColor");
								String componentHeight = e.getAttribute("Height");
								String componentWidth = e.getAttribute("Width");
								String componentLeft = e.getAttribute("Left");
								String componentTop = e.getAttribute("Top");
								String componentType = e.getAttribute("Type");
								ComponentType comType=ComponentType.fromString(componentType);
								switch(comType)
								{
								//[start] datetime
								case DateTime:
								{
									component = new DateTimeComponent(context);
									component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
									component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
									component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
									component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
									DateTimeSetting setting = new DateTimeSetting();
									TextPropertySetting textproperty = new TextPropertySetting();
									Element[] properties = xmlHelper
											.getElementsByParentElement(e);
									if(properties!=null)
									{
										for (int k = 0; k < properties.length; k++) {
											Element property = properties[k];
											String property_name = "";
											String property_val = "";
											property_name = property
													.getAttribute("name");
											property_val = property.getTextContent();
											if (property_name.equalsIgnoreCase("Font")) {
												String[] style = property_val
														.split(",");
												if(style!=null&&style.length>=2)
												{
													textproperty.FontSize = CommonConvertHelper.StringToFloat(style[1].substring(0, style[1].length()-2));
													String weight="";
													if(style.length>=3)
													{
														weight=style[2];

													}
													textproperty.FontStyle = ComponentPropertyHelper
															.GetTypeface(style[0],weight);
												}
											} else if (property_name
													.equalsIgnoreCase("ForeColor")) {
												textproperty.FontColor = ComponentPropertyHelper
														.GetColor(property_val);
											} else if (property_name
													.equalsIgnoreCase("TimeFormat")) {
												setting.Format = property_val;
											}

										}
									}
									setting.TextProperty = textproperty;
									setting.BackColor = ComponentPropertyHelper
											.GetColor(componentBackcolor);

									component.Init(setting);
								}
								break;
								//[end]

								//[start]ticker
								case TickerText:
								{
									component = new TickerTextComponent(context);
									component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
									component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
									component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
									component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
									TickerTextSetting setting = new TickerTextSetting();
									TextPropertySetting textproperty = new TextPropertySetting();
									String text="";
									int speed=0;

									Element[] properties = xmlHelper
											.getElementsByParentElement(e);
									if(properties!=null)
									{
										for (int k = 0; k < properties.length; k
												++) {
											Element property = properties[k];
											String property_name = "";
											String property_val = "";
											property_name = property
													.getAttribute("name");
											property_val = property.getTextContent();
											if (property_name.equalsIgnoreCase("Font")) {
												String[] style = property_val
														.split(",");
												if(style!=null&&style.length>=3)
												{
													textproperty.FontSize =CommonConvertHelper.StringToFloat(style[1].substring(0, style[1].length()-2));
													textproperty.FontStyle = ComponentPropertyHelper
															.GetTypeface(style[0], style[2]);
												}
											} else if (property_name
													.equalsIgnoreCase("ForeColor")) {
												textproperty.FontColor = ComponentPropertyHelper
														.GetColor(property_val);
											}
											else if(property_name
													.equalsIgnoreCase("TextString")){
												text=property_val;
											}
											else if(property_name
													.equalsIgnoreCase("Speed")){
												speed=CommonConvertHelper.StringToInt(property_val);
											}
										}
									}
									setting.Speed=speed;
									setting.TextProperty = textproperty;
									setting.Context=text;
									setting.BackColor = ComponentPropertyHelper
											.GetColor(componentBackcolor.trim());

									component.Init(setting);

								} 
								break;
								//[end]

								//[start]schedulemedia
								case ScheduleMedia:
								{
									ScheduleMediaSetting setting = new ScheduleMediaSetting();
									component = new ScheduleMediaComponent(context);
									component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
									component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
									component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
									component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
									Element[] properties = xmlHelper
											.getElementsByParentElement(e);
									this.m_playlist_id_list.clear();
									if(properties!=null)
									{
										for (int k = 0; k < properties.length; k++) {
											Element property = properties[k];
											String property_name = "";
											String property_val = "";
											property_name = property
													.getAttribute("name");
											property_val = property.getTextContent();
											if (property_name.equalsIgnoreCase("PlaylistId"))
											{
												setting.setIdleplaylistId(CommonConvertHelper.StringToInt(property_val));

											} 
											/*else if (property_name
													.equalsIgnoreCase("PLGroupId"))
											{
												setting.setPlgroupId(CommonConvertHelper.StringToInt(property_val));

											} */
											else if (property_name.equalsIgnoreCase("Mute")) 
											{
												setting.Mute = Boolean
														.parseBoolean(property_val);
											}

										}

									}
									//this.defaultPlaylist=setting.Idleplaylist;
									//this.timelineplaylist=setting.TimelinePlaylist;
									//this.GetFullPlaylistCollection();
									//this.MergePlaylist();
									//setting.PlaylistList=MergePlaylistHelper.playlist;

									component.Init(setting);
								}
								break;
								//[end]

								//[start]interactive
								case Interactive:
									try
									{
										InteractiveSetting setting = new InteractiveSetting();
										component = new InteractiveComponent(context);
										Element[] properties = xmlHelper
												.getElementsByParentElement(e);

										if(properties!=null)
										{
											for (int k = 0; k < properties.length; k++) {
												Element property = properties[k];
												String property_name = "";
												String property_val = "";
												property_name = property
														.getAttribute("name");
												property_val = property.getTextContent();
												if (property_name.equalsIgnoreCase("Address"))
												{
													setting.Web_Url=property_val;
												}
											}

										}
										component = new InteractiveComponent(context);

										component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
										component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
										component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
										component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
										component.Init(setting);
									}
									catch(Exception ex){}
									break;
									//[end]

								case StreamingComponent:
									try
									{
										StreamingSetting setting = new StreamingSetting();
										component = new StreamingComponent(context);
										Element[] properties = xmlHelper
												.getElementsByParentElement(e);

										if(properties!=null)
										{
											for (int k = 0; k < properties.length; k++) {
												Element property = properties[k];
												String property_name = "";
												String property_val = "";
												property_name = property
														.getAttribute("name");
												property_val = property.getTextContent();
												if (property_name.equalsIgnoreCase("Address"))
												{
													setting.Url=property_val;
												}
												else if (property_name.equalsIgnoreCase("mute"))
												{
													setting.Mute=Boolean
															.parseBoolean(property_val);
												}
											}

										}
										component = new StreamingComponent(context);
										component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
										component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
										component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
										component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
										component.Init(setting);
									}
									catch(Exception ex){}
									break;
									//[start] rss

								case Rss:
									try
									{
										component = new RSSComponent(context);
										component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
										component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
										component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
										component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
										RSSSetting setting = new RSSSetting();
										TextPropertySetting subtextproperty = new TextPropertySetting();
										TextPropertySetting contexttextproperty = new TextPropertySetting();
										String url="";
										int speed=0;

										Element[] properties = xmlHelper
												.getElementsByParentElement(e);
										if(properties!=null)
										{
											for (int k = 0; k < properties.length; k++) {
												Element property = properties[k];
												String property_name = "";
												String property_val = "";
												property_name = property
														.getAttribute("name");
												property_val = property.getTextContent();
												if (property_name.equalsIgnoreCase("SubjectFont")) {
													String[] style = property_val
															.split(",");
													if(style!=null&&style.length>=3)
													{
														subtextproperty.FontSize =CommonConvertHelper.StringToFloat(style[1].substring(0, style[1].length()-2));
														subtextproperty.FontStyle = ComponentPropertyHelper
																.GetTypeface(style[0], style[2]);
													}
												} else if (property_name
														.equalsIgnoreCase("SubjectForeColor")) {
													subtextproperty.FontColor= ComponentPropertyHelper
															.GetColor(property_val);
												}
												else if (property_name.equalsIgnoreCase("BodyFont")) {
													String[] style = property_val
															.split(",");
													if(style!=null&&style.length>=3)
													{
														contexttextproperty.FontSize =CommonConvertHelper.StringToFloat(style[1].substring(0, style[1].length()-2));
														contexttextproperty.FontStyle = ComponentPropertyHelper
																.GetTypeface(style[0], style[2]);
													}
												} else if (property_name
														.equalsIgnoreCase("BodyForeColor")) {
													contexttextproperty.FontColor= ComponentPropertyHelper
															.GetColor(property_val);
												}
												else if(property_name
														.equalsIgnoreCase("Address")){
													url=property_val;
												}
												else if(property_name
														.equalsIgnoreCase("Speed")){
													speed=CommonConvertHelper.StringToInt(property_val);
												}

											}
										}
										setting.SubTextProperty = subtextproperty;
										setting.BodyTextProperty= contexttextproperty;
										setting.RSSURL=url;
										setting.Speed=speed;
										setting.BackColor = ComponentPropertyHelper
												.GetColor(componentBackcolor.trim());

										component.Init(setting);
									}catch(Exception ex)
									{

									}
									break;
									//[end]
								case AudioComponent:
									try
									{
										component=new AudioComponent(context);
										component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
										component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
										component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
										component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
										AudioComponentSetting setting=new AudioComponentSetting();
										Element[] properties = xmlHelper
												.getElementsByParentElement(e);
										if(properties!=null)
										{
											for (int k = 0; k < properties.length; k++) {
												Element property = properties[k];
												String property_name = "";
												String property_val = "";
												property_name = property
														.getAttribute("name");
												property_val = property.getTextContent();
												if(property_name.equalsIgnoreCase("PlayMode"))
												{
													setting.PlayMode=PlayMode.fromString(property_val);

												}
												else if(property_name.equalsIgnoreCase("MediaId"))
												{
													MediaFile audio=MediaStorage.GetMediaFile(CommonConvertHelper.StringToInt(property_val));
													if(audio!=null)
														setting.AudioList.add(audio);
												}

											}
										}
										component.Init(setting);
									}
									catch(Exception ex)
									{

									}
									break;
								case WeatherComponent:{
									try{
										WeatherSetting setting = new WeatherSetting();
										component = new WeatherComponent(context);
										Element[] properties = xmlHelper
												.getElementsByParentElement(e);
		
										if(properties!=null)
										{
											for (int k = 0; k < properties.length; k++) {
												Element property = properties[k];
												String property_name = "";
												String property_val = "";
												property_name = property
														.getAttribute("name");
												property_val = property.getTextContent();
												if (property_name.equalsIgnoreCase("LangCode"))
												{
													setting.Language=property_val;
												}
												else if (property_name.equalsIgnoreCase("WeatherCity"))
												{
													setting.City=property_val;
												}
												else if (property_name.equalsIgnoreCase("LayoutXML"))
												{
													setting.LayoutXMLPath=property_val;
												}
												else if (property_name.equalsIgnoreCase("TemperatureType"))
												{
													setting.Type=TemperatureType.fromString(property_val);
												}
											}
										}
										component.setCmpheight( CommonConvertHelper.StringToInt(componentHeight));
										component.setCmpwidth( CommonConvertHelper.StringToInt(componentWidth));
										component.setCmpleft(CommonConvertHelper.StringToInt(componentLeft));
										component.setCmptop(CommonConvertHelper.StringToInt(componentTop));
										component.Init(setting);
									}
									catch(Exception ex){}
									break;
								}
								default:
									break;

								}
								if(component!=null)
								{
									component.componentType = comType;
									component.index = CommonConvertHelper.StringToInt(componentIndex);
									componentlist.add(component);
								}
							}

						}
						Collections.sort(componentlist);
					}

					pbu.Components = componentlist;
					
					int dur=0;
					//��ȡduration
					if (duration.equals(""))

						dur = PBUStorage.GetPBUDuration(pbu);

					else
						dur = DateTimeHelper.ConvertToMin(duration);
					
					pbu.Duration=dur;
					pbu.OriginalDuration=dur;

					//trigger
					pbu.TriggerContent=trigger;
					if (trigger != null && !trigger.equals("")&&!trigger.isEmpty())
					{
						XMLHelper helper=new XMLHelper(trigger);
						Element triggers = helper.getElementByXPath("triggers");
						if(triggers!=null)
						{
							Element[] triggerList=helper.getElementsByParentElement(triggers);
							{
								if (triggerList != null && triggerList.length > 0)
								{
									try
									{
										pbu.internalTriggers = new ArrayList<VirtualTrigger>();
										for (Element e :triggerList)
										{
											VirtualTrigger vTrigger = null;
											TriggerType type = TriggerType.fromString(e.getAttribute("type"));
											if (type == TriggerType.Mouse)
											{
												vTrigger = new MouseTrigger();
												pbu.CursorEnabled = true;
											}
											else
											{
												vTrigger = new TCPTrigger();
											}
											vTrigger.triggerType = type;
											vTrigger.actionType = TriggerActionType.fromString(e.getAttribute("actionType"));
											vTrigger.interval =CommonConvertHelper.StringToInt(e.getAttribute("interval"));
											if (vTrigger.actionType == TriggerActionType.App)
											{
												if (e.getAttribute("value")== "")
												{
													LoggerHelper.WriteLogfortxt("Player PBU trigger  ==> External App path is null ");
													continue;
												}
												vTrigger.triggerAction =e.getAttribute("value");
												vTrigger.openType = TriggerOpenType.fromString(e.getAttribute("openType"));
												vTrigger.exitType = TriggerExitType.fromString(e.getAttribute("exitType"));
											}
											else if (vTrigger.actionType == TriggerActionType.PBU)
											{

												if (e.getAttribute("value")== "")
												{
													LoggerHelper.WriteLogfortxt("Player PBU trigger  ==> not find PBU that pbuId= " + CommonConvertHelper.StringToInt(e.getAttribute("value")));
													continue;
												}
												vTrigger.triggerAction = CommonConvertHelper.StringToInt(e.getAttribute("value"));
											}

											pbu.internalTriggers.add(vTrigger);
										}
									}
									catch (Exception ex) { LoggerHelper.WriteLogfortxt("Player PBU trigger  ==> " + ex.getMessage()); }
								}
							}
						}
					}

					if (!pbumap.containsKey(pbu.ID)) {
						pbumap.put(pbu.ID, pbu);
					}

				}
			}

			return pbumap;
		} catch (Exception ex) {
			LoggerHelper.WriteLogfortxt("PBUXML GetPBUList==>"+ex.getMessage());
		}
		return pbumap;
	}

	public List<TimelinePlaylist> timelineplaylist;
	public Playlist defaultPlaylist;
	public List<Integer> m_playlist_id_list;
	public Stack<Playlist> m_playlistCollection;


	private void MergePlaylist() {
		// TODO Auto-generated method stub
		try
		{
			this.m_playlistCollection.clear();
			MergePlaylistHelper.MergeAllTimelilneUnit(this.timelineplaylist,defaultPlaylist);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void GetFullPlaylistCollection() {
		// TODO Auto-generated method stub
		if(timelineplaylist!=null)
		{
			for (TimelinePlaylist timeline : this.timelineplaylist) {

				timeline.GetPlaylistCollection();
			}
		}
		if (m_playlist_id_list!=null&&defaultPlaylist!=null&&!m_playlist_id_list.contains(defaultPlaylist.ID))
			this.m_playlist_id_list.add(defaultPlaylist.ID);

		try {
			if(timelineplaylist!=null)
			{
				for (int i = this.timelineplaylist.size() - 1; i >= 0; i--) {
					TimelinePlaylist timelineplaylist = this.timelineplaylist.get(i);
					for (int j = 0; j < timelineplaylist.playlistList.size(); j++) {
						if (this.m_playlist_id_list
								.contains(timelineplaylist.playlistList.get(j).ID) == false)

							this.m_playlist_id_list.add(timelineplaylist.playlistList.get(j).ID);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
