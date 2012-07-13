/**  
        * @title OverViewManger.java  
        * @package com.damuzhi.travel.model.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-22 ����9:58:35  
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

import com.damuzhi.travel.activity.overview.CommonCtiyBaseActivity;
import com.damuzhi.travel.activity.overview.CommonOverViewActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.CityOverviewProtos.CityOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverviewType;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-22 ����9:58:35  
 */

public class OverViewManager
{
	
	
	private static final String TAG = "OverViewManger";
	private CityOverview cityOverview ;
	
	
	public static CityOverview getOverviewDataByFile(String dataPath)
	{
		CityOverview cityOverview = null;
		try
		{
			FileUtil fileUtil = new FileUtil();			
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.OVERVIEW_TAG, ConstantField.EXTENSION, true);
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				cityOverview = CityOverview.parseFrom(fileInputStream);
				
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return cityOverview;
	}
		

	public static CommonOverview getOverviewByUrl(String url)
	{
		HttpTool httpTool = new HttpTool();
		InputStream inputStream;
		CommonOverview commonOverview = null;
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{									
				commonOverview =  TravelResponse.parseFrom(inputStream).getOverview();				
			}
			
		} catch (Exception e1)
		{
			
			e1.printStackTrace();
		}
		return commonOverview;
	}


	public CityOverview getCityOverview()
	{
		return cityOverview;
	}


	public void setCityOverview(CityOverview cityOverview)
	{
		this.cityOverview = cityOverview;
	}


	
	public void clear()
	{
		this.cityOverview = null;
		
	}
	
	public CommonOverview getCityCommonOverview(int commonOverviewType)
	{
		if(cityOverview != null)
		{
			switch (commonOverviewType)
			{
			case CommonOverviewType.CITY_BASIC_VALUE:
				return cityOverview.getCityBasic();
			case CommonOverviewType.TRAVEL_PREPRATION_VALUE:
				return cityOverview.getTravelPrepration();
			case CommonOverviewType.TRAVEL_UTILITY_VALUE:
				return cityOverview.getTravelUtility();
			case CommonOverviewType.TRAVEL_TRANSPORTATION_VALUE:
				return cityOverview.getTravelTransportation();
			}
			return null;
		}else {
			return null;
		}
		
	}
	
	
}
