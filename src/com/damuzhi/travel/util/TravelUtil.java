package com.damuzhi.travel.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import android.R.style;
import android.content.Context;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;

public class TravelUtil
{

	public static int getForecastImage(int categoryId)
	{
		int icon = 0;
		switch (categoryId)
		{
		case 1:
			icon = R.drawable.pin_jd;
			break;
		case 2:
			icon = R.drawable.pin_ht;
			break;
		case 3:
			icon = R.drawable.pin_cg;
			break;
		case 4:
			icon = R.drawable.pin_gw;
			break;
		case 5:
			icon = R.drawable.pin_yl;
			break;
		default:
			icon = R.drawable.my_point;
			break;
		}
		return icon;
	}

	public static int getServiceImage(int providedServiceId)
	{
		int icon = 0;
		switch (providedServiceId)
		{
		case 3:
			icon = R.drawable.ico9;
			break;
		case 4:
			icon = R.drawable.ico14;
			break;
		case 5:
			icon = R.drawable.ico8;
			break;
		case 9:
			icon = R.drawable.ico10;
			break;
		case 11:
			icon = R.drawable.ico12;
			break;
		case 12:
			icon = R.drawable.ico1;
			break;
		case 13:
			icon = R.drawable.ico13;
			break;
		case 14:
			icon = R.drawable.ico8;
			break;
		case 87:
			icon = R.drawable.ico5;
			break;
		case 88:
			icon = R.drawable.ico5;
			break;
		case 89:
			icon = R.drawable.ico5;
			break;
		case 90:
			icon = R.drawable.ico5;
			break;
		default:
			icon = R.drawable.ico5;
			break;
		}
		return icon;
	}

	public static String getHotelStar(Context context, int hotelStar)
	{
		String hotelStart = "";
		switch (hotelStar)
		{
		case 3:
			hotelStart = context.getString(R.string.start3);
			break;
		case 4:
			hotelStart = context.getString(R.string.start4);
			break;
		case 5:
			hotelStart = context.getString(R.string.start5);
			break;
		case 6:
			hotelStart = context.getString(R.string.start6);
			break;
		case 7:
			hotelStart = context.getString(R.string.start7);
			break;
		default:

			break;
		}
		return hotelStart;
	}

	public static ArrayList<Place> service(int providedServiceID,
			ArrayList<Place> placeList)
	{
		if (providedServiceID == -1)
		{
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			return placeList;
		} else
		{
			ArrayList<Place> list = new ArrayList<Place>();
			for (Place place : placeList)
			{
				for (int ID : place.getProvidedServiceIdList())
				{
					if (ID == providedServiceID)
					{
						list.add(place);
					}
				}
			}
			return list;
		}

	}

	public static ArrayList<Place> price(int priceRank,
			ArrayList<Place> placeList)
	{
		if (priceRank == 0)
		{
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			return placeList;
		} else
		{
			ArrayList<Place> list = new ArrayList<Place>();
			for (Place place : placeList)
			{
				if (place.getPriceRank() == priceRank)
				{
					list.add(place);
				}
			}

			return list;
		}

	}

	public static ArrayList<Place> area(int areaID, ArrayList<Place> placeList)
	{
		if (areaID == -1)
		{
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			return placeList;
		} else
		{
			ArrayList<Place> list = new ArrayList<Place>();
			for (Place place : placeList)
			{
				if (areaID == place.getAreaId())
				{
					list.add(place);
				}
			}
			return list;
		}

	}

	public static ArrayList<Place> sort(int subCategoryID,
			ArrayList<Place> placeList)
	{

		if (subCategoryID == -1)
		{
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			return placeList;
		} else
		{

			ArrayList<Place> list = new ArrayList<Place>();
			for (Place place : placeList)
			{
				if (subCategoryID == place.getSubCategoryId())
				{
					list.add(place);
				}
			}
			return list;
		}

	}

	public static ArrayList<Place> sortCategory(int categoryID,
			ArrayList<Place> placeList)
	{
		if (categoryID == -1)
		{
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			return placeList;
		} else
		{
			ArrayList<Place> list = new ArrayList<Place>();
			for (Place place : placeList)
			{
				if (categoryID == place.getCategoryId())
				{
					list.add(place);
				}
			}
			return list;
		}
	}

