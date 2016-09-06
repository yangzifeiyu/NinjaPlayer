package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mfusion.commons.controllers.AbstractFragment;
import com.mfusion.commons.tools.CallbackBundle;
import com.mfusion.commons.tools.InternalKeyWords;
import com.mfusion.commons.tools.OperateCallbackBundle;
import com.mfusion.commons.view.ImageTextView;
import com.mfusion.ninjaplayer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LogFragment extends AbstractFragment {

    EditText logcat;
    ImageTextView load, clear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        load = (ImageTextView) rootView.findViewById(R.id.btnReload);//load log
        load.setText("Load Log");
        load.setImage(R.drawable.mf_log);
        clear = (ImageTextView) rootView.findViewById(R.id.btnClear);//clear log information
        clear.setText("Clear Log");
        clear.setImage(R.drawable.mf_clear);
        logcat = (EditText) rootView.findViewById(R.id.etLog);//log info

        load.setOnClickListener(new View.OnClickListener() {

            int count = 0;
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                load.setText("Loading ...");
                Thread m_loading_thread=new Thread(log_runnable);
                m_loading_thread.start();
            }
        });//load log information method

        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                try {
                    File logFile = new File(InternalKeyWords.Log_Path);//file path
                    if(logFile.exists())
                        logFile.delete();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();//display when clearing log unsuccessfully
                }

                logcat.setText("");//set editText to empty 

            }


        });//clear log information method

        return rootView;//return statement
    }

    private Boolean isLoadingLog;
    private Stack<String> logList=new Stack<String>();
    Runnable log_runnable = new Runnable() {
        @Override
        public void run() {//run()���µ��߳�������
            try {
                logList.clear();
                File logFile=new File(InternalKeyWords.Log_Path);
                if(logFile.exists()){
                    BufferedReader br = new BufferedReader(new FileReader(logFile));
                    String line = "";
                    int index=0;
                    while((line = br.readLine())!=null){
                        logList.push(line);
                    }
                    br.close();
                }

                mHandler.obtainMessage(0,"").sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            StringBuilder logs=new StringBuilder();
            while (logList.size()>0){
                logs.append(logList.pop()+"\n");
            }
            logcat.setText(logs);
            load.setText("Load Log");
        }
    };

    @Override
    public void saveModification(OperateCallbackBundle callbackBundle) {

    }

    @Override
    public void cancelSaveModification() {

    }

    @Override
    public void showFragment() {

    }

    @Override
    public void hideFragment() {

    }
}
