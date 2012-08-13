package com.damuzhi.travel.activity.entry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.common.HelpMission;
import com.damuzhi.travel.mission.common.UserMission;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.ZipUtil;

public class WelcomeActivity extends MenuActivity
{	
	private static final String TAG = "WelcomeActivity";
	private LocationClient mLocClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);	
		TravelApplication.getInstance().addActivity(this);
		init();
	}
	
	
	private void init()
	{
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				boolean initFlag = FileUtil.checkFileIsExits(ConstantField.LOCAL_APP_DATA_FILE);
				if(!initFlag)
				{
					AppMission.getInstance().initAppData(WelcomeActivity.this);
				}			
				AppMission.getInstance().updateAppData(WelcomeActivity.this);
				HelpMission.getInstance().updateHelpData(WelcomeActivity.this);     
				String userId = UserManager.getInstance().getUserId(WelcomeActivity.this);		
				if(userId==null ||userId.equals(""))
				{
					TelephonyManager telephonyManager = (TelephonyManager) WelcomeActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
					String deviceId = telephonyManager.getDeviceId();
					UserMission.getInstance().register(deviceId,WelcomeActivity.this);
				}	
				LocationUtil.getLocation(WelcomeActivity.this);
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, IndexActivity.class);
				//overridePendingTransition(android.R.anim.accelerate_interpolator, android.R.anim.fade_out);
				startActivity(intent);
				finish();		
			}

			

			
		};

		task.execute();
	}
	
	private boolean checkGPSisOpen() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips2), Toast.LENGTH_SHORT).show();
			return false;
	}
	
	
	/*public  void getLocation(Context context)
	{
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);	
		option.setAddrType("detail");
		option.setCoorType("bd09ll");		
		option.setScanSpan(10000);
		mLocClient = TravelApplication.getInstance().mLocationClient;
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();
		else 
			Log.d(TAG, " baidu locationSDK locClient is null or not started");
	}*/


	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		/*if(mLocClient !=null)
		{
			mLocClient.stop();
		}*/
	}
	
	
	
}
