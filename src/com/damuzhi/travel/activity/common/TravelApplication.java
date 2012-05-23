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
	private int dataFlag;//0为local，1为HTTP
	private int cityID;//城市ID
	private HashMap<Integer, CityArea> cityAreaMap = new HashMap<Integer, CityArea>();//区域列表
	private HashMap<Integer, List<CityArea>> cityAreaList = new HashMap<Integer, List<CityArea>>();
	private HashMap<String, Integer> cityNameMap = new HashMap<String,Integer>();//城市列表	
	private HashMap<Integer, String> symbolMap = new HashMap<Integer, String>();//货币显示符号
	private HashMap<PlaceCategoryType, PlaceMeta> PlaceMetaMap = new HashMap<PlaceCategoryType, PlaceMeta>();//PLACE分类ID	
	private HashMap<Integer, String> subCatNameMap = new HashMap<Integer, String>();
	private HashMap<PlaceCategoryType, List<NameIdPair>> subCategoryMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();//地点分类下的所有子分类
	private HashMap<PlaceCategoryType, List<NameIdPair>> providedServiceMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();//地点分类下的可用的所有服务选项列表
	//private HashMap<PlaceCategoryType, HashMap<Integer, String>> providedServiceIconMap = new HashMap<PlaceCategoryType, HashMap<Integer,String>>();
	private ArrayList<Place> placeData;//具体类别的PLACE
	private HashMap<String, Double> location = new HashMap<String, Double>();//经纬度信息
	private Place place;//一个具体的PLACE
	private int placeCategoryID;
	private String overviewType;//overview类型
	private CommonTravelTip commonTravelTip;
	
	private static TravelApplication travelApplication;
	
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
		return providedServiceMap;
	}



	/*public HashMap<Integer, CityArea> getCityAreaMap()
	{
		return cityAreaMap;
	}
*/

	public void setSubCategoryMap(
			HashMap<PlaceCategoryType, List<NameIdPair>> subCategoryMap)
	{
		this.subCategoryMap = subCategoryMap;
	}




	public void setProvidedServiceMap(
			HashMap<PlaceCategoryType, List<NameIdPair>> providedServiceMap)
	{
		this.providedServiceMap = providedServiceMap;
	}





	/*public void setCityAreaMap(HashMap<Integer, CityArea> cityAreaMap)
	{
		this.cityAreaMap = cityAreaMap;
	}*/




	public HashMap<String, Integer> getCityNameMap()
	{
		return cityNameMap;
	}




	public void setCityNameMap(HashMap<String, Integer> cityNameMap)
	{
		this.cityNameMap = cityNameMap;
	}




	public HashMap<PlaceCategoryType, PlaceMeta> getPlaceMetaMap()
	{
		return PlaceMetaMap;
	}




	public void setPlaceMetaMap(HashMap<PlaceCategoryType, PlaceMeta> placeMetaMap)
	{
		PlaceMetaMap = placeMetaMap;
	}




	public HashMap<Integer, String> getSymbolMap()
	{
		return symbolMap;
	}




	public void setSymbolMap(HashMap<Integer, String> symbolMap)
	{
		this.symbolMap = symbolMap;
	}




	public HashMap<Integer, List<CityArea>> getCityAreaList()
	{
		return cityAreaList;
	}




	public void setCityAreaList(HashMap<Integer, List<CityArea>> cityAreaList)
	{
		this.cityAreaList = cityAreaList;
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
