package com.damuzhi.travel.activity.common.location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.damuzhi.travel.model.constant.ConstantField;
public class LocationUtil
{
	private static final String TAG = "LocationUtil";
	private static HashMap<String, Double> loc = new HashMap<String, Double>();
	private static LocationClient mLocClient;
	private static LocationUtil instance = null;

	public static LocationUtil getInstance()
	{
		if (instance == null)
		{
			instance = new LocationUtil();
		}
		return instance;
	}

	public String getLocationCityName(HashMap<String, Double> location)
	{
		String resultString = "";
		String urlString = String.format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&language=en&sensor=true", location.get(ConstantField.LATITUDE),location.get(ConstantField.LONGITUDE));
		Log.i("URL", urlString);
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
		try
		{
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuff = new StringBuffer();
			String result = null;
			while ((result = buffReader.readLine()) != null)
			{
				strBuff.append(result);
			}
			resultString = strBuff.toString();
			Log.d(TAG, "google address = " + resultString);
			
			if (resultString != null && resultString.length() > 0)
			{
				JSONObject jsonobject = new JSONObject(resultString);
				JSONArray jsonArray = new JSONArray(jsonobject.get("results").toString());
				JSONObject jsonObject2 = jsonArray.getJSONObject(0);
				JSONArray jsonArray2 = new JSONArray(jsonObject2.getString("address_components"));
				resultString  = jsonArray2.getJSONObject(3).getString("long_name");
				Log.d(TAG, "google city name = "+resultString);
			}
		} catch (Exception e)
		{
		} finally
		{
			get.abort();
			client = null;
		}

		return resultString;
	}

	public void getCoordinate(String addr)
	{
		String address = null;
		try
		{
			address = java.net.URLEncoder.encode(addr, "UTF-8");
			String output = "csv";
			String key = "abc";
			String url = String.format("http://maps.google.com/maps/geo?q=%s&output=%s&key=%s",address, output, key);
			URL myURL = null;
			URLConnection httpsConn = null;
			myURL = new URL(url);
			httpsConn = myURL.openConnection();
			if (httpsConn != null)
			{
				InputStreamReader insr = new InputStreamReader(
						httpsConn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(insr);
				String data = null;
				if ((data = br.readLine()) != null)
				{
					String[] retList = data.split(",");
					double latitude = Double.parseDouble(retList[2]);
					double longitude = Double.parseDouble(retList[3]);
					//initLocation(latitude, longitude);
				}
				insr.close();
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<getCoordinate> but catch exception = " + e.toString(),
					e);
		}
	}

	/*
	 * public static void getLocation(final Context context) { if(loc!= null&&loc.size()>0) { TravelApplication.getInstance().setLocation(loc); }else { baiduLocation(); } }
	 * 
	 * 
	 * private static void baiduLocation() { LocationClientOption option = new LocationClientOption(); option.setOpenGps(true); option.setAddrType("detail"); option.setCoorType("bd09ll"); option.setScanSpan(20000); mLocClient = TravelApplication.getInstance().mLocationClient; mLocClient.setLocOption(option); mLocClient.start(); if (mLocClient != null && mLocClient.isStarted()) mLocClient.requestLocation(); else Log.d(TAG, " baidu locationSDK locClient is null or not started"); }
	 * 
	 * public static void stop() { if(TravelApplication.getInstance().mLocationClient != null) { TravelApplication.getInstance().mLocationClient.stop(); } }
	 * 
	 * 
	 * private static void getLoc(Context context) { LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); Criteria criteria = new Criteria(); criteria.setAccuracy(Criteria.ACCURACY_FINE); criteria.setAltitudeRequired(false); criteria.setBearingRequired(false); criteria.setCostAllowed(true); criteria.setPowerRequirement(Criteria.POWER_LOW); String provider = locationManager.getBestProvider(criteria, true); HashMap<String, Double> location = new HashMap<String, Double>(); if(provider !=null&&locationManager.isProviderEnabled(provider)) { locationManager.requestLocationUpdates(provider, 60000, 5, locationListener); Location location2 = locationManager.getLastKnownLocation(provider); if(location2 != null) { loc.put(ConstantField.LONGITUDE, location2.getLongitude()); loc.put(ConstantField.LATITUDE, location2.getLatitude()); }
	 * 
	 * } }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public static LocationListener locationListener = new LocationListener() {
	 * 
	 * @Override public void onLocationChanged(Location location) { setLocation(location); }
	 * 
	 * @Override public void onProviderDisabled(String provider) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onProviderEnabled(String provider) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onStatusChanged(String provider, int status, Bundle extras) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * };
	 * 
	 * 
	 * private static void setLocation(Location location) { if(location != null) { loc.put(ConstantField.LONGITUDE, location.getLongitude()); loc.put(ConstantField.LATITUDE, location.getLatitude()); } }
	 */

}
