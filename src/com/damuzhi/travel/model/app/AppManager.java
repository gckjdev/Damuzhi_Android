package com.damuzhi.travel.model.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.Loader;
import android.util.Log;

import com.damuzhi.travel.R.id;
import com.damuzhi.travel.mission.AppMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.HelpInfo;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.util.FileUtil;

public class AppManager
{
	private static final String TAG = "AppManager";
	private App app;
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
		String dataPath = ConstantField.APP_DATA_FILE;
		if (!FileUtil.checkFileIsExits(dataPath))
		{
			Log.e(TAG, "load app data from file = " + dataPath
					+ " but file not found");
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
			Log.e(TAG, "load app data from file = " + dataPath
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
		// TODO current city Id should be persistented
		//int cityId = instance.getLastCityId(context);
		/*if(cityId == -1)
		{
			currentCityId = app.getCities(0).getCityId();
		}else {
			currentCityId = cityId;
		}*/
		
		Log.i(TAG, "current city id is "+currentCityId);
	}

	
	
	
	
	
	/**
	 * 
	 * @description reload app data from local file
	 * @version 1.0
	 * @author liuxiaokun
	 * @update 2012-5-24 下午2:14:10
	 */
	public void reloadData()
	{
		load();
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
				subCategoryMap.put(placeMeta.getCategoryId(),
						placeMeta.getSubCategoryListList());
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
				providedServiceMap.put(placeMeta.getCategoryId(),
						placeMeta.getProvidedServiceListList());

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
				for (NameIdPair nameIdPair : placeMeta
						.getProvidedServiceListList())
				{
					map.put(nameIdPair.getId(), nameIdPair.getImage());
				}
				providedServiceIconMap.put(placeMeta.getCategoryId(), map);
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
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = instance
				.getSubCatMap();
		List<NameIdPair> nameIdPairs = subCatMap.get(PlaceCategoryType.valueOf(placeCategoryType));
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			map.put(nameIdPair.getId(), nameIdPair.getName());
		}
		return map;
	}

	public HashMap<Integer, String> getAllSubCatMap()
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = instance
				.getSubCatMap();
		Set<PlaceCategoryType> keyset = subCatMap.keySet();
		for (PlaceCategoryType placeCategoryType : keyset)
		{
			List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
			for (NameIdPair nameIdPair : nameIdPairs)
			{
				map.put(nameIdPair.getId(), nameIdPair.getName());
			}
		}

		return map;
	}

	public String[] getSubCatNameList(PlaceCategoryType placeCategoryType)
	{
		int i = 1;
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = instance
				.getSubCatMap();
		List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
		String[] subCat = new String[nameIdPairs.size() + 1];
		subCat[0] = ConstantField.ALL_PLACE;
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			subCat[i] = nameIdPair.getName();
			i++;
		}
		return subCat;
	}

	public int[] getSubCatKeyList(PlaceCategoryType placeCategoryType)
	{
		int i = 1;
		HashMap<PlaceCategoryType, List<NameIdPair>> subCatMap = instance
				.getSubCatMap();
		List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
		int[] subCatKey = new int[nameIdPairs.size() + 1];
		subCatKey[0] = -1;
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			subCatKey[i] = nameIdPair.getId();
			i++;
		}
		return subCatKey;
	}

	public  String[] getProvidedServiceNameList(
			PlaceCategoryType placeCategoryType)
	{
		int i = 1;
		HashMap<PlaceCategoryType, List<NameIdPair>> proSerMap = instance
				.getProSerMap();
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		String[] proServiceName = new String[nameIdPairs.size() + 1];
		proServiceName[0] = ConstantField.ALL_PLACE;
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			proServiceName[i] = nameIdPair.getName();
			i++;
		}
		return proServiceName;
	}

	public int[] getProvidedServiceKeyList(PlaceCategoryType placeCategoryType)
	{
		int i = 1;
		HashMap<PlaceCategoryType, List<NameIdPair>> proSerMap = instance
				.getProSerMap();
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		int[] proServiceKey = new int[nameIdPairs.size() + 1];
		proServiceKey[0] = -1;
		for (NameIdPair nameIdPair : nameIdPairs)
		{
			proServiceKey[i] = nameIdPair.getId();
			i++;
		}
		return proServiceKey;
	}

	public HashMap<Integer, String> getProServiceMap(
			PlaceCategoryType placeCategoryType)
	{
		HashMap<PlaceCategoryType, List<NameIdPair>> proSerMap = instance
				.getProSerMap();
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
		int i = 1;
		HashMap<Integer, List<CityArea>> ctiyAreaList = instance
				.getCityAreaList();
		List<CityArea> ctiyAreas = ctiyAreaList.get(cityID);
		String[] ctiyAreasName = new String[ctiyAreas.size() + 1];
		ctiyAreasName[0] = ConstantField.ALL_PLACE;
		for (CityArea cityArea : ctiyAreas)
		{
			ctiyAreasName[i] = cityArea.getAreaName();
			i++;
		}
		return ctiyAreasName;
	}

	public int[] getCityAreaKeyList(int cityID)
	{
		int i = 1;
		HashMap<Integer, List<CityArea>> cityAreaList = instance
				.getCityAreaList();
		List<CityArea> ctiyAreas = cityAreaList.get(cityID);
		int[] ctiyAreasKey = new int[ctiyAreas.size() + 1];
		ctiyAreasKey[0] = -1;
		for (CityArea cityArea : ctiyAreas)
		{
			ctiyAreasKey[i] = cityArea.getAreaId();
			i++;
		}
		return ctiyAreasKey;
	}

	public HashMap<Integer, String> getCityAreaMap(int cityID)
	{
		HashMap<Integer, List<CityArea>> cityAreaList = instance
				.getCityAreaList();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		List<CityArea> ctiyAreas = cityAreaList.get(cityID);
		for (CityArea cityArea : ctiyAreas)
		{
			map.put(cityArea.getAreaId(), cityArea.getAreaName());
		}
		return map;
	}

	
	
	public String[] getPriceRank(int cityID)
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
		}
		return currentCityName;
	}

	/*public void setCurrentCityName(String currentCityName)
	{
		this.currentCityName = currentCityName;
	}*/

	
	public List<City> getCityList()
	{
		if (app != null)
		{
			return app.getCitiesList();
		}		
		return null;
	}

	public int getDefaulCityId()
	{
		return app.getCities(0).getCityId();
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
			Log.i(TAG, "load help data from file = " + dataPath);
			HelpInfo helpInfo = HelpInfo.parseFrom(inputStream);
			url = helpInfo.getHelpHtml();
		} catch (Exception e)
		{
			Log.e(TAG, "load help data from file = " + dataPath
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
}
