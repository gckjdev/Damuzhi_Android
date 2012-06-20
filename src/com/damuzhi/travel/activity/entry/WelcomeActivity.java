package com.damuzhi.travel.activity.entry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.AppMission;
import com.damuzhi.travel.mission.CollectMission;
import com.damuzhi.travel.mission.LocalStorageMission;
import com.damuzhi.travel.mission.UserMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.service.DataService;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.LocationUtil;
import com.damuzhi.travel.util.ZipUtil;

public class WelcomeActivity extends TravelActivity
{	
	private static final String TAG = "WelcomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);	
		/*AppMission.getInstance().initAppData(this);
		AppMission.getInstance().updateAppData(this);*/
		// load place data by current city
		int cityId = AppManager.getInstance().getCurrentCityId();
		LocalStorageMission.getInstance().loadLocalData(cityId);
		//TravelApplication.getInstance().setLocation(LocationUtil.getLocationByTower(this));
		String userId = UserManager.getInstance().getUserId(this);		
		if(userId==null ||userId.equals(""))
		{
			TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId();
			UserMission.getInstance().register(deviceId,this);
		}		
		Intent intent = new Intent();
		intent.setClass(WelcomeActivity.this, IndexActivity.class);
		startActivity(intent);
		finish();		
	}
}
