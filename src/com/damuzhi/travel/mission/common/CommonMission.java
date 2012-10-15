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
import java.util.Collections;

import org.json.JSONObject;

import android.R.bool;
import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PackageProtos.UserInfo;
import com.google.protobuf.CodedInputStream;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-2 下午2:43:01  
 */

public class CommonMission
{
	private static final String TAG = "CommonMission";
	private String resultInfo = "";
	private String token = "";
	private String userId = "";
	
	private int type =0;//0=common,1=memberLogin,2=registerDevice
	
	private static CommonMission instance = null;
	//private UserManager userManager = UserManager.getInstance();
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
		String url = String.format(ConstantField.REGISTER,deviceId,channelId);
		Log.d(TAG, "<registerDevice> url = "+url);
		boolean result = getDataByURL(url);
		if(result)
		{
			Log.d(TAG, "<register> save userId = "+userId);		
			editor.putString(ConstantField.USER_ID, userId);
			editor.commit();
		}
		
		
	}
	
	
	public boolean registerMember(String url) {
		boolean result = false;
		//result = registerMemberByUrl(url);
		result = getDataByURL(url);
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
	
	
	
	

	
	public boolean memberLogin(String userName,String password)
	{
		String url = String.format(ConstantField.MEMBER_LOGIN_URL, userName,password);
		Log.d(TAG, "<memberLogin> url = "+url);
		//boolean result = memberLoginByUrl(url);
		boolean result = getDataByURL(url);
		return result;
	}
	


	
	public boolean getVerification(String loginId, String telephone)
	{
		String url = String.format(ConstantField.GET_MEMBER_VERIFICATION_CODE, loginId,telephone);
		Log.d(TAG, "<getVerification> url = "+url);
		//boolean result = getVerification(url);
		boolean result = getDataByURL(url);
		return result;
		
	}
	

	
	public boolean verificationCode(String phoneNum, String verificationCode)
	{
		String url = String.format(ConstantField.VERIFICATION_CODE,phoneNum,verificationCode);
		Log.d(TAG, "<verificationCode> url = "+url);
		//boolean result = verificationCode(url);
		boolean result = getDataByURL(url);
		return result;
	}
	
	
	
	public boolean findPassword(String telephone)
	{
		String url = String.format(ConstantField.FIND_PASSWORD,telephone);
		Log.d(TAG, "<findPassword> url = "+url);
		//boolean result = verificationCode(url);
		boolean result = getDataByURL(url);
		return result;
	}
	
	
	
	
	
	
	private boolean getDataByURL(String url)
	{
		Log.d(TAG, "<getDataByURL>   ,url = "+url);
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
				Log.d(TAG, "<getDataByURL> result "+result);
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
				if(resultData.has("resultInfo"))
				{
					resultInfo = resultData.getString("resultInfo");
				}
				if (resultData!= null&& resultCode == 0){
					/*if(type == 1)
					{
						token = resultData.getString("token");
						return true;
					}
					
					if(type == 2)
					{
						userId = resultData.getString("userId");
						return true;
					}*/
					if(resultData.has("token"))
					{
						token = resultData.getString("token");
					}
					if(resultData.has("userId"))
					{
						userId = resultData.getString("userId");
					}
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
			Log.e(TAG, "<getDataByURL> catch exception = "+e.toString(), e);
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

	public String getResultInfo()
	{
		return resultInfo;
	}

	public void setResultInfo(String resultInfo)
	{
		this.resultInfo = resultInfo;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	
	public UserInfo getUserInfo(String loginId, String token)
	{
		// TODO Auto-generated method stub
		String url = String.format(ConstantField.GET_USER_INFO_URL, loginId,token);
		Log.d(TAG, "<getUserInfo> url = "+url);
		return getUserInfoByURL(url);
	}

	
	private UserInfo getUserInfoByURL(String url)
	{
		Log.i(TAG, "<getUserInfoByURL> load place data from http ,url = "+url);
		HttpTool httpTool = HttpTool.getInstance();
		InputStream inputStream = null;
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				try
				{
					TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
					if (travelResponse == null || travelResponse.getResultCode() != 0 ||
							travelResponse.getPlaceList() == null){
						return null;
					}
					
					inputStream.close();
					inputStream = null;					
					return travelResponse.getUserInfo();
				} catch (Exception e)
				{					
					Log.e(TAG, "<getPlaceListByUrl> catch exception = "+e.toString(), e);
					return null;
				}				
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getPlaceListByUrl> catch exception = "+e.toString(), e);
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return null;
		}
		
	}

	/**  
	        * @param oldPassword
	        * @param newPasswrod
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-10-9 下午5:58:09  
	*/
	public boolean changePassword(String loginId,String token,String oldPassword, String newPasswrod)
	{
		String url = String.format(ConstantField.CHANGE_PASSWORD_URL,loginId,token,oldPassword,newPasswrod);
		Log.d(TAG, "<changePassword> url = "+url);
		boolean result = getDataByURL(url);
		return result;
	}

	/**  
	        * @param loginId
	        * @param token2
	        * @param nickName
	        * @param name
	        * @param email
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-10-12 上午10:12:18  
	*/
	public boolean changeUserInfo(String loginId, String token2,String nickName, String name,String telephone, String email)
	{
		String url = String.format(ConstantField.CHANGE_USER_INFO_URL,loginId,token,name,nickName,"",telephone,email,"");
		Log.d(TAG, "<changeUserInfo> url = "+url);
		boolean result = getDataByURL(url);
		return result;
	}
	
	
}
