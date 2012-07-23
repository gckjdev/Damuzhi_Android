/**  
        * @title PlaceMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午3:34:02  
        * @version V1.0  
        */
package com.damuzhi.travel.mission.place;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.network.PlaceNetworkHandler;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:34:02  
 */

public class PlaceMission
{
	private static final String TAG = "PlaceMission";
	private static final int count = 20;
	private static PlaceMission instance = null;
	private PlaceManager localPlaceManager = new PlaceManager();
	private PlaceManager remotePlaceManager = new PlaceManager();
	private List<Place> retPlaceList = new ArrayList<Place>();;
	private PlaceMission() {
	}
	
	public static PlaceMission getInstance() {
		if (instance == null) {
			instance = new PlaceMission();
		}
		return instance;
	}
	
	public List<Place> getAllPlace(final int categoryId, Activity activity)
	{
		//retPlaceList = Collections.emptyList();
		retPlaceList.clear();
		final int cityId = AppManager.getInstance().getCurrentCityId();		
		if (LocalStorageMission.getInstance().hasLocalCityData(cityId)){
			LocalStorageMission.getInstance().loadCityPlaceData(cityId);
			// read local
			List<Place> localPlaceList= localPlaceManager.getPlaceLists();
			for(Place place:localPlaceList)
			{
				if(place.getCategoryId() == categoryId)
				{
					retPlaceList.add(place);
				}
			}
			
		}
		else{
			// send remote
			
				final List<Place> remotePlaceList = getPlaceListByUrl(cityId, categoryId);
				if(remotePlaceList != null && remotePlaceList.size() > 0)
				{
					retPlaceList.addAll(remotePlaceList);
				}
								
				// TODO save data in UI thread
				if (remotePlaceList != null && remotePlaceList.size() > 0){	
					remotePlaceManager.clear();
					remotePlaceManager.addPlaces(remotePlaceList);
				}		
		}					
		return retPlaceList;
	}
	
	
	public List<Place> getPlaceNearby(Place place,int num)
	{
		List<Place> nearbyPlaceList = Collections.emptyList();
		/*if (LocalStorageMission.getInstance().currentCityHasLocalData()){
			//return localPlaceManager.getPlaceNearBy(place);
		}
		else{
			int cityId = AppManager.getInstance().getCurrentCityId();
			String url = String.format(ConstantField.PLACE_LIST_NEARBY, ConstantField.NEARBY_PLACE_LIST, cityId, place.getPlaceId(),null,null,num,null,ConstantField.LANG_HANS,null);
			nearbyPlaceList = getNearByPlaceListByUrl(url);	
			//remotePlaceManager.clearNearbyList();
			//remotePlaceManager.setNearbyPlaceList(nearbyPlaceList);
		}*/
		
		int cityId = AppManager.getInstance().getCurrentCityId();
		String url = String.format(ConstantField.PLACE_LIST_NEARBY, ConstantField.NEARBY_PLACE_LIST, cityId, place.getPlaceId(),null,null,num,null,ConstantField.LANG_HANS,null);
		nearbyPlaceList = getNearByPlaceListByUrl(url);		
		return nearbyPlaceList;
	}
	
	
	public List<Place> getPlaceNearbyInDistance(Place place,float distance)
	{
		List<Place> nearbyPlaceList = Collections.emptyList();
		/*if (LocalStorageMission.getInstance().currentCityHasLocalData()){
			//return localPlaceManager.getPlaceNearBy(place);
		}
		else{
			int cityId = AppManager.getInstance().getCurrentCityId();
			String url = String.format(ConstantField.PLACE_LIST_NEARBY, ConstantField.NEARBY_PLACE_LIST_IN_DISTANCE, cityId, place.getPlaceId(),null,null,null,distance,ConstantField.LANG_HANS,null);
			nearbyPlaceList = getNearByPlaceListByUrl(url);	
			//remotePlaceManager.clearNearbyList();
			//remotePlaceManager.setNearbyPlaceList(nearbyPlaceList);
		}*/
		int cityId = AppManager.getInstance().getCurrentCityId();
		String url = String.format(ConstantField.PLACE_LIST_NEARBY, ConstantField.NEARBY_PLACE_LIST_IN_DISTANCE, cityId, place.getPlaceId(),null,null,null,distance,ConstantField.LANG_HANS,null);
		nearbyPlaceList = getNearByPlaceListByUrl(url);	
		return nearbyPlaceList;
	}
	
	
	public List<Place> getPlaceNearbyInDistance(HashMap<String, Double> location,String distance,String placeCategory)
	{
		List<Place> nearbyPlaceList = Collections.emptyList();
		/*if (LocalStorageMission.getInstance().currentCityHasLocalData()){
			//return localPlaceManager.getPlaceNearBy(place);
		}
		else{
			if(location !=null && location.size()>0)
			{
				int cityId = AppManager.getInstance().getCurrentCityId();
				String url = String.format(ConstantField.PLACE_LIST_NEARBY, placeCategory, cityId, null,location.get(ConstantField.LATITUDE),location.get(ConstantField.LONGITUDE),null,distance,ConstantField.LANG_HANS,null);
				nearbyPlaceList = getNearByPlaceListByUrl(url);				
				//remotePlaceManager.clearNearbyList();
				//remotePlaceManager.setNearbyPlaceList(nearbyPlaceList);
			}
		}*/		
		if(location !=null && location.size()>0)
		{
			int cityId = AppManager.getInstance().getCurrentCityId();
			String url = String.format(ConstantField.PLACE_LIST_NEARBY, placeCategory, cityId, null,location.get(ConstantField.LATITUDE),location.get(ConstantField.LONGITUDE),null,distance,ConstantField.LANG_HANS,null);
			nearbyPlaceList = getNearByPlaceListByUrl(url);				
		}
		return nearbyPlaceList;
	}
	
	
	
