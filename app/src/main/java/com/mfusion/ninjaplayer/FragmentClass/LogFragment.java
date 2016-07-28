package com.mfusion.ninjaplayer.FragmentClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mfusion.ninjaplayer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LogFragment extends Fragment {

    EditText logcat;
    Button load, clear;
    private static final int MSG_LogList = 0;

    private static final String processId = Integer.toString(android.os.Process.myPid());
    ArrayList<String> rows = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        load = (Button) rootView.findViewById(R.id.btnReload);//load log
        clear = (Button) rootView.findViewById(R.id.btnClear);//clear log
        logcat = (EditText) rootView.findViewById(R.id.etLog);//display log info 

               /* try {
                    StringBuffer sb = new StringBuffer();
                    String logPath = Environment.getExternalStorageDirectory().getPath() + "/Log/" + "log.txt";
                    BufferedReader br = new BufferedReader(new FileReader(new File(logPath)));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    TextView tv = (TextView)rootView.findViewById(R.id.tvLog);
                    tv.setText(sb.toString());
                } catch (Exception e) {
                    // TODO: handle exception
                }*/

        load.setOnClickListener(new View.OnClickListener() {

            int count = 0;
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                try {
                    File myFile = new File("/sdcard/Mfusion/log.txt");//file path
                    FileInputStream fIn = new FileInputStream(myFile);
                    BufferedReader myReader = new BufferedReader(
                            new InputStreamReader(fIn));
                    String aDataRow = "";
                    String aBuffer = "";


                    while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";

                    }



                    logcat.setText(aBuffer);//show buffer
                    logcat.append("\n");

                    myReader.close();
                    Toast.makeText(getActivity(),
                            "Done reading SD 'log.txt'",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Error();
                }




            }

        });//load all information from log


        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                try {
                    File myFile = new File("/sdcard/MFusion/log.txt");//file path
                    PrintWriter writer = new PrintWriter(myFile);
                    writer.print("");
                    writer.close();
                    Toast.makeText(getActivity(),
                            "Cleared everything from log.txt'",
                            Toast.LENGTH_SHORT).show();//display when click clear button 
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }


                logcat.setText("");

            }


        });//clear all information from log


        return rootView;
    }


    private void Error()
    {
        try {
            File myFile = new File("/sdcard/MFusion/log.txt");

            if (!myFile.exists()) {
                myFile.createNewFile();
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Error Getting Data in LogFragment at : "+ currentDateandTime + "\n");//display in log info when there is an error
                myOutWriter.close();
                fOut.close();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }//error log information


    }


}
