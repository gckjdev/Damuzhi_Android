/**  
        * @title CollectMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 上午10:21:37  
        * @version V1.0  
 */
package com.damuzhi.travel.mission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.util.Log;

import com.damuzhi.travel.model.common.CollectManager;
import com.damuzhi.travel.model.constant.ConstantField;
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

public class CollectMission
{
	private static final String TAG = "CollectMission";
	private static CollectMission instance = null;
	private CollectManager collectManger = new CollectManager();
	private CollectMission() {
	}
	
	public static CollectMission getInstance() {
		if (instance == null) {
			instance = new CollectMission();
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
		HttpTool httpTool = new HttpTool();
		InputStream inputStream = null;
		int count = 0;
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
				} catch (Exception e)
				{					
					Log.e(TAG, "<getFavoriteCountByUrl> catch exception = "+e.toString(), e);
					return count;
				}				
			}
			else{
				return count;
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
			return count;
		}
	}

	
	public int addFavorite(String userId, Place place)
	{
		int result = addFavorite(userId, place.getPlaceId());
		if(result == 0)
		{
			if(!collectManger.addFavorite(place))
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
		HttpTool httpTool = new HttpTool();
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
		}
	}
	
}
