package com.mfusion.commons.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guoyu on 2016/8/18.
 */
public class LogOperator {

    private static long maxLength = 100000;

    public static void WriteLogfortxt(String logMsg)
    {Thread.UncaughtExceptionHandler dd;
        WriteLogfortxt(logMsg,Calendar.getInstance().getTime());
    }

    public static void WriteLogfortxt(String logMsg,Date logTime)
    {
        File logfile=null;
        try
        {
            logfile=new File(InternalKeyWords.Log_Path);
            if(!logfile.exists())
                logfile.createNewFile();

            String content = DateConverter.convertDateToStr("yyyy-MM-dd HH:mm:ss",logTime)+ "  " + logMsg + "\r\n";
            FileWriter fw=new FileWriter(InternalKeyWords.Log_Path,true);
            fw.append(content);
            fw.close();
            fw=null;

            logfile=new File(InternalKeyWords.Log_Path);
            long fileLength=logfile.length();
            if (fileLength > maxLength)
            {
                int rowCountOfRemove = getTotalline() / 2;
                deleteLine(rowCountOfRemove);
            }
        }
        catch(Exception ex){ex.printStackTrace(); }
        finally
        {
            //System.gc();
        }
    }
    private static int getTotalline()
    {
        int count = 0;
        try
        {
            File f = new File(InternalKeyWords.Log_Path);
            InputStream input = new FileInputStream(f);

            BufferedReader b = new BufferedReader(new InputStreamReader(input));

            String value = b.readLine();
            if(value != null)

                while(value !=null){
                    count++;
                    value = b.readLine();
                }

            b.close();
            input.close();
            b=null;
            input=null;
        }catch(Exception ex)
        {

        }
        finally
        {
            //System.gc();
        }
        return count;
    }

    private  static void  deleteLine(int indexLine)
    {
        int counter = 1;
        FileWriter writer = null;
        BufferedReader buffReader = null;
        StringBuffer tempTxt = new StringBuffer();
        try {
            File file = new File(InternalKeyWords.Log_Path);
            FileReader freader = new FileReader(file);
            buffReader = new BufferedReader(freader);
            while(buffReader.ready())
            {
                if(counter >indexLine)
                {
                    tempTxt.append( buffReader.readLine()+"\n");
                }
                else
                {
                    buffReader.readLine();
                }
                counter++ ;
            }

            buffReader.close();
            buffReader=null;
            writer = new FileWriter(file);
            writer.write(tempTxt.toString());
            writer.close();
            writer=null;
        }
        catch (Exception e) { e.printStackTrace();  }
        finally
        {
            //System.gc();
        }
    }
}
