/**  
        * @title UserMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 下午2:43:01  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.google.protobuf.CodedInputStream;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-2 下午2:43:01  
 */

public class CommonMission
{
	private static final String TAG = "UserMission";
	
	private static CommonMission instance = null;
	private UserManager userManager = UserManager.getInstance();
	private CommonMission() {
	}
	
	public static CommonMission getInstance() {
		if (instance == null) {
			instance = new CommonMission();
		}
		return instance;
	}

	
	public void registerDevice(String deviceId,String channelId,Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.USER_ID, 0);
		Editor editor = userSharedPreferences.edit();
		String userId = registerDevice(deviceId,channelId);
		Log.i(TAG, "<register> save userId = "+userId);		
		editor.putString(ConstantField.USER_ID, userId);
		editor.commit();
		
	}
	
	
	public boolean registerMember(String url) {
		boolean result = false;
		result = registerMemberByUrl(url);
		return result;
	}
	
	
	
	
	
	public void postChannelIDandDeviceID(String deviceId,String channelId) {
		postID(deviceId, channelId);
	}
	
	private void postID(String deviceId,String channelId)
	{
		String url = String.format(ConstantField.POST_CHANNEL_ID, deviceId,channelId);
		Log.d(TAG, "<postID> post deviceID,channelID ,url = "+url);
		HttpTool httpTool = HttpTool.getInstance();
		httpTool.sendGetRequest(url);
		httpTool.stopConnection();
	}
	
	
	
	
	private boolean registerMemberByUrl(String url) {
		Log.d(TAG, "<registerMemberByUrl> register member  ,url = "+url);
		InputStream inputStream = null;
		BufferedReader br = null;
		InputStreamReader inputStreamReader = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				inputStreamReader = new InputStreamReader(inputStream);
				br = new BufferedReader(inputStreamReader);
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				Log.i(TAG, "<registerMemberByUrl> result "+result);
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				if(sb.length() <= 1){
					return false;
				}
				JSONObject resultData = new JSONObject(sb.toString());
				int resultCode = resultData.getInt("result");
				Log.d(TAG, "result code = "+resultCode);
				if (resultData!= null&& resultCode == 0){
					return true;
				}else {
					return false;
				}			
			}
			else{
				return false;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<registerDevice> catch exception = "+e.toString(), e);
			return false;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){				
						inputStream.close();
				}
				if (inputStreamReader != null){
						inputStreamReader.close();
				}
				if (br != null){				
						br.close();
				}
			} catch (IOException e1)
			{
			}
		}
	
	}
	
	
	
	
	
	
	private String registerDevice(String deviceId,String channelId)
	{
		String url = String.format(ConstantField.REGISTER,deviceId,channelId);
		Log.d(TAG, "<registerDevice> register device ,url = "+url);
		InputStream inputStream = null;
		BufferedReader br = null;
		String userId = "";
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				br = new BufferedReader(new InputStreamReader(inputStream));
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
				userId = registerData.getString("userId");
				return userId;						
			}
			else{
				return userId;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<registerDevice> catch exception = "+e.toString(), e);
			try
			{
				if (inputStream != null){
					inputStream.close();
				}
				if (br != null){
					br.close();
				}
			} catch (IOException e1)
			{
			}
			return userId;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){
					inputStream.close();
				}
				if (br != null){
					br.close();
				}
			} catch (IOException e1)
			{
			}
			
		}		
	}
	
	
}