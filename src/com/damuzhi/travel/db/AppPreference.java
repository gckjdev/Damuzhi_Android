/**  
        * @title AppPreference.java  
        * @package com.damuzhi.travel.db  
        * @description   
        * @author liuxiaokun  
        * @update 2012-11-26 上午11:48:24  
        * @version V1.0  
 */
package com.damuzhi.travel.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-11-26 上午11:48:24  
 */

public class AppPreference
{
	 private static final String APP_DB_NAME= "AppPreference";
	 private static final String TITLE = "update_time";
	    
		public  void saveUpdateTime(Context context,long updateTime)
		{
			Editor downloadData = context.getSharedPreferences(APP_DB_NAME, 0).edit();  
			downloadData.putLong(TITLE, updateTime);  
	        downloadData.commit();  
		}
		
		
		public  long getLastUpdateTime(Context context)
		{
			SharedPreferences downloadData = context.getSharedPreferences(APP_DB_NAME, 0);  
		    long downloadStatus = downloadData.getLong(TITLE,-1);
		    return downloadStatus;
		}
}
