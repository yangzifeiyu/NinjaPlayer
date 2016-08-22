package com.mfusion.player.library.Controller;

import com.mfusion.player.library.Helper.LoggerHelper;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.provider.ContactsContract.CommonDataKinds.Event;

import com.mfusion.player.common.Player.MainActivity;

public class WebServiceAccesser {
	HttpTransportSE transport = null;
	
	SoapSerializationEnvelope envelope=null;
	
	String nameSpace = "http://m-fusion.com.sg/";
	// ���õķ������
	String methodName;
	// EndPoint
	String endPoint;
	// SOAP Action
	String soapAction;
	
	public WebServiceAccesser(){
		methodName = MainActivity.Instance.PlayerSetting.getRemoteServiceMethod();
		
		endPoint =MainActivity.Instance.PlayerSetting.getRemoteServiceAddress();
		
		soapAction = nameSpace+methodName;
	}
	
	public JSONObject GetWeatherDataFromService(String city,String language,int dates){

		if(transport==null||envelope==null){
			try {
				// ָ��WebService������ռ�͵��õķ�����
				SoapObject rpc = new SoapObject(nameSpace, methodName);

				rpc.addProperty("city", city);
				rpc.addProperty("language", language);
				rpc.addProperty("dateNum", dates);

				// ��ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
				envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);

				envelope.bodyOut = rpc;
				// �����Ƿ���õ���dotNet������WebService
				envelope.dotNet = true;
				// �ȼ���envelope.bodyOut = rpc;
				envelope.setOutputSoapObject(rpc);
				
				Element[] header = new Element[1];
			    header[0] = new Element().createElement(nameSpace, "PermissionHeader");
			    Element username = new Element().createElement(nameSpace, "UserName");
			    username.addChild(Node.TEXT, "mfusion");
			    header[0].addChild(Node.ELEMENT, username);
			    Element password = new Element().createElement(nameSpace, "PassWord");
			    password.addChild(Node.TEXT, "mfusion");
			    header[0].addChild(Node.ELEMENT, password);
			    
				envelope.headerOut=header;

				transport = new HttpTransportSE(endPoint);
				
			} catch (Exception e) {
				LoggerHelper.WriteLogfortxt("WeatherDataHelper GetWeatherDataFromService==>"+e.getMessage());
			}
		}
		
		if(transport!=null&&envelope!=null){
			try {
				// ����WebService
				transport.call(soapAction, envelope);
				
				if(envelope!=null&&envelope.bodyIn!=null){
					SoapObject object = (SoapObject) envelope.bodyIn;
					// ��ȡ���صĽ��
					Object testObject = object.getProperty(0).getClass();
					String result = object.getProperty(0).toString();
					
					JSONObject json=new JSONObject(result);
					
					return json;
				}
			} catch (Exception e) {
				LoggerHelper.WriteLogfortxt("WeatherDataHelper GetWeatherDataFromService==>"+e.getMessage());
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
