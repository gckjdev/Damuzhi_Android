/**  
        * @title MoreMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-20 下午4:52:55  
        * @version V1.0  
 */
package com.damuzhi.travel.mission.more;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-20 下午4:52:55  
 */

public class MoreMission
{
	private static final String TAG = "MoreMission";
	private static MoreMission instance = null;
	
	private MoreMission() {
	}
	
	public static MoreMission getInstance() {
		if (instance == null) {
			instance = new MoreMission();
		}
		return instance;
	}

	
	public boolean isShowListImage()
	{		
		SharedPreferences showListImage = TravelApplication.getInstance().getSharedPreferences(ConstantField.SHOW_LIST_IMAGE, 0);
		boolean isShow = showListImage.getBoolean(ConstantField.SHOW_LIST_IMAGE, true);
		return isShow;
		
	}
	
	
	public void saveIsShowImage(boolean isShow,Context context)
	{
		SharedPreferences userSharedPreferences = context.getSharedPreferences(ConstantField.SHOW_LIST_IMAGE, 0);
		Editor editor = userSharedPreferences.edit();		
		editor.putBoolean(ConstantField.SHOW_LIST_IMAGE, isShow);
		editor.commit();
		
	}
	
	public float getNewVersion()
	{
		float version = 0f;
		String url = ConstantField.ANDROID_VERSION;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader br = null;
		try
		{
			inputStream = HttpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				
					inputStreamReader = new InputStreamReader(inputStream);
					br = new BufferedReader(inputStreamReader);
					StringBuffer sb = new StringBuffer();
					String result = br.readLine();
					Log.i(TAG, "<getNewVersion> result "+result);
					while (result != null) {
						sb.append(result);
						result = br.readLine();
					}
					if(sb.length() <= 1){
						return version;
					}
					JSONObject versionData = new JSONObject(sb.toString());
					inputStream.close();
					inputStream = null;	
					br.close();
					if (versionData != null ){
						version = Float.parseFloat(versionData.getString("app_version"));
						return version;
					}					
			}		
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getNewVersion> catch exception = "+e.toString(), e);
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
			return version;
		}finally
		{
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
		return version;
	}
}
