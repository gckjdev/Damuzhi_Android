/**  
        * @title CollectManger.java  
        * @package com.damuzhi.travel.model.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 上午10:23:56  
        * @version V1.0  
 */
package com.damuzhi.travel.model.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-2 上午10:23:56  
 */

public class CollectManager
{

	
	private static final String TAG = "CollectManager";

	public boolean addFavorite(Place place)
	{
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }
        
		FileOutputStream output = null;
		try
		{
			if (place != null){
				output = new FileOutputStream(ConstantField.FAVORITE_FILE_PATH);
				PlaceList.Builder placeBuilder = PlaceList.newBuilder();
				placeBuilder.addList(place);
				placeBuilder.build().writeTo(output);		
				result = true;
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<downloadAppData> catch exception = "+e.toString(), e);
			result = false;
		}		
		try
		{
			output.close();
		} catch (IOException e)
		{
		}	
		return result;
		
	}

	
	public boolean checkPlaceIsCollected(int placeId)
	{
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_FILE_PATH))
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_FILE_PATH
					+ " but file not found");
			return false;
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_FILE_PATH);
			PlaceList favoritePlaceList = PlaceList.parseFrom(inputStream);
			for(Place place:favoritePlaceList.getListList())
			{
				if(place.getPlaceId() == placeId)
				{
					return true;
				}
			}
			return false;
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return false;
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}

}
