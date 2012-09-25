/**  
        * @title TravelTipsManger.java  
        * @package com.damuzhi.travel.model.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-22 ����3:11:52  
        * @version V1.0  
        */
package com.damuzhi.travel.model.overview;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTips;
import com.damuzhi.travel.util.FileUtil;


public class TravelTipsManager
{
		
	private static final String TAG = "TravelTipsManager";
	
	private List<CommonTravelTip> travelGuides = new ArrayList<CommonTravelTip>();
	private List<CommonTravelTip> travelRoutes = new ArrayList<CommonTravelTip>();	
	
	public static TravelTips getTravelGuideByFile(String dataPath)
	{
		TravelTips travelTips = null;
		try
		{
			FileUtil fileUtil = new FileUtil();			
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.GUIDE_TAG, ConstantField.EXTENSION, true);
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				travelTips = TravelTips.parseFrom(fileInputStream);
				
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<getTravelGuideByFile> but catch exception:"+e.toString(),e);
		} 
		return travelTips;
	}
		
	
	public static TravelTips getTravelRouteByFile(String dataPath)
	{
		TravelTips travelTips = null;
		try
		{
			FileUtil fileUtil = new FileUtil();			
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.ROUTE_TAG, ConstantField.EXTENSION, true);
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				travelTips = TravelTips.parseFrom(fileInputStream);
				
			}
			
		} catch (Exception e)
		{
			Log.e(TAG, "<getTravelRouteByFile> but catch exception:"+e.toString(),e);
		} 
		return travelTips;
	}
	
	
	
	
	public static List<CommonTravelTip> getTravelTipsByUrl(String url)
	{
		InputStream inputStream = null;
		List<CommonTravelTip> list = null;
		HttpTool httpTool = HttpTool.getInstance();
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				try
				{
					
					list =  TravelResponse.parseFrom(inputStream).getTravelTipList().getTipListList();
					
				} catch (IOException e)
				{
					Log.d(TAG, "getData from http error...");
					e.printStackTrace();
				}
			}
			
		} catch (Exception e1)
		{
			if(inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (Exception e)
				{
				}
			}
			e1.printStackTrace();
		}finally
		{
			httpTool.stopConnection();
			if(inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (Exception e)
				{
				}
			}
		}
		return list;
	}


	
	public void guidesClear()
	{
		travelGuides.clear();
		
	}


	
	public void addGuides(List<CommonTravelTip> guideList)
	{
		if (guideList == null)
			return;
		
		travelGuides.addAll(guideList);
		
	}
	
	
	public void routesClear()
	{
		travelRoutes.clear();
		
	}


	
	public void addRoutes(List<CommonTravelTip> routesList)
	{
		if (routesList == null)
			return;
		
		travelRoutes.addAll(routesList);
		
	}


	public List<CommonTravelTip> getTravelGuides()
	{
		return travelGuides;
	}


	public List<CommonTravelTip> getTravelRoutes()
	{
		return travelRoutes;
	}


	public void setTravelGuides(List<CommonTravelTip> travelGuides)
	{
		this.travelGuides = travelGuides;
	}


	public void setTravelRoutes(List<CommonTravelTip> travelRoutes)
	{
		this.travelRoutes = travelRoutes;
	}
}
