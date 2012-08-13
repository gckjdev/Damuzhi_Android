
package com.damuzhi.travel.mission.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

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
import com.damuzhi.travel.protos.AppProtos.HelpInfo;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;

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
		try
		{
			String url = String.format(ConstantField.APP, ConstantField.LANG_HANS);
			appInputStream =  HttpTool.sendGetRequest(url);
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
	
	
	
	
	
	
	
	/*public void initAppData(Context context){
	this.context = context;
	final AssetManager assets = context.getAssets();
	FileOutputStream outputStream = null;
	InputStream appInputStream =null;
	try
	{
		appInputStream  = assets.open(ConstantField.APP_FILE);		
		boolean sdcardEnable = FileUtil.sdcardEnable();
		if(sdcardEnable)
		{		
			if(!FileUtil.checkFileIsExits(ConstantField.APP_DATA_PATH))
			{						
				File file = new File(ConstantField.APP_DATA_PATH);
				file.mkdirs();
				FileUtil.copyFile(appInputStream, ConstantField.APP_DATA_FILE);	
				//appInputStream.close();										
			}				
			AppManager.getInstance().load();
		}else {				
			outputStream = context.openFileOutput(ConstantField.APP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
			FileUtil.copyFile(appInputStream, outputStream);
			//outputStream.close();
			AppManager.getInstance().load(context);	
		}
		outputStream = context.openFileOutput(ConstantField.APP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
		FileUtil.copyFile(appInputStream, outputStream);
		//outputStream.close();
		AppManager.getInstance().load(context);
	}catch (Exception e) {
		Log.e(TAG, "<initAppData> but catch exception while read app data from apk, exception = "+e.toString(), e);
	}		
	
	int cityId = getCurrentCityId(context);
	if(cityId == -1||cityId ==0)
	{
		cityId =  AppManager.getInstance().getDefaulCityId();
	}
	AppManager.getInstance().setCurrentCityId(cityId);
}*/
	
	
	/*private class UpdateAppTask extends AsyncTask<String, Void, Boolean>{

	@Override
	protected Boolean doInBackground(String... params)
	{
		boolean sdcardEnable = FileUtil.sdcardEnable();
		boolean result = false;
		if(sdcardEnable)
		{
			result = downloadAppData();
		}else {
			result = downloadAppDataToLocal();
		}
		result = downloadAppDataToLocal();
		if (result){
			try
			{			
				if(sdcardEnable)
				{
					result = FileUtil.copyFile(ConstantField.APP_DATA_TEMP_FILE, ConstantField.APP_DATA_FILE);	
				}else {
					FileOutputStream fileOutputStream = context.openFileOutput(ConstantField.APP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
					FileInputStream fileInputStream = context.openFileInput(ConstantField.APP_TEMP_FILE);
					result = FileUtil.copyFile(fileInputStream,fileOutputStream);
				}	
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
			AppManager.getInstance().reloadData();
		}
	}
}*/
	
	
	/*private boolean downloadHelpZipDataToLocal()
	{		
		boolean result = false;
		
        String url = AppManager.getInstance().getHelpURL();
        if(url != null)
		{ 
    		InputStream inStream = null;
    		RandomAccessFile threadfile = null;  
    		FileOutputStream outputStream = null;
    		try
    		{  			
    			URL helpURL = new URL(url);
    			int fileSize = HttpTool.getConnection(url).getContentLength();
    			inStream = HttpTool.getDownloadInputStream(helpURL, 0, fileSize);
    			if(inStream !=null)
    			{
    				String fileName = HttpTool.getFileName(HttpTool.getConnection(url), url);
        			outputStream = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
        			//File helpFile = new File(android.os.Environment.getDataDirectory() ,fileName);
    				byte[] buffer = new byte[1024];
        			int offset = 0;
        			threadfile = new RandomAccessFile(fileName, "rwd");
        			while ((offset = inStream.read(buffer, 0, 1024)) != -1) {					
        				threadfile.write(buffer, 0, offset);										
        			}
        			threadfile.close();
        			inStream.close();	
        			result = true;
    			}
    			
    		} catch (Exception e)
    		{
    			Log.e(TAG, "<downloadHelpZipData> catch exception = "+e.toString(), e);
    			result = false;
    		}finally
    		{
    			try
				{
    				if(threadfile != null)
    				{
    					threadfile.close();
    				}
					if(inStream != null)
					{
						inStream.close();
					}	
					if(outputStream != null)
					{
						outputStream.close();
					}
				} catch (IOException e)
				{
				}
    			
    		}		
		}
		
		return result;
				
	}*/
	
	
	/*private boolean downloadHelpProtoDataToLocal()
	{		
		boolean result = false;    
		InputStream helpInputStream = null;
		TravelResponse travelResponse = null;
		FileOutputStream output = null;
		String url = String.format(ConstantField.HELP, ConstantField.LANG_HANS);
		try
		{
			helpInputStream =  HttpTool.sendGetRequest(url);
			if(helpInputStream !=null)
			{
				travelResponse = TravelResponse.parseFrom(helpInputStream);	
				if (travelResponse != null && travelResponse.getResultCode() == 0){
					output = context.openFileOutput(ConstantField.HELP_TEMP_FILE, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
					HelpInfo help = travelResponse.getHelpInfo();
					HelpInfo.Builder helpBuilder = HelpInfo.newBuilder();
					helpBuilder.mergeFrom(help);
					helpBuilder.build().writeTo(output);		
					result = true;
				}
			}else
			{
				result = false;
				//Toast.makeText(context, context.getString(R.string.conn_fail_exception), Toast.LENGTH_LONG).show();
			}			
		} catch (Exception e)
		{
			Log.e(TAG, "<downloadHelpProtoDataToLocal> catch exception = "+e.toString(), e);
			result = false;
		}
		finally
		{
			try
			{
				if(output != null)
				{
					output.close();
				}
				if(helpInputStream != null)
				{
					helpInputStream.close();
				}			
			} catch (IOException e)
			{
			}				
		}
		
		return result;
				
	}*/
	
	
	/*private boolean downloadAppData()
	{		
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_TEMP_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }
		InputStream appInputStream = null;
		FileOutputStream output = null;
		try
		{
			String url = String.format(ConstantField.APP, ConstantField.LANG_HANS);
			appInputStream =  HttpTool.sendGetRequest(url);
			if(appInputStream != null)
			{
				TravelResponse travelResponse = TravelResponse.parseFrom(appInputStream);	
				if (travelResponse != null && travelResponse.getResultCode() == 0){
					output = new FileOutputStream(ConstantField.APP_DATA_TEMP_FILE);
					App app = travelResponse.getAppInfo();
					App.Builder appBuilder = App.newBuilder();
					appBuilder.mergeFrom(app);
					appBuilder.build().writeTo(output);		
					result = true;								
					output.close();
					appInputStream.close();					
				}				
			}else {
				result = false;
				//Toast.makeText(context, context.getString(R.string.conn_fail_exception), Toast.LENGTH_LONG).show();
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<downloadAppData> catch exception = "+e.toString(), e);
			result = false;
		}finally
		{
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
				
	}*/
	

}
