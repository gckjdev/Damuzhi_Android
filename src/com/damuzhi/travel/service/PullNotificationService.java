/**  
 * @title PullNotificationService.java  
 * @package com.damuzhi.travel.service  
 * @description   
 * @author liuxiaokun  
 * @update 2012-10-22 下午1:32:44  
 * @version V1.0  
 */
package com.damuzhi.travel.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.entry.WelcomeActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.util.TravelUtil;

/**
 * @description
 * @version 1.0
 * @author liuxiaokun
 * @update 2012-10-22 下午1:32:44
 */

public class PullNotificationService extends Service
{
	public static final String TAG = "PullNotificationService";
	public static final String ACTION_START = "action_start";
	public static final String ACTION_STOP = "action_stop";
	public static boolean isAvailable = true;
	public static final int TYPE_DEFAULT = 0;
	private Notification mNotification;
	private NotificationManager mManager;
	private String url ="";
	private JSONObject jsonObject;
	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate()
	{
		String deviceId = TravelApplication.getInstance().getDeviceId();
		url = String.format(ConstantField.ANDROID_NOTIFY_URL, deviceId);
		initNotifiManager();
	}

	private void initNotifiManager()
	{
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.dmzlogo;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = getResources().getString(R.string.app_name);
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		// 点击后自动消失
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	private void launchNotification(String title,String content)
	{
		mNotification.when = System.currentTimeMillis();
		Intent i = new Intent();
		
			i.setClass(this, MainActivity.class);
		
		// i = new Intent(this, WelcomeActivity.class);
		//Intent i = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();
		if(jsonObject != null)
		{
			try
			{
				bundle.putString("title", jsonObject.getString("Title"));
				bundle.putString("content", jsonObject.getString("Content"));
				bundle.putString("type", jsonObject.getString("Type"));
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		i.putExtra("notify", bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,Intent.FLAG_ACTIVITY_CLEAR_TOP);		
		mNotification.setLatestEventInfo(this,title, content, pendingIntent); 
		mManager.notify(TYPE_DEFAULT, mNotification);
	}

	

	@Override
	public void onStart(Intent intent, int startId)
	{
	}

	
	static ExecutorService unzipExecutorService = Executors.newFixedThreadPool(1);
	
	private void pullNotofication()
	{
		unzipExecutorService.execute(new Runnable()
		{
			
			@Override
			public void run()
			{
				JSONObject[] jsonObjects = checkNotificationFromServer(url);
				Log.d(TAG, "json object array length = "+jsonObjects.length);
					if (jsonObjects == null||jsonObjects.length==0)
					{
						return;
					}
					try
					{
						for(JSONObject object:jsonObjects)
						{
							if(object.has("Title"))
							{
								Log.d(TAG, "launch Notification ....");
								jsonObject = object;
								launchNotification(object.get("Title").toString(),object.get("Content").toString());
							}
							
						}
					} catch (Exception e)
					{
						Log.d(TAG, "<pullNotofication> but catch exception = "+e.toString());
					}
					
				
				
			}
		});
	}
	
	

	public JSONObject[] checkNotificationFromServer(String url)
	{
		Log.d(TAG, "<checkNotificationFromServer>   ,url = "+url);
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
				Log.d(TAG, "<checkNotificationFromServer> result "+result);
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				if(sb.length() <= 2){
					return null;
				}
				//JSONObject resultData = new JSONObject(sb.toString());
				JSONObject[] resultData = TravelUtil.getJsonArray(sb.toString());
				return resultData;		
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<checkNotificationFromServer> catch exception = "+e.toString(), e);
			return null;
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		pullNotofication();
		Log.d(TAG, "pull notify info ......");
		long now = System.currentTimeMillis();
		//long updateMilis = 24*60 * 1000+now;
		//long updateMilis = 24*60*60*1000+now;
		long updateMilis = 24*60*60*1000+now;
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, updateMilis, pendingIntent);
		stopSelf();
		return START_STICKY;
	}
	
	
	

}
