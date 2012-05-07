package com.damuzhi.travel.activity.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
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

import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;

import android.R.integer;
import android.app.Application;
import android.os.DeadObjectException;
import android.util.Log;

public class TravelApplication extends Application
{
	private static final String TAG = "TravelApplication";
	private DefaultHttpClient defaultHttpClient;
	private int dataFlag;//0为local，1为HTTP
	private App app;
	private ArrayList<Place> placeData;
	private PlaceList placeList; 
	//城市列表
	private HashMap<String, Integer> cityNameList = new HashMap<String,Integer>();
	//分类ID
	private HashMap<PlaceCategoryType, PlaceMeta> map = new HashMap<PlaceCategoryType, PlaceMeta>();
	//子分类ID
	private HashMap<Integer, NameIdPair> subCategoryMap = new HashMap<Integer, NameIdPair>();
	private List<String> subCategoryList = new ArrayList<String>();
	//地点分类下的可用的所有服务选项列表
	private HashMap<Integer, NameIdPair> providedServiceMap = new HashMap<Integer, NameIdPair>();
	private List<String> providedServiceList = new ArrayList<String>();
	private HashMap<String, Double> location = new HashMap<String, Double>();
	private HashMap<Integer, City> cityMap = new HashMap<Integer, City>();
	private HashMap<Integer, CityArea> cityAreaMap = new HashMap<Integer, CityArea>();
	private int cityID;
	private Place place;

	
	
	
	
	
	
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		defaultHttpClient = createHttpClient();
	}
	
	@Override
	public void onLowMemory()
	{
		// TODO Auto-generated method stub
		super.onLowMemory();
		shutdownHttpClient();
	}


	@Override
	public void onTerminate()
	{
		// TODO Auto-generated method stub
		super.onTerminate();
		shutdownHttpClient();
	}
	
	private DefaultHttpClient createHttpClient()
	{
		Log.d(TAG, "createHttpClient().....");
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schreg.register(new Scheme("https",SSLSocketFactory.getSocketFactory(),443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schreg);
		return new DefaultHttpClient(manager, params);
		
	}
	
	public DefaultHttpClient getHttpClient()
	{
		return defaultHttpClient;
	}
	
	private void shutdownHttpClient()
	{
		if(defaultHttpClient !=null && defaultHttpClient.getConnectionManager() !=null)
		{
			defaultHttpClient.getConnectionManager().shutdown();
		}
	}
	
	public App getApp()
	{
		return app;
	}
	
	public void setApp(App app)
	{
		this.app = app;
	}
	
	public HashMap<String, Integer> getCityList()
	{
		if(app != null)
		{
			for (City city : app.getCitiesList())
			{
				cityNameList.put(city.getCityName(), city.getCityId());
			}
		}
		return cityNameList;
	}
	
	
	public HashMap<PlaceCategoryType, PlaceMeta> getPlaceMeta()
	{
		if(app != null)
		{
			for(PlaceMeta placeMeta :app.getPlaceMetaDataListList())
			{
				map.put(placeMeta.getCategoryId(), placeMeta);
			}
		}
		
		return map;
	}
	
	public HashMap<Integer, City> getCity()
	{
		
		for(City city : app.getCitiesList())
		{
			cityMap.put(city.getCityId(), city);
		}
		return cityMap;
	}
	
	//地点分类下的所有子分类
	public HashMap<Integer, NameIdPair> getSubCatMap()
	{
		if(app != null)
		{
			for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
			{
				for(NameIdPair nameIdPair : placeMeta.getSubCategoryListList())
				{
					subCategoryMap.put(nameIdPair.getId(), nameIdPair);
				}
			}
		}
		return subCategoryMap;
	}
	
	//地点分类下的所有子分类
		public List<String> getSubCatList()
		{
			if(app != null)
			{
				for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
				{
					for(NameIdPair nameIdPair : placeMeta.getSubCategoryListList())
					{
						subCategoryList.add(nameIdPair.getName());
					}
				}
			}
			return subCategoryList;
		}
	
	//地点分类下的可用的所有服务选项列表
	public HashMap<Integer, NameIdPair> getProSerMap()
	{
		if(app != null)
		{
			for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
			{
				for(NameIdPair nameIdPair : placeMeta.getProvidedServiceListList())
				{
					providedServiceMap.put(nameIdPair.getId(), nameIdPair);
				}
			}
		}
		return subCategoryMap;
	}
	
	
	//地点分类下的可用的所有服务选项列表
		public List<String> getProSerList()
		{
			if(app != null)
			{
				for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
				{
					for(NameIdPair nameIdPair : placeMeta.getProvidedServiceListList())
					{
						providedServiceList.add(nameIdPair.getName());
					}
				}
			}
			return providedServiceList;
		}
	
	
	public HashMap<Integer, CityArea> getCityAreaMap()
	{
		for(City city:app.getCitiesList())
		{
			for(CityArea cityArea :city.getAreaListList())
			{
				cityAreaMap.put(cityArea.getAreaId(), cityArea);
			}
			
		}
		return cityAreaMap;
	}

	public ArrayList<int[][]> getLocations()
	{
		ArrayList<int[][]> locations = new ArrayList<int[][]>();
		for(Place place:placeData)
		{
			int latitude = (int)(place.getLatitude()*1E6);
			int longitude = (int)(place.getLongitude()*1E6);
			int[][] location = new int[][]{{latitude,longitude}};
			locations.add(location);;
		}
		return locations;
	}
	
	
	

	public HashMap<String, Double> getLocation()
	{
		return location;
	}

	public void setLocation(HashMap<String, Double> location)
	{
		this.location = location;
	}

	public Place getPlace()
	{
		return place;
	}

	public void setPlace(Place place)
	{
		this.place = place;
	}

	public int getCityID()
	{
		return cityID;
	}

	public void setCityID(int cityID)
	{
		this.cityID = cityID;
	}

	public ArrayList<Place> getPlaceData()
	{
		return placeData;
	}

	public void setPlaceData(ArrayList<Place> placeData)
	{
		this.placeData = placeData;
	}

	public int getDataFlag()
	{
		return dataFlag;
	}

	public void setDataFlag(int dataFlag)
	{
		this.dataFlag = dataFlag;
	}


	

	
}
