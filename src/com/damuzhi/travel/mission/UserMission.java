/**  
        * @title UserMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 下午2:43:01  
        * @version V1.0  
 */
package com.damuzhi.travel.mission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.damuzhi.travel.model.common.CollectManager;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-2 下午2:43:01  
 */

public class UserMission
{

	
	
	private static final String TAG = "UserMission";
	
	private static UserMission instance = null;
	private UserManager userManager = UserManager.getInstance();
	private UserMission() {
	}
	
	public static UserMission getInstance() {
		if (instance == null) {
			instance = new UserMission();
		}
		return instance;
	}

	
	public void register(String deviceId,Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.USER_ID, 0);
		Editor editor = userSharedPreferences.edit();
		String userId = registerDevice(deviceId);
		Log.i(TAG, "<register> save userId = "+userId);
		
		editor.putString(ConstantField.USER_ID, userId);
		editor.commit();
		
	}
	
	
	private String registerDevice(String deviceId)
	{
		String url = String.format(ConstantField.REGISTER,deviceId);
		Log.i(TAG, "<registerDevice> register device ,url = "+url);
		HttpTool httpTool = new HttpTool();
		InputStream inputStream = null;
		String userId = "";
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
					Log.i(TAG, "<registerDevice> result "+result);
					while (result != null) {
						sb.append(result);
						result = br.readLine();
					}
					if(sb.length() <= 1){
						return userId;
					}
					JSONObject registerData = new JSONObject(sb.toString());
					if (registerData == null || registerData.getInt("result")!= 0){
						return userId;
					}
					
					inputStream.close();
					br.close();
					inputStream = null;
					userId = registerData.getString("userId");
					return userId;
				} catch (Exception e)
				{					
					Log.e(TAG, "<registerDevice> catch exception = "+e.toString(), e);
					return userId;
				}				
			}
			else{
				return userId;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<registerDevice> catch exception = "+e.toString(), e);
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return userId;
		}
	}
}
