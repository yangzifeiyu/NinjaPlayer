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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_log, container, false);
        load = (Button) rootView.findViewById(R.id.btnReload);//load log
        clear = (Button) rootView.findViewById(R.id.btnClear);//clear log information
        logcat = (EditText) rootView.findViewById(R.id.etLog);//log info
        logcat.setMovementMethod(new ScrollingMovementMethod());//make editext scroll , scrollbar
               

        load.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


//                 try {
                    // File myFile = new File("/sdcard/Mfusion/log.txt");//file path
                    // FileInputStream fIn = new FileInputStream(myFile);// in order to retrive log
//                    BufferedReader myReader = new BufferedReader(
//                            new InputStreamReader(fIn));
//                    String aDataRow = "";
//                    String aBuffer = "";
//
//
//                    while ((aDataRow = myReader.readLine()) != null) {
//                        aBuffer += aDataRow + "\n";
//
//                    }
//
//                    logcat.setText(aBuffer);
//                    logcat.append("\n");
//
//                    myReader.close();
//                    Toast.makeText(getActivity(), "Done reading SD 'log.txt'",Toast.LENGTH_SHORT).show();//display when loading finished

//                } catch (Exception e) {
//                    Error();//call error method if loading failed
//                }//without time descending order


                try {
                    FileReader fr=new FileReader("/sdcard/Mfusion/log.txt");//file path
                    BufferedReader br=new BufferedReader(fr);//bufferedreader
                    String s;

                    List<String> tmp = new ArrayList<String>();//store data into array
                    boolean appendSeparator = false;
                    StringBuilder sb = new StringBuilder();//string builder-to retrive data from array

                    while ((s=br.readLine()) != null ) {
                        tmp.add(s);
                    }

                    for(int i=tmp.size()-1;i>=0;i--) {

                        if (appendSeparator)
                            sb.append("\n"); // line break (display line by line)
                        appendSeparator = true;

                        sb.append(tmp.get(i));//build will append array 
                    }//for loop 

                   logcat.setText(sb.toString());//set Text(value) to the log info (to display)

                } catch (Exception e) {
                    Error();//error log
                }

            }//with time descendinh order

        });//load log information method


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
                            Toast.LENGTH_SHORT).show();//display when clearing log successfully
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();//display when clearing log unsuccessfully
                }


                logcat.setText("");//set editText to empty 

            }



         });//clear log information method


    return rootView;//return statement
    }


    private void Error()
    {
        try {
            File myFile = new File("/sdcard/MFusion/log.txt");//file path

            if (!myFile.exists()) {
                myFile.createNewFile();//create new file
                //Toast.makeText(getActivity(),"Created 'log.txt'",Toast.LENGTH_SHORT).show();
            } else if (myFile.exists()) {
                FileOutputStream fOut = new FileOutputStream(myFile, true);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(fOut);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");//set time display format
                String currentDateandTime = sdf.format(new Date());
                myOutWriter.append("Error Getting Data in LogFragment at : "+ currentDateandTime + "\n");//display when error occured
                myOutWriter.close();
                fOut.close();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();//display message  
        }


    }//display error log


}
