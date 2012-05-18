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
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.service.DataService;
import com.damuzhi.travel.service.MainService;
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
		final TravelApplication application = TravelApplication.getInstance();
		final AssetManager assets = WelcomeActivity.this.getAssets();
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				/*String path = String.format(ConstantField.DATA_PATH,1);
				String savepath = ConstantField.SAVE_PATH;
				File saveFile = new File(savepath);
				File file = new File(path);
				String zipFile = "1.zip";
				InputStream inputStream1;
				InputStream inputStream2;
				if(!saveFile.exists())
				{
					saveFile.mkdirs();
				}
				if(!file.exists())
				{				
					try
					{
						inputStream1 = assets.open(ConstantField.APP_FILE);
						inputStream2 = assets.open(zipFile);
						ZipUtil.copyAppDataToSd(inputStream1);						
						ZipUtil.upZipFile(inputStream2,path);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}	*/
					//Thread.sleep(2000);
					/*AppManager appData = new AppManager(ConstantField.APP_DATA_PATH,null);
					application.setApp(appData.getApp());	*/
				LocationUtil getLocation = new LocationUtil(WelcomeActivity.this);	
				application.setLocation( getLocation.getLocationByTower());	
				DataService placeDataService = new DataService(application);
				placeDataService.getAppData(ConstantField.APP_DATA, ConstantField.LANG_HANS,null);
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, IndexActivity.class);
				startActivity(intent);
				finish();
			}
		});
		thread.start();
		
	}

  
}
