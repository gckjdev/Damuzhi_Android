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

	
	public List<CommonTravelTip> getTravelTips(final int travelTipType,final int currentCityId,Activity activity)
	{
		List<CommonTravelTip> retTravelTips = null;		
		if (LocalStorageMission.getInstance().hasLocalCityData(activity,currentCityId)){
			// read local
			if(travelTipType == TravelTipType.GUIDE_VALUE)
			{
				LocalStorageMission.getInstance().loadCityTravelGuideData(currentCityId);	
			}else if (travelTipType == TravelTipType.ROUTE_VALUE) {
				LocalStorageMission.getInstance().loadCityTravelRouteData(currentCityId);	
			}
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
		}
						
		return retTravelTips;
	}
	
	
	
	private  List<CommonTravelTip> getTravelTipsByUrl(int travelTipType,int cityId)
	{
		String tipsType = TravelUtil.getTravelTipsType(travelTipType);
		String url = String.format(ConstantField.PLACElIST, tipsType,cityId,ConstantField.LANG_HANS);
		Log.i(TAG, "<getTravelTipsByUrl> load place data from http ,url = "+url);
		InputStream inputStream = null;
		HttpTool httpTool = new HttpTool();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getOverview() == null){
					return null;
				}
				
				inputStream.close();
				inputStream = null;					
				return travelResponse.getTravelTipList().getTipListList();						
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
		}finally
		{
			httpTool.stopConnection();
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}	
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
