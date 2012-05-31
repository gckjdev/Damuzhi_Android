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
	public DataService()
	{
		super();
		this.application = TravelApplication.getInstance();
	}



	/**  
	        * @param placeType place���
	        * @param cityID
	        * @param lang  
	        * @description  
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 ����10:19:39  
	        */
	public void getPlace(String placeType,int cityID,String lang) 
	{
		
		PlaceManager placeManager ;
		String url = String.format(ConstantField.PLACElIST, placeType,cityID,lang);
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));//
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
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));//����Ƿ��������ļ�����
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
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));//����Ƿ��������ļ�����
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
	
	
	
	
	public ArrayList<Place> getAllPlaceInArea(Place targetPlace,int distance,String placeType,int cityID,String lang)
	{
		String url = String.format(ConstantField.PLACElIST, placeType,cityID,lang);
		ArrayList<Place> commnedPlace = new ArrayList<Place>();
		//boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));
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
	        * @description   ��ȡAPP�����
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-8 ����10:19:17  
	        */
	/*public void getAppData()
	{
		AppManager appManager = AppManager.getInstance();
		appManager.getPlaceMeta();		
		application.setDataFlag(ConstantField.DATA_HTTP);
	}*/
	
	
	
	private static double getDistance(Place targetPlace,Place itemPlace)
	{	
		if(targetPlace==null||itemPlace ==null)
		return 0;
		double distance = LocationUtil.GetDistance(targetPlace.getLongitude(), targetPlace.getLatitude(), itemPlace.getLongitude(), itemPlace.getLatitude());
		return distance;
	}
}
