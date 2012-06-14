/**  
        * @title OverviewMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 上午10:31:34  
        * @version V1.0  
 */
package com.damuzhi.travel.mission;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.util.Log;

import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.overview.OverViewManager;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.network.PlaceNetworkHandler;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;



public class OverviewMission
{
	private static final String TAG = "OverviewMission";
	private static OverviewMission instance = null;
	private OverViewManager localOverviewManager = new OverViewManager();
	private OverViewManager remoteOverviewManager = new OverViewManager();
	
	private OverviewMission() {
	}
	
	public static OverviewMission getInstance() {
		if (instance == null) {
			instance = new OverviewMission();
		}
		return instance;
	}

	
	public CommonOverview getOverview(String overviewType,int currentCityId,Activity activity)
	{
		CommonOverview retCommonOverview = null;		
		if (LocalStorageMission.getInstance().hasLocalCityData(currentCityId)){
			// read local
			//retCommonOverview = localOverviewManager.getPlaceDataList();
		}
		else{
			// send remote
			final CommonOverview remoteCommonOverview = getOverviewByUrl(overviewType, currentCityId);
			retCommonOverview = remoteCommonOverview;
			
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
						
		return retCommonOverview;
	}
	
	
	
	private  CommonOverview getOverviewByUrl(String overviewType,int cityId)
	{
		String url = String.format(ConstantField.OVERVIEW, overviewType,cityId,ConstantField.LANG_HANS);
		Log.i(TAG, "<getOverviewByUrl> load place data from http ,url = "+url);
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
					return travelResponse.getOverview();
				} catch (Exception e)
				{					
					Log.e(TAG, "<getOverviewByUrl> catch exception = "+e.toString(), e);
					return null;
				}				
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getOverviewByUrl> catch exception = "+e.toString(), e);
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
