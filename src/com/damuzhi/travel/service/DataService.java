package com.damuzhi.travel.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.overview.OverViewManager;
import com.damuzhi.travel.model.overview.TravelTipsManager;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.LocationUtil;

public class DataService
{
	private static final String TAG = "DataService";
	private TravelApplication application;
	public static final String SCENERY = "21";
	public static final String HOTEL = "22";
	public static final String RESTAURANT = "23";
	public static final String SHOPPING = "24";
	public static final String FUN = "25";

	/**
	 * @param application
	 */
	public DataService(TravelApplication application)
	{
		super();
		this.application = application;
	}



	/**  
	        * @param placeType place类别
	        * @param cityID
	        * @param lang  
	        * @description   获取具体类别的placelist 如景点/酒店/...
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 上午10:19:39  
	        */
	public void getPlace(String placeType,int cityID,String lang) 
	{
		
		PlaceManager placeManager ;
		String url = String.format(ConstantField.PLACElIST, placeType,cityID,lang);
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));//检查是否有离线文件存在
		/*if(fileExit)
		{
			String dataPath = String.format(ConstantField.DATA_PATH,cityID);
			placeManager = new PlaceManager(dataPath,null);
			int type = Integer.parseInt(placeType);
			switch (type)
			{
			case ALL_SCENERY_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getSceneryListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_HOTEL_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getHotelListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_SHOPPING_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getShoppingListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;	
			case ALL_RESTAURANT_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getRestraurantListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_FUN_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getFunListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			default:
				break;
			}
			
		}else {*/
			placeManager = new PlaceManager(null,url);
			application.setPlaceData(placeManager.getPlaceDataList());
			application.setDataFlag(ConstantField.DATA_HTTP);
		//}
		 
		
	}
	
	public CommonOverview getCommonOverview(String placeType,int cityID,String lang)
	{
		CommonOverview commonOverview = null;
		String url = String.format(ConstantField.OVERVIEW, placeType,cityID,lang);
		Log.d(TAG, url);
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));//检查是否有离线文件存在
		/*if(fileExit)
		{
			String dataPath = String.format(ConstantField.DATA_PATH,cityID);
			placeManager = new PlaceManager(dataPath,null);
			int type = Integer.parseInt(placeType);
			switch (type)
			{
			case ALL_SCENERY_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getSceneryListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_HOTEL_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getHotelListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_SHOPPING_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getShoppingListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;	
			case ALL_RESTAURANT_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getRestraurantListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_FUN_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getFunListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			default:
				break;
			}
			
		}else {*/
		commonOverview = OverViewManager.getOverviewByUrl(url);
		application.setDataFlag(ConstantField.DATA_HTTP);
		return commonOverview;
		//}
	}
	
	
	public List<CommonTravelTip> getCommonTravelTips(String placeType,int cityID,String lang)
	{
		List<CommonTravelTip> commonTravelTips = null;
		String url = String.format(ConstantField.PLACElIST, placeType,cityID,lang);
		Log.d(TAG, url);
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));//检查是否有离线文件存在
		/*if(fileExit)
		{
			String dataPath = String.format(ConstantField.DATA_PATH,cityID);
			placeManager = new PlaceManager(dataPath,null);
			int type = Integer.parseInt(placeType);
			switch (type)
			{
			case ALL_SCENERY_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getSceneryListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_HOTEL_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getHotelListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_SHOPPING_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getShoppingListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;	
			case ALL_RESTAURANT_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getRestraurantListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			case ALL_FUN_ORDER_BY_RANK:
				application.setPlaceData(placeManager.getFunListOrderByrank());
				application.setDataFlag(ConstantField.DATA_LOCAL);
				break;
			default:
				break;
			}
			
		}else {*/
		commonTravelTips = TravelTipsManager.getTravelTipsByUrl(url);
		application.setDataFlag(ConstantField.DATA_HTTP);
		return commonTravelTips;
		//}
	}
	
	
	
	/**  
	        * @param place
	        * @param distance与具体地点之间的距离范围半径
	        * @param placeType
	        * @param cityID
	        * @param lang
	        * @return  
	        * @description 获取周边推荐place
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 上午10:16:29  
	        */
	public ArrayList<Place> getAllPlaceInArea(Place targetPlace,int distance,String placeType,int cityID,String lang)
	{
		String url = String.format(ConstantField.PLACElIST, placeType,cityID,lang);
		ArrayList<Place> commnedPlace = new ArrayList<Place>();
		//boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));////检查是否有离线文件存在
		PlaceManager placeManager = new PlaceManager(null,url);
		ArrayList<Place> placeList = placeManager.getPlaceDataList();
		for(int i =0;i<placeList.size();i++)
		{
			Place itemPlace = placeList.get(i);
			double placeDistance = getDistance(targetPlace, itemPlace);
			if(placeDistance <distance&&itemPlace.getPlaceId()!=targetPlace.getPlaceId())
			{
				commnedPlace.add(itemPlace);
			}
		}
		
		
		return commnedPlace;
	}
	
	/**  
	        * @param placeType
	        * @param lang  
	        * @description   获取APP基础数据
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 上午10:19:17  
	        */
	public void getAppData(String placeType,String lang,String filePath)
	{
		String url = String.format(ConstantField.APP, placeType,lang);
		AppManager appManager = new AppManager(null,url);
		appManager.getPlaceMeta();
		application.setSymbolMap(appManager.getSymbolMap());
		application.setCityNameMap(appManager.getCityNameMap());
		application.setCityAreaList(appManager.getCityAreaList());
		application.setSubCategoryMap(appManager.getSubCatMap());
		application.setProvidedServiceMap(appManager.getProSerMap());
		application.setPlaceMetaMap(appManager.getPlaceMeta());
		//application.setProvidedServiceIconMap(appManager.getProSerIconMap());
		application.setDataFlag(ConstantField.DATA_HTTP);
	}
	
	
	
	private static double getDistance(Place targetPlace,Place itemPlace)
	{
		double distance = LocationUtil.GetDistance(targetPlace.getLongitude(), targetPlace.getLatitude(), itemPlace.getLongitude(), itemPlace.getLatitude());
		return distance;
	}
}
