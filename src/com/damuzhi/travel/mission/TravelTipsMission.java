/**  
        * @title TravelTipsMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午3:01:14  
        * @version V1.0  
 */
package com.damuzhi.travel.mission;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.overview.OverViewManager;
import com.damuzhi.travel.model.overview.TravelTipsManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTipList;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTips;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-14 下午3:01:14  
 */

public class TravelTipsMission
{
	private static final String TAG = "OverviewMission";
	private static TravelTipsMission instance = null;
	private TravelTipsManager localOverviewManager = new TravelTipsManager();
	private TravelTipsManager remoteOverviewManager = new TravelTipsManager();
	
	private TravelTipsMission() {
	}
	
	public static TravelTipsMission getInstance() {
		if (instance == null) {
			instance = new TravelTipsMission();
		}
		return instance;
	}

	
	public CommonTravelTipList getTravelTips(String overviewType,int currentCityId,Activity activity)
	{
		CommonTravelTipList retTravelTips = null;		
		if (LocalStorageMission.getInstance().hasLocalCityData(currentCityId)){
			// read local
			//retCommonOverview = localOverviewManager.getPlaceDataList();
		}
		else{
			// send remote
			final CommonTravelTipList remoteTravelTips = getTravelTipsByUrl(overviewType, currentCityId);
			retTravelTips = remoteTravelTips;
			
			// TODO save data in UI thread
			/*if (retCommonOverview != null ){
				activity.runOnUiThread(new Runnable()
				{				
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						remotePlaceManager.clear();
						remotePlaceManager.addPlaces(remotePlaceList);
					}
				});				
			}*/
		}
						
		return retTravelTips;
	}
	
	
	
	private  CommonTravelTipList getTravelTipsByUrl(String overviewType,int cityId)
	{
		String url = String.format(ConstantField.PLACElIST, overviewType,cityId,ConstantField.LANG_HANS);
		Log.i(TAG, "<getTravelTipsByUrl> load place data from http ,url = "+url);
		HttpTool httpTool = new HttpTool();
		InputStream inputStream = null;
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				try
				{
					TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
					if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getOverview() == null){
						return null;
					}
					
					inputStream.close();
					inputStream = null;					
					return travelResponse.getTravelTipList();
				} catch (Exception e)
				{					
					Log.e(TAG, "<getTravelTipsByUrl> catch exception = "+e.toString(), e);
					return null;
				}				
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getTravelTipsByUrl> catch exception = "+e.toString(), e);
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return null;
		}
	}
}
