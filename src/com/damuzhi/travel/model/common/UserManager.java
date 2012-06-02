/**  
        * @title UserMission.java  
        * @package com.damuzhi.travel.model.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 下午2:42:05  
        * @version V1.0  
 */
package com.damuzhi.travel.model.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.damuzhi.travel.mission.UserMission;
import com.damuzhi.travel.model.constant.ConstantField;



public class UserManager
{
	private static final String TAG = "UserManager";
	private static UserManager instance = null;
	private UserManager() {
	}
	
	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}

	
	public String getUserId(Context context)
	{
		SharedPreferences userInfo = context.getSharedPreferences(ConstantField.USER_ID, 0);
		String userId = userInfo.getString(ConstantField.USER_ID, null);
		Log.i(TAG, "<getUserId> userId = "+userId);
		return userId;
	}
	
	
	
}
