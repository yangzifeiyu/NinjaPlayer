// ITestAidlInterface.aidl
package com.mfusion.player;

// Declare any non-default types here with import statements

import java.util.List;
//import com.mfusion.player.common.Entity.DownloadObject;

interface IMFServiceInterface {
    void ClosePlayer();
    void RestartPlayer(in String serverIP,in String serverPort,in String mediaPort);
    void StopDownload();
    /*void DownloadRequest(in List<DownloadObject> m_media_list,in String serverIP,in String mediaPort);
    List<DownloadObject> GetMediaStatus();
    String CheckDownloadStatus();*/
}
