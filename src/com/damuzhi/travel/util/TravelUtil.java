package com.damuzhi.travel.util;

import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.PrivateCredentialPermission;

import android.R.style;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverviewType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTipType;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;

public class TravelUtil
{

	private static final ComparatorRank comparatorRank = new ComparatorRank();
	private static final String TAG = "TravelUtil";
	
	
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
		private double longitude;
		private double latitude;
		
		public ComparatorDistance(HashMap<String, Double> location)
		{
			super();
			if(location!=null&&location.size()>0)
			{
				this.location = location;
				this.longitude = location.get(ConstantField.LONGITUDE);
				this.latitude = location.get(ConstantField.LATITUDE);
			}
			
		}
		
		public ComparatorDistance(double longitude,double latitude)
		{
			super();
			if(location!=null&&location.size()>0)
			{
				this.longitude = longitude;
				this.latitude = latitude;
			}
			
		}

		@Override
		public int compare(Place lhs, Place rhs)
		{
			if(longitude!=0&&latitude!=0)
			{
				double lhsDistance = LocationUtil.GetDistance(longitude,latitude, lhs.getLongitude(),lhs.getLatitude());
				double rhsDistance = LocationUtil.GetDistance(longitude,latitude, rhs.getLongitude(),rhs.getLatitude());
				int flag = Double.valueOf(lhsDistance).compareTo(Double.valueOf(rhsDistance));
				return flag;
			}else {
				return 1;
			}
			
		}
	}

	
	public static String getDistance(double longitude, double latitude)
	{
		String distanceStr = "";
		double locationLonggitude = 0;
		double locationLatitude = 0;
		if(TravelApplication.getInstance().getLocation().size() >0)
		{
			HashMap<String, Double> location = TravelApplication.getInstance().getLocation();			
			locationLonggitude = location.get(ConstantField.LONGITUDE);
			locationLatitude = location.get(ConstantField.LATITUDE);
			int distance = (int) LocationUtil.GetDistance(longitude, latitude,
					locationLonggitude, locationLatitude);
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
	
	
	public static String getDistance(double targetLongitude, double targetLatitude,double longitude, double latitude)
	{
		String distanceStr = "";		
			int distance = (int) LocationUtil.GetDistance(longitude, latitude,
					targetLongitude, targetLatitude);
			if (distance > 1000)
			{
				distanceStr = distance / 1000 + "km";
			} else
			{
				distanceStr = distance + "m";
			}
		return distanceStr;
	}

	
	public static String getPriceStr(String price,String symbol)
	{
		// TODO Auto-generated method stub
		String priceStr = "";
		if(price.equals("")||price==null)
		{
			return priceStr;
		}
		if(price.equals("0"))
		{
			priceStr = "免费";
		}else {
			priceStr = symbol+price;
		}
		return priceStr;
	}
	
	public static int  getPlaceCategoryImage(int placeCategory)
	{
		switch (placeCategory)
		{
		case PlaceCategoryType.PLACE_SPOT_VALUE:
			return  R.drawable.jd;
		case PlaceCategoryType.PLACE_HOTEL_VALUE:
			return R.drawable.ht;
		case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
			return R.drawable.cg;
		case PlaceCategoryType.PLACE_SHOPPING_VALUE:
			return R.drawable.gw;
		case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
			return R.drawable.yl;
		}
		return 0;
	}

	
	public static CharSequence getDataSize(int dataSize)
	{
		float size = dataSize/(1024f*1024f) ;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String dataSizeStr = decimalFormat.format(size)+"M";
		return dataSizeStr;
	}

	
	public static boolean checkHelpIsNeedUpdate(String localDataPath, float httpVersion)
	{
		Float localVersion = 0f;
		boolean result = false;
		if(localDataPath!= null&&FileUtil.checkFileIsExits(localDataPath))
		{
			String localVersionStr = AppManager.getInstance().getLocalHelpVersion();
			localVersion = Float.valueOf(localVersionStr);
		}	
		if(localVersion<httpVersion)
		{
			result = true;
		}
		return result;
	}

	
	public static String getTravelTipsType(int travelTipType)
	{
		if(travelTipType == TravelTipType.GUIDE_VALUE)
		{
			return ConstantField.TRAVEL_GUIDE_LIST;
		}else if (travelTipType == TravelTipType.ROUTE_VALUE) {
			return ConstantField.TRAVEL_ROUTE_LIST;
		}else {
			return null;
		}
		
	}

	
	public static String getOverviewType(int overviewType)
	{
		switch (overviewType)
		{
		case CommonOverviewType.CITY_BASIC_VALUE:
			return ConstantField.CITY_BASE;
		case CommonOverviewType.TRAVEL_PREPRATION_VALUE:
			return ConstantField.TRAVEL_PREPRATION;
		case CommonOverviewType.TRAVEL_UTILITY_VALUE:
			return ConstantField.TRAVEL_UTILITY;
		case CommonOverviewType.TRAVEL_TRANSPORTATION_VALUE:
			return ConstantField.TRAVEL_TRANSPORTAION;
		}
		return null;
	}

	
	public static boolean isEmail(String strEmail) { 
	    String strPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(strEmail);
	    return m.matches();
	}
	
	public static boolean isNumber(String strNumber) { 
	    String strPattern = "^[0-9]*$";
	    Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(strNumber);
	    return m.matches();
	}
	
	public static float getVersionName(Context context) {
       PackageManager packageManager = context.getPackageManager();
       PackageInfo packInfo;
	try
	{
		packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
		float version = Float.parseFloat(packInfo.versionName);          
        return version;
	} catch (Exception e)
	{
		Log.e(TAG, "<getVersionName> but catch exception :"+e.toString(),e);
		return 0f;
	}
       
   }

	
	public static int checkImageSouce(String url)
	{
		if(url.contains("http://"))
		{
			return ConstantField.DATA_HTTP;
		}else {
			return ConstantField.DATA_LOCAL;
		}
		
	}
	
	
}
