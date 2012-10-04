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
import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;



public class UserManager
{
	private static final String TAG = "UserManager";
	private static UserManager instance = null;
	private static final String USER_INFO_DB = "user_info";
	private static final String USER_NAME = "user_name";
	private static final String PASSWORD = "password";
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
		String userId = userInfo.getString(ConstantField.USER_ID, "");
		Log.i(TAG, "<getUserId> userId = "+userId);
		return userId;
	}
	
	public void saveUserName(Context context,String userName)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(USER_INFO_DB, 0);
		Editor editor = userSharedPreferences.edit();
		Log.i(TAG, "<saveUserName> save userName = "+userName);		
		editor.putString(USER_NAME, userName);
		editor.commit();
	}
	
	
	public void savePassword(Context context,String password)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(USER_INFO_DB, 0);
		Editor editor = userSharedPreferences.edit();
		Log.i(TAG, "<saveUserName> save password = "+password);		
		editor.putString(PASSWORD, password);
		editor.commit();
	}
	
	
	
	public String getUserName(Context context)
	{
		SharedPreferences userInfo = context.getSharedPreferences(USER_INFO_DB, 0);
		String userName = userInfo.getString(USER_NAME, "");
		Log.i(TAG, "<getUserName> userName = "+userName);
		return userName;
	}
	
	
	public String getPassword(Context context)
	{
		SharedPreferences userInfo = context.getSharedPreferences(USER_INFO_DB, 0);
		String password = userInfo.getString(PASSWORD, "");
		Log.i(TAG, "<getPassword> password = "+password);
		return password;
	}
}
