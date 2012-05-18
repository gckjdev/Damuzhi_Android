package com.damuzhi.travel.model.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.damuzhi.travel.R.id;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;

public class AppManager
{
	private static final String TAG = "AppManager";
	private String dataPath ;
	private String url;
    private PlaceMeta placeMeta;
	private App app;
	private HashMap<Integer, City> cityMap = new HashMap<Integer, City>();
	private HashMap<Integer, CityArea> cityAreaMap = new HashMap<Integer, CityArea>();
	private HashMap<Integer, List<CityArea>> cityAreaList = new HashMap<Integer, List<CityArea>>();
	private HashMap<String, Integer> cityNameMap = new HashMap<String,Integer>();//城市列表	
	private HashMap<PlaceCategoryType, PlaceMeta> map = new HashMap<PlaceCategoryType, PlaceMeta>();//PLACE分类ID
	private HashMap<Integer, String> symbolMap = new HashMap<Integer, String>();//货币显示符号
	private HashMap<PlaceCategoryType, List<NameIdPair>> subCategoryMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();//地点分类下的所有子分类
	private HashMap<PlaceCategoryType, List<NameIdPair>> providedServiceMap = new HashMap<PlaceCategoryType, List<NameIdPair>>();//地点分类下的可用的所有服务选项列表
	private HashMap<PlaceCategoryType, HashMap<Integer, String>> providedServiceIconMap = new HashMap<PlaceCategoryType, HashMap<Integer,String>>();

	
	/**
	 * @param dataPath
	 * @param url
	 */
	public AppManager(String dataPath, String url)
	{
		super();
		try
		{
		if(dataPath != null){			
			app = App.parseFrom(new FileInputStream(new File(dataPath)));		
		}else {
		    HttpTool httpTool = new HttpTool();
		    TravelResponse travelResponse = TravelResponse.parseFrom(httpTool.sendGetRequest(url));
		    app = travelResponse.getAppInfo();
		}
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HashMap<Integer, City> getCityMap()
	{
		for(City city:app.getCitiesList())
		{
			cityMap.put(city.getCityId(), city);
		}
		return cityMap;
	}
	
	
	/*public HashMap<Integer, CityArea> getCityAreaMap()
	{
		for(City city:app.getCitiesList())
		{
			for(CityArea cityArea :city.getAreaListList())
			{
				cityAreaMap.put(cityArea.getAreaId(), cityArea);
			}
			
		}
		return cityAreaMap;
	}*/
	
	public HashMap<Integer, List<CityArea>> getCityAreaList()
	{
		if(app != null)
		{
			for (City city : app.getCitiesList())
			{
				cityAreaList.put(city.getCityId(), city.getAreaListList());
			}
		}
		return cityAreaList;
	}
	
	public HashMap<String, Integer> getCityNameMap()
	{
		if(app != null)
		{
			for (City city : app.getCitiesList())
			{
				cityNameMap.put(city.getCityName(), city.getCityId());
			}
		}
		return cityNameMap;
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
	public HashMap<PlaceCategoryType, List<NameIdPair>> getSubCatMap()
	{
		if(app != null)
		{
			for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
			{
				subCategoryMap.put(placeMeta.getCategoryId(), placeMeta.getSubCategoryListList());
			}
		}
		return subCategoryMap;
	}
	
	
	
	//地点分类下的可用的所有服务选项列表
	public HashMap<PlaceCategoryType, List<NameIdPair>> getProSerMap()
	{
		if(app != null)
		{
			for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
			{
				providedServiceMap.put(placeMeta.getCategoryId(), placeMeta.getProvidedServiceListList());
				
			}
		}
		return providedServiceMap;
	}
	
	//地点分类下的可用的所有服务选项列表
		public HashMap<PlaceCategoryType, HashMap<Integer, String>> getProSerIconMap()
		{
			if(app != null)
			{
				for(PlaceMeta placeMeta:app.getPlaceMetaDataListList())
				{
					HashMap<Integer, String> map = new HashMap<Integer, String>();
					for(NameIdPair nameIdPair:placeMeta.getProvidedServiceListList())
					{
						map.put(nameIdPair.getId(), nameIdPair.getImage());
					}
				providedServiceIconMap.put(placeMeta.getCategoryId(), map);					
				}
			}
			return providedServiceIconMap;
		}
	
	
	//地点分类下的可用的所有服务选项列表
		public HashMap<Integer,String> getSymbolMap()
		{
			if(app != null)
			{
				for(City city:app.getCitiesList())
				{
					symbolMap.put(city.getCityId(), city.getCurrencySymbol());
				}
			}
			return symbolMap;
		}
		
}
