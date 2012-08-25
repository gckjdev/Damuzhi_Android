/**  
        * @title LocalStorageService.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-25 上午10:35:04  
        * @version V1.0  
        */
package com.damuzhi.travel.mission.place;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.damuzhi.travel.db.DownloadPreference;
import com.damuzhi.travel.mission.overview.OverviewMission;
import com.damuzhi.travel.mission.overview.TravelTipsMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.CityOverviewProtos.CityOverview;
import com.damuzhi.travel.protos.PackageProtos.Package;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTips;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-25 上午10:35:04  
 */

public class LocalStorageMission
{
	private static final String TAG = "LocalStorageMission";	
	
	private PlaceMission placeMission = PlaceMission.getInstance();
	private TravelTipsMission tipsMission = TravelTipsMission.getInstance();
	private OverviewMission overviewMission = OverviewMission.getInstance();
	private static LocalStorageMission instance = null;
	private LocalStorageMission() {
	}
	public static LocalStorageMission getInstance() {
		if (instance == null) {
			instance = new LocalStorageMission();
		}
		return instance;
	}
	
	
	public boolean hasLocalCityData(Context context,int cityId)
	{
		boolean result = false;
		int downloadStatus = DownloadPreference.getDownloadInfo(context, Integer.toString(cityId));
		if(downloadStatus == 1)
		{
			result = true;
		}
		/*String dataPath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH,cityId);
		boolean result =  FileUtil.checkFileIsExits(dataPath);*/
		return result;
	}
	
	
	
