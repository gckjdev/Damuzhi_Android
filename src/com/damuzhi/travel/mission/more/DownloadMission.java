/**  
        * @title DownloadMission.java  
        * @package com.damuzhi.travel.mission.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-8-8 下午12:32:32  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.more;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.damuzhi.travel.mission.common.HelpMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.place.PlaceManager;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-8-8 下午12:32:32  
 */

public class DownloadMission
{
	private static final String TAG = "DownloadMission";	
	private static DownloadMission instance = null;
	private DownloadMission() {
	}
	public static DownloadMission getInstance() {
		if (instance == null) {
			instance = new DownloadMission();
		}
		return instance;
	}
	
	public Map<Integer, String> getNewVersionCityData(
			List<Integer> installedCityList)
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
}
