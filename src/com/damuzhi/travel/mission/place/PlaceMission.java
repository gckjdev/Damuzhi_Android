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

import android.R.integer;
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
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceStatistics;
import com.damuzhi.travel.protos.PlaceListProtos.Statistics;
import com.umeng.common.net.k;

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
	private List<Place> retPlaceList = new ArrayList<Place>();
	private PlaceStatistics placeStatistics = null;
	private boolean hasLocalData = false;
	private int totalCount = 0;
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
		hasLocalData = false;
		retPlaceList.clear();
		final int cityId = AppManager.getInstance().getCurrentCityId();		
		if (LocalStorageMission.getInstance().hasLocalCityData(activity,cityId)){
			hasLocalData = true;
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
			totalCount = retPlaceList.size();
			
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
				placeStatistics =  getPlaceStatisticsByUrl(cityId, categoryId);
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
				totalCount = travelResponse.getTotalCount();
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
	
	/*public void  getPlaceStatistics(int cityId,int categoryId)
	{
		placeStatistics =  getPlaceStatisticsByUrl(cityId, categoryId);
	}*/
	
	
	private PlaceStatistics getPlaceStatisticsByUrl(int cityId, int categoryId)
	{
		int objectType = PlaceNetworkHandler.categoryIdToObjectType(categoryId);
		String url = String.format(ConstantField.PLACE_PAGE_URL, objectType, cityId, 0,count,ConstantField.LANG_HANS);
		Log.i(TAG, "<getPlaceStatisticsByUrl> load place data from http ,url = "+url);
		InputStream inputStream = null;
		try
		{
			inputStream = HttpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getPlaceList() == null){
					return null;
				}					
				inputStream.close();
				inputStream = null;					
				return travelResponse.getPlaceStatistics();			
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getPlaceStatisticsByUrl> catch exception = "+e.toString(), e);
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

		
		public Place getPlaceById(Context context,int placeId)
		{
			if (LocalStorageMission.getInstance().currentCityHasLocalData(context)){
				return localPlaceManager.getPlaceById(placeId);
			}
			else{
				return remotePlaceManager.getPlaceById(placeId);
			}
			
		}

		
		
		
		public String[] countPlaceBySubcate(String[] subcateName, int[] subcateID)
		{
			String[] name = null;
			int total = 0;
			if(subcateName !=null && subcateName.length>0)
			{
				name = new String[subcateName.length+1];
					for(int i=0;i<subcateID.length;i++)
					{
						int count = 0;
						for(Place place:retPlaceList)
						{
							if(place.getSubCategoryId() == subcateID[i])
							{
								count++;
								total++;
							}
						}
						name[i+1] = subcateName[i]+"("+count+")";
						
					}
			}		
			name[0] = "全部("+total+")";
			return name;
		}

		
		public String[] countPlaceByPrice(String[] priceName, int[] priceID)
		{
			String[] name = new String[priceName.length+1];
			int total = 0;
			for(int i=0;i<priceID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					if(place.getPriceRank() == priceID[i])
					{
						count++;
						total++;
					}
				}
				name[i+1] = priceName[i]+"("+count+")";
				
			}
			name[0] = "全部("+total+")";
			return name;
		}
		
		
		public String[] countPlaceByArea(String[] areaName, int[] areaID)
		{
			String[] name = new String[areaName.length+1]; 
			int total = 0;
			for(int i=0;i<areaID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					if(place.getAreaId() == areaID[i])
					{
						count++;
						total++;
					}
				}
				name[i+1] = areaName[i]+"("+count+")";
				
			}
			name[0] = "全部("+total+")";
			return name;
			
		}
		
		
		public String[] countPlaceByService(String[] serviceName, int[] serviceID)
		{
			String[] name = new String[serviceName.length+1];
			int total = 0;
			for(int i=0;i<serviceID.length;i++)
			{
				int count = 0;
				for(Place place:retPlaceList)
				{
					for(int proServiceID:place.getProvidedServiceIdList() )
					{
						if(proServiceID == serviceID[i])
						{
							total++;
							count++;
							break;
						}				
					}
				}
				name[i+1] = serviceName[i]+"("+count+")";
				
			}
			name[0] = "全部("+total+")";
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

		
		public String[] getPriceRank(int cityID)
		{			
			String [] price = AppManager.getInstance().getPriceRank(cityID);
			String [] priceName = null;
			if(price!= null && price.length>0)
			{
				priceName = new String[price.length+1];
				priceName[0] = "全部";
				int i =1;
				for(String name:price)
				{
					priceName[i] = name;
					i++;
				}
			}			
			return priceName;			
		}

		
		public int[] getPriceId(int cityID)
		{
			int [] price =  AppManager.getInstance().getPriceId(cityID);
			int [] priceId = null;
			if(price != null && price.length>0)
			{
				priceId = new int[price.length+1];
				priceId[0] = -1;
				int i = 1;
				for(int id:price)
				{
					priceId[i] = id;
					i++;
				}	
			}
			return priceId;
		}

		
		public int[] getCityAreaKeyList(int cityID)
		{
			int [] areaID = null;
			if(hasLocalData)
			{
				int [] key = AppManager.getInstance().getCityAreaKeyList(cityID);
				areaID = new int[key.length+1];
				areaID[0] = -1;
				int i = 1;
				for(int id:key)
				{
					areaID[i] = id;
					i++;
				}
				return  areaID;
			}else {
				if(placeStatistics != null)
				{
					areaID = new int[placeStatistics.getAreaStaticsCount()];
					int i = 0;
					for(Statistics areaStatistics:placeStatistics.getAreaStaticsList())
					{
						areaID[i] = areaStatistics.getId();
						i++;
					}
				}
				return areaID;
			}
			
		}

		
		public String[] getCityAreaNameList(int cityID)
		{
			
			String [] areaName = null;
			if(hasLocalData)
			{
				String [] name  = AppManager.getInstance().getCityAreaNameList(cityID);
				int [] id = AppManager.getInstance().getCityAreaKeyList(cityID);
				areaName = countPlaceByArea(name, id);
				return   areaName;
			}else {
				if(placeStatistics != null)
				{
					areaName = new String[placeStatistics.getAreaStaticsCount()];
					int i = 0;
					for(Statistics areaStatistics:placeStatistics.getAreaStaticsList())
					{
						areaName[i] = areaStatistics.getName()+"("+areaStatistics.getCount()+")";
						i++;
					}
				}
				return areaName;
			}
		}

		
		public int[] getProvidedServiceKeyList(PlaceCategoryType categoryType)
		{
			int [] serviceID = null;
			if(hasLocalData)
			{
				int [] key =  AppManager.getInstance().getProvidedServiceKeyList(categoryType);
				serviceID = new int[key.length+1];
				serviceID[0] = -1;
				int i =1;
				for(int id:key)
				{
					serviceID[i] = id;
					i++;
				}
				return serviceID;
			}else {
				if(placeStatistics != null)
				{
					serviceID = new int[placeStatistics.getServiceStaticsCount()];
					int i = 0;
					for(Statistics serviceStatistics:placeStatistics.getServiceStaticsList())
					{
						serviceID[i] = serviceStatistics.getId();
						i++;
					}
				}
				return serviceID;
			}
		}

		
		
		public String[] getProvidedServiceNameList(PlaceCategoryType categoryType)
		{
			String [] serviceName = null;
			if(hasLocalData)
			{
				String [] name  = AppManager.getInstance().getProvidedServiceNameList(categoryType);
				int [] id = AppManager.getInstance().getProvidedServiceKeyList(categoryType);
				serviceName = countPlaceByService(name, id);
				return  serviceName;
			}else {
				if(placeStatistics != null)
				{
					serviceName = new String[placeStatistics.getServiceStaticsCount()];
					int i = 0;
					for(Statistics serviceStatistics:placeStatistics.getServiceStaticsList())
					{
						serviceName[i] = serviceStatistics.getName()+"("+serviceStatistics.getCount()+")";
						i++;
					}
				}
				return serviceName;
			}
		}

		
		public String[] getSubCatNameList(PlaceCategoryType categoryType)
		{
			String [] subCatName = null;
			if(hasLocalData)
			{
				String [] name  = AppManager.getInstance().getSubCatNameList(categoryType);;
				int [] id = AppManager.getInstance().getSubCatKeyList(categoryType);
				subCatName = countPlaceBySubcate(name, id);
				return  subCatName;
			}else {
				if(placeStatistics != null)
				{
					subCatName = new String[placeStatistics.getSubCategoryStaticsCount()];
					int i = 0;
					for(Statistics subCateStatistics:placeStatistics.getSubCategoryStaticsList())
					{
						subCatName[i] = subCateStatistics.getName()+"("+subCateStatistics.getCount()+")";
						i++;
					}
				}
				return subCatName;
			}
		}

		
		public int[] getSubCatKeyList(PlaceCategoryType categoryType)
		{
			int [] subCatKey = null;
			if(hasLocalData)
			{
				int [] key = AppManager.getInstance().getSubCatKeyList(categoryType);
				subCatKey = new int[key.length+1];
				subCatKey[0] = -1;
				int i = 1;
				for(int id:key)
				{
					subCatKey[i] = id;
					i++;
				}
				return subCatKey;
			}else {
				if(placeStatistics != null)
				{
					subCatKey = new int[placeStatistics.getSubCategoryStaticsCount()];
					int i = 0;
					for(Statistics subCateStatistics:placeStatistics.getSubCategoryStaticsList())
					{
						subCatKey[i] = subCateStatistics.getId();
						i++;
					}
				}
				return subCatKey;
			}
		}

		
		public List<Place> filterPlace(int categoryType,Activity activity, String subcateType,String areaId, String serviceId, String priceRankId,String sortType,int start)
		{
			retPlaceList.clear();
			int cityId = AppManager.getInstance().getCurrentCityId();
			int objectType = PlaceNetworkHandler.categoryIdToObjectType(categoryType);
			String url = String.format(ConstantField.PLACE_PAGE_FILTER_URL, objectType, cityId,subcateType,areaId,serviceId, priceRankId,sortType,start,count,ConstantField.LANG_HANS);
			Log.i(TAG, "<filterPlace> load place data from http ,url = "+url);
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
				Log.e(TAG, "<filterPlace> catch exception = "+e.toString(), e);
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

		/**  
		        * @return  
		        * @description   
		        * @version 1.0  
		        * @author liuxiaokun  
		        * @update 2012-8-6 下午2:46:47  
		*/
		public int getPlaceTotalCount()
		{
			return totalCount;
		}

		
		
		
		
		
		
		
}
