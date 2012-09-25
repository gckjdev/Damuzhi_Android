package com.damuzhi.travel.activity.common.location;


import java.util.HashMap;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.MyLocation.LocationResult;
import com.damuzhi.travel.model.constant.ConstantField;



public class LocationUtil
{
	private static final String TAG ="LocationUtil";
	private static HashMap<String, Double> loc = new HashMap<String, Double>();
	private Location location;
	private static LocationClient mLocClient;
	public static void getLocation(final Context context)
	{
		if(loc!= null&&loc.size()>0)
		{
			 TravelApplication.getInstance().setLocation(loc);
		}else
		{
			baiduLocation();
		}
	}
	
	
	private static void baiduLocation()
	{
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);	
		option.setAddrType("detail");
		option.setCoorType("bd09ll");		
		option.setScanSpan(20000);
		mLocClient = TravelApplication.getInstance().mLocationClient;
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();
		else 
			Log.d(TAG, " baidu locationSDK locClient is null or not started");
	}
	
	public static void stop()
	{
		if(TravelApplication.getInstance().mLocationClient != null)
		{
			TravelApplication.getInstance().mLocationClient.stop();
		}
	}
	

	private static void getLoc(Context context)
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
		if(provider !=null&&locationManager.isProviderEnabled(provider))
		{			
			locationManager.requestLocationUpdates(provider, 60000, 5, locationListener);
			Location location2 = locationManager.getLastKnownLocation(provider);
			if(location2 != null)
			{
				loc.put(ConstantField.LONGITUDE, location2.getLongitude());
				loc.put(ConstantField.LATITUDE, location2.getLatitude());
			}	
						
		}
	}
	
	

	
	
	

	
	public static LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location)
		{
			setLocation(location);
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
	
	
	private static void setLocation(Location location)
	{
		if(location != null)
		{
			loc.put(ConstantField.LONGITUDE, location.getLongitude());
			loc.put(ConstantField.LATITUDE, location.getLatitude());
		}
	}
	
}
	
