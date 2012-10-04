package com.damuzhi.travel.mission.touristRoute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.network.PlaceNetworkHandler;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.protos.TouristRouteProtos.Order;
import com.damuzhi.travel.protos.TouristRouteProtos.TouristRoute;

public class TouristRouteMission {

	private static final String TAG = "TouristRouteMission";
	private static TouristRouteMission instance = null;
	private static final int count = 20;
	private int lastCityId = -100;
	private List<LocalRoute> localRoutesList = new ArrayList<LocalRoute>();
	
	private TouristRouteMission() {
	}
	
	public static TouristRouteMission getInstance() {
		if (instance == null) {
			instance = new TouristRouteMission();
		}
		return instance;
	}
	
	
	public List<LocalRoute> getLocalRoutes(int cityId)
	{					
		Log.d(TAG, "get url data ");
		lastCityId = cityId;
		localRoutesList.clear();
		final List<LocalRoute> remotePlaceList = getLocalRouteListByUrl(cityId);
		if(remotePlaceList != null && remotePlaceList.size() > 0)
		{
			localRoutesList.addAll(remotePlaceList);
		}										
		return localRoutesList;
	}

	private List<LocalRoute> getLocalRouteListByUrl(int cityId) {
		
		String deviceId = TravelApplication.getInstance().deviceId;
		String url = String.format(ConstantField.TOURIST_ROUTE_URL, ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_LIST,cityId, 0,count,ConstantField.LANG_HANS,deviceId);
		Log.i(TAG, "<getTouristRouteListByUrl> load place data from http ,url = "+url);
		InputStream inputStream = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getRouteList() == null){
					return Collections.emptyList();
				}					
				inputStream.close();
				inputStream = null;			
				Log.d(TAG, "get route count = "+travelResponse.getRouteList().getRoutesCount());
				return travelResponse.getLocalRoutes().getRoutesList();			
			}
			else{
				return Collections.emptyList();
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getTouristRouteListByUrl> catch exception = "+e.toString(), e);
			return Collections.emptyList();
		}finally
		{
			httpTool.stopConnection();
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
		}	
	}

	public List<LocalRoute> loadMoreLocalRoutes(int cityId,int start) {
		
		String deviceId = TravelApplication.getInstance().deviceId;
		String url = String.format(ConstantField.TOURIST_ROUTE_URL, ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_LIST,cityId, start,count,ConstantField.LANG_HANS,deviceId);
		Log.i(TAG, "<loadMoreLocalRoutes> load place data from http ,url = "+url);
		InputStream inputStream = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getRouteList() == null){
					return Collections.emptyList();
				}					
				inputStream.close();
				inputStream = null;			
				Log.d(TAG, "get route count = "+travelResponse.getRouteList().getRoutesCount());
				return travelResponse.getLocalRoutes().getRoutesList();			
			}
			else{
				return Collections.emptyList();
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<loadMoreLocalRoutes> catch exception = "+e.toString(), e);
			return Collections.emptyList();
		}finally
		{
			httpTool.stopConnection();
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
		}
	}

	public boolean nonMemberBookingOrder(String userId,String routeId,String departPlaceId,String departDate,String adult,String child,String contactPerson,String contact) {
		String url = String.format(ConstantField.LOCAL_ROUTE_NON_MENBER_BOOKING_ORDER_URL, userId,routeId,departPlaceId,departDate,adult,child,contactPerson,contact);
		Log.d(TAG, "<nonMemberBookingOrder> submit booking ,url = "+url);
		InputStream inputStream = null;
		BufferedReader br = null;
		InputStreamReader inputStreamReader = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				inputStreamReader = new InputStreamReader(inputStream);
				br = new BufferedReader(inputStreamReader);
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				Log.i(TAG, "<nonMemberBookingOrder> result "+result);
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				if(sb.length() <= 1){
					return false;
				}
				JSONObject submitData = new JSONObject(sb.toString());
				if (submitData != null &&submitData.getInt("result") == 0){
					return true;
				}else{
					return false;
				}			
			}
			else{
				return false;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<nonMemberBookingOrder> catch exception = "+e.toString(), e);
			return false;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){				
						inputStream.close();
				}
				if (inputStreamReader != null){
						inputStreamReader.close();
				}
				if (br != null){				
						br.close();
				}
			} catch (IOException e1)
			{
			}
		}
		//return false;
	
	}

	public List<Order> getOrderList(int cityId,String orderType, String userId,String loginId, String token) {
		String url = String.format(ConstantField.TOURIST_ROUTE_ORDER_LIST_URL,orderType,cityId,userId,loginId,token,ConstantField.LANG_HANS);
		Log.i(TAG, "<getOrderList> load place data from http ,url = "+url);
		InputStream inputStream = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getOrderList() == null){
					return Collections.emptyList();
				}					
				inputStream.close();
				inputStream = null;			
				Log.d(TAG, "get order list count = "+travelResponse.getOrderList().getOrdersCount());
				return travelResponse.getOrderList().getOrdersList();			
			}
			else{
				return Collections.emptyList();
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getOrderList> catch exception = "+e.toString(), e);
			return Collections.emptyList();
		}finally
		{
			httpTool.stopConnection();
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
		}	
	}

	public LocalRoute getLocalRouteDetail(int routeId) {
		String url = "";
		url = String.format(ConstantField.TOURIST_ROUTE_OBJECT_URL,ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_DETAIL,routeId,ConstantField.LANG_HANS);
		Log.i(TAG, "<getLocalRouteDetail> load local route data from http ,url = "+url);
		InputStream inputStream = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0 ||travelResponse.getLocalRoute() == null){
					return null;
				}					
				inputStream.close();
				inputStream = null;		
				return travelResponse.getLocalRoute();			
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getLocalRouteDetail> catch exception = "+e.toString(), e);
			return null;
		}finally
		{
			httpTool.stopConnection();
			if (inputStream != null){
				try
				{
					inputStream.close();
				} catch (IOException e1)
				{
				}
			}
		}	
	}


	public boolean memberBookingOrder(String loginId, String token,String routeId, String departPlaceId, String departDate, String adult,String child)
	{
		String url = String.format(ConstantField.LOCAL_ROUTE_MEMBER_BOOKING_ORDER_URL, loginId,token,routeId,"",departDate,adult,child);
		Log.d(TAG, "<memberBookingOrder> submit booking ,url = "+url);
		InputStream inputStream = null;
		BufferedReader br = null;
		InputStreamReader inputStreamReader = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				inputStreamReader = new InputStreamReader(inputStream);
				br = new BufferedReader(inputStreamReader);
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				Log.i(TAG, "<memberBookingOrder> result "+result);
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				if(sb.length() <= 1){
					return false;
				}
				JSONObject submitData = new JSONObject(sb.toString());
				if (submitData != null &&submitData.getInt("result") == 0){
					return true;
				}else{
					return false;
				}			
			}
			else{
				return false;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<memberBookingOrder> catch exception = "+e.toString(), e);
			return false;
		}finally
		{
			httpTool.stopConnection();
			try
			{
				if (inputStream != null){				
						inputStream.close();
				}
				if (inputStreamReader != null){
						inputStreamReader.close();
				}
				if (br != null){				
						br.close();
				}
			} catch (IOException e1)
			{
			}
		}
		//return false;
	}
	
	
}
