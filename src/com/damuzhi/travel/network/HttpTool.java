package com.damuzhi.travel.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.model.constant.ConstantField;

public class HttpTool
{

	private static final String TAG = "HttpTool";
	
	public InputStream sendGetRequest(String url) throws Exception
	{
		
			 try{
			        HttpURLConnection urlConnection = (HttpURLConnection)new URL(url).openConnection();
					urlConnection.setDoInput(true);
					urlConnection.setUseCaches(true);
					urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
					urlConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
					urlConnection.setRequestProperty("Charset", "UTF-8"); 
			        urlConnection.setConnectTimeout(5000);
			        urlConnection.setRequestMethod("GET");
			        if (urlConnection.getResponseCode() != 200)
			        {
			        	Log.d(TAG, "连接失败");
			        	return null;
			        }			          
			        return urlConnection.getInputStream();
			
			} catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				Log.d(TAG, "连接有异常");
				Log.d(TAG, "url = "+url);
				e.printStackTrace();
				throw new Exception("网络连接失败");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				 throw new Exception("网络连接失败");
			}
	}
}
