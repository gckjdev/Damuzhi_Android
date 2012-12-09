package com.damuzhi.travel.activity.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.favorite.FavoriteManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.R;
import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
public class TravelApplication extends Application
{
	private static final String TAG = "TravelApplication";
	//private HttpClient httpClient;
	private static HashMap<String, Double> location = new HashMap<String, Double>();	
	private static TravelApplication travelApplication;
	//public LocationClient mLocationClient = null;
	//public MyLocationListenner myListener = new MyLocationListenner();
	public String address = "";
	//public BDLocation bdLocation;
	public Map<String, Integer> downloadStatusMap = new HashMap<String, Integer>();
	private Map<Integer, Integer> favoritePlaceMap = new HashMap<Integer, Integer>();
	public String deviceId;
	public  Map<Integer, Integer> installCityData;
	public  Map<Integer, String> newVersionCityData;
	private String token ="";
	private String loginID = "";
	private boolean cityFlag = false;
	private static TravelApplication instance;
	private AsyncHttpClient asyncHttpClient;
	public static TravelApplication getInstance()
	{
		if(instance == null)
		{
			instance = travelApplication;
		}
		return instance;
	}
	
	
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		travelApplication = this;
		/*mLocationClient = new LocationClient( this );
		mLocationClient.registerLocationListener( myListener );*/
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		//httpClient = createHttpClient();
		asyncHttpClient = new AsyncHttpClient();
		FavoriteManager favoriteManager = new FavoriteManager();
		favoritePlaceMap = favoriteManager.getFavoritePlace();
		
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "damuzhi/cahce");
		int minCacheMemory = 4;
		int maxCacheMemory = 20;
		ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		int runTimeMaxMemory = activityManager.getMemoryClass();
		Double cacheMemorySize = new Double( runTimeMaxMemory*0.15);
		Log.d(TAG, "cahce memory size = "+cacheMemorySize);
		int cacheSize = cacheMemorySize.intValue();
		if(cacheSize<minCacheMemory){
			cacheSize = minCacheMemory;
		}
		if(cacheSize>maxCacheMemory){
			cacheSize = maxCacheMemory;
		}
		Log.d(TAG, "image loader memory cache size = "+cacheSize);
		
		
		// Get singletone instance of ImageLoader
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.default_s)
        .showImageForEmptyUri(R.drawable.default_s)
        .cacheInMemory()
        //.cacheOnDisc()
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .build();
		
		
		// Create configuration for ImageLoader (all options are optional, use only those you really want to customize)
		// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of using ALL options. Most of them have default values.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		           // .memoryCacheExtraOptions(320, 190) // max width, max height
		           // .discCacheExtraOptions(320, 190, CompressFormat.JPEG, 75) // Can slow ImageLoader, use it carefully (Better don't use it)
		           // .threadPoolSize(3)
		           // .threadPriority(Thread.NORM_PRIORITY - 1)
		           // .denyCacheImageMultipleSizesInMemory()
		           // .offOutOfMemoryHandling()
		            .memoryCache(new UsingFreqLimitedMemoryCache(cacheSize * 1024 * 1024)) // You can pass your own memory cache implementation
		           // .discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
		           // .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
		           //.imageDownloader(new URLConnectionImageDownloader(5 * 1000, 20 * 1000)) // connectTimeout (5 s), readTimeout (20 s)
		           // .tasksProcessingOrder(QueueProcessingType.FIFO)
		            .defaultDisplayImageOptions(options)
		            //.enableLogging()
		            .build();
		// Initialize ImageLoader with created configuration. Do it once on Application start.
		imageLoader.init(config);
	}
	
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		shutdownHttpClient();
		//imageLoader.stop();
	}


	@Override
	public void onTerminate()
	{
		super.onTerminate();
		shutdownHttpClient();
	}
	
	/*public HttpClient getHttpClient()
	{
		return httpClient;
	}
*/	
	private HttpClient createHttpClient()
	{
		Log.d(TAG, "createHttpClient()......");
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		return new DefaultHttpClient(conMgr,params);
	}
	
	
	
	private void shutdownHttpClient()
	{
		/*if(httpClient !=null && httpClient.getConnectionManager() !=null)
		{
			httpClient.getConnectionManager().shutdown();
		}*/
	}
	 
 

	
	public HashMap<String, Double> getLocation()
	{
		return location;
	}

	public void setLocation(HashMap<String, Double> location)
	{
		TravelApplication.location = location;
	}
	
	public  boolean checkNetworkConnection()
	{
		//return HttpTool.checkNetworkConnection(getApplicationContext());
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	
	public void noNetworkConnectionToast()
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				 
					 Looper.prepare();
					 Toast.makeText(getApplicationContext(), travelApplication.getString(R.string.conn_fail_exception), Toast.LENGTH_SHORT).show();
					 Looper.loop();
				 				
			}
		});
		thread.start();
		
	}
	
	
	public void downloadFailToast()
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				 
					 Looper.prepare();
					 Toast.makeText(getApplicationContext(), travelApplication.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
					 Looper.loop();
				 			
			}
		});
		thread.start();
		
	}
	
	
	public void notEnoughMemoryToast()
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				
					 Looper.prepare();
					 Toast.makeText(getApplicationContext(), travelApplication.getString(R.string.not_enough_memory), Toast.LENGTH_LONG).show();
					 Looper.loop();
				 				
			}
		});
		thread.start();
		
	}
	
	
	public void getSDcardFailToast()
	{
		Thread thread = new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				
					 Looper.prepare();
					 Toast.makeText(getApplicationContext(), travelApplication.getString(R.string.get_sdcard_fail), Toast.LENGTH_LONG).show();
					 Looper.loop();
				 			
			}
		});
		thread.start();
		
	}
	
	/*baidu location */
	
	
	/*public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			bdLocation = location;
			address = location.getAddrStr();	
			Double latitude = location.getLatitude()-0.0060;
			Double longitude = location.getLongitude()-0.0065;
			initLocation(latitude, longitude);
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}*/
	
	
	
	
	
	public  HashMap<String, Double> initLocation(Double latitude,Double longitude)
	{
		 location.put(ConstantField.LATITUDE, latitude);
		 location.put(ConstantField.LONGITUDE, longitude);
		 return location;
	}
		

	 
	 
	 public String getDeviceId()
	{
		return deviceId;
	}




	public Map<Integer, Integer> getInstallCityData()
	{
		return installCityData;
	}




	public Map<Integer, String> getNewVersionCityData()
	{
		return newVersionCityData;
	}




	public void setInstallCityData(Map<Integer, Integer> installCityData)
	{
		this.installCityData = installCityData;
	}




	public void setNewVersionCityData(Map<Integer, String> newVersionCityData)
	{
		this.newVersionCityData = newVersionCityData;
	}




	public String getToken()
	{
		return token;
	}




	public void setToken(String token)
	{
		this.token = token;
	}




	public String getLoginID()
	{
		return loginID;
	}




	public void setLoginID(String loginID)
	{
		this.loginID = loginID;
	}




	public boolean isCityFlag()
	{
		return cityFlag;
	}




	public void setCityFlag(boolean cityFlag)
	{
		this.cityFlag = cityFlag;
	}




	/**  
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-12-6 下午1:56:50  
	*/
	public AsyncHttpClient getAsyncHttpClient()
	{
		return asyncHttpClient;
	}




	public Map<Integer, Integer> getFavoritePlaceMap()
	{
		return favoritePlaceMap;
	}




	
	 
	
}
