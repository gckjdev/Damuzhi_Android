/**  
        * @title BrowseHistoryManager.java  
        * @package com.damuzhi.travel.model.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-18 上午10:45:00  
        * @version V1.0  
 */
package com.damuzhi.travel.model.more;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-18 上午10:45:00  
 */

public class BrowseHistoryManager
{
	private static final String TAG = "BrowseHistoryManager";

	public boolean addHistory(Place place)
	{
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }
        
		
		try
		{
			PlaceList.Builder placeBuilder = null;
			FileOutputStream output = null;			
	        if (place != null){
	        	output = new FileOutputStream(ConstantField.HISTORY_FILE_PATH, true);
				placeBuilder = PlaceList.newBuilder();
				placeBuilder.addList(place);
				placeBuilder.build().writeTo(output);
				result = true;
			}
	        try
    		{
    			output.close();
    		} catch (IOException e)
    		{
    		}	
		} catch (Exception e)
		{
			Log.e(TAG, "<addHistory> catch exception = "+e.toString(), e);
			result = false;
		}		
		
		return result;
		
	}
	
	
	public List<Place> loadHistoryData()
	{
		String dataPath = ConstantField.HISTORY_FILE_PATH;
		Log.i(TAG, "<loadHistoryData> load place data from "+dataPath);
		boolean fileIsExits = FileUtil.checkFileIsExits(dataPath);
		if(fileIsExits)
		{			
			try
			{
				FileInputStream fileInputStream = new FileInputStream(new File(dataPath));
				if(fileInputStream !=null )
				{
					PlaceList placeList = PlaceList.parseFrom(fileInputStream);					
					fileInputStream.close();
					return placeList.getListList();				
				}
			} catch (Exception e)
			{
				Log.e(TAG, "<loadHistoryData> but catch exception : "+e.toString(),e);
			}			
		}else{
			return Collections.emptyList();
		}
		return Collections.emptyList();
		
		
	}

	
	


	
	public void clearHistory()
	{
		FileUtil.deleteFile(ConstantField.HISTORY_FILE_PATH);
		
	}
}
