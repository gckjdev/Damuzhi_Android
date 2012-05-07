package com.damuzhi.travel.model.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.App;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.PlaceMeta;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;

public class AppManager
{
	private String dataPath ;
	private String url;
    private PlaceMeta placeMeta;
	private App app;
	private HashMap<Integer, City> cityMap = new HashMap<Integer, City>();
	private HashMap<Integer, CityArea> cityAreaMap = new HashMap<Integer, CityArea>();
	
	
	
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
	public App getApp()
	{
		return app;
	}
	public void setApp(App app)
	{
		this.app = app;
	}
	
	public HashMap<Integer, City> getCityMap()
	{
		for(City city:app.getCitiesList())
		{
			cityMap.put(city.getCityId(), city);
		}
		return cityMap;
	}
	
	
	public HashMap<Integer, CityArea> getCityAreaMap()
	{
		for(City city:app.getCitiesList())
		{
			for(CityArea cityArea :city.getAreaListList())
			{
				cityAreaMap.put(cityArea.getAreaId(), cityArea);
			}
			
		}
		return cityAreaMap;
	}
	
}
