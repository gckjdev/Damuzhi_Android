package com.damuzhi.travel.model.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.Loader;
import android.util.Log;

import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.Agency;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.CityLocationInfo;
import com.damuzhi.travel.protos.AppProtos.HelpInfo;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.AppProtos.RecommendedApp;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.PinyinComparator;

public class AppManager
{
	private Context context;
	private static final String TAG = "AppManager";
	private static App app;
	private static AppManager instance = null;
	private int currentCityId;
	private String currentCityName;
	private AppManager()
	{
		load();
	}

	public static AppManager getInstance()
	{
		if (instance == null)
		{
			instance = new AppManager();
		}
		return instance;
	}

	public void load()
	{
		String dataPath = ConstantField.LOCAL_APP_DATA_FILE;
		if (!FileUtil.checkFileIsExits(dataPath))
		{
			Log.e(TAG, "load app data from file = " + dataPath+ " but file not found");
			return;
		}

		File appData = new File(dataPath);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(appData);
			Log.i(TAG, "load app data from file = " + dataPath);
			app = App.parseFrom(inputStream);
		} catch (Exception e)
		{
			Log.e(TAG, "load app data from file = " + dataPath+ " but catch exception = " + e.toString(), e);
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
		// TODO current city Id should be persistented
		Log.i(TAG, "current city id is "+currentCityId);
	}

	
	
	
	public void load(Context context)
	{
		this.context = context;
		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = context.openFileInput(ConstantField.APP_FILE);
		if (fileInputStream == null)
		{
			Log.e(TAG, "load app data from file = " + ConstantField.LOCAL_APP_DATA_PATH+ " but file not found");
			return;
		}
		
			app = App.parseFrom(fileInputStream);
		} catch (Exception e)
		{
			Log.e(TAG, "load app data from file = " + ConstantField.LOCAL_APP_DATA_PATH+ " but catch exception = " + e.toString(), e);
		}
		finally
		{
			try
			{
				fileInputStream.close();
			} catch (Exception e)
			{
			}
		}
		// TODO current city Id should be persistented
		Log.i(TAG, "current city id is "+currentCityId);
	}
	
	public void load(InputStream inputStream)
	{
		try
		{
		if (inputStream == null)
		{
			Log.e(TAG, "load app data from assest  but file not found");
			return;
		}
		
			app = App.parseFrom(inputStream);
		} catch (Exception e)
		{
			Log.e(TAG, "load app data from assest  but catch exception = " + e.toString(), e);
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
		// TODO current city Id should be persistented
		Log.i(TAG, "current city id is "+currentCityId);
	}
	
	
	
	/*public void reloadData()
	{
		boolean sdcardEnable = FileUtil.sdcardEnable();
		if(sdcardEnable)
		{
			load();
		}else
		{
			load(context);
		}
		
	}*/
	
	
	public void reloadData(Context context)
	{
		load(context);
	}

	public HashMap<Integer, City> getCityMap()
	{
		HashMap<Integer, City> cityMap = null;
		if (app != null)
		{
			cityMap = new HashMap<Integer, City>();
			for (City city : app.getCitiesList())
			{
				cityMap.put(city.getCityId(), city);
			}
			for (City city : app.getTestCitiesList())
			{
				cityMap.put(city.getCityId(), city);
			}
		}
		return cityMap;
	}

	public HashMap<Integer, List<CityArea>> getCityAreaList()
	{
		HashMap<Integer, List<CityArea>> cityAreaList = null;
		if (app != null)
		{
			cityAreaList = new HashMap<Integer, List<CityArea>>();
			for (City city : app.getCitiesList())
			{
				cityAreaList.put(city.getCityId(), city.getAreaListList());
			}
		}
		return cityAreaList;
	}

	public HashMap<String, Integer> getCityNameMap()
	{
		HashMap<String, Integer> cityNameMap = null;
		if (app != null)
		{
			cityNameMap = new HashMap<String, Integer>();
			for (City city : app.getCitiesList())
			{
				cityNameMap.put(city.getCityName(), city.getCityId());
			}
			for (City city : app.getTestCitiesList())
			{
				cityNameMap.put(city.getCityName(), city.getCityId());
			}
		}
		return cityNameMap;
	}

	public HashMap<PlaceCategoryType, PlaceMeta> getPlaceMeta()
	{
		HashMap<PlaceCategoryType, PlaceMeta> map = null;
		if (app != null)
		{
			map = new HashMap<PlaceCategoryType, PlaceMeta>();
			for (PlaceMeta placeMeta : app.getPlaceMetaDataListList())
			{
				map.put(placeMeta.getCategoryId(), placeMeta);
			}
		}

		return map;
	}

	public HashMap<Integer, City> getCity()
	{
		HashMap<Integer, City> cityMap = null;
		if (app != null)
		{
			cityMap = new HashMap<Integer, City>();
			for (City city : app.getCitiesList())
			{
				cityMap.put(city.getCityId(), city);
			}
			for (City city : app.getTestCitiesList())
			{
				cityMap.put(city.getCityId(), city);
			}
		}

		return cityMap;
	}

	public HashMap<PlaceCategoryType, List<NameIdPair>> getSubCatMap()
	{
		HashMap<PlaceCategoryType, List<NameIdPair>> subCategoryMap = null;
		if (app != null)
		{
			subCategoryMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();
			for (PlaceMeta placeMeta : app.getPlaceMetaDataListList())
			{
				subCategoryMap.put(placeMeta.getCategoryId(),placeMeta.getSubCategoryListList());
			}
		}
		return subCategoryMap;
	}

	public HashMap<PlaceCategoryType, List<NameIdPair>> getProSerMap()
	{
		HashMap<PlaceCategoryType, List<NameIdPair>> providedServiceMap = null;
		if (app != null)
		{
			providedServiceMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();
			for (PlaceMeta placeMeta : app.getPlaceMetaDataListList())
			{
				providedServiceMap.put(placeMeta.getCategoryId(),placeMeta.getProvidedServiceListList());

			}
		}
		return providedServiceMap;
	}

	public HashMap<PlaceCategoryType, HashMap<Integer, String>> getProSerIconMap()
	{
		HashMap<PlaceCategoryType, HashMap<Integer, String>> providedServiceIconMap = null;
		if (app != null)
		{
			providedServiceIconMap = new HashMap<PlaceCategoryType, HashMap<Integer, String>>();
			for (PlaceMeta placeMeta : app.getPlaceMetaDataListList())
			{
				HashMap<Integer, String> map = new HashMap<Integer, String>();
				if(placeMeta!=null)
				{
					for (NameIdPair nameIdPair : placeMeta.getProvidedServiceListList())
					{
						map.put(nameIdPair.getId(), nameIdPair.getImage());
					}
					providedServiceIconMap.put(placeMeta.getCategoryId(), map);
				}				
				
			}
		}
		return providedServiceIconMap;
	}

	public HashMap<Integer, String> getSymbolMap()
	{
		HashMap<Integer, String> symbolMap = null;
		if (app != null)
		{
			symbolMap = new HashMap<Integer, String>();
			for (City city : app.getCitiesList())
			{
				symbolMap.put(city.getCityId(), city.getCurrencySymbol());
			}
		}
		return symbolMap;
	}

	public HashMap<Integer, String> getPlaceSubCatMap(int placeCategoryType)
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = AppManager.getInstance().getSubCatMap();
		if(subCatMap!=null && subCatMap.size()>0)
		{
			List<NameIdPair> nameIdPairs = subCatMap.get(PlaceCategoryType.valueOf(placeCategoryType));
			if(nameIdPairs!=null &&nameIdPairs.size()>0)
			{
				for (NameIdPair nameIdPair : nameIdPairs)
				{
					map.put(nameIdPair.getId(), nameIdPair.getName());
				}
			}
			
		}
		
		return map;
	}

	public HashMap<Integer, String> getAllSubCatMap()
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = AppManager.getInstance().getSubCatMap();
		Set<PlaceCategoryType> keyset = subCatMap.keySet();
		if(subCatMap!=null && subCatMap.size()>0)
		{
			for (PlaceCategoryType placeCategoryType : keyset)
			{
				List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
				if(nameIdPairs!=null &&nameIdPairs.size()>0)
				{
					for (NameIdPair nameIdPair : nameIdPairs)
					{
						map.put(nameIdPair.getId(), nameIdPair.getName());
					}
				}
				
			}
		}
		

		return map;
	}

	
	
	public String[] getSubCatNameList(PlaceCategoryType placeCategoryType  )
	{
		int i = 0;
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = AppManager.getInstance().getSubCatMap();
		String[] subCat = null;
		if(subCatMap!=null && subCatMap.size()>0)
		{
			List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
			if(nameIdPairs!=null && nameIdPairs.size()>0)
			{
				subCat = new String[nameIdPairs.size()];
				for (NameIdPair nameIdPair : nameIdPairs)
				{
					subCat[i] = nameIdPair.getName();
					i++;
				}				
			}
			
		}
		
		return subCat;
	}

	public int[] getSubCatKeyList(PlaceCategoryType placeCategoryType)
	{
		int i = 0;
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = AppManager.getInstance().getSubCatMap();
		int[] subCatKey =null;
		if(subCatMap!=null && subCatMap.size()>0)
		{
			List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
			if(nameIdPairs!=null && nameIdPairs.size()>0)
			{
				subCatKey = new int[nameIdPairs.size()];
				for (NameIdPair nameIdPair : nameIdPairs)
				{
					subCatKey[i] = nameIdPair.getId();
					i++;
				}
			}
		    
		}
		
		return subCatKey;
	}

	
	
	public  String[] getProvidedServiceNameList(
			PlaceCategoryType placeCategoryType)
	{
		int i = 0;
		HashMap<PlaceCategoryType, List<NameIdPair>> proSerMap = AppManager.getInstance().getProSerMap();
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		String[] proServiceName = new String[nameIdPairs.size()];
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			proServiceName[i] = nameIdPair.getName();
			i++;
		}
		return proServiceName;
	}

	public int[] getProvidedServiceKeyList(PlaceCategoryType placeCategoryType)
	{
		int i = 0;
		HashMap<PlaceCategoryType, List<NameIdPair>> proSerMap = AppManager.getInstance().getProSerMap();
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		int[] proServiceKey = new int[nameIdPairs.size()];
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			proServiceKey[i] = nameIdPair.getId();
			i++;
		}
		return proServiceKey;
	}

	public HashMap<Integer, String> getProServiceMap(PlaceCategoryType placeCategoryType)
	{
		HashMap<PlaceCategoryType, List<NameIdPair>> proSerMap = AppManager.getInstance().getProSerMap();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			map.put(nameIdPair.getId(), nameIdPair.getName());
		}
		return map;
	}

	

	
	public  String[] getCityAreaNameList(int cityID)
	{
		int i = 0;
		HashMap<Integer, List<CityArea>> cityAreaList = AppManager.getInstance().getCityAreaList();
		if(cityAreaList.containsKey(cityID)){
			List<CityArea> cityAreas = cityAreaList.get(cityID);
			String[] ctiyAreasName = new String[cityAreas.size()];
			if(cityAreas!=null&&cityAreas.size()>0)
			{
				for (CityArea cityArea : cityAreas)
				{
					ctiyAreasName[i] = cityArea.getAreaName();
					i++;
				}
			}		
			return ctiyAreasName;
		}
		return null;
	}

	public int[] getCityAreaKeyList(int cityID)
	{
		int i = 0;
		HashMap<Integer, List<CityArea>> cityAreaList = AppManager.getInstance().getCityAreaList();
		if(cityAreaList.containsKey(cityID)){
			List<CityArea> cityAreas = cityAreaList.get(cityID);
			int[] ctiyAreasKey = new int[cityAreas.size()];
			if(cityAreas!=null&&cityAreas.size()>0)
			{
				for (CityArea cityArea : cityAreas)
				{
					ctiyAreasKey[i] = cityArea.getAreaId();
					i++;
				}
			}	
			return ctiyAreasKey;
		}
		return null;
	}
	
	public HashMap<Integer, String> getCityAreaMap(int cityID)
	{
		HashMap<Integer, List<CityArea>> cityAreaList = AppManager.getInstance().getCityAreaList();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		if(cityAreaList.containsKey(cityID))
		{
			List<CityArea> ctiyAreas = cityAreaList.get(cityID);
			if(cityAreaList!=null &&ctiyAreas.size()>0)
			{
				for (CityArea cityArea : ctiyAreas)
				{
					map.put(cityArea.getAreaId(), cityArea.getAreaName());
				}
			}	
		}
			
		return map;
	}

	
	
	/*public String[] getPriceRank(int cityID)
	{
		String[] price = new String[]{"全部"}; 
		if (app != null)
		{
			
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == cityID)
				{
					price = new String[city.getPriceRank()+1];
					price[0] = "全部";
					String priceLogo = "";
					for(int i=0;i<city.getPriceRank();i++)
					{
						priceLogo+="$";
						price[i+1] = priceLogo;
					}
				}
			}
		}
		return price;
	}*/
	
	public String[] getPriceRank(int cityID)
	{
		String[] price = null; 
		if (app != null)
		{
			
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == cityID)
				{
					price = new String[city.getPriceRank()];
					String priceLogo = "";
					for(int i=0;i<city.getPriceRank();i++)
					{
						priceLogo+="$";
						price[i] = priceLogo;
					}
				}
			}
		}
		return price;
	}

	public int getCurrentCityId()
	{
		return currentCityId;
	}

	public void setCurrentCityId(int currentCityId)
	{
		this.currentCityId = currentCityId;
	}

	public String getCurrentCityName()
	{
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == currentCityId)
				{
					currentCityName = city.getCityName();
				}
			}
			for (City city : app.getTestCitiesList())
			{
				if(city.getCityId() == currentCityId)
				{
					currentCityName = city.getCityName();
				}
			}
		}
		return currentCityName;
	}



	
	/*public List<City> getCityList()
	{
		List<City> citylList = new ArrayList<City>();
		if(app != null)
		{
			citylList.addAll(app.getCitiesList());
			//citylList.addAll(app.getTestCitiesList());
		}
		return citylList;
	}*/
	
	/*public List<City> getCityList()
	{
		List<City> citylList = null;
		if(app != null)
		{
			citylList = new ArrayList<City>();
			for(City city:app.getCitiesList())
			{			
			  citylList.add(city);	
			}
		}
		return citylList;
	}*/
	
	public List<City> getCityList()
	{
		List<City> citylList = null;
		List<City> temp = null;
		if(app != null)
		{
			citylList = new ArrayList<City>();
			temp = new ArrayList<City>();
			for(City city:app.getCitiesList())
			{			
			  if(city.getHotCity())
			  {
				  citylList.add(city);
			  }
			}
			for(City city :app.getTestCitiesList())
			{
				if(city.getHotCity())
				  {
					  citylList.add(city);
				  }
			}
			temp.addAll(app.getCitiesList());
			temp.addAll(app.getTestCitiesList());
			Collections.sort(temp, new PinyinComparator());
			citylList.addAll(temp);
			temp.clear();
			temp = null;
		}
		return citylList;
	}
	
	public List<City> getHotCityList()
	{
		List<City> citylList = null;
		if(app != null)
		{
			citylList = new ArrayList<City>();
			for(City city:app.getCitiesList())
			{			
			  if(city.getHotCity())
			  {
				  citylList.add(city);
			  }
			}
		}
		return citylList;
	}

	public int getDefaulCityId()
	{
		if(app!=null){
			Log.d(TAG, "default city id = "+app.getCities(0).getCityId());
			for(City city :app.getCitiesList())
			{
				if(city.getCityName().contains("香港"))
				{
					return city.getCityId();
				}
			}
			return app.getCities(0).getCityId();
		}else {
			return 0;
		}
		
	}

	
	public List<City> getSearchCityList()
	{
		List<City> citylList = null;
		if(app != null)
		{
			citylList = new ArrayList<City>();			
			citylList.addAll(app.getCitiesList());
			Collections.sort(citylList, new PinyinComparator());
		}	
		return citylList;
	}
	
	public City getCityByCityId(int cityId)
	{
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == cityId)
				{
					return city;
				}
			}
		}
		return null;
	}
	
	
	public String getHelpURL()
	{
		String url = "";
		String dataPath = ConstantField.HELP_DATA_FILE;
		if (!FileUtil.checkFileIsExits(dataPath))
		{
			Log.e(TAG, "load help data from file = " + dataPath
					+ " but file not found");
			return "";
		}

		File appData = new File(dataPath);
		FileInputStream inputStream = null;
		try
		{
			 inputStream = new FileInputStream(appData);
			Log.i(TAG, "load help data help html url from file = " + dataPath);
			HelpInfo helpInfo = HelpInfo.parseFrom(inputStream);
			url = helpInfo.getHelpHtml();
		} catch (Exception e)
		{
			Log.e(TAG, "load help data help html url from file = " + dataPath
					+ " but catch exception = " + e.toString(), e);
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
		return url;
	}

	
	public String getLocalHelpVersion()
	{
		String version = "";
		String dataPath = ConstantField.HELP_DATA_FILE;
		if (!FileUtil.checkFileIsExits(dataPath))
		{
			Log.e(TAG, "load help data version from file = " + dataPath
					+ " but file not found");
			return "";
		}

		File appData = new File(dataPath);
		FileInputStream inputStream = null;
		try
		{
			 inputStream = new FileInputStream(appData);
			Log.i(TAG, "load help data from file = " + dataPath);
			HelpInfo helpInfo = HelpInfo.parseFrom(inputStream);
			version = helpInfo.getVersion();
		} catch (Exception e)
		{
			Log.e(TAG, "load help data  version from file = " + dataPath
					+ " but catch exception = " + e.toString(), e);
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
		return version;
	}


	public List<RecommendedApp> getRecommendedApp()
	{
		List<RecommendedApp> list  = null;
		if (app != null)
		{
			list = app.getRecommendedAppsList();
		}
		return list;
	}

	
	public int[] getPriceId(int cityID)
	{
		int[] priceId = null; 
		if (app != null)
		{
			
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == cityID)
				{
					priceId = new int[city.getPriceRank()];
					int priceRank = 0;
					for(int i=0;i<city.getPriceRank();i++)
					{
						priceRank++;
						priceId[i] = priceRank;
					}
				}
			}
		}
		return priceId;
	}

	
	public HashMap<Integer, Float> getlatestVersion()
	{
		HashMap<Integer, Float> latestVersion = new HashMap<Integer, Float>();
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				int cityId = city.getCityId();
				float version = Float.parseFloat(city.getLatestVersion());
				latestVersion.put(cityId, version);
			}
		}
		return latestVersion;
	}
	
	
	public HashMap<Integer, String> getlatestDataDownloadURL()
	{
		HashMap<Integer, String> latestDataURL = new HashMap<Integer, String>();
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				int cityId = city.getCityId();
				String downloadURL = city.getDownloadURL();
				latestDataURL.put(cityId, downloadURL);
			}
		}
		return latestDataURL;
	}

	public String getAgencyNameById(int agencyId) {
		String agencyName = "";
		if (app != null)
		{
			for(Agency agency:app.getAgenciesList())
			{
				if(agency.getAgencyId() == agencyId )
				{
					agencyName = agency.getName();
				}
			}
		}
		return agencyName;
	}
	
	
	public String getAgencyShortNameById(int agencyId) {
		String agencyName = "";
		if (app != null)
		{
			for(Agency agency:app.getAgenciesList())
			{
				if(agency.getAgencyId() == agencyId )
				{
					agencyName = agency.getShortName();
				}
			}
		}
		return agencyName;
	}

	public String getSymbolByCityId(int cityId) {
		
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				if(cityId == city.getCityId())
				{
					 return city.getCurrencySymbol();	
				}
			}
		}
		return "";
	}

	public String getCityNameById(int cityId) {
		String cityName = "";
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == cityId)
				{
					cityName = city.getCityName();
				}
			}
		}
		return cityName;
	}

	
	public Agency getAgencyById(int agencyId)
	{
		if (app != null)
		{
			for(Agency agency:app.getAgenciesList())
			{
				if(agency.getAgencyId() == agencyId )
				{
					return agency;
				}
			}
		}
		return null;
	}

	/**  
	        * @param currentCityId2
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-11-6 下午4:47:54  
	*/
	public String getCityCountryById(int cityId)
	{
		String countryName = "";
		if (app != null)
		{
			for (City city : app.getCitiesList())
			{
				if(city.getCityId() == cityId)
				{
					countryName = city.getCountryName();
				}
			}
		}
		return countryName;
	}
	
	
	public List<CityLocationInfo> getCityLocationInfoById(int cityId)
	{
		/*CityLocationInfo.Builder builder = CityLocationInfo.newBuilder();
		builder.setCityName("Guangzhou");
		builder.setLatitude(23.7726);
		builder.setLongitude(113.5576);
		List<CityLocationInfo> cityLocationInfo = new ArrayList<CityLocationInfo>();
		cityLocationInfo.add(builder.build());*/
		List<CityLocationInfo> cityLocationInfo = null;
		if(app!=null)
		{
			City city = getCityByCityId(cityId);
			if(city!=null){
				cityLocationInfo = city.getLocationInfoList();
			}
		}
		
		return cityLocationInfo;
	}
	
}
