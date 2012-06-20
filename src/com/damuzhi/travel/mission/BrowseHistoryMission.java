/**  
        * @title BrowseHistoryMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-18 上午10:40:09  
        * @version V1.0  
 */
package com.damuzhi.travel.mission;

import java.util.List;

import android.util.Log;

import com.damuzhi.travel.model.more.BrowseHistoryManager;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-18 上午10:40:09  
 */

public class BrowseHistoryMission
{
	private static final String TAG = "BrowseHistoryMission";
	private static BrowseHistoryMission instance = null;
	private static BrowseHistoryManager historyManager = new BrowseHistoryManager();
	private BrowseHistoryMission() {
	}
	
	public static BrowseHistoryMission getInstance() {
		if (instance == null) {
			instance = new BrowseHistoryMission();
		}
		return instance;
	}
	
	public void addBrowseHistory(Place place)
	{
		if(place != null)
		{
			historyManager.addHistory(place);
			Log.i(TAG, "add browse history place = "+place.getName());
		}
	}
	
	public List<Place> loadBrowseHistory()
	{
		return historyManager.loadHistoryData();
	}
	
	
	public void clearHistory()
	{
		historyManager.clearHistory();
	}

	
}
