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
	
	// TODO move all http handling here
	
	public InputStream sendGetRequest(String url) throws Exception
	{
			 HttpURLConnection urlConnection = null;
			 try{
				 	urlConnection = (HttpURLConnection)new URL(url).openConnection();
					urlConnection.setDoInput(true);
					urlConnection.setUseCaches(true);
					urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
					urlConnection.setRequestProperty("Connection", "Keep-Alive");// 
					urlConnection.setRequestProperty("Charset", "UTF-8"); 
			        urlConnection.setConnectTimeout(5000);
			        urlConnection.setRequestMethod("GET");
			        if (urlConnection.getResponseCode() != 200)
			        {
			        	Log.d(TAG, "<sendGetRequest> can not get http connection");
			        	return null;
			        }			          
			        return urlConnection.getInputStream();
			
			} catch (Exception e)
			{			
				Log.e(TAG, "<sendGetRequest> but catch exception = "+e.toString(),e);
				return null;
			}
	}
}
