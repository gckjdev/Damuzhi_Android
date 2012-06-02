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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

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
				Place.Builder placeBuilder = Place.newBuilder();
				placeBuilder.mergeFrom(place);
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

}
