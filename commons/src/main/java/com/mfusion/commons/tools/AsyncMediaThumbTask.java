package com.mfusion.commons.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by ThinkPad on 2016/10/11.
 */
public class AsyncMediaThumbTask extends AsyncTask<String, Integer, String> {

    Context context;

    String videoPath;

    Integer imageW,imageH;

    Bitmap imageBitmap;

    MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    public AsyncMediaThumbTask(Context context, String url){
        this.context=context;
        this.videoPath=url;
    }

    public AsyncMediaThumbTask(Context context, String url, int width, int height){
        this.context=context;
        this.videoPath=url;
        this.imageW=width;
        this.imageH=height;
    }

    @Override
    protected String doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        try {

            File videoFile=new File(videoPath);
            if(!videoFile.exists())
                return null;

            retriever.setDataSource(videoPath);
            //retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            //retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            Bitmap bitmap = retriever.getFrameAtTime();
            System.out.println(" width :"+bitmap.getWidth()+" height :"+bitmap.getHeight());
            ImageHelper.saveBitmap(bitmap, InternalKeyWords.VideoThumbPath+videoFile.getName()+".jpg");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        finally {
            try{
                retriever.release();
            }catch (Exception ex){}
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            super.cancel(true);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
