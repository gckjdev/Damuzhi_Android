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

import com.damuzhi.travel.mission.AppMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.util.LocationUtil;

import android.R.integer;
import android.app.Application;
import android.os.DeadObjectException;
import android.util.Log;

public class TravelApplication extends Application
{
	private static final String TAG = "TravelApplication";
	private DefaultHttpClient defaultHttpClient;
	private int dataFlag;
	private int cityID;
	private HashMap<Integer, CityArea> cityAreaMap = new HashMap<Integer, CityArea>();
	private HashMap<Integer, List<CityArea>> cityAreaList = new HashMap<Integer, List<CityArea>>();
	private HashMap<String, Integer> cityNameMap = new HashMap<String,Integer>();
	private HashMap<Integer, String> symbolMap = new HashMap<Integer, String>();
	private HashMap<PlaceCategoryType, PlaceMeta> PlaceMetaMap = new HashMap<PlaceCategoryType, PlaceMeta>();
	private HashMap<Integer, String> subCatNameMap = new HashMap<Integer, String>();
	private HashMap<PlaceCategoryType, List<NameIdPair>> subCategoryMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();
	private HashMap<PlaceCategoryType, List<NameIdPair>> providedServiceMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();
	//private HashMap<PlaceCategoryType, HashMap<Integer, String>> providedServiceIconMap = new HashMap<PlaceCategoryType, HashMap<Integer,String>>();
	private ArrayList<Place> placeData;
	private HashMap<String, Double> location = new HashMap<String, Double>();
	private Place place;
	private int placeCategoryID;
	private String overviewType;
	private CommonTravelTip commonTravelTip;
	private static TravelApplication travelApplication;
	private int task;
	
	
	
	public static TravelApplication getInstance()
	{
		return travelApplication;
	}
	
	
	
	
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		travelApplication = this;
		defaultHttpClient = createHttpClient();
		AppMission.getInstance().initAppData(this);
		AppMission.getInstance().updateAppData(this);
		
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

	public HashMap<String, Double> getLocation()
	{
		return location;
	}

	public void setLocation(HashMap<String, Double> location)
	{
		this.location = location;
	}



	public HashMap<PlaceCategoryType, List<NameIdPair>> getSubCategoryMap()
	{
		return subCategoryMap;
	}




	public HashMap<PlaceCategoryType, List<NameIdPair>> getProvidedServiceMap()
	{
		return AppManager.getInstance().getProSerMap();
	}



	/*public HashMap<Integer, CityArea> getCityAreaMap()
	{
		return cityAreaMap;
	}
*/







	/*public void setCityAreaMap(HashMap<Integer, CityArea> cityAreaMap)
	{
		this.cityAreaMap = cityAreaMap;
	}*/




	public HashMap<String, Integer> getCityNameMap()
	{
		return AppManager.getInstance().getCityNameMap();
	}





	public HashMap<PlaceCategoryType, PlaceMeta> getPlaceMetaMap()
	{
		return AppManager.getInstance().getPlaceMeta();
	}




	




	public HashMap<Integer, String> getSymbolMap()
	{
		return AppManager.getInstance().getSymbolMap();
	}




	




	public HashMap<Integer, List<CityArea>> getCityAreaList()
	{
		return AppManager.getInstance().getCityAreaList();
	}




	



	public HashMap<Integer, String> getSubCatNameMap()
	{
		return subCatNameMap;
	}




	public void setSubCatNameMap(HashMap<Integer, String> subCatNameMap)
	{
		this.subCatNameMap = subCatNameMap;
	}




	public int getPlaceCategoryID()
	{
		return placeCategoryID;
	}




	public void setPlaceCategoryID(int placeCategoryID)
	{
		this.placeCategoryID = placeCategoryID;
	}




	public String getOverviewType()
	{
		return overviewType;
	}




	public void setOverviewType(String overviewType)
	{
		this.overviewType = overviewType;
	}




	public CommonTravelTip getCommonTravelTip()
	{
		return commonTravelTip;
	}




	public void setCommonTravelTip(CommonTravelTip commonTravelTip)
	{
		this.commonTravelTip = commonTravelTip;
	}




	public int getTask()
	{
		return task;
	}




	public void setTask(int task)
	{
		this.task = task;
	}






	




/*	public HashMap<PlaceCategoryType, HashMap<Integer, String>> getProvidedServiceIconMap()
	{
		return providedServiceIconMap;
	}




	public void setProvidedServiceIconMap(
			HashMap<PlaceCategoryType, HashMap<Integer, String>> providedServiceIconMap)
	{
		this.providedServiceIconMap = providedServiceIconMap;
	}*/




	

	

	
}
