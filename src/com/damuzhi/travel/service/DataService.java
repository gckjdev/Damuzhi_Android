package com.damuzhi.travel.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.content.Context;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.FileUtil;

public class DataService
{
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



	public void getPlace(String placeType,int cityID,String lang) 
	{
		
		PlaceManager placeManager ;
		String url = String.format(ConstantField.PLACElIST, placeType,cityID,lang);
		boolean fileExit =  FileUtil.checkFileIsExits(Integer.toString(cityID));
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
	
	public void getAppData(String placeType,String lang)
	{
		AppManager appManager;
		String url = String.format(ConstantField.APP, placeType,lang);
		appManager = new AppManager(null,url);
		application.setApp(appManager.getApp());
		application.setDataFlag(ConstantField.DATA_HTTP);
	}
}