	public static ArrayList<Place> placeComposite(int compositeType,
			ArrayList<Place> placeList, HashMap<String, Double> location)
	{
		switch (compositeType)
		{
		case 0:
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			break;
		case 1:
			ComparatorDistance comparatorDistance = new ComparatorDistance(
					location);
			Collections.sort(placeList, comparatorDistance);
			break;
		case 2:
			ComparatorPrice comparatorPrice = new ComparatorPrice();
			Collections.sort(placeList, comparatorPrice);
			break;
		default:
			break;
		}
		return placeList;
	}

	public static ArrayList<Place> getPlaceInDistance(int distance,
			ArrayList<Place> placeList, HashMap<String, Double> location,
			int placeCategoryID)
	{
		ArrayList<Place> places = new ArrayList<Place>();
		for (Place place : placeList)
		{
			if (placeCategoryID == ConstantField.ALL_PLACE_CATEGORY_ID)
			{
				double distan = LocationUtil.GetDistance(
						location.get(ConstantField.LONGITUDE),
						location.get(ConstantField.LATITUDE),
						place.getLongitude(), place.getLatitude());
				if (distan < distance)
				{
					places.add(place);
				}
			} else
			{
				if (place.getCategoryId() == placeCategoryID)
				{
					double distan = LocationUtil.GetDistance(
							location.get(ConstantField.LONGITUDE),
							location.get(ConstantField.LATITUDE),
							place.getLongitude(), place.getLatitude());
					if (distan < distance)
					{
						places.add(place);
					}
				}
			}

		}
		return places;
	}

	public static ArrayList<Place> hotelComposite(int compositeType,
			ArrayList<Place> placeList, HashMap<String, Double> location)
	{
		switch (compositeType)
		{
		case 0:
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			break;
		case 1:
			ComparatorStartRank comparatorStartRank = new ComparatorStartRank();
			Collections.sort(placeList, comparatorStartRank);
			break;
		case 2:
			ComparatorPrice comparatorPrice = new ComparatorPrice();
			Collections.sort(placeList, comparatorPrice);
			break;
		case 3:
			ComparatorPriceContrary comparatorPriceContrary = new ComparatorPriceContrary();
			Collections.sort(placeList, comparatorPriceContrary);
			break;
		case 4:
			ComparatorDistance comparatorDistance = new ComparatorDistance(
					location);
			Collections.sort(placeList, comparatorDistance);
			break;

		default:
			break;
		}
		return placeList;
	}

	public static ArrayList<Place> restaurantComposite(int compositeType,
			ArrayList<Place> placeList, HashMap<String, Double> location)
	{
		switch (compositeType)
		{
		case 0:
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			break;
		case 1:
			ComparatorPrice comparatorPrice = new ComparatorPrice();
			Collections.sort(placeList, comparatorPrice);
			break;
		case 2:
			ComparatorPriceContrary comparatorPriceContrary = new ComparatorPriceContrary();
			Collections.sort(placeList, comparatorPriceContrary);
			break;
		case 3:
			ComparatorDistance comparatorDistance = new ComparatorDistance(
					location);
			Collections.sort(placeList, comparatorDistance);
			break;

		default:
			break;
		}
		return placeList;
	}

	public static ArrayList<Place> shoppingComposite(int compositeType,
			ArrayList<Place> placeList, HashMap<String, Double> location)
	{
		switch (compositeType)
		{
		case 0:
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			break;
		case 1:
			ComparatorDistance comparatorDistance = new ComparatorDistance(
					location);
			Collections.sort(placeList, comparatorDistance);
			break;

		default:
			break;
		}
		return placeList;
	}

