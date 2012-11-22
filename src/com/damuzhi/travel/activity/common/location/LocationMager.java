/**  
        * @title LocationManager.java  
        * @package com.damuzhi.travel.activity.common.location  
        * @description   
        * @author liuxiaokun  
        * @update 2012-11-20 下午3:55:23  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common.location;

import java.util.HashMap;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.mapapi.location.LocationManagerProxy;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-11-20 下午3:55:23  
 */

public class LocationMager implements LocationListener
{
	private static final String TAG = "LocationManager";
	private LocationManagerProxy locationManager = null;
	private static HashMap<String, Double> loc = new HashMap<String, Double>();
	private Handler handler;
	boolean isGetLocation = false;
	
	/**  
	* Constructor Method   
	* @param context  
	*/
	public LocationMager(Context context)
	{
		locationManager = LocationManagerProxy.getInstance(context);
		enableMyLocation();
	}


	
	
	public void getLocation(Handler handler)
	{
		this.handler = handler;
		isGetLocation = true;
		//enableMyLocation();
	}
	
	public void getLocation()
	{
		isGetLocation = true;
		
	}
	
	private static void setLocation(Location location)
	{
		if(location != null)
		{
			loc.put(ConstantField.LONGITUDE, location.getLongitude());
			loc.put(ConstantField.LATITUDE, location.getLatitude());
		}
	}
	
	
	public boolean enableMyLocation() {
		boolean result = true;
		Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		cri.setAltitudeRequired(false);
		cri.setBearingRequired(false);
		cri.setCostAllowed(false);
		String bestProvider = locationManager.getBestProvider(cri, true);
		locationManager.requestLocationUpdates(bestProvider, 10000, 20, this);
		return result;
	}

	public void disableMyLocation() {
		locationManager.removeUpdates(this);
	}
	
	
	public void destroyMyLocation()
	{
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager.destory();
		}
		locationManager = null;
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {			
			setLocation(location);
			TravelApplication.getInstance().setLocation(loc);		
			if(handler!=null&&isGetLocation){
				Message message = handler.obtainMessage(1);
				message.obj = loc;
				isGetLocation = false;
				handler.sendMessage(message);
			}			
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
}
