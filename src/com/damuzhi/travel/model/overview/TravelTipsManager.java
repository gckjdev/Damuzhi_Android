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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.CityOverviewProtos.CityOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTip;
import com.damuzhi.travel.protos.TravelTipsProtos.CommonTravelTipList;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTips;
import com.damuzhi.travel.util.FileUtil;


public class TravelTipsManager
{
		
	private static final String TAG = "TravelTipsManager";
	
	
	
	public static TravelTips getTravelTipsByFile(String dataPath)
	{
		TravelTips travelTips = null;
		try
		{
			FileUtil fileUtil = new FileUtil();			
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.OVERVIEW_TAG, ConstantField.EXTENSION, true);
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				travelTips = TravelTips.parseFrom(fileInputStream);
				
			}
			
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return travelTips;
	}
		
	
	public static List<CommonTravelTip> getTravelTipsByUrl(String url)
	{
		HttpTool httpTool = new HttpTool();
		InputStream inputStream;
		List<CommonTravelTip> list = null;
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
					// TODO Auto-generated catch block
					Log.d(TAG, "getData from http error...");
					e.printStackTrace();
				}
			}
			
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return list;
	}
}
