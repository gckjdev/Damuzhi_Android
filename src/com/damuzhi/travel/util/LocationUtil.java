package com.damuzhi.travel.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;


public class LocationUtil
{

	
	private static final String TAG ="LocationUtil";
	private Context context ;
	private HashMap<String, Double> locationMap = new HashMap<String, Double>();
	private DefaultHttpClient client ;
	private Location loc ;
	private LocationManager locationManager;
	/**
	 * @param context
	 */
	public LocationUtil(Context context)
	{
		super();
		this.context = context;
		TravelApplication application = (TravelApplication) context.getApplicationContext();
		client = application.getHttpClient();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	
	
	/**  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 下午12:03:57  
	        */  
	public class CellIDInfo {
			
			public int cellId;
			public String mobileCountryCode;
			public String mobileNetworkCode;
			public int locationAreaCode;
			public String radioType;
			public CellIDInfo(){}
		}

	
	
	
	
	public  HashMap<String, Double> getLocation()
	{
		getLocationByGps();
		if(locationMap.isEmpty())
		{
			getLocationByTower();
			
		}
	    return locationMap;


	}
	
	
	
	/**  
	        * @return  
	        * @description   GPS定位
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 下午12:04:44  
	        */
	public HashMap<String, Double> getLocationByGps()
	{
		//LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//showCurrentLocation();
		Criteria criteria = new Criteria(); 	
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 获得最好的定位效果 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		// 使用省电模式 
		criteria.setPowerRequirement(Criteria.POWER_LOW);    
		// 获得当前的位置提供者 
		String provider = locationManager.getBestProvider(criteria, true); 				
		Log.d(TAG, "Location found? "+ (loc==null?"NO":"YES"));	
		// 获得当前位置的纬度 
		loc = locationManager.getLastKnownLocation(provider);
		if(loc !=null)
		{
			Double latitude = loc.getLatitude() * 1E6; 			
			// 获得当前位置的经度 
			Double longitude = loc.getLongitude() * 1E6; 
			locationMap.put(ConstantField.LATITUDE, latitude);
			locationMap.put(ConstantField.LONGITUDE, longitude);
			//Toast.makeText(context, "gps ok", Toast.LENGTH_LONG).show();
			
		}		
		locationManager.requestLocationUpdates(provider, 1000, 5, locationListener);			
		return locationMap;
	}
	
	
	/**  
	        * @return  
	        * @description   WIFI定位
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 下午12:11:06  
	        */
	public HashMap<String, Double> getLocationByNetWork()
	{
		//LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		/*Criteria criteria = new Criteria(); 
		// 获得最好的定位效果 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		// 使用省电模式 
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		// 获得当前的位置提供者 
		String provider = locationManager.getBestProvider(criteria, true); */		
		// 获得当前的位置 
		if(locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
		{
			Log.d(TAG, "请开启网络连接");
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 1, locationListener);
			loc = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
		}
		
		 
		if(loc != null)
		{
			// 获得当前位置的纬度 
			Double latitude = loc.getLatitude() * 1E6; 			
			// 获得当前位置的经度 
			Double longitude = loc.getLongitude() * 1E6; 
			locationMap.put(ConstantField.LATITUDE, latitude);
			locationMap.put(ConstantField.LONGITUDE, longitude);
			//Toast.makeText(context, "gps ok", Toast.LENGTH_LONG).show();
		}		
		return locationMap;
	}
	
	
	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location)
		{
			// TODO Auto-generated method stub
			Log.d(TAG, "onLocationChanged");
			loc = location;
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
		/**  
	     * @return  
	     * @description   
	     * @version 1.0  
	     * @author liuxiaokun  
	     * @update 2012-5-8 下午12:04:02  
	     */
	public HashMap<String, Double> getLocationByTower(){
		String locString = "";
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = tm.getNetworkType();
		//在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA，电信的3G为EVDO 
		//String OperatorName = tm.getNetworkOperatorName(); 
		Location loc = null;
		ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
		//中国电信为CTC
		//NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
		//NETWORK_TYPE_CDMA电信2G是CDMA
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type ==TelephonyManager.NETWORK_TYPE_1xRTT)
		{
			CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
			int cellIDs = location.getBaseStationId();
			int networkID = location.getNetworkId();
			StringBuilder nsb = new StringBuilder();
			nsb.append(location.getSystemId());
	     CellIDInfo info = new CellIDInfo();
	     info.cellId = cellIDs;
	     info.locationAreaCode = networkID; //ok
	     info.mobileNetworkCode = nsb.toString();
	     Log.i(TAG, "countryCode = "+tm.getNetworkOperator());
	     info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
	     info.radioType = "cdma";
	     CellID.add(info);
		}
		//移动2G卡 + CMCC + 2 
		//type = NETWORK_TYPE_EDGE
		else if(type == TelephonyManager.NETWORK_TYPE_EDGE)
		{
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			int cellIDs = location.getCid();  
			int lac = location.getLac(); 
			CellIDInfo info = new CellIDInfo();
	     info.cellId = cellIDs;
	     info.locationAreaCode = lac;
	     info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);   
	     info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
	     info.radioType = "gsm";
	     CellID.add(info);
		}
		//联通的2G经过测试 China Unicom   1 NETWORK_TYPE_GPRS
		else if(type == TelephonyManager.NETWORK_TYPE_GPRS)
		{
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			int cellIDs = location.getCid();  
			int lac = location.getLac(); 
			CellIDInfo info = new CellIDInfo();
	     info.cellId = cellIDs;
	     info.locationAreaCode = lac;
	     //经过测试，获取联通数据以下两行必须去掉，否则会出现错误，错误类型为JSON Parsing Error
	     //info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);   
	     //info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
	     info.radioType = "gsm";
	     CellID.add(info);
		}
		else
		{
			
		}	
		//locString = callGear(CellID);
		return callGear(CellID);
	}



	/**  
	     * @param cellID
	     * @return  封装基站信息
	     * @description   
	     * @version 1.0  
	     * @author liuxiaokun  
	     * @update 2012-5-8 下午12:04:37  
	     */
	private HashMap<String, Double> callGear(ArrayList<CellIDInfo> cellID) {
	if (cellID == null) return null;		
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();
	try {
		holder.put("version", "1.1.0");
		holder.put("host", "maps.google.com");
		holder.put("home_mobile_country_code", cellID.get(0).mobileCountryCode);
		holder.put("home_mobile_network_code", cellID.get(0).mobileNetworkCode);
		holder.put("radio_type", cellID.get(0).radioType);
		holder.put("request_address", true);
		if ("460".equals(cellID.get(0).mobileCountryCode)){ 
			holder.put("address_language", "zh_CN");
		}else{
			holder.put("address_language", "en_US");
			}
		JSONObject data,current_data;
		JSONArray array = new JSONArray();
		current_data = new JSONObject();
		current_data.put("cell_id", cellID.get(0).cellId);
		current_data.put("location_area_code", cellID.get(0).locationAreaCode);
		current_data.put("mobile_country_code", cellID.get(0).mobileCountryCode);
		current_data.put("mobile_network_code", cellID.get(0).mobileNetworkCode);
		current_data.put("age", 0);
		array.put(current_data);
		if (cellID.size() > 2) {
			for (int i = 1; i < cellID.size(); i++) {
				data = new JSONObject();
				data.put("cell_id", cellID.get(i).cellId);
				data.put("location_area_code", cellID.get(i).locationAreaCode);
				data.put("mobile_country_code", cellID.get(i).mobileCountryCode);
				data.put("mobile_network_code", cellID.get(i).mobileNetworkCode);
				data.put("age", 0);
				array.put(data);
			}
		}
		holder.put("cell_towers", array);
		StringEntity se = new StringEntity(holder.toString());
		Log.e("Location send", holder.toString());
		post.setEntity(se);
		HttpResponse resp = client.execute(post);
		HttpEntity entity = resp.getEntity();
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(entity.getContent()));
		StringBuffer sb = new StringBuffer();
		String result = br.readLine();
		while (result != null) {
			Log.e("Locaiton receive", result);
			sb.append(result);
			result = br.readLine();
		}
		if(sb.length() <= 1){
			return null;
		}
		data = new JSONObject(sb.toString());
		data = (JSONObject) data.get("location");
		//data = (JSONObject) data.get("address");
		/*Location loc = new Location(LocationManager.NETWORK_PROVIDER);
		loc.setLatitude((Double) data.get("latitude"));
		loc.setLongitude((Double) data.get("longitude"));
		loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
		loc.setTime(GetUTCTime());*/
		//String city = (String) data.get("city");
		Double latitude = (Double) data.get("latitude");
		Double longitude = (Double) data.get("longitude");
		locationMap.put(ConstantField.LATITUDE, latitude);
		locationMap.put(ConstantField.LONGITUDE, longitude);
		//Toast.makeText(context, "tower ", Toast.LENGTH_LONG).show();
		return locationMap;
	} catch (JSONException e) {
	return null;
	} catch (UnsupportedEncodingException e) {
	e.printStackTrace();
	} catch (ClientProtocolException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	return null;
	}
	
	//计算两点之间的距离
	private static final double EARTH_RADIUS = 6378137;
	
    private static double rad(double d)
    {
       return d * Math.PI / 180.0;
    }
    
    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double GetDistance(double lng1, double lat1, double lng2, double lat2)
    {
       double radLat1 = rad(lat1);
       double radLat2 = rad(lat2);
       double a = radLat1 - radLat2;
       double b = rad(lng1) - rad(lng2);
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
       s = s * EARTH_RADIUS;
       s = Math.round(s * 10000) / 10000;
       return s;
    }
    
    

	
	
	
	/**  
	        * @param itude
	        * @return
	        * @throws Exception  
	        * @description   获取地理位置
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 下午12:05:15  
	        */
	public String getLocation(Location itude) throws Exception {
	String resultString = "";
	
	/** 这里采用get方法，直接将参数加到URL上 */
	String urlString = String.format("http://maps.google.cn/maps/geo?key=abcdefg&q=%s,%s", itude.getLatitude(), itude.getLongitude());
	Log.i("URL", urlString);
	
	/** 新建HttpClient */
	
	/** 采用GET方法 */
	HttpGet get = new HttpGet(urlString);
	try {
	/** 发起GET请求并获得返回数据 */
	HttpResponse response = client.execute(get);
	HttpEntity entity = response.getEntity();
	BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
	StringBuffer strBuff = new StringBuffer();
	String result = null;
	while ((result = buffReader.readLine()) != null) {
		strBuff.append(result);
	}
	resultString = strBuff.toString();
	
	/** 解析JSON数据，获得物理地址 */
	if (resultString != null && resultString.length() > 0) {
		JSONObject jsonobject = new JSONObject(resultString);
		JSONArray jsonArray = new JSONArray(jsonobject.get("Placemark").toString());
		resultString = "";
		for (int i = 0; i < jsonArray.length(); i++) {
			resultString = jsonArray.getJSONObject(i).getString("address");
		}
	}
	} catch (Exception e) 
	{
		throw new Exception("获取物理位置出现错误:" + e.getMessage());
	} finally 
	{
		get.abort();
		client = null;
	}
	
	return resultString;
	}
	
	
	/**  
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 下午12:05:33  
	        *//*
	public long GetUTCTime() { 
		Calendar cal = Calendar.getInstance(Locale.CHINA); 
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET); 
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET); 
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset)); 
		return cal.getTimeInMillis();
	}*/
	
	
}
