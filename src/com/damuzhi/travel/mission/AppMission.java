/**  
        * @title AppMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-23 下午5:33:16  
        * @version V1.0  
        */
package com.damuzhi.travel.mission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.damuzhi.travel.activity.entry.WelcomeActivity;
import com.damuzhi.travel.download.DownloadProgressListener;
import com.damuzhi.travel.download.FileDownloader;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.PackageProtos.Package;
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
		
		final AssetManager assets = context.getAssets();
		
		File appFileFolder = new File(ConstantField.APP_DATA_PATH);
		File appFile = new File(ConstantField.APP_DATA_FILE);
		InputStream appInputStream = null;
		
		if(!appFileFolder.exists())
		{
			appFileFolder.mkdirs();
		}
		
		if(!appFile.exists())
		{				
			try
			{
				appInputStream = assets.open(ConstantField.APP_FILE);
				FileUtil.copyFile(appInputStream, ConstantField.APP_DATA_FILE);	
				appInputStream.close();				
			} catch (IOException e)
			{
				Log.e(TAG, "<initAppData> but catch exception while read app data from apk, exception = "
						+e.toString(), e);
			}			
		}	
		
		AppManager.getInstance().load();
		int cityId = getCurrentCityId(context);
		if(cityId == -1)
		{
			cityId =  AppManager.getInstance().getDefaulCityId();
		}
		AppManager.getInstance().setCurrentCityId(cityId);
	}
	
	
	public int getCurrentCityId(Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.LAST_CITY_ID, 0);
		int cityId = userSharedPreferences.getInt(ConstantField.LAST_CITY_ID, -1);
		AppManager.getInstance().setCurrentCityId(cityId);
		return cityId;
	}

	public boolean saveLastCityId(Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.LAST_CITY_ID, 0);
		Editor editor = userSharedPreferences.edit();		
		int currentCityID = AppManager.getInstance().getCurrentCityId();
		editor.putInt(ConstantField.LAST_CITY_ID,currentCityID );
		return editor.commit();
	}
	
	
	public void updateAppData(Context context)
	{
		UpdateAppTask task = new UpdateAppTask();
		task.execute();
	}
	
	private boolean downloadAppData()
	{		
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_TEMP_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }
        
		HttpTool httpTool = new HttpTool();
		InputStream appInputStream = null;
		TravelResponse travelResponse = null;
		FileOutputStream output = null;
		try
		{
			appInputStream =  httpTool.sendGetRequest(String.format(ConstantField.APP, ConstantField.LANG_HANS));
			travelResponse = TravelResponse.parseFrom(appInputStream);	
			if (travelResponse != null && travelResponse.getResultCode() == 0){
				output = new FileOutputStream(ConstantField.APP_DATA_TEMP_FILE);
				App app = travelResponse.getAppInfo();
				App.Builder appBuilder = App.newBuilder();
				appBuilder.mergeFrom(app);
				appBuilder.build().writeTo(output);		
				result = true;
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<downloadAppData> catch exception = "
					+e.toString(), e);
			result = false;
		}
		
		try
		{
			output.close();
		} catch (IOException e)
		{
		}
		
		try
		{
			appInputStream.close();
		} catch (IOException e)
		{
		}	
		
		return result;
				
	}
	
	private class UpdateAppTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params)
		{
			boolean result = downloadAppData();
			if (result){
				try
				{
					result = FileUtil.copyFile(ConstantField.APP_DATA_TEMP_FILE, ConstantField.APP_DATA_FILE);
					if (!result){
						return Boolean.valueOf(false);
					}
					
					Log.i(TAG, "<updateAppData> new data load, try update...");				
					
				} catch (Exception e)
				{
					Log.e(TAG, "<updateAppData> catch exception = "
							+e.toString(), e);
				}
			}
			
			return Boolean.valueOf(result);
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			if (result.booleanValue()){
				AppManager.getInstance().reloadData();
			}
		}
	}

}
