package com.damuzhi.travel.activity.entry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.more.OpenCityActivity;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.download.DownloadService;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.common.HelpMission;
import com.damuzhi.travel.mission.common.CommonMission;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.ZipUtil;
import com.damuzhi.travel.R;
public class WelcomeActivity extends MenuActivity
{	
	private static final String TAG = "WelcomeActivity";
//	private LocationClient mLocClient;
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);	
		bundle = getIntent().getBundleExtra("notify");
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
					File file = new File(ConstantField.IMAGE_CACHE_PATH);
					file.mkdirs();
				}			
				AppMission.getInstance().updateAppData(WelcomeActivity.this);
				HelpMission.getInstance().updateHelpData(WelcomeActivity.this);     
				String userId = UserManager.getInstance().getUserId(WelcomeActivity.this);		
				String channelId="000000";  
		        try {  
		               ApplicationInfo  ai = WelcomeActivity.this.getPackageManager().getApplicationInfo(WelcomeActivity.this.getPackageName(), PackageManager.GET_META_DATA);  
		               Object value = ai.metaData.get("YOUMI_CHANNEL");  
		               if (value != null) {  
		            	   channelId= (String)value;  		          
		               }  
		           } catch (Exception e) {  
		               //  
		           }  
				if(userId==null ||userId.equals(""))
				{
					Intent intent2 = new Intent();
					intent2.setAction("com.damuzhi.travel.service.PullNotificationService");
					startService(intent2);
				}
				TelephonyManager telephonyManager = (TelephonyManager) WelcomeActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
				String deviceId = telephonyManager.getDeviceId();
				CommonMission.getInstance().registerDevice(deviceId,channelId,WelcomeActivity.this);
				LocationUtil.getLocation(WelcomeActivity.this);
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);				
				Intent intent = new Intent();
				if(bundle != null)
				{
					Log.d(TAG, "put notify bundle to main activity");
					intent.putExtra("notify", bundle);
				}
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				finish();		
			}

			

			
		};

		task.execute();
	}
	
/*	private boolean checkGPSisOpen() {
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
			Toast.makeText(this, getString(R.string.open_gps_tips2), Toast.LENGTH_SHORT).show();
			return false;
	}*/
	
	
	

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
	}




	@Override
	protected void onResume()
	{
		super.onResume();
	}




	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		bundle = intent.getBundleExtra("notify");
	}
	
	
	
}
