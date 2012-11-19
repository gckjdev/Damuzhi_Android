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
import com.damuzhi.travel.protos.PackageProtos.RouteFeekback;
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
	//private List<LocalRoute> remotePlaceList;
	private String resultInfo;
	private String deviceId = TravelApplication.getInstance().deviceId;
	private int totalCount = 0;
	
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
		if(lastCityId ==cityId)
		{
			return localRoutesList;
		}else
		{
			lastCityId = cityId;
			localRoutesList.clear();
			String url = String.format(ConstantField.TOURIST_ROUTE_URL, ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_LIST,cityId, 0,count,ConstantField.LANG_HANS,deviceId);
			TravelResponse travelResponse = getTravelResponseByURL(url);
			if(travelResponse!=null)
			{
				totalCount = travelResponse.getTotalCount();
				Log.d(TAG, "local route total count = "+totalCount);
				if(travelResponse.getLocalRoutes().getRoutesCount()>0)
				{
					localRoutesList.addAll(travelResponse.getLocalRoutes().getRoutesList());
				}
				return localRoutesList;
			}else
			{
				return Collections.emptyList();
			}
		}
		
	}

	

	public List<LocalRoute> loadMoreLocalRoutes(int cityId,int start) {
		String url = String.format(ConstantField.TOURIST_ROUTE_URL, ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_LIST,cityId, start,count,ConstantField.LANG_HANS,deviceId);
		Log.i(TAG, "<loadMoreLocalRoutes> load place data from http ,url = "+url);
		//return getLocalRouteListByUrl(url);
		TravelResponse travelResponse = getTravelResponseByURL(url);
		if(travelResponse!=null)
		{
			return travelResponse.getLocalRoutes().getRoutesList();
		}else
		{
			return Collections.emptyList();
		}
	}

	public boolean nonMemberBookingOrder(String userId,String routeId,String departPlaceId,String departDate,String adult,String child,String contactPerson,String contact) {
		String url = String.format(ConstantField.LOCAL_ROUTE_NON_MENBER_BOOKING_ORDER_URL, userId,routeId,departPlaceId,departDate,adult,child,contactPerson,contact);
		Log.d(TAG, "<nonMemberBookingOrder> submit booking ,url = "+url);
		return getDataByURL(url);	
	}

	
	
	
	public List<Order> getOrderList(int cityId,String orderType, String userId,String loginId, String token) {
		String url = String.format(ConstantField.TOURIST_ROUTE_ORDER_LIST_URL,orderType,cityId,userId,loginId,token,ConstantField.LANG_HANS);
		Log.i(TAG, "<getOrderList> load place data from http ,url = "+url);
		TravelResponse travelResponse = getTravelResponseByURL(url);
		if(travelResponse!=null)
		{
			return travelResponse.getOrderList().getOrdersList();
		}else
		{
			return Collections.emptyList();
		}
	}

	public LocalRoute getLocalRouteDetail(int routeId) {
		String url  = String.format(ConstantField.TOURIST_ROUTE_OBJECT_URL,ConstantField.TOURIST_ROUTE_LOCAL_ROUTE_DETAIL,routeId,ConstantField.LANG_HANS);
		Log.i(TAG, "<getLocalRouteDetail> load local route data from http ,url = "+url);
		TravelResponse travelResponse = getTravelResponseByURL(url);
		if(travelResponse!=null)
		{
			return travelResponse.getLocalRoute();
		}else
		{
			return null;
		}
	}


	public boolean memberBookingOrder(String loginId, String token,String routeId, String departPlaceId, String departDate, String adult,String child)
	{
		String url = String.format(ConstantField.LOCAL_ROUTE_MEMBER_BOOKING_ORDER_URL, loginId,token,routeId,"",departDate,adult,child);
		Log.d(TAG, "<memberBookingOrder> submit booking ,url = "+url);
		return getDataByURL(url);
	}

	
	public boolean routeFeedBack(String loginId, String token, int routeId,int orderId, int praiseRank, String content)
	{
		String url = String.format(ConstantField.ROUTE_FEEDBACK_URL, loginId,token,routeId,orderId,praiseRank,content);
		return getDataByURL(url);
	}
	
	
	private boolean getDataByURL(String url)
	{
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
				Log.i(TAG, "<getDataByURL> result "+result);
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				if(sb.length() <= 1){
					return false;
				}
				JSONObject jsonData = new JSONObject(sb.toString());
				if(jsonData.has("resultInfo"))
				{
					resultInfo = jsonData.getString("resultInfo");
				}				
				if (jsonData != null &&jsonData.getInt("result") == 0){
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
			Log.e(TAG, "<getDataByURL> catch exception = "+e.toString(), e);
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
	}

	

	
	public List<RouteFeekback> getRouteFeedBacks(int cityId, int routeId)
	{
		String url = String.format(ConstantField.GET_ROUTE_FEEDBACKS_URL,ConstantField.TOURIST_ROUTE_ROUTE_FEEDBACKS,cityId,routeId,ConstantField.LANG_HANS);
		TravelResponse travelResponse = getTravelResponseByURL(url);
		if(travelResponse!=null)
		{
			return travelResponse.getRouteFeekbackList().getRouteFeekbacksList();
		}else
		{
			return Collections.emptyList();
		}
		
	}
	
	
	
	private TravelResponse getTravelResponseByURL(String url)
	{
		Log.i(TAG, "<getTravelResponseByURL> get TravelResponse from http ,url = "+url);
		InputStream inputStream = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{				
				TravelResponse travelResponse = TravelResponse.parseFrom(inputStream);
				if (travelResponse == null || travelResponse.getResultCode() != 0){
					return null;
				}					
				inputStream.close();
				inputStream = null;			
				return travelResponse;			
			}
			else{
				return null;
			}
			
		} 
		catch (Exception e)
		{
			Log.e(TAG, "<getTravelResponseByURL> catch exception = "+e.toString(), e);
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
	
	public String getResultInfo()
	{
		return resultInfo;
	}

	public void setResultInfo(String resultInfo)
	{
		this.resultInfo = resultInfo;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(int totalCount)
	{
		this.totalCount = totalCount;
	}
	
	
	
}
