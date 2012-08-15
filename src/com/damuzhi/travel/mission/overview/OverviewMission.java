/**  
        * @title OverviewMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 上午10:31:34  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.overview;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.util.Log;

import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.overview.OverViewManager;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.network.PlaceNetworkHandler;
import com.damuzhi.travel.protos.CityOverviewProtos.CityOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;



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

	
	public CommonOverview getOverview(int commonOverviewType,final int currentCityId,Activity activity)
	{
		 CommonOverview retCommonOverview = null;		
		if (LocalStorageMission.getInstance().hasLocalCityData(activity,currentCityId)){
			// read local
			LocalStorageMission.getInstance().loadCityOverviewData(currentCityId);
			retCommonOverview = localOverviewManager.getCityCommonOverview(commonOverviewType);
		}
		else{
			// send remote
			final CommonOverview remoteCommonOverview = getOverviewByUrl(commonOverviewType, currentCityId);
			retCommonOverview = remoteCommonOverview;
		}
						
		return retCommonOverview;
	}
	
	
	
	private  CommonOverview getOverviewByUrl(int overviewType,int cityId)
	{
		String CityOverviewType = TravelUtil.getOverviewType(overviewType);
		String url = String.format(ConstantField.OVERVIEW, CityOverviewType,cityId,ConstantField.LANG_HANS);
		Log.i(TAG, "<getOverviewByUrl> load place data from http ,url = "+url);
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
				return travelResponse.getOverview();		
			}
			else{
				return null;
			}			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getOverviewByUrl> catch exception = "+e.toString(), e);
				try
				{
					if (inputStream != null){
						inputStream.close();
					}
				} catch (IOException e1)
				{
				}
			return null;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){
					inputStream.close();
				}
			} catch (IOException e1)
			{
			}
		}
	}

	
	public void clearLocalOverData()
	{
		localOverviewManager.clear();
		
	}

	
	public void addLocalCityOverview(CityOverview cityOverview)
	{
		localOverviewManager.setCityOverview(cityOverview);
		
	}
}
