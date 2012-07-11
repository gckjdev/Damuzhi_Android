package com.damuzhi.travel.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
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
	private static HashMap<String, Double> locationMap = new HashMap<String, Double>();
	private DefaultHttpClient client ;
	private static Location loc ;
	
	public static class CellIDInfo {
			
			public int cellId;
			public String mobileCountryCode;
			public String mobileNetworkCode;
			public int locationAreaCode;
			public String radioType;
			public CellIDInfo(){}
		}

	
	
	public static HashMap<String, Double> getLocation(Context context)
	{
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 	
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 20000, 5, locationListener);
		HashMap<String, Double> location = new HashMap<String, Double>();
		if(provider !=null&&locationManager.isProviderEnabled(provider))
		{			
			loc = locationManager.getLastKnownLocation(provider);
			if(loc != null)
			{
				location.put(ConstantField.LONGITUDE, loc.getLongitude());
				location.put(ConstantField.LATITUDE, loc.getLatitude());
			}	
						
		}
		if(loc ==null )
		{
			location = getLocationByTower(context);
		}
		if(location ==null||location.size()==0)
		{
			Toast.makeText(context, context.getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
		}
		return location;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static HashMap<String, Double> getLocationByGps(Context context)
	{
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 	
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		HashMap<String, Double> location = new HashMap<String, Double>();
		if(provider!=null&&locationManager.isProviderEnabled(provider))
		{			
			loc = locationManager.getLastKnownLocation(provider);
			if(loc == null)
			{
				loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if(loc != null)
			{
				location.put(ConstantField.LONGITUDE, loc.getLongitude());
				location.put(ConstantField.LATITUDE, loc.getLatitude());
			}		
			locationManager.requestLocationUpdates(provider, 20000, 5, locationListener);			
		}	
		return location;
	}
	
	
	
	public HashMap<String, Double> getLocationByNetWork(Context context)
	{
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setCostAllowed(true); 
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		String provider = locationManager.getBestProvider(criteria, true); 		
		if(provider!=null&&locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
		{
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 1, locationListener);
			loc = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
		}		 
		if(loc != null)
		{
			Double latitude = loc.getLatitude() * 1E6; 			
			Double longitude = loc.getLongitude() * 1E6; 
			locationMap.put(ConstantField.LATITUDE, latitude);
			locationMap.put(ConstantField.LONGITUDE, longitude);
		}		
		return locationMap;
	}
	
	
	public static LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location)
		{
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
	
	
		
	public static HashMap<String, Double> getLocationByTower(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = tm.getNetworkType();
		ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type ==TelephonyManager.NETWORK_TYPE_1xRTT)
		{
			CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
			if(location != null)
			{
				int cellIDs = location.getBaseStationId();
				int networkID = location.getNetworkId();
				StringBuilder nsb = new StringBuilder();
				nsb.append(location.getSystemId());
			    CellIDInfo info = new CellIDInfo();
			    info.cellId = cellIDs;
			    info.locationAreaCode = networkID; 
			    info.mobileNetworkCode = nsb.toString();
			    String countryCode = tm.getNetworkOperator();
			    if(countryCode!=null&&!countryCode.trim().equals(""))
			     {
			    	 info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
			     }else
			     {
			    	 Toast.makeText(context, context.getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
			    	// return null;
			     }
			    info.radioType = "cdma";
			    CellID.add(info);
			}
			
		}
		else if(type == TelephonyManager.NETWORK_TYPE_EDGE)
		{
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();
			if(location !=null)
			{
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
			
		}
		else if(type == TelephonyManager.NETWORK_TYPE_GPRS)
		{
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			if(location != null)
			{
				int cellIDs = location.getCid();  
				int lac = location.getLac(); 
				CellIDInfo info = new CellIDInfo();
			    info.cellId = cellIDs;
			    info.locationAreaCode = lac;
			    String countryCode = tm.getNetworkOperator();
			    if(countryCode!=null&&!countryCode.trim().equals(""))
			     {
			    	info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);   
				    info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);			  
			     }else
			     {
			    	 Toast.makeText(context, context.getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
			    	// return null;
			     }	   
			    info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);   
			    info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
			    info.radioType = "gsm";
			    CellID.add(info);
			}
			
		}
		HashMap<String, Double> location = callGear(CellID,context);
		return location;
	}



	
	private static HashMap<String, Double> callGear(ArrayList<CellIDInfo> cellID,Context context) {
		if (cellID == null || cellID.size() == 0)
		{
			return null;
		} 		
			HttpPost post = new HttpPost("http://www.google.com/loc/json");
			JSONObject holder = new JSONObject();
			InputStreamReader inputStream = null;
			BufferedReader br = null; 
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
			Log.i("Location send", holder.toString());
			post.setEntity(se);
			DefaultHttpClient client = TravelApplication.getInstance().getHttpClient();
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			inputStream = new InputStreamReader(entity.getContent());
			br = new BufferedReader(inputStream);
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				Log.i(TAG,"<getLocationByTower> Locaiton receive = --->"+result);
				sb.append(result);
				result = br.readLine();
			}
			if(sb.length() <= 1){
				return null;
			}
			data = new JSONObject(sb.toString());
			data = (JSONObject) data.get("location");			
			Double latitude = (Double) data.get("latitude");
			Double longitude = (Double) data.get("longitude");
			locationMap.put(ConstantField.LATITUDE, latitude);
			locationMap.put(ConstantField.LONGITUDE, longitude);
			return locationMap;
		} catch (Exception e) {
			Log.e(TAG, "<getLocationByTower> but catch exception :"+e.toString(),e);
			//Toast.makeText(context, context.getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
			return null;
		} finally
		{
			if(inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (IOException e)
				{
				}
			}
			if (br != null)
			{
				try
				{
					br.close();
				} catch (IOException e)
				{
				}
			}
			
		}
	}
	
	
	
	
	
	public String getLocationAddress(Location itude){
		String resultString = "";
		String urlString = String.format("http://maps.google.cn/maps/geo?key=abcdefg&q=%s,%s", itude.getLatitude(), itude.getLongitude());
		Log.i("URL", urlString);
		HttpGet get = new HttpGet(urlString);
		try {
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuffer strBuff = new StringBuffer();
		String result = null;
		while ((result = buffReader.readLine()) != null) {
			strBuff.append(result);
		}
		resultString = strBuff.toString();
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
			Log.e(TAG, "<getLocation> but catch exception:"+e.toString(),e);
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
	        * @update 2012-5-8 ����12:05:33  
	        *//*
	public long GetUTCTime() { 
		Calendar cal = Calendar.getInstance(Locale.CHINA); 
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET); 
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET); 
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset)); 
		return cal.getTimeInMillis();
	}*/
	
	
}
