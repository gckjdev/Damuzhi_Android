/**  
        * @title HelpMission.java  
        * @package com.damuzhi.travel.mission.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-7-27 上午11:20:07  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.HelpInfo;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.ZipUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-27 上午11:20:07  
 */

public class HelpMission
{
	private static final String TAG = "HelpMission";	
	private Context context;
	private static HelpMission instance = null;
	private HelpMission() {
	}
	public static HelpMission getInstance() {
		if (instance == null) {
			instance = new HelpMission();
		}
		return instance;
	}
	
	public void updateHelpData(Context context)
	{
		this.context = context;
		UpdateHelpTask helpTask = new UpdateHelpTask();
		helpTask.execute();
	}
	
	
	public void initHelpData()
	{
		final AssetManager assets = context.getAssets();
		FileOutputStream helpHtmlOutputStream = null;
		FileOutputStream helpDataOutputStream = null;
		FileOutputStream helpJPGOutputStream = null;
		InputStream helpHtmlInputStream = null;
		InputStream helpDataInputStream = null;
		InputStream helpJPGInputStream = null;
		try
		{			
			boolean sdcardEnable = FileUtil.sdcardEnable(); 
			if(sdcardEnable)
			{
				boolean isExits = FileUtil.checkFileIsExits(ConstantField.HELP_PATH);
				if(!isExits)
				{
					File file = new File(ConstantField.HELP_PATH);
					file.mkdirs();					
				}
				helpHtmlInputStream  = assets.open(ConstantField.LOCAL_HELP_HTML_FILE);	
				helpDataInputStream = assets.open(ConstantField.HELP_FILE);
				helpJPGInputStream = assets.open(ConstantField.LOCAL_HELP_JPG_FILE);
				helpHtmlOutputStream = new FileOutputStream(new File(ConstantField.HELP_HTML_FILE));
				helpDataOutputStream = new FileOutputStream(new File(ConstantField.HELP_DATA_FILE));
				helpJPGOutputStream = new FileOutputStream(new File(ConstantField.HELP_JPG_FILE));
				
				
				long helpDataLength = helpDataInputStream.available();
				long helpHtmlLength = helpHtmlInputStream.available();
				long helpJPGLength = helpJPGInputStream.available();
				long sdFreeMb = FileUtil.getAvailableExternalMemorySize();
				if(sdFreeMb>helpDataLength)
				{
					FileUtil.copyFile(helpDataInputStream,helpDataOutputStream);
				}
				sdFreeMb = FileUtil.getAvailableExternalMemorySize();
				if (sdFreeMb>helpHtmlLength)
				{
					FileUtil.copyFile(helpHtmlInputStream,helpHtmlOutputStream);
				}
				sdFreeMb = FileUtil.getAvailableExternalMemorySize();
				if(sdFreeMb>helpJPGLength)
				{
					FileUtil.copyFile(helpJPGInputStream,helpJPGOutputStream);
				}
				
				
			}
		}catch (Exception e) {
			Log.e(TAG, "<initHelpData> but catch exception while read app data from apk, exception = "+e.toString(), e);
		}		
		
		
	}
	
	
	private boolean downloadHelpProtoData()
	{		
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_TEMP_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }      
		InputStream helpInputStream = null;
		TravelResponse travelResponse = null;
		FileOutputStream output = null;
		String url = String.format(ConstantField.HELP, ConstantField.LANG_HANS);
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			helpInputStream =  httpTool.sendGetRequest(url);
			if(helpInputStream !=null)
			{
				long sdFreeM = FileUtil.getAvailableExternalMemorySize();
				long fileLength = helpInputStream.available();
				/*travelResponse = TravelResponse.parseFrom(helpInputStream);	
				if (travelResponse != null && travelResponse.getResultCode() == 0){
					output = new FileOutputStream(ConstantField.HELP_DATA_TEMP_FILE);
					HelpInfo help = travelResponse.getHelpInfo();
					HelpInfo.Builder helpBuilder = HelpInfo.newBuilder();
					helpBuilder.mergeFrom(help);
					helpBuilder.build().writeTo(output);		
					result = true;
				}*/
				if(sdFreeM >fileLength)
				{
					travelResponse = TravelResponse.parseFrom(helpInputStream);	
					if (travelResponse != null && travelResponse.getResultCode() == 0){
						output = new FileOutputStream(ConstantField.HELP_DATA_TEMP_FILE);
						HelpInfo help = travelResponse.getHelpInfo();
						HelpInfo.Builder helpBuilder = HelpInfo.newBuilder();
						helpBuilder.mergeFrom(help);
						helpBuilder.build().writeTo(output);		
						result = true;
					}
				}else {
					TravelApplication.getInstance().notEnoughMemoryToast();
					Log.e(TAG, "<downloadHelpProtoData> download help proto file fail,cause sdcard memory not enough ");
					result = false;
				}
				
			}else
			{
				result = false;
				//Toast.makeText(context, context.getString(R.string.conn_fail_exception), Toast.LENGTH_LONG).show();
			}			
		} catch (Exception e)
		{
			Log.e(TAG, "<downloadHelpProtoData> catch exception = "+e.toString(), e);
			result = false;
		}
		finally
		{
			httpTool.stopConnection();
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
	}
	
	

	
	
	
	
	private boolean downloadHelpZipData()
	{		
		boolean result = false;
		
        String url = AppManager.getInstance().getHelpURL();
        if(url != null)
		{
    		File helpFolder = new File(ConstantField.HELP_PATH);  
    		InputStream inStream = null;
    		RandomAccessFile threadfile = null;
            if (!helpFolder.exists())
            {
            	helpFolder.mkdirs();
            }        
            HttpTool httpTool = HttpTool.getInstance();
    		try
    		{
    			File helpFile = new File(helpFolder ,HttpTool.getFileName(httpTool.getConnection(url), url));
    			URL helpURL = new URL(url);
    			int fileSize = httpTool.getConnection(url).getContentLength();
    			inStream = httpTool.getDownloadInputStream(helpURL, 0, fileSize);
    			//long sdFreeM = FileUtil.freeSpaceOnSd();
    			if(inStream !=null )
    			{
    				byte[] buffer = new byte[1024];
        			int offset = 0;
        			threadfile = new RandomAccessFile(helpFile, "rwd");
        			while ((offset = inStream.read(buffer, 0, 1024)) != -1) {					
        				threadfile.write(buffer, 0, offset);										
        			}
        			threadfile.close();
        			inStream.close();	
        			result = true;
    			}else {
					TravelApplication.getInstance().notEnoughMemoryToast();
					Log.e(TAG, "<downloadHelpZipData> download help zip file fail,cause sdcard memory not enough ");
					result = false;
				}
    			
    		} catch (Exception e)
    		{
    			Log.e(TAG, "<downloadHelpZipData> catch exception = "+e.toString(), e);
    			result = false;
    		}finally
    		{
    			httpTool.stopConnection();
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
				} catch (IOException e)
				{
				}
    			
    		}		
		}
		
		return result;
				
	}
	
	private class UpdateHelpTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params)
		{
			//boolean sdcardEnable = FileUtil.sdcardEnable(); 
			boolean isExits = FileUtil.checkFileIsExits(ConstantField.HELP_PATH);
			boolean result = false;
			if(!isExits)
			{
				initHelpData();
			}else
			{
				String localDataPath = ConstantField.HELP_DATA_FILE;
				float httpVersion = getHelpHttpVersion();
				boolean checkVersion = TravelUtil.checkHelpIsNeedUpdate(localDataPath,httpVersion);
				if(checkVersion)
				{				
					result = downloadHelpProtoData();
					/*if(sdcardEnable)
					{
						result = downloadHelpProtoData();
					}*/
					if(result)
					{
						result =  FileUtil.copyFile(ConstantField.HELP_DATA_TEMP_FILE, ConstantField.HELP_DATA_FILE);
						if(result)
						{
							result = downloadHelpZipData();	
							Log.i(TAG, "<UpdateHelpTask> update data load, try download...");				
						}
						
					}	
				}else
				{
					return Boolean.valueOf(result);
				}
			}			
			return Boolean.valueOf(result);
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			if (result.booleanValue()){
				ZipUtil.upZipFile(ConstantField.HELP_DATA_ZIP_FILE, ConstantField.HELP_PATH);
			}
		}
	}
	
	
	
	
	private float getHelpHttpVersion()
	{
		float version = 0f;	
		InputStream helpInputStream = null;
		TravelResponse travelResponse = null;
		String url = String.format(ConstantField.HELP, ConstantField.LANG_HANS);
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			helpInputStream =  httpTool.sendGetRequest(url);
			if(helpInputStream != null)
			{
				travelResponse = TravelResponse.parseFrom(helpInputStream);	
				if (travelResponse != null && travelResponse.getResultCode() == 0){
					HelpInfo help = travelResponse.getHelpInfo();
					String versionString = help.getVersion();
					version = Float.valueOf(versionString);
				}
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<getHelpHttpVersion> catch exception = "+e.toString(), e);
		}	
		finally
		{
			httpTool.stopConnection();
			try{
				if(helpInputStream != null)
				{
					helpInputStream.close();
				}				
			} catch (IOException e)
			{
			}	
		}
			
		
		return version;
	}
}
