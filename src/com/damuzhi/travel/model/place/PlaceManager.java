package com.damuzhi.travel.model.place;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.IInterface;
import android.util.Log;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.network.HttpTool;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PackageProtos.Package;
import com.damuzhi.travel.protos.PackageProtos.TravelResponse;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.util.FileUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;

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
 	
	private List<Place> placeLists = new ArrayList<Place>();
	
	private List<Place> nearbyPlaceList = new ArrayList<Place>();
	
	public void clear(){
		placeLists.clear();
	}
	
	public void addPlaces(List<Place> list){
		if (list == null)
			return;
		
		placeLists.addAll(list);
	}
	
	
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

	
	

	
	public PlaceManager()
	{
		// TODO Auto-generated constructor stub
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
		HttpTool httpTool = HttpTool.getInstance();
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
		ComparatorRank comparatorRank = TravelUtil.getComparatorRank();
		Collections.sort(spotList, comparatorRank);	
		return spotList;
	}
	
	public ArrayList<Place> getHotelListOrderByrank()
	{		
		ComparatorRank comparatorRank = TravelUtil.getComparatorRank();
		Collections.sort(hotelList, comparatorRank);	
		return hotelList;
	}
	
	public ArrayList<Place> getRestraurantListOrderByrank()
	{		
		ComparatorRank comparatorRank = TravelUtil.getComparatorRank();
		Collections.sort(restraurantList, comparatorRank);	
		return restraurantList;
	}
	
	
	public ArrayList<Place> getShoppingListOrderByrank()
	{		
		ComparatorRank comparatorRank = TravelUtil.getComparatorRank();
		Collections.sort(shoppingList, comparatorRank);	
		return shoppingList;
	}
	
	public ArrayList<Place> getFunListOrderByrank()
	{		
		ComparatorRank comparatorRank = TravelUtil.getComparatorRank();
		Collections.sort(entertrainList, comparatorRank);	
		return entertrainList;
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

	
	public Place getPlaceById(int placeId)
	{
		for(Place place:placeLists)
		{
			if(place.getPlaceId() == placeId)
			{
				return place;
			}
		}
		return null;
	}

	
	public List<Place> getPlaceNearbyPlaceList(Place place,List<Place> placeList)
	{
		
		ComparatorDistance comparatorDistance = new ComparatorDistance(place.getLongitude(),place.getLatitude());
		Collections.sort(placeList, comparatorDistance);
		for(int i=0;i<10;i++)
		{
			nearbyPlaceList.add(placeList.get(i));
		}
		return nearbyPlaceList;
	}

	public List<Place> getNearbyPlaceList()
	{
		return nearbyPlaceList;
	}

	public void setNearbyPlaceList(List<Place> nearbyPlaceList)
	{
		this.nearbyPlaceList.addAll(nearbyPlaceList);
	}
	
	
	public void clearNearbyList()
	{
		nearbyPlaceList.clear();
	}

	public List<Place> getPlaceLists()
	{
		return placeLists;
	}

	public void setPlaceLists(List<Place> placeLists)
	{
		this.placeLists = placeLists;
	}
	
	
}
