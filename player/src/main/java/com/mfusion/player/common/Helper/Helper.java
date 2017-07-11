package com.mfusion.player.common.Helper;

import javax.xml.parsers.ParserConfigurationException;
import com.mfusion.player.common.Entity.Control.ControlManager;

import org.xml.sax.SAXException;

import com.mfusion.player.common.Helper.RSS.RSSContentHelper;

public class Helper {
	 public static RSSContentHelper RSS;
	 public static ControlManager ControlManager;
	 public Helper() throws ParserConfigurationException, SAXException
     {
         Helper.RSS = new RSSContentHelper();
         Helper.ControlManager=new ControlManager();
     }

    public static void pause(){
        Helper.RSS.pauseRefresh();
    }
}
