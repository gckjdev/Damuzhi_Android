/**  
        * @title PlaceMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午3:34:02  
        * @version V1.0  
        */
package com.damuzhi.travel.mission;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.damuzhi.travel.activity.place.CommonPlaceActivity;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.network.PlaceNetworkHandler;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:34:02  
 */

public class PlaceMission
{
	private static final String TAG = "PlaceMission";
	private static PlaceMission instance = null;
	private PlaceManager localPlaceManager = new PlaceManager();
	private PlaceManager remotePlaceManager = new PlaceManager();
	
	private PlaceMission() {
	}
	
	public static PlaceMission getInstance() {
		if (instance == null) {
			instance = new PlaceMission();
		}
		return instance;
	}
	
	public List<Place> getAllPlace(int categoryId,Activity activity)
	{
		List<Place> retPlaceList = Collections.emptyList();
		String cityId = AppManager.getInstance().getCurrentCityId();		
		if (LocalStorageMission.getInstance().hasLocalCityData(cityId)){
			// read local
			retPlaceList = localPlaceManager.getPlaceDataList();
		}
		else{
			// send remote
			final List<Place> remotePlaceList = getPlaceListByUrl(cityId, categoryId);
			retPlaceList = remotePlaceList;
			
			// TODO save data in UI thread
			if (remotePlaceList != null || remotePlaceList.size() > 0){
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
			}
		}
						
		return retPlaceList;
	}
	
	
	public List<Place> getPlaceNearby(Place place,int num)
	{
		List<Place> nearbyPlaceList = Collections.emptyList();
		if (LocalStorageMission.getInstance().currentCityHasLocalData()){
			//return localPlaceManager.getPlaceNearBy(place);
		}
		else{
			String cityId = AppManager.getInstance().getCurrentCityId();
			String url = String.format(ConstantField.PLACE_LIST_NEARBY, ConstantField.NEARBY_PLACE_LIST, cityId, place.getPlaceId(),null,null,num,null,ConstantField.LANG_HANS,null);
			nearbyPlaceList = getNearByPlaceListByUrl(url);	
			//remotePlaceManager.clearNearbyList();
			//remotePlaceManager.setNearbyPlaceList(nearbyPlaceList);
		}
		return nearbyPlaceList;
	}
	
	
	public List<Place> getPlaceNearbyInDistance(Place place,float distance)
	{
		List<Place> nearbyPlaceList = Collections.emptyList();
		if (LocalStorageMission.getInstance().currentCityHasLocalData()){
			//return localPlaceManager.getPlaceNearBy(place);
		}
		else{
			String cityId = AppManager.getInstance().getCurrentCityId();
			String url = String.format(ConstantField.PLACE_LIST_NEARBY, ConstantField.NEARBY_PLACE_LIST_IN_DISTANCE, cityId, place.getPlaceId(),null,null,null,distance,ConstantField.LANG_HANS,null);
			nearbyPlaceList = getNearByPlaceListByUrl(url);	
			//remotePlaceManager.clearNearbyList();
			//remotePlaceManager.setNearbyPlaceList(nearbyPlaceList);
		}
		return nearbyPlaceList;
	}
	
	
	private List<Place> getPlaceListByUrl(String cityId, int categoryId)
	{
		int objectType = PlaceNetworkHandler.categoryIdToObjectType(categoryId);
		String url = String.format(ConstantField.PLACElIST, objectType, cityId, ConstantField.LANG_HANS);
		Log.i(TAG, "<getPlaceListByUrl> load place data from http ,url = "+url);
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
					if (travelResponse == null || travelResponse.getResultCode() != 0 ||
							travelResponse.getPlaceList() == null){
						return Collections.emptyList();
					}
					
					inputStream.close();
					inputStream = null;
					return travelResponse.getPlaceList().getListList();
				} catch (Exception e)
				{					
					Log.e(TAG, "<getPlaceListByUrl> catch exception = "+e.toString(), e);
					return Collections.emptyList();
				}				
			}
			else{
				return Collections.emptyList();
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getPlaceListByUrl> catch exception = "+e.toString(), e);
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return Collections.emptyList();
		}
	}
	
		
	private List<Place> getNearByPlaceListByUrl(String url)
	{
		Log.d(TAG, "<getNearByPlaceListByUrl> load place data from http ,url = "+url);
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
					if (travelResponse == null || travelResponse.getResultCode() != 0 ||
							travelResponse.getPlaceList() == null){
						return Collections.emptyList();
					}
					
					inputStream.close();
					inputStream = null;
					return travelResponse.getPlaceList().getListList();
				} catch (Exception e)
				{					
					Log.e(TAG, "<getNearByPlaceListByUrl> catch exception = "+e.toString(), e);
					return Collections.emptyList();
				}				
			}
			else{
				return Collections.emptyList();
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getNearByPlaceListByUrl> catch exception = "+e.toString(), e);
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return Collections.emptyList();
		}
	}
	
		public void clearLocalData()
		{
			localPlaceManager.clear();			
		}

		
		public void addLocalPlaces(List<Place> list)
		{
			localPlaceManager.addPlaces(list);
		}

		
		public Place getPlaceById(int placeId)
		{
			if (LocalStorageMission.getInstance().currentCityHasLocalData()){
				return localPlaceManager.getPlaceById(placeId);
			}
			else{
				return remotePlaceManager.getPlaceById(placeId);
			}
			
		}

		
		
		
		
	

}
