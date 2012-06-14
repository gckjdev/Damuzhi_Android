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

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.CityOverviewProtos.CityOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
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
	
	
}
