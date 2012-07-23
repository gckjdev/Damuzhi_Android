/**  
        * @title DownloadPreference.java  
        * @package com.damuzhi.travel.db  
        * @description   
        * @author liuxiaokun  
        * @update 2012-7-23 下午3:47:44  
        * @version V1.0  
 */
package com.damuzhi.travel.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-23 下午3:47:44  
 */

public class DownloadPreference
{
    private static final String DOWNLOAD_DB_NAME= "downloadInfo";
	/* downloadStatus 0 = upzip ,1= success*/ 
    
	public static void insertDownloadInfo(Context context,String cityId,int downloadStatus)
	{
		Editor downloadData = context.getSharedPreferences(DOWNLOAD_DB_NAME, 0).edit();  
		downloadData.putInt(cityId,downloadStatus);  
        downloadData.commit();  
	}
	
	
	public static void updateDownloadInfo(Context context,String cityId,int downloadStatus)
	{
		Editor downloadData = context.getSharedPreferences(DOWNLOAD_DB_NAME, 0).edit();  
		downloadData.putInt(cityId,downloadStatus);  
		downloadData.commit();  
	}
	
	
	public static int getDownloadInfo(Context context,String cityId)
	{
		SharedPreferences downloadData = context.getSharedPreferences(DOWNLOAD_DB_NAME, 0);  
	    int downloadStatus = downloadData.getInt(cityId, -1);
	    return downloadStatus;
	}
	
	public static void  deleteDownloadInfo(Context context,String cityId)
	{
		Editor downloadData = context.getSharedPreferences(DOWNLOAD_DB_NAME, 0).edit();  
		downloadData.remove(cityId);
		downloadData.commit();
	}
	
	public static HashMap<Integer, Integer> getAllDownloadInfo(Context context)
	{
		SharedPreferences downloadData = context.getSharedPreferences(DOWNLOAD_DB_NAME, 0); 
		HashMap<Integer, Integer> installCityData = new HashMap<Integer, Integer>();
	    HashMap<String, Integer> downloadInfoHashMap = (HashMap<String, Integer>) downloadData.getAll();
	    Iterator iterator = downloadInfoHashMap.entrySet().iterator();
	    while(iterator.hasNext())
	    {
	    	Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
	    	String key = entry.getKey();
    	    Integer val = entry.getValue();
    	    if(val == 1)
    	    {
    	    	installCityData.put(Integer.parseInt(key), val);
    	    }
	    }
	    
	    return installCityData;
	}
	
}