	public static ArrayList<Place> entertainmentComposite(int compositeType,
			ArrayList<Place> placeList, HashMap<String, Double> location)
	{
		switch (compositeType)
		{
		case 0:
			ComparatorRank comparatorRank = new ComparatorRank();
			Collections.sort(placeList, comparatorRank);
			break;
		case 1:
			ComparatorPrice comparatorPrice = new ComparatorPrice();
			Collections.sort(placeList, comparatorPrice);
			break;
		case 2:
			ComparatorPriceContrary comparatorPriceContrary = new ComparatorPriceContrary();
			Collections.sort(placeList, comparatorPriceContrary);
			break;
		case 3:
			ComparatorDistance comparatorDistance = new ComparatorDistance(
					location);
			Collections.sort(placeList, comparatorDistance);
			break;

		default:
			break;
		}
		return placeList;
	}

	private static final ComparatorRank comparatorRank = new ComparatorRank();

	public static ComparatorRank getComparatorRank()
	{
		return comparatorRank;
	}

	

	public static class ComparatorRank implements Comparator<Place>
	{

		@Override
		public int compare(Place lhs, Place rhs)
		{
			int flag = -Integer.valueOf(lhs.getRank()).compareTo(
					Integer.valueOf(rhs.getRank()));
			return flag;
		}

	}

	
	public static class ComparatorPrice implements Comparator<Place>
	{

		@Override
		public int compare(Place lhs, Place rhs)
		{
			// TODO Auto-generated method stub
			int flag = Integer.valueOf(lhs.getPrice()).compareTo(
					Integer.valueOf(rhs.getPrice()));
			return -flag;
		}
	}

	
	public static class ComparatorPriceContrary implements Comparator<Place>
	{

		@Override
		public int compare(Place lhs, Place rhs)
		{
			// TODO Auto-generated method stub
			int flag = Integer.valueOf(lhs.getPrice()).compareTo(
					Integer.valueOf(rhs.getPrice()));
			return flag;
		}
	}

	public static class ComparatorStartRank implements Comparator<Place>
	{

		@Override
		public int compare(Place lhs, Place rhs)
		{
			// TODO Auto-generated method stub
			int flag = Integer.valueOf(lhs.getHotelStar()).compareTo(
					Integer.valueOf(rhs.getHotelStar()));
			return -flag;
		}
	}

	
	public static class ComparatorDistance implements Comparator<Place>
	{
		private HashMap<String, Double> location;

		public ComparatorDistance(HashMap<String, Double> location)
		{
			super();
			this.location = location;
		}

		@Override
		public int compare(Place lhs, Place rhs)
		{
			// TODO Auto-generated method stub
			double lhsDistance = LocationUtil.GetDistance(
					location.get(ConstantField.LONGITUDE),
					location.get(ConstantField.LATITUDE), lhs.getLongitude(),
					lhs.getLatitude());
			double rhsDistance = LocationUtil.GetDistance(
					location.get(ConstantField.LONGITUDE),
					location.get(ConstantField.LATITUDE), rhs.getLongitude(),
					rhs.getLatitude());
			int flag = Double.valueOf(lhsDistance).compareTo(
					Double.valueOf(rhsDistance));
			return flag;
		}
	}

	
	public static String getDistance(double longitude, double latitude)
	{
		// TODO Auto-generated method stub
		String distanceStr = "";
		double locationLonggitude = 0;
		double locationLatitude = 0;
		HashMap<String, Double> location = TravelApplication.getInstance()
				.getLocation();
		if (location.size() > 0 || location != null)
		{
			locationLonggitude = location.get(ConstantField.LONGITUDE);
			locationLatitude = location.get(ConstantField.LATITUDE);
			int distance = (int) LocationUtil.GetDistance(longitude, latitude,
					longitude, latitude);
			if (distance > 1000)
			{
				distanceStr = distance / 1000 + "km";
			} else
			{
				distanceStr = distance + "m";
			}
		}

		return distanceStr;
	}

	/**  
	        * @param price
	        * @return  
	        * @description   
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-26 下午3:21:49  
	*/
	public static String getPriceStr(String price,String symbol)
	{
		// TODO Auto-generated method stub
		String priceStr = "";
		if(price.equals("0"))
		{
			priceStr = "免费";
		}else {
			priceStr = symbol+price;
		}
		return priceStr;
	}

}
