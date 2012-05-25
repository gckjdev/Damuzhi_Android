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

import android.util.Log;

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
	
	public List<Place> getAllPlace(int categoryId)
	{
		List<Place> retPlaceList = Collections.emptyList();
		String cityId = AppManager.getInstance().getCurrentCityId();		
		if (LocalStorageMission.getInstance().hasLocalCityData(cityId)){
			// read local
			retPlaceList = localPlaceManager.getPlaceDataList();
		}
		else{
			// send remote
			retPlaceList = getPlaceListByUrl(cityId, categoryId);
		}
						
		return retPlaceList;
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
	
		
		public void clearLocalData()
		{
			localPlaceManager.clear();			
		}

		
		public void addLocalPlaces(List<Place> list)
		{
			localPlaceManager.addPlaces(list);
		}
		
		
	

}
