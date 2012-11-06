
package com.damuzhi.travel.mission.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-23 下午5:33:16  
 */

public class AppMission
{
	private static final String TAG = "AppMission";	
	private Context context;
	private static AppMission instance = null;
	private AppMission() {
	}
	public static AppMission getInstance() {
		if (instance == null) {
			instance = new AppMission();
		}
		return instance;
	}

	public void initAppData(Context context){
		this.context = context;
		final AssetManager assets = context.getAssets();
		FileOutputStream outputStream = null;
		InputStream appInputStream =null;
		try
		{
			appInputStream  = assets.open(ConstantField.APP_FILE);		
			outputStream = context.openFileOutput(ConstantField.APP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			ActivityManager.MemoryInfo minfo = new ActivityManager.MemoryInfo();
			activityManager.getMemoryInfo(minfo);
			long sysMemo = minfo.availMem;
			long fileLength = appInputStream.available();
			if(sysMemo>fileLength){
				FileUtil.copyFile(appInputStream, outputStream);
				AppManager.getInstance().load(context);
			}else
			{
				TravelApplication.getInstance().notEnoughMemoryToast();
				AppManager.getInstance().load(appInputStream);
				Log.e(TAG, "<initAppData> init  app file fail,cause  memory not enough ");
			}
			
		}catch (Exception e) {
			Log.e(TAG, "<initAppData> but catch exception while read app data from apk, exception = "+e.toString(), e);
		}		
	}
	
	
	
	
	
	
	public int getCurrentCityId(Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.LAST_CITY_ID, 0);
		int cityId = userSharedPreferences.getInt(ConstantField.LAST_CITY_ID, -1);
		AppManager.getInstance().setCurrentCityId(cityId);
		return cityId;
	}

	public boolean saveCurrentCityId(Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.LAST_CITY_ID, 0);
		Editor editor = userSharedPreferences.edit();		
		int currentCityID = AppManager.getInstance().getCurrentCityId();
		editor.putInt(ConstantField.LAST_CITY_ID,currentCityID );
		return editor.commit();
	}
	
	
	public void updateAppData(Context context)
	{
		int cityId = getCurrentCityId(context);
		if(cityId == -1||cityId ==0)
		{
			cityId =  AppManager.getInstance().getDefaulCityId();
		}
		AppManager.getInstance().setCurrentCityId(cityId);
		this.context = context;
		UpdateAppTask task = new UpdateAppTask();
		task.execute();
	}
	
	
	
	
	private boolean downloadAppDataToLocal()
	{				
		boolean result = false;
		InputStream appInputStream = null;
		FileOutputStream output = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			String url = String.format(ConstantField.APP, ConstantField.LANG_HANS);
			appInputStream =  httpTool.sendGetRequest(url);
			if(appInputStream != null)
			{
				ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				ActivityManager.MemoryInfo minfo = new ActivityManager.MemoryInfo();
				activityManager.getMemoryInfo(minfo);
				long sysMemo = minfo.availMem;
				long fileLength = appInputStream.available();
				if(sysMemo>fileLength){
					TravelResponse travelResponse = TravelResponse.parseFrom(appInputStream);	
					if (travelResponse != null && travelResponse.getResultCode() == 0){
						output = context.openFileOutput(ConstantField.APP_TEMP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
						App app = travelResponse.getAppInfo();
						App.Builder appBuilder = App.newBuilder();
						appBuilder.mergeFrom(app);
						appBuilder.build().writeTo(output);		
						result = true;					
						output.close();
						appInputStream.close();
					}
				}else
				{
					TravelApplication.getInstance().notEnoughMemoryToast();
					Log.e(TAG, "<downloadAppDataToLocal> download  app file fail,cause  memory not enough ");
					result = false;
				}				
				
			}else {
				result = false;
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<downloadAppDataToLocal> catch exception = "+e.toString(), e);
			result = false;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if(output != null)
				{
					output.close();
				}
				if(appInputStream != null)
				{
					appInputStream.close();
				}
				
			} catch (IOException e)
			{
			}
			
		}	
		return result;
				
	}
	
	
	
	
	
	
	
	
	
	
	private class UpdateAppTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params)
		{
			boolean result = false;
			result = downloadAppDataToLocal();
			if (result){
				try
				{			
					FileOutputStream fileOutputStream = context.openFileOutput(ConstantField.APP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
					FileInputStream fileInputStream = context.openFileInput(ConstantField.APP_TEMP_FILE);
					result = FileUtil.copyFile(fileInputStream,fileOutputStream);
					if (!result){						
						return Boolean.valueOf(false);
					}					
					Log.i(TAG, "<updateAppData> new data load, try update...");				
					
				} catch (Exception e)
				{
					Log.e(TAG, "<updateAppData> catch exception = "+e.toString(), e);
				}
			}
			
			return Boolean.valueOf(result);
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			if (result.booleanValue()){
				AppManager.getInstance().reloadData(context);
			}
		}
	}
		

}
