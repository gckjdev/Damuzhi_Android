/**  
        * @title LocalStorageService.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-25 上午10:35:04  
        * @version V1.0  
        */
package com.damuzhi.travel.mission;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.util.Log;

import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
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
	private static LocalStorageMission instance = null;
	private LocalStorageMission() {
	}
	public static LocalStorageMission getInstance() {
		if (instance == null) {
			instance = new LocalStorageMission();
		}
		return instance;
	}
	/**  
	        * @param cityId
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-25 上午10:44:05  
	        */
	public boolean hasLocalCityData(String cityId)
	{
		// TODO Auto-generated method stub
		String dataPath = String.format(ConstantField.DATA_PATH,cityId);
		boolean result =  FileUtil.checkFileIsExits(dataPath);
		return result;
	}
	/**  
	        * @param cityId
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-25 上午10:45:00  
	        */
	public String getCityDataPath(String cityId)
	{
		String dataPath = String.format(ConstantField.DATA_PATH,cityId);
		return dataPath;
	}
	
	public void loadCityPlaceData(String cityId){
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
					Log.i(TAG, "<loadCityPlaceData> read "+placeList.getListCount()+" place");
				}
				fileInputStream.close();
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<loadCityPlaceData> read local city data but catch exception="+e.toString(), e);
		} 
	}
	/**  
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-29 下午2:59:33  
	*/
	public boolean currentCityHasLocalData()
	{
		// TODO Auto-generated method stub
		String cityId = AppManager.getInstance().getCurrentCityId();		
		return LocalStorageMission.getInstance().hasLocalCityData(cityId);
	}
}
