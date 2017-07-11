package com.mfusion.commons.tools;

import android.util.Base64;

import com.mfusion.commons.data.DALSettings;
import com.mfusion.commons.entity.license.LicenseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by ThinkPad on 2017/1/4.
 */
public class LicenseDecoder {

    public static LicenseEntity license;

    private static void test(long deviceBinary){
        long startBinary=getStartTimeEncode(Calendar.getInstance().getTime(),(deviceBinary>>11)&0x7,(deviceBinary>>8)&0x7);
        long endBinary=getEndTimeEncode(Calendar.getInstance().getTime(),(deviceBinary>>2)&0x7);
        long licenseBinary=(((deviceBinary>>14<<21)|startBinary)<<18)|(((((deviceBinary>>5)&0x7)<<13)|endBinary)<<2)|(deviceBinary&0x3);

        long start=(licenseBinary>>18)&0x1fffff;
        long end=(licenseBinary>>2)&0x1fff;

        long startTime=start>>15;
        long start_month=(start>>8)&0xf;
        long start_date=start&0x1f;
        long endTime=end>>7;
        long end_month=end&0xf;

        long device=(licenseBinary>>39<<14)|(((licenseBinary>>30)&0x7)<<11)|(((licenseBinary>>23)&0x7)<<8)|(((licenseBinary>>15)&0x7)<<5)|(((licenseBinary>>6)&0x7)<<2)|(licenseBinary&0x3);
    }
    public static int FNVHash1(String data)
    {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for(int i=0;i<data.length();i++)
            hash = (hash ^ data.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash=Math.abs(hash);
        return (hash>>15)^(hash & 0x1FFF);
    }

    static SimpleDateFormat date_format=new SimpleDateFormat("yyyyMMdd");

    /**
     * Max year is 63
     * @param time
     * @return
     */
    private static int getStartTimeEncode(Date time){
        int start_year=63,start_month=12,start_date=31;
        int result=(start_year<<9)|(start_month<<5)|start_date;

        long year=result>>9;
        long month=(result>>5)&0xF;
        long date=result&0x1F;

        return (Integer.valueOf(String.valueOf(time.getYear()).substring(1))<<9)|((time.getMonth()+1)<<5)|time.getDate();
    }

    private static long getStartTimeEncode(Date time,long yearSplit,long monthSplit){
        int start_year=17,start_month=5,start_date=25;
        long result=(start_year<<15)|(yearSplit<<12)|(start_month<<8)|(monthSplit<<5)|start_date;

        long year=result>>15;
        long month=(result>>8)&0xF;
        long date=result&0x1F;

        return (Integer.valueOf(String.valueOf(time.getYear()).substring(1))<<11)|(yearSplit<<10)|((time.getMonth()+1)<<6)|(monthSplit<<5)|time.getDate();
    }

    private static Date getStartTimeDecode(long timeBinary){
        try {
            int date=(int)(timeBinary&0x1F);
            int month=(int)((timeBinary>>5)&0xF);
            return date_format.parse("20"+String.valueOf(timeBinary>>9)+(month>9?"":"0")+String.valueOf(month)+(date>9?"":"0")+String.valueOf(date));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static int getEndTimeEncode(Date time){
        int start_year=63,start_month=12;
        int result=(start_year<<4)|start_month;

        long year=result>>4;
        long month=result&0xF;

        return (Integer.valueOf(String.valueOf(time.getYear()).substring(1))<<4)|(time.getMonth()+1);
    }

    private static long getEndTimeEncode(Date time,long yearSplit){
        int start_year=17,start_month=12,start_date=24;
        long result=(start_year<<7)|(yearSplit<<4)|start_month;

        long year=result>>7;
        long month=result&0xF;

        return (Integer.valueOf(String.valueOf(time.getYear()).substring(1))<<5)|(yearSplit<<4)|(time.getMonth()+1);
    }

    private static Date getEndTimeDecode(long timeBinary,int date){
        try {
            int month=(int)(timeBinary&0xF);
            return date_format.parse("20"+String.valueOf(timeBinary>>4)+(month>9?"":"0")+String.valueOf(month)+(date>9?"":"0")+String.valueOf(date));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    private static String intToBinary(long data){

        if(data<=0)
            return "";
        String result=intToBinary(data/2);
        return result+String.valueOf(data%2);
    }

    public static Boolean checkLicenseValidity(){
        /*if(license==null)
            license=DALSettings.getInstance().getLicenseInfo();
        license=decoderLicense(createLicense());
        checkLicenseValidity(license);
        return license.validity==LicenseStatus.valid;*/
        return true;
    }

    /**
     *
     * @param licenseStr
     * @return -1:no match; -2:expiry
     */
    public static LicenseEntity getLicenseInfo(String licenseStr){

        LicenseEntity entity=decoderLicense(licenseStr);

        checkLicenseValidity(entity);

        return entity;
    }

    public static void checkLicenseValidity(LicenseEntity entity){

        if(entity.deviceHashCode!=FNVHash1(InternalKeyWords.DeviceId))
            entity.validity = LicenseStatus.notMatch;
        else if((entity.startDate==null||entity.startDate.compareTo(DateConverter.getCurrentDate())>0)||(entity.validDate!=null&&entity.validDate.compareTo(DateConverter.getCurrentDate())<0))
            entity.validity =  LicenseStatus.expiry;
        else
            entity.validity=LicenseStatus.valid;
    }
    public static LicenseEntity decoderLicense(String licenseHex){

        long licenseBinary=Long.parseLong(licenseHex, 16)>>2;
        long start=(licenseBinary>>21)&0x7fff;
        long end=(licenseBinary>>5)&0x3ff;
        long device=(licenseBinary>>36<<11)|(((licenseBinary>>15)&0x3f)<<5)|(licenseBinary&0x1f);

        LicenseEntity entity=new LicenseEntity();
        entity.deviceId=InternalKeyWords.DeviceId;
        entity.license=licenseHex;
        entity.startDate=getStartTimeDecode((licenseBinary>>21)&0x7fff);
        entity.validDate=getEndTimeDecode((licenseBinary>>5)&0x3ff,entity.startDate.getDate());
        entity.deviceHashCode=(int)((licenseBinary>>36<<11)|(((licenseBinary>>15)&0x3f)<<5)|(licenseBinary&0x1f));

        return entity;
    }

    public static String getMacAddress(){

        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                macAddress= "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            macAddress="02:00:00:00:00:02";
        }
        macAddress= macAddress.replace(":","").toUpperCase();
        return macAddress;
    }

    public static String createLicense(){

        long startBinary=getStartTimeEncode(Calendar.getInstance().getTime());
        String startHexStr = Integer.toHexString((int)startBinary);
        Date startTime=getStartTimeDecode(startBinary);

        long endBinary=getEndTimeEncode(Calendar.getInstance().getTime());
        String endHexStr = Integer.toHexString((int)endBinary);
        Date endTime=getEndTimeDecode(endBinary,startTime.getDate());

        long deviceBinary=FNVHash1(InternalKeyWords.DeviceId);
        String deviceHex=Integer.toHexString((int)deviceBinary);

        long licenseBinary=(((deviceBinary>>11<<15)|startBinary)<<21)|(((((deviceBinary>>5)&0x3F)<<10)|endBinary)<<5)|(deviceBinary&0x1F);
        String licenseHex=Long.toHexString((licenseBinary<<2)|0x3);

        test(deviceBinary);

        return licenseHex;
    }
}
