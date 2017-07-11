package com.mfusion.commons.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mfusion.commons.entity.license.RGBLuminanceSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThinkPad on 2017/1/4.
 */
public class QRBuilder {

    public static Boolean createQRForDevice(Activity activity){
        return createQRForDevice(activity,400,400);
    }

    public static Boolean createQRForDevice(Activity activity, int width, int height){
        TelephonyManager tm = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        System.out.println(tm.getDeviceId());
        return createQRForDevice(tm.getDeviceId(),width,height,InternalKeyWords.DeviceQR_Path);
    }

    private static Boolean createQRForDevice(String qrContent,int width,int height,String qrPath){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            Bitmap qrImage = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
            if(ImageHelper.saveBitmap(qrImage,qrPath,Bitmap.CompressFormat.PNG)!=null)
                return true;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readQRImage(String qrPath){
        Bitmap bmap=ImageHelper.getBitmap(qrPath);
        if (bmap == null)
            return "";

        LuminanceSource source = new RGBLuminanceSource(bmap);
        com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(new HybridBinarizer(source));
        Result result;
        try
        {
            result = new MultiFormatReader().decode(bitmap);
        }
        catch(ReaderException re)
        {
            re.printStackTrace();
            return "";
        }
        return result.getText();
    }
}
