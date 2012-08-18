/**  
        * @title CollectMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 上午10:21:37  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.favorite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.util.Log;

import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.favorite.FavoriteManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.network.PlaceNetworkHandler;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-2 上午10:21:37  
 */

public class FavoriteMission
{
	private static final String TAG = "FavoriteMission";
	private static FavoriteMission instance = null;
	private FavoriteManager favoriteManger = new FavoriteManager();
	private FavoriteMission() {
	}
	
	public static FavoriteMission getInstance() {
		if (instance == null) {
			instance = new FavoriteMission();
		}
		return instance;
	}
	
	public int getFavoriteCount(int placeId)
	{
		int count = getFavoriteCountByUrl(placeId);		
		return count;
	}
	
	
	private int getFavoriteCountByUrl(int placeId)
	{
		String url = String.format(ConstantField.QUERY_PLACE_FAVORITE_COUNT,null,placeId);
		Log.i(TAG, "<getFavoriteCountByUrl> load collect data from http ,url = "+url);
		InputStream inputStream = null;
		BufferedReader br = null;
		int count = 0;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				if(sb.length() <= 1){
					return count;
				}
				JSONObject favoriteData = new JSONObject(sb.toString());
				if (favoriteData == null || favoriteData.getInt("result")!= 0){
					return count;
				}
				
				inputStream.close();
				br.close();
				inputStream = null;
				count = favoriteData.getInt("placeFavoriteCount");
				return count;						
			}
			else{
				return count;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getFavoriteCountByUrl> catch exception = "+e.toString(), e);
			try
			{
				if (inputStream != null){
					inputStream.close();
				}
				if (br != null){
					br.close();
				}
			} catch (IOException e1)
			{
			}
			return count;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){
					inputStream.close();
				}
				if (br != null){
					br.close();
				}
			} catch (IOException e1)
			{
			}
		}
	}

	
	public int addFavorite(String userId, Place place)
	{
		int result = addFavorite(userId, place.getPlaceId());
		if(result == 0)
		{
			if(!favoriteManger.addFavorite(place))
			{
				result = -1;
			}
		}
		return result;
	}

	
	
	private int addFavorite(String userId,int placeId)
	{
		int resultCode = -1;
		String url = String.format(ConstantField.ADD_FAVORITE,userId,placeId,null,null);
		Log.i(TAG, "<addFavorite> add favorite  ,url = "+url);
		HttpTool httpTool = HttpTool.getInstance();
		InputStream inputStream = null;
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				try
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
					StringBuffer sb = new StringBuffer();
					String result = br.readLine();
					while (result != null) {
						sb.append(result);
						result = br.readLine();
					}
					if(sb.length() <= 1){
						return resultCode;
					}
					JSONObject favoriteData = new JSONObject(sb.toString());
					if (favoriteData == null || favoriteData.getInt("result")!= 0){
						return resultCode;
					}
					
					inputStream.close();
					br.close();
					inputStream = null;
					resultCode = favoriteData.getInt("result");
					return resultCode;
				} catch (Exception e)
				{					
					Log.e(TAG, "<getFavoriteCountByUrl> catch exception = "+e.toString(), e);
					return resultCode;
				}				
			}
			else{
				return resultCode;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getFavoriteCountByUrl> catch exception = "+e.toString(), e);
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return resultCode;
		}finally
		{
			httpTool.stopConnection();
		}
	}

	
	public boolean checkPlaceIsCollected(int placeId)
	{
		boolean isCollected = false;
		isCollected = favoriteManger.checkPlaceIsCollected(placeId);
		return isCollected;
	}

	
	public  List<Place> getMyFavorite(int cityId)
	{
		List<Place> list = favoriteManger.getMyFavorite(cityId);
		return list;
	}
	
	public  List<Place> getMyFavorite(int cityId,int placeCategoryId)
	{
		List<Place> list = favoriteManger.getMyFavorite(cityId,placeCategoryId);
		return list;
	}

	
	

	
	public List<Place> getFavorite(int categoryId)
	{
		int cityId = AppManager.getInstance().getCurrentCityId();
		List<Place> list = getPlaceListByUrl(cityId, categoryId);
		return list;
	}
	
	
	
	private List<Place> getPlaceListByUrl(int cityId, int categoryId)
	{
		String url = String.format(ConstantField.PLACElIST, categoryId, cityId, ConstantField.LANG_HANS);
		Log.i(TAG, "<getPlaceListByUrl> load place data from http ,url = "+url);
		HttpTool httpTool = HttpTool.getInstance();
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

	
	public boolean deleteFavorite(int placeId)
	{
		return favoriteManger.deleteFavorite(placeId);
		
	}
	
}
