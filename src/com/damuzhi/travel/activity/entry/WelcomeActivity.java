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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.common.UserMission;
import com.damuzhi.travel.mission.favorite.FavoriteMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.LocationUtil;
import com.damuzhi.travel.util.ZipUtil;

public class WelcomeActivity extends MenuActivity
{	
	private static final String TAG = "WelcomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);	
		TravelApplication.getInstance().addActivity(this);
		HashMap<String, Double> location = LocationUtil.getLocation(this);
		TravelApplication.getInstance().setLocation(location);
		init();
	}
	
	
	private void init()
	{
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				AppMission.getInstance().initAppData(WelcomeActivity.this);
				AppMission.getInstance().updateAppData(WelcomeActivity.this);
				int cityId = AppManager.getInstance().getCurrentCityId();
				LocalStorageMission.getInstance().loadLocalData(cityId);
				
				String userId = UserManager.getInstance().getUserId(WelcomeActivity.this);		
				if(userId==null ||userId.equals(""))
				{
					TelephonyManager telephonyManager = (TelephonyManager) WelcomeActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
					String deviceId = telephonyManager.getDeviceId();
					UserMission.getInstance().register(deviceId,WelcomeActivity.this);
				}		
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				super.onPostExecute(result);
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, IndexActivity.class);
				startActivity(intent);
				finish();		
			}

			

			
		};

		task.execute();
	}
	
	
}
