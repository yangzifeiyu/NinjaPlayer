package com.mfusion.templatedesigner;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.DataFormatException;

/**
 * Created by 1B15182 on 27/7/2016 0027.
 */
public class RssParser {
    private static final String TAG = "RssParser";
    private String address;
    private String result;
    private RssParserListener listener;

    public RssParser(String address, RssParserListener listener) {
        this.address = address;
        this.listener = listener;
    }
    public void startFetching(){
        new FetchWorker().execute();
    }

    public interface RssParserListener{
        void onSuccess(String result);
        void onFailure();
    }

    private class FetchWorker extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean finished=false;
            try {
                InputStream inputStream=new URL(address).openConnection().getInputStream();
                Feed feed= EarlParser.parseOrThrow(inputStream,0);
                StringBuilder sb=new StringBuilder();
                for(Item item:feed.getItems()){
                    String title=item.getTitle()==null?"No Title":item.getTitle();
                    String content=item.getDescription()==null?"No Content":item.getDescription();
                    sb.append(" * ");
                    sb.append(title);
                    sb.append(" * ");
                    sb.append(content);
                    sb.append(">-----<");
                }
                result=sb.toString();
                Log.i(TAG, "doInBackground: "+result);
                finished=true;
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: open connection error",e );
            } catch (DataFormatException | XmlPullParserException e) {
                Log.e(TAG, "doInBackground: rss parse error",e );
            }

            SystemClock.sleep(1000);
            return finished;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
                listener.onSuccess(result);
            else
                listener.onFailure();

        }
    }
}
