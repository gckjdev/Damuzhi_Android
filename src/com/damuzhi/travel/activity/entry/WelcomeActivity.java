package com.damuzhi.travel.activity.entry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.AppMission;
import com.damuzhi.travel.mission.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.service.DataService;
import com.damuzhi.travel.service.MainService;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.LocationUtil;
import com.damuzhi.travel.util.ZipUtil;

public class WelcomeActivity extends MenuActivity
{	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);	
		
		// load place data by current city
		String cityId = AppManager.getInstance().getCurrentCityId();
		LocalStorageMission.getInstance().loadCityPlaceData(cityId);
		
		Thread thread = new Thread(new Runnable()
		{			
			@Override
			public void run()
			{
				
				//Thread.sleep(2000);
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, IndexActivity.class);
				startActivity(intent);
				finish();
			}
		});
		thread.start();		
	}
}