	public  String getCityDataPath(int cityId)
	{
		String dataPath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH,cityId);
		return dataPath;
	}
	
	
	public void loadLocalData(int cityId)
	{
		loadCityPlaceData(cityId);
		loadCityTravelGuideData(cityId);
		loadCityTravelRouteData(cityId);
		loadCityOverviewData(cityId);
	}
	
	
	
	
	public void loadCityPlaceData(int cityId){
		try
		{
			String dataPath = getCityDataPath(cityId);
			Log.i(TAG, "<loadCityPlaceData> load place data from "+dataPath+" for city "+cityId);
			
			// delete all old data
			placeMission.clearLocalData();
			
			// read data from place files
			FileUtil fileUtil = new FileUtil();
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.PLACE_TAG, ConstantField.EXTENSION, true);
			if(fileInputStreams ==null || fileInputStreams.size()==0)
			return ;
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				
				PlaceList placeList = PlaceList.parseFrom(fileInputStream);
				if (placeList != null){				
					placeMission.addLocalPlaces(placeList.getListList());
					//Log.i(TAG, "<loadCityPlaceData> read "+placeList.getListCount()+" place");
				}
				fileInputStream.close();
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<loadCityPlaceData> read local city data but catch exception="+e.toString(), e);
		} 
	}
	
	
	
	public boolean currentCityHasLocalData(Context context)
	{
		int cityId = AppManager.getInstance().getCurrentCityId();		
		return LocalStorageMission.getInstance().hasLocalCityData(context,cityId);
	}
	
	
	public void loadCityTravelGuideData(int cityId)
	{
		try
		{
			String dataPath = getCityDataPath(cityId);
			Log.i(TAG, "<loadCityTravelGuideData> load commonTravelTips data from "+dataPath+" for city "+cityId);
			
			// delete all old data
			tipsMission.clearLocalGuideData();
			
			// read data from place files
			FileUtil fileUtil = new FileUtil();
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.GUIDE_TAG, ConstantField.EXTENSION, true);
			if(fileInputStreams ==null || fileInputStreams.size()==0)
			return ;
			TravelTips.Builder travelTips = TravelTips.newBuilder();
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				/*TravelTips travelTips = TravelTips.parseFrom(fileInputStream);
				if (travelTips != null){
					if(travelTips.getGuideListCount()>0)
					{
						tipsMission.addLocalTravelGuide(travelTips.getGuideListList());
					}
					
					//Log.i(TAG, "<loadCityTravelGuideData> read "+travelTips.getRouteListCount()+" travelGuide");
				}*/
				CommonTravelTip commonTravelTip = CommonTravelTip.parseFrom(fileInputStream);
				if (commonTravelTip != null){
					travelTips.addGuideList(commonTravelTip);
				}
				fileInputStream.close();
			}
			if(travelTips.getGuideListCount()>0)
			{
				tipsMission.addLocalTravelGuide(travelTips.getGuideListList());
			}
			
			
		} catch (Exception e)
		{
			Log.e(TAG, "<loadCityTravelGuideData> read local city commonTravelTips data but catch exception="+e.toString(), e);
		} 
	}
	
	
	public void loadCityTravelRouteData(int cityId)
	{
		try
		{
			String dataPath = getCityDataPath(cityId);
			Log.i(TAG, "<loadCityTravelRouteData> load commonTravelTips data from "+dataPath+" for city "+cityId);
			
			// delete all old data
			tipsMission.clearLocalRouteData();
			
			// read data from place files
			FileUtil fileUtil = new FileUtil();
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.ROUTE_TAG, ConstantField.EXTENSION, true);
			if(fileInputStreams ==null || fileInputStreams.size()==0)
			return ;
			TravelTips.Builder travelTips = TravelTips.newBuilder();
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				/*TravelTips travelTips = TravelTips.parseFrom(fileInputStream);
				if (travelTips != null){
					if(travelTips.getRouteListCount()>0)
					{
						tipsMission.addLocalTravelRoute(travelTips.getRouteListList());
					}					
					//Log.i(TAG, "<loadCityTravelRouteData> read "+travelTips.getRouteListCount()+" travelRoute");
				}*/
				CommonTravelTip commonTravelTip = CommonTravelTip.parseFrom(fileInputStream);
				if (commonTravelTip != null){
					travelTips.addRouteList(commonTravelTip);
				}
				fileInputStream.close();
			}
			if(travelTips.getRouteListCount()>0)
			{
				tipsMission.addLocalTravelRoute(travelTips.getRouteListList());
			}
			
			
		} catch (Exception e)
		{
			Log.e(TAG, "<loadCityTravelRouteData> read local city commonTravelTips data but catch exception="+e.toString(), e);
		} 
	}
	
	
	
	public void loadCityOverviewData(int cityId)
	{
		try
		{
			String dataPath = getCityDataPath(cityId);
			Log.i(TAG, "<loadCityOverviewData> load cityOverview data from "+dataPath+" for city "+cityId);
			
			// delete all old data
			overviewMission.clearLocalOverData();
			
			// read data from place files
			FileUtil fileUtil = new FileUtil();
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.OVERVIEW_TAG, ConstantField.EXTENSION, true);
			if(fileInputStreams ==null || fileInputStreams.size()==0)
			return ;
			
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				CityOverview cityOverview = CityOverview.parseFrom(fileInputStream);
				if (cityOverview != null){				
					overviewMission.addLocalCityOverview(cityOverview);
				}
				fileInputStream.close();
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<loadCityOverviewData> read local cityOverview data but catch exception="+e.toString(), e);
		} 
	}
	
	
	public HashMap<Integer, Float> getDataVersion(List<Integer> installedCityList)
	{
		HashMap<Integer, Float> dataVersion = new HashMap<Integer, Float>();
		try
		{	
			for(int cityId:installedCityList)
			{
				String dataPath = getCityDataPath(cityId);
				Log.i(TAG, "<getDataVersion> load package data from "+dataPath+" for city "+cityId);
				// read data from place files
				FileUtil fileUtil = new FileUtil();
				FileInputStream fileInputStream = fileUtil.getInputStream(dataPath, ConstantField.PACKAGE_TAG, ConstantField.EXTENSION, true);
				if(fileInputStream ==null )
				{
				return null;	
				}
				if(fileInputStream != null)
				{
					Package packageData = com.damuzhi.travel.protos.PackageProtos.Package.parseFrom(fileInputStream);
					if (packageData != null){				
						float version = Float.parseFloat(packageData.getVersion());
						dataVersion.put(packageData.getCityId(), version);
					}
					fileInputStream.close();
				}
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<loadCityPlaceData> read local city data but catch exception="+e.toString(), e);
		} 
		return dataVersion;
	}
	
	
	
}
