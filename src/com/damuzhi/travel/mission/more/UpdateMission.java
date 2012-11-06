/**  
        * @title DownloadMission.java  
        * @package com.damuzhi.travel.mission.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-8 下午12:32:32  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.more;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-8 下午12:32:32  
 */

public class UpdateMission
{
	private static final String TAG = "DownloadMission";	
	private static UpdateMission instance = null;
	private String appUpdateTile ;
	private String appUpdateContent;
	
	private UpdateMission() {
	}
	public static UpdateMission getInstance() {
		if (instance == null) {
			instance = new UpdateMission();
		}
		return instance;
	}
	
	public Map<Integer, String> getNewVersionCityData(List<Integer> installedCityList)
	{
		HashMap<Integer, String> newVersionCityData = new HashMap<Integer, String>();
		if(installedCityList != null &&installedCityList.size()>0)
		{
			HashMap<Integer, Float> latestVersionHashMap = AppManager.getInstance().getlatestVersion();
			HashMap<Integer, Float> dataVersionHashMap = LocalStorageMission.getInstance().getDataVersion(installedCityList);
			HashMap<Integer, String> latestDataDownloadURL = AppManager.getInstance().getlatestDataDownloadURL();
			if(latestVersionHashMap != null&&dataVersionHashMap != null)
			{
				for(int cityId:installedCityList)
				{
					if(latestVersionHashMap.containsKey(cityId)&&dataVersionHashMap.containsKey(cityId))
					{
						float latestVersion = latestVersionHashMap.get(cityId);
						float dataVersion = dataVersionHashMap.get(cityId);
						String downloadURL = latestDataDownloadURL.get(cityId);
						if(latestVersion>dataVersion)
						{
							newVersionCityData.put(cityId, downloadURL);
						}
					}
				}
			}
		}
		return newVersionCityData;
	}
	
	public float getNewVersion()
	{
		float version = 0f;
		String url = ConstantField.ANDROID_VERSION;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader br = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				
					inputStreamReader = new InputStreamReader(inputStream);
					br = new BufferedReader(inputStreamReader);
					StringBuffer sb = new StringBuffer();
					String result = br.readLine();
					Log.i(TAG, "<getNewVersion> result "+result);
					while (result != null) {
						sb.append(result);
						result = br.readLine();
					}
					if(sb.length() <= 1){
						return version;
					}
					JSONObject versionData = new JSONObject(sb.toString());
					if(versionData.has("app_update_title"))
					{
						this.appUpdateTile = versionData.getString("app_update_title");
					}
					if(versionData.has("app_update_content"))
					{
						this.appUpdateContent = versionData.getString("app_update_content");
					}
					inputStream.close();
					inputStream = null;	
					br.close();
					if (versionData != null ){
						version = Float.parseFloat(versionData.getString("app_version"));
						return version;
					}					
			}		
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getNewVersion> catch exception = "+e.toString(), e);
			try
			{
				if (inputStream != null){				
						inputStream.close();
				}
				if (inputStreamReader != null){
						inputStreamReader.close();
				}
				if (br != null){				
						br.close();
				}
			} catch (IOException e1)
			{
			}
			return version;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){				
						inputStream.close();
				}
				if (inputStreamReader != null){
						inputStreamReader.close();
				}
				if (br != null){				
						br.close();
				}
			} catch (IOException e1)
			{
			}
		}
		return version;
	}
	public String getAppUpdateTile()
	{
		return appUpdateTile;
	}
	public String getAppUpdateContent()
	{
		return appUpdateContent;
	}
	public void setAppUpdateTile(String appUpdateTile)
	{
		this.appUpdateTile = appUpdateTile;
	}
	public void setAppUpdateContent(String appUpdateContent)
	{
		this.appUpdateContent = appUpdateContent;
	}
}
