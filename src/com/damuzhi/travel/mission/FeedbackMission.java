/**  
        * @title FeedbackMission.java  
        * @package com.damuzhi.travel.mission  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-20 上午11:20:46  
        * @version V1.0  
 */
package com.damuzhi.travel.mission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;



public class FeedbackMission
{
	private static final String TAG = "FeedbackMission";

	public boolean submitFeedback(String url)
	{
		Log.i(TAG, "<submitFeedback> submit feedback ,url = "+url);
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
					Log.i(TAG, "<submitFeedback> result "+result);
					while (result != null) {
						sb.append(result);
						result = br.readLine();
					}
					if(sb.length() <= 1){
						return false;
					}
					JSONObject submitData = new JSONObject(sb.toString());
					if (submitData == null || submitData.getInt("result")!= 0){
						return false;
					}else if (submitData.getInt("result") == 0) {
						return true;
					}
					
					inputStream.close();
					br.close();
					inputStream = null;
				} catch (Exception e)
				{					
					Log.e(TAG, "<registerDevice> catch exception = "+e.toString(), e);
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
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
			return false;
		}
		return false;
	}
}