	/*private List<Place> getPlaceListByUrl(int cityId, int categoryId)
	{
		int objectType = PlaceNetworkHandler.categoryIdToObjectType(categoryId);
		String url = String.format(ConstantField.PLACElIST, objectType, cityId, ConstantField.LANG_HANS);
		Log.i(TAG, "<getPlaceListByUrl> load place data from http ,url = "+url);
		InputStream inputStream = null;
		try
		{
			inputStream = HttpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getPlaceList() == null){
					return Collections.emptyList();
				}					
				inputStream.close();
				inputStream = null;					
				return travelResponse.getPlaceList().getListList();			
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
	}*/
	
	
	private List<Place> getPlaceListByUrl(int cityId, int categoryId)
	{
		int objectType = PlaceNetworkHandler.categoryIdToObjectType(categoryId);
		String url = String.format(ConstantField.PLACE_PAGE_URL, objectType, cityId, 0,count,ConstantField.LANG_HANS);
		Log.i(TAG, "<getPlaceListByUrl> load place data from http ,url = "+url);
		InputStream inputStream = null;
		try
		{
			inputStream = HttpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getPlaceList() == null){
					return Collections.emptyList();
				}					
				inputStream.close();
				inputStream = null;					
				return travelResponse.getPlaceList().getListList();			
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
		InputStream inputStream = null;
		try
		{
			inputStream = HttpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getPlaceList() == null)
				{
					return Collections.emptyList();
				}
				
				inputStream.close();
				inputStream = null;
				return travelResponse.getPlaceList().getListList();						
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

		
		
		
		public String[] countPlaceBySubcate(String[] subcateName, int[] subcateID)
		{
			String[] name = new String[subcateName.length];
			for(int i=0;i<subcateID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					if(place.getSubCategoryId() == subcateID[i])
					{
						count++;
					}
				}
				name[i] = subcateName[i]+"("+count+")";
				
			}
			return name;
		}

		
		public String[] countPlaceByPrice(String[] priceName, int[] priceID)
		{
			String[] name = new String[priceName.length];
			for(int i=0;i<priceID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					if(place.getPriceRank() == priceID[i])
					{
						count++;
					}
				}
				name[i] = priceName[i]+"("+count+")";
				
			}
			return name;
		}
		
		
		public String[] countPlaceByArea(String[] areaName, int[] areaID)
		{
			String[] name = new String[areaName.length]; 
			for(int i=0;i<areaID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					if(place.getAreaId() == areaID[i])
					{
						count++;
					}
				}
				name[i] = areaName[i]+"("+count+")";
				
			}
			return name;
			
		}
		
		
		public String[] countPlaceByService(String[] serviceName, int[] serviceID)
		{
			String[] name = new String[serviceName.length];
			for(int i=0;i<serviceID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					for(int proServiceID:place.getProvidedServiceIdList() )
					{
						if(proServiceID == serviceID[i])
						{
							count++;
							break;
						}
						
					}
				}
				name[i] = serviceName[i]+"("+count+")";
				
			}
			return name;
		}

		
		public List<Place> loadMorePlace(int categoryId,Activity activity,int start,String subcategoryId,String areaId,String serviceId,String priceRankId,String sortType)
		{
			int cityId = AppManager.getInstance().getCurrentCityId();
			int objectType = PlaceNetworkHandler.categoryIdToObjectType(categoryId);
			String url = String.format(ConstantField.PLACE_PAGE_LOAD_MORE_URL, objectType, cityId,subcategoryId,areaId,serviceId, priceRankId,sortType,start,count,ConstantField.LANG_HANS);
			Log.i(TAG, "<loadMorePlace> load place data from http ,url = "+url);
			InputStream inputStream = null;
			try
			{
				inputStream = HttpTool.sendGetRequest(url);
				if(inputStream !=null)
				{				
					TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
					if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getPlaceList() == null){
						return Collections.emptyList();
					}					
					inputStream.close();
					inputStream = null;	
					List<Place> placeList = travelResponse.getPlaceList().getListList(); 
					if(placeList!=null &&placeList.size()>0)
					{
						retPlaceList.addAll(placeList);	
					}
					return placeList;			
				}
				else{
					return Collections.emptyList();
				}
				
			} 
			catch (Exception e)
			{
				Log.e(TAG, "<loadMorePlace> catch exception = "+e.toString(), e);
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
	
}
