/**  
        * @title TravelTipsMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午3:01:14  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.overview;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.util.Log;

import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.overview.OverViewManager;
import com.damuzhi.travel.model.overview.TravelTipsManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTipList;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTipType;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTips;
import com.damuzhi.travel.util.TravelUtil;

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
	private TravelTipsManager localTravelTipsManager = new TravelTipsManager();
	private TravelTipsManager remoteTravelTipsManager = new TravelTipsManager();
	
	private TravelTipsMission() {
	}
	
	public static TravelTipsMission getInstance() {
		if (instance == null) {
			instance = new TravelTipsMission();
		}
		return instance;
	}

	
	public List<CommonTravelTip> getTravelTips(int travelTipType,int currentCityId,Activity activity)
	{
		List<CommonTravelTip> retTravelTips = null;		
		if (LocalStorageMission.getInstance().hasLocalCityData(currentCityId)){
			// read local
			if(travelTipType == TravelTipType.GUIDE_VALUE)
			{
				retTravelTips = localTravelTipsManager.getTravelGuides();
			}else if (travelTipType == TravelTipType.ROUTE_VALUE) {
				retTravelTips = localTravelTipsManager.getTravelRoutes();
			}
		}
		else{
			// send remote
			final List<CommonTravelTip> remoteTravelTips = getTravelTipsByUrl(travelTipType, currentCityId);
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
	
	
	
	private  List<CommonTravelTip> getTravelTipsByUrl(int travelTipType,int cityId)
	{
		String tipsType = TravelUtil.getTravelTipsType(travelTipType);
		String url = String.format(ConstantField.PLACElIST, tipsType,cityId,ConstantField.LANG_HANS);
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
					return travelResponse.getTravelTipList().getTipListList();
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

	
	
	public void clearLocalGuideData()
	{
		localTravelTipsManager.guidesClear();
		
	}

	
	public void addLocalTravelGuide(List<CommonTravelTip> guideList)
	{
		localTravelTipsManager.addGuides(guideList);
		
	}
	
	
	public void clearLocalRouteData()
	{
		localTravelTipsManager.routesClear();
		
	}

	
	public void addLocalTravelRoute(List<CommonTravelTip> routeList)
	{
		localTravelTipsManager.addRoutes(routeList);
		
	}
}
