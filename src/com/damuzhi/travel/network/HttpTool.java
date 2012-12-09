package com.damuzhi.travel.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.common.HelpMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class HttpTool
{

	private static final String TAG = "HttpTool";
	private  HttpURLConnection urlConnection = null;
	private HttpClient httpClient;
	private static HttpTool instance = null;
	
	private HttpTool() {
	}
	public static HttpTool getInstance() {		
			instance = new HttpTool();		
		return instance;
	}
	
	
	
	
	public  InputStream sendGetRequest(String url) 
	{
		int retry = 5;
		int count = 0;
		while (count<5)
		{		
			try
			{
				//return executeHttpClient(url);
				
				return httpGetRequerst(url);
				//return getInputStreamByAsycnClient(url);
			} catch (Exception e)
			{
				count += 1;
				if(count <retry)
				{
					Log.d(TAG, "sendGetRequest retry times = "+count);
					continue;
				}else {
					httpClient.getConnectionManager().closeExpiredConnections();
					TravelApplication.getInstance().downloadFailToast();
					Log.d(TAG, "colud not success with retry...");
				}
			}
			
		}
		return null;
	}
	
	private InputStream executeHttpClient(String url) throws Exception
	{
		BufferedInputStream bufferedInputStream = null;
		//httpClient = TravelApplication.getInstance().getHttpClient();
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(URI.create(url));
		HttpResponse response = httpClient.execute(httpGet);
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			bufferedInputStream = new BufferedInputStream(response.getEntity().getContent());	
		}else
		{
			TravelApplication.getInstance().downloadFailToast();
			httpClient.getConnectionManager().closeExpiredConnections();
		}		 
		return bufferedInputStream;
	}
	
	
	public  InputStream httpGetRequerst(String url) 
	{
		 boolean connEnable = TravelApplication.getInstance().checkNetworkConnection();
		 if(connEnable)
		 {
			 try{
				 	
				 	URL url2 = new URL(url);
				 	urlConnection = (HttpURLConnection)url2.openConnection();
					urlConnection.setDoInput(true);
					urlConnection.setUseCaches(true);
					urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
					urlConnection.setRequestProperty("Connection", "Keep-Alive");
					urlConnection.setRequestProperty("Charset", "UTF-8"); 
					urlConnection.setRequestProperty("Accept-Encoding", "identity");
			        urlConnection.setConnectTimeout(5000);
			        urlConnection.setRequestMethod("GET");
			        if(urlConnection !=null&&urlConnection.getDoInput())
			        {		          
			        	InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				        return inputStream;
			        }else {
						return null;
					}	
			} catch (Exception e)
			{	
				TravelApplication.getInstance().downloadFailToast();
				Log.e(TAG, "<sendGetRequest> but catch exception = "+e.toString(),e);
				urlConnection.disconnect();
				return null;
			}
		 }else {
			 TravelApplication.getInstance().noNetworkConnectionToast();
			 return null;
		}
		 
	}
	
	/*InputStream inputStream = null;
	public InputStream getInputStreamByAsycnClient(String url)
	{
		
		AsyncHttpClient client = TravelApplication.getInstance().getAsyncHttpClient();
		client.get(url, new HttpInputStreamHandel(){

			@Override
			public void onSuccess(byte[] binaryData)
			{
				// TODO Auto-generated method stub
				super.onSuccess(binaryData);
			}

			@Override
			public void onFailure(Throwable error, byte[] binaryData)
			{
				// TODO Auto-generated method stub
				super.onFailure(error, binaryData);
				inputStream = null;
				throw new RuntimeException();
			}

			@Override
			protected void inputStreamReceived(InputStream arg0)
			{
				inputStream = arg0;
				if(arg0 == null){
					throw new RuntimeException();
				}
				super.inputStreamReceived(arg0);
			}
			
		});
		return inputStream;
	}*/
	
	
	
	public void stopConnection()
	{
		if(urlConnection != null)
		{
			urlConnection.disconnect();
		}
		if(httpClient != null)
		{
			httpClient.getConnectionManager().closeExpiredConnections();
		}
	}
	
	
	public void closeConnection()
	{
		
		if(httpClient != null)
		{
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	


}
