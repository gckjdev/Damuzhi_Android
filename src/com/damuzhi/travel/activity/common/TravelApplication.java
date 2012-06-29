package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.util.LocationUtil;

import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.os.DeadObjectException;
import android.util.Log;

public class TravelApplication extends Application
{
	private static final String TAG = "TravelApplication";
	private DefaultHttpClient defaultHttpClient;
	private HashMap<String, Double> location = new HashMap<String, Double>();	
	private static TravelApplication travelApplication;
	private List<Activity> activityList = new LinkedList<Activity>();
	
	public static TravelApplication getInstance()
	{
		return travelApplication;
	}
	
	
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		travelApplication = this;
		defaultHttpClient = createHttpClient();
		AppMission.getInstance().initAppData(this);
		AppMission.getInstance().updateAppData(this);
		TravelApplication.getInstance().setLocation(LocationUtil.getLocationByTower(this));
	}
	
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		shutdownHttpClient();
	}


	@Override
	public void onTerminate()
	{
		super.onTerminate();
		shutdownHttpClient();
	}
	
	private DefaultHttpClient createHttpClient()
	{
		Log.d(TAG, "createHttpClient().....");
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schreg.register(new Scheme("https",SSLSocketFactory.getSocketFactory(),443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schreg);
		return new DefaultHttpClient(manager, params);
		
	}
	
	public DefaultHttpClient getHttpClient()
	{
		return defaultHttpClient;
	}
	
	private void shutdownHttpClient()
	{
		if(defaultHttpClient !=null && defaultHttpClient.getConnectionManager() !=null)
		{
			defaultHttpClient.getConnectionManager().shutdown();
		}
	}
	 
    public void addActivity(Activity activity){  
        activityList.add(activity);  
    }  
        
    public void exit(){  
        for(Activity activity:activityList){  
            activity.finish();  
        }  
        System.exit(0);  
    }  

	
	public HashMap<String, Double> getLocation()
	{
		return location;
	}

	public void setLocation(HashMap<String, Double> location)
	{
		this.location = location;
	}
	
}
