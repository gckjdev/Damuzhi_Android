package com.damuzhi.travel.model.place;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PackageProtos.Package;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.FileUtil;

public class PlaceManager
{
	private static final String TAG = "PlaceManager";
	private PlaceList placeList;
	private TravelResponse travelResponse;
	private ArrayList<Place> spotList = new ArrayList<Place>();
	private ArrayList<Place> hotelList = new ArrayList<Place>();
	private ArrayList<Place> restraurantList = new ArrayList<Place>();
	private ArrayList<Place> shoppingList = new ArrayList<Place>();
	private ArrayList<Place> entertrainList = new ArrayList<Place>();
	private ArrayList<Place> placeDataList = new ArrayList<Place>();
 	

	/**
	 * @param dataPath
	 * @param url
	 */
	public PlaceManager(String dataPath, String url)
	{
		super();
		if(url == null||url.trim().equals(""))
		{
			getPlaceDataByFile(dataPath);
		}else {
			getPlaceListByUrl(url);
		}
	}


	private  void getPlaceDataByFile(String dataPath)
	{
		try
		{
			FileUtil fileUtil = new FileUtil();
			ArrayList<FileInputStream> fileInputStreams = fileUtil.getFileInputStreams(dataPath, ConstantField.PLACE_TAG, ConstantField.EXTENSION, true);
			for(FileInputStream fileInputStream : fileInputStreams)
			{
				placeList = PlaceList.parseFrom(fileInputStream);
				if(placeList!= null)
				{
					for (Place place:placeList.getListList())
					{
						switch (place.getCategoryId())
						{
						case PlaceCategoryType.PLACE_SPOT_VALUE:
							 spotList.add(place);
							break;
						case PlaceCategoryType.PLACE_HOTEL_VALUE:
							 hotelList.add(place);
							break;
						case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
							 entertrainList.add(place);
							break;
						case PlaceCategoryType.PLACE_SHOPPING_VALUE:
							 shoppingList.add(place);
							break;
						case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
							 restraurantList.add(place);
							break;
						default:
							break;
						}
					}
				
				}
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
	}
		

	private void getPlaceListByUrl(String url)
	{
		HttpTool httpTool = new HttpTool();
		InputStream inputStream;
		try
		{
			inputStream = httpTool.sendGetRequest(url);
			if(inputStream !=null)
			{
				try
				{
					travelResponse = TravelResponse.parseFrom(inputStream);
					//System.out.println("size = "+placeList.getListCount());
					for(Place place:travelResponse.getPlaceList().getListList())
					{
						placeDataList.add(place);
					}
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
	}
	
	public ArrayList<Place> getSceneryListOrderByrank()
	{		
		ComparatorRank comparatorRank = new ComparatorRank();
		Collections.sort(spotList, comparatorRank);	
		return spotList;
	}
	
	public ArrayList<Place> getHotelListOrderByrank()
	{		
		ComparatorRank comparatorRank = new ComparatorRank();
		Collections.sort(hotelList, comparatorRank);	
		return hotelList;
	}
	
	public ArrayList<Place> getRestraurantListOrderByrank()
	{		
		ComparatorRank comparatorRank = new ComparatorRank();
		Collections.sort(restraurantList, comparatorRank);	
		return restraurantList;
	}
	
	
	public ArrayList<Place> getShoppingListOrderByrank()
	{		
		ComparatorRank comparatorRank = new ComparatorRank();
		Collections.sort(shoppingList, comparatorRank);	
		return shoppingList;
	}
	
	public ArrayList<Place> getFunListOrderByrank()
	{		
		ComparatorRank comparatorRank = new ComparatorRank();
		Collections.sort(entertrainList, comparatorRank);	
		return entertrainList;
	}
	
	private class ComparatorRank implements Comparator<Place>
	{

		@Override
		public int compare(Place lhs, Place rhs)
		{
			// TODO Auto-generated method stub
			int flag = -Integer.valueOf(lhs.getRank()).compareTo(Integer.valueOf(rhs.getRank()));
			return flag;
		}

	
	}
	
	
	
	
	public PlaceList getPlaceList()
	{
		return placeList;
	}
	public void setPlaceList(PlaceList placeList)
	{
		this.placeList = placeList;
	}


	public ArrayList<Place> getPlaceDataList()
	{
		return placeDataList;
	}


	public void setPlaceDataList(ArrayList<Place> placeDataList)
	{
		this.placeDataList = placeDataList;
	}
	
	
	
}
