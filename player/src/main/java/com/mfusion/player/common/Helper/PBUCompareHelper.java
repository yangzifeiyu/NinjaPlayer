package com.mfusion.player.common.Helper;

import java.util.List;

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
import com.mfusion.player.common.Enum.ComponentType;
import com.mfusion.player.common.Setting.Component.AudioComponentSetting;
import com.mfusion.player.common.Setting.Component.DateTimeSetting;
import com.mfusion.player.common.Setting.Component.InteractiveSetting;
import com.mfusion.player.common.Setting.Component.RSSSetting;
import com.mfusion.player.common.Setting.Component.ScheduleMediaSetting;
import com.mfusion.player.common.Setting.Component.StreamingSetting;
import com.mfusion.player.common.Setting.Component.TextPropertySetting;
import com.mfusion.player.common.Setting.Component.TickerTextSetting;


//����Ƚ�pbu�Ƿ���ͬ
public class PBUCompareHelper {
	public static boolean IsPBUCompare(PBU firstPBU, PBU secondPBU)
	{
		try
		{
			Template secondTem = secondPBU.Template;
			if (firstPBU.Template != null && secondTem != null)
			{
				if (firstPBU.Template.Height!= secondTem.Height)
					return false;
				if (firstPBU.Template.Width != secondTem.Width)
					return false;
				if (firstPBU.Template.BackColor != secondTem.BackColor)
					return false;
				if ((firstPBU.Template.BackMediaFile==null&&secondTem.BackMediaFile!=null)||(firstPBU.Template.BackMediaFile!=null&&secondTem.BackMediaFile==null))
					return false;
				if (firstPBU.Template.BackMediaFile!=null&&secondTem.BackMediaFile!=null&&!firstPBU.Template.BackMediaFile.FilePath.equals(secondTem.BackMediaFile.FilePath))
					return false;
				List<BasicComponent> secondComs =secondPBU.Components;
				if (firstPBU.Components != null && secondComs != null)
				{
					if (firstPBU.Components.size() != secondComs.size())
						return false;

					for (int i = 0; i < secondComs.size(); i++)
					{
						ComponentType comType = secondComs.get(i).componentType;
						if (firstPBU.Components.get(i).componentType.equals(comType)!=true)
							return false;
						if (firstPBU.Components.get(i).getCmpwidth()!=secondComs.get(i).getCmpwidth())
							return false;
						if (firstPBU.Components.get(i).getCmpheight()!=secondComs.get(i).getCmpheight())
							return false;
						if (firstPBU.Components.get(i).getCmpleft()!=secondComs.get(i).getCmpleft())
							return false;
						if (firstPBU.Components.get(i).getCmptop()!=secondComs.get(i).getCmptop())
							return false;

						switch(comType)
						{
						case ScheduleMedia:
						{
							ScheduleMediaSetting first=((ScheduleMediaComponent)firstPBU.Components.get(i)).setting;
							ScheduleMediaSetting sec=((ScheduleMediaComponent)secondPBU.Components.get(i)).setting;

							try
							{
								if(first.Mute!=sec.Mute)
									return false;
								//List<TimelinePlaylist> tl1=first.TimelinePlaylist;
								//List<TimelinePlaylist> tl2=first.TimelinePlaylist;
								//if(tl1!=null&&tl2!=null)
								//{
								//	return false;
								//}
								Playlist pl1=first.Idleplaylist;
								Playlist pl2=sec.Idleplaylist;

								if (pl1 != null && pl2 != null)
								{
									if (pl1.Medias.size() != pl2.Medias.size())
										return false;
									for (int k = 0; k < pl2.Medias.size(); k++)
									{
										if (pl1.Medias.get(k).FileMD5 != null &&pl1.Medias.get(k).FileMD5.equals( pl2.Medias.get(k).FileMD5)!=true)
											return false;
										if (pl1.Medias.get(k).Duration != pl2.Medias.get(k).Duration)
											return false;
										if (pl1.Medias.get(k).Effect.equals(pl2.Medias.get(k).Effect)!=true)
											return false;
										if (pl1.Medias.get(k).FileName.equals(pl2.Medias.get(k).FileName)!=true)
											return false;
										if (pl1.Medias.get(k).FilePath.equals(pl2.Medias.get(k).FilePath)!=true)
											return false;
										if (pl1.Medias.get(k).MediaSource.equals(pl2.Medias.get(k).MediaSource)!=true)
											return false;
									}
								}


							}

							catch(Exception ex){ return false; } 
							break;
						}
						case DateTime:
						{
							try
							{
								DateTimeSetting setting = (DateTimeSetting)(((DateTimeComponent)(firstPBU.Components.get(i))).setting);
								DateTimeSetting secondValue =(DateTimeSetting)(((DateTimeComponent)(secondPBU.Components.get(i))).setting);
								if (setting!= null && secondValue != null)
								{
									if(setting.Format.equals(secondValue.Format)!=true)
										return false;
									if (setting.BackColor !=secondValue.BackColor)
										return false;
									TextPropertySetting TextProperty1=setting.TextProperty;
									TextPropertySetting TextProperty2=secondValue.TextProperty;
									if(TextProperty1.FontColor!=TextProperty2.FontColor)
										return false;
									if(TextProperty1.FontSize!=TextProperty2.FontSize)
										return false;
									if(TextProperty1.FontStyle!=TextProperty2.FontStyle)
										return false;

								}
							}
							catch(Exception ex) { return false; }
							break;


						}
						case TickerText:
						{
							try
							{
								TickerTextSetting setting = (TickerTextSetting)(((TickerTextComponent)(firstPBU.Components.get(i))).setting);
								TickerTextSetting secondValue =(TickerTextSetting)(((TickerTextComponent)(secondPBU.Components.get(i))).setting);
								if (setting!= null && secondValue != null)
								{
									if(setting.Speed!=secondValue.Speed)
										return false;
									if(setting.Context.equals(secondValue.Context)!=true)
										return false;
									if (setting.BackColor !=secondValue.BackColor)
										return false;
									TextPropertySetting TextProperty1=setting.TextProperty;
									TextPropertySetting TextProperty2=secondValue.TextProperty;
									if(TextProperty1.FontColor!=TextProperty2.FontColor)
										return false;
									if(TextProperty1.FontSize!=TextProperty2.FontSize)
										return false;
									if(TextProperty1.FontStyle!=TextProperty2.FontStyle)
										return false;

								}
							}
							catch(Exception ex) { return false; }

							break;
						}

						case Rss:
						{
							try
							{
								RSSSetting setting = (RSSSetting)(((RSSComponent)(firstPBU.Components.get(i))).setting);
								RSSSetting secondValue =(RSSSetting)(((RSSComponent)(secondPBU.Components.get(i))).setting);
								if (setting!= null && secondValue != null)
								{
									if(setting.Speed!=secondValue.Speed)
										return false;
									if(setting.RSSURL.equals(secondValue.RSSURL)!=true)
										return false;
									if (setting.BackColor !=secondValue.BackColor)
										return false;
									TextPropertySetting bodyTextProperty1=setting.BodyTextProperty;
									TextPropertySetting bodyTextProperty2=secondValue.BodyTextProperty;
									if(bodyTextProperty1.FontColor!=bodyTextProperty2.FontColor)
										return false;
									if(bodyTextProperty1.FontSize!=bodyTextProperty2.FontSize)
										return false;
									if(bodyTextProperty1.FontStyle!=bodyTextProperty2.FontStyle)
										return false;

									TextPropertySetting subTextProperty1=setting.SubTextProperty;
									TextPropertySetting subTextProperty2=secondValue.SubTextProperty;
									if(subTextProperty1.FontColor!=subTextProperty2.FontColor)
										return false;
									if(subTextProperty1.FontSize!=subTextProperty2.FontSize)
										return false;
									if(subTextProperty1.FontStyle!=subTextProperty2.FontStyle)
										return false;

								}
							}
							catch(Exception ex) { return false; }


							break;
						}
						case  Interactive:
						{
							try
							{
								InteractiveSetting setting = (InteractiveSetting)(((InteractiveComponent)(firstPBU.Components.get(i))).setting);
								InteractiveSetting secondValue =(InteractiveSetting)(((InteractiveComponent)(secondPBU.Components.get(i))).setting);
								if (setting!= null && secondValue != null)
								{
									if(setting.Web_Url.equals(secondValue.Web_Url)!=true)
										return false;
								}
							}
							catch(Exception ex) { return false; }
							break;
						}
						case  StreamingComponent:
						{
							try
							{
								StreamingSetting setting = (StreamingSetting)(((StreamingComponent)(firstPBU.Components.get(i))).setting);
								StreamingSetting secondValue =(StreamingSetting)(((StreamingComponent)(secondPBU.Components.get(i))).setting);
								if (setting!= null && secondValue != null)
								{
									if(setting.Url.equals(secondValue.Url)!=true)
										return false;
									if(setting.Mute!=secondValue.Mute)
										return false;
									
								}
							}
							catch(Exception ex) { return false; }
							break;
						}
						case AudioComponent:
						{
							try
							{
								AudioComponentSetting setting = (AudioComponentSetting)(((AudioComponent)(firstPBU.Components.get(i))).setting);
								AudioComponentSetting secondValue =(AudioComponentSetting)(((AudioComponent)(secondPBU.Components.get(i))).setting);
								if (setting!= null && secondValue != null)
								{

									if(setting.PlayMode!=secondValue.PlayMode)
										return false;
									List<MediaFile> pl1=setting.AudioList;
									List<MediaFile> pl2=secondValue.AudioList;

									if (pl1 != null && pl2 != null)
									{
										if (pl1.size() != pl2.size())
											return false;
										for (int k = 0; k < pl2.size(); k++)
										{
											if (pl1.get(k).FileMD5 != null &&pl1.get(k).FileMD5.equals( pl2.get(k).FileMD5)!=true)
												return false;
											if (pl1.get(k).Duration != pl2.get(k).Duration)
												return false;
											if (pl1.get(k).Effect.equals(pl2.get(k).Effect)!=true)
												return false;
											if (pl1.get(k).FileName.equals(pl2.get(k).FileName)!=true)
												return false;
											if (pl1.get(k).FilePath.equals(pl2.get(k).FilePath)!=true)
												return false;
											if (pl1.get(k).MediaSource.equals(pl2.get(k).MediaSource)!=true)
												return false;
										}
									}


								}


							}
							catch(Exception ex) { return false; }

							break;
						}
						default:
							break;
						}
					}

				}
			}
		}
		catch(Exception ex){return false;}
		return true;
	}
}
