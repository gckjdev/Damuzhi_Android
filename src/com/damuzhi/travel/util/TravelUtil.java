package com.damuzhi.travel.util;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverviewType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TravelTipsProtos.TravelTipType;

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
		case 8:
			icon = R.drawable.ico11;
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
				double distan = TravelUtil.GetDistance(
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
					double distan = TravelUtil.GetDistance(
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
				double lhsDistance = TravelUtil.GetDistance(longitude,latitude, lhs.getLongitude(),lhs.getLatitude());
				double rhsDistance = TravelUtil.GetDistance(longitude,latitude, rhs.getLongitude(),rhs.getLatitude());
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
		HashMap<String, Double> location = TravelApplication.getInstance().getLocation();	
		if(location!=null&&location.size() >0)
		{					
			locationLonggitude = location.get(ConstantField.LONGITUDE);
			locationLatitude = location.get(ConstantField.LATITUDE);
			int distance = (int) TravelUtil.GetDistance(longitude, latitude,
					locationLonggitude, locationLatitude);
			if (distance > 1000)
			{
				distance = distance / 1000;
				if(distance >100)
				{
					distanceStr = ">100km";					
				}else {
					distanceStr =  distance+ "km";
				}
				return distanceStr;
				
			} else
			{
				distanceStr = distance + "m";
				return distanceStr;
			}
			
		}
		return distanceStr;
	}
	
	
	public static String getDistance(double targetLongitude, double targetLatitude,double longitude, double latitude)
	{
		String distanceStr = "";		
			int distance = (int) TravelUtil.GetDistance(longitude, latitude,targetLongitude, targetLatitude);
			if (distance > 1000)
			{
				distance = distance / 1000;
				if(distance >100)
				{
					distanceStr = ">100km";					
				}else {
					distanceStr =  distance+ "km";
				}
				return distanceStr;
				
			} else
			{
				distanceStr = distance + "m";
				return distanceStr;
			}
	}

	
	public static String getPriceStr(String price,String symbol)
	{
		if(symbol == null ||symbol.equals(""))
		{
			symbol = "￥";
		}
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
		if(strEmail == null||strEmail.equals(""))
		{
			return false;
		}
	    String strPattern = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(strEmail);
	    return m.matches();
	}
	
	public static boolean isNumber(String strNumber) { 
		if(strNumber == null||strNumber.equals(""))
		{
			return false;
		}
	    String strPattern = "^1[0-9]{10}$";
	    Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(strNumber);
	    return m.matches();
	}
	
	public static boolean isPhoneNumber(String phoneNum)
	{
		if(phoneNum == null||phoneNum.equals(""))
		{
			return false;
		}
		String strPattern = "^0{0,1}(13[0-9]|15[0-9]|15[0-2]|18[0-9])[0-9]{8}$";
		Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(phoneNum);
	    return m.matches();
	}
	
	
	public static boolean isShort(String password) {
		if(password == null||password.equals(""))
		{
			Log.d(TAG, "password is null");
			return false;
		}
		String strPattern = "^[0-9A-Za-z]{6,}$";
		Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(password);
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
	
	
	public static String getCityDataPath(int cityId)
	{
		//int cityId = AppManager.getInstance().getCurrentCityId();
		String dataPath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH,cityId);
		return dataPath;
	}

	public static String getCityDataPath()
	{
		int cityId = AppManager.getInstance().getCurrentCityId();
		String dataPath = String.format(ConstantField.DOWNLOAD_CITY_DATA_PATH,cityId);
		return dataPath;
	}
	
	public static String getHtmlUrl(String url)
	{
		String htmlUrl = "";
		if(!url.contains("http://"))
		{
			htmlUrl = "file:///"+getCityDataPath()+url;
		}else
		{
			htmlUrl = url;
		}
		return htmlUrl;
	}
	
	
	
	private static final double EARTH_RADIUS = 6378137;

    private static double rad(double d)
    {
       return d * Math.PI / 180.0;
    }
    
   
    public static double GetDistance(double lng1, double lat1, double lng2, double lat2)
    {
       double radLat1 = rad(lat1);
       double radLat2 = rad(lat2);
       double a = radLat1 - radLat2;
       double b = rad(lng1) - rad(lng2);
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
       s = s * EARTH_RADIUS;
       s = Math.round(s * 10000) / 10000;
       return s;
    }

	
	public static String handlerString(String introduction)
	{
		String newString = introduction.replace("\n", "\n\t\t");
		return newString;
	}

	
	public static String getDownloadFileName(String downloadURL)
	{
		int index = downloadURL.lastIndexOf("/")+1;
		String fileName = downloadURL.substring(index);
		return fileName;
	}

	
	public static String getImageUrl(int cityId, String url)
	{
		String htmlUrl = "";
		if(!url.contains("http://"))
		{
			htmlUrl = "file:///"+getCityDataPath(cityId)+url;
		}else
		{
			htmlUrl = url;
		}
		return htmlUrl;
	}

	public static String getRouteDays(int days) {
		String routeDays = String.format(ConstantField.ROUTE_DAYS, days);
		return routeDays;
	}

	public static String getDepartTime(long time) {
		Calendar date = Calendar.getInstance();
		time = time*1000;
		long targetTime = time - TimeZone.getDefault().getRawOffset();	
		date.setTimeInMillis(targetTime);
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy年MM月dd日 E");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}
	
	public static String getDateShortString(long time ) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy年MM月dd日");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}
	
	public static String getDateLongString(long time ) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMMdd");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}
	
	/*public static String getDateShortString(long time ) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMMdd ");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}*/
	
	public static String getDate(long time) {
		Calendar date = Calendar.getInstance();
		time = time*1000;
		long targetTime = time - TimeZone.getDefault().getRawOffset();
		date.setTimeInMillis(targetTime);
		SimpleDateFormat dateformat=new SimpleDateFormat("MM月dd日");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}
	
	
	public static String getDateString(long time) {
		Calendar date = Calendar.getInstance();
		time = time*1000;
		long targetTime = time - TimeZone.getDefault().getRawOffset();
		date.setTimeInMillis(targetTime);
		
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}

	public static String getBookingDate(long time) {
		Calendar date = Calendar.getInstance();
		time = time*1000;
		long targetTime = time - TimeZone.getDefault().getRawOffset();	
		date.setTimeInMillis(targetTime);
		SimpleDateFormat dateformat=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
		String dateString =dateformat.format(date.getTime());
		return dateString;
	}

	
	public static String getOrderStatus(int orderStatus)
	{
		String status = "";
		switch (orderStatus) {
        case 1:
             status = "意向订单";
             return status;            
        case 2:
        	 status ="处理中";
        	 return status;  
            
        case 3:
        	 status ="待支付";
        	 return status;  
            
        case 4:
        	 status ="已支付";
        	 return status;  
            
        case 5:
        	 status ="已完成";
        	 return status;  
            
        case 6:
        	status ="取消";
        	return status;          
        default:
        	return status;  
		}
	}

	
	public static String StringFilter(String str){
	    str=str.replaceAll("【","[").replaceAll("】","]").replaceAll("！","!").replaceAll("，", ",").replaceAll("。", ".").replaceAll("、", ",");//替换中文标号
	    String regEx="[『』]"; // 清除掉特殊字符
	    Pattern p = Pattern.compile(regEx);
	    Matcher m = p.matcher(str);
	    return m.replaceAll("").trim();
	}

	
	public static JSONObject[] getJsonArray(String jsonString)
	{
		if(jsonString == null||jsonString.equals(""))
		{
			return null;
		}
		try
		{
			jsonString = jsonString.replace("[", "");
			jsonString = jsonString.replace("]", "");
			jsonString = jsonString.replace("},", "}?");
			String[] data = jsonString.split("\\?");
			JSONObject [] result = new JSONObject[data.length];
			for(int i=0;i<data.length;i++)
			{
					result[i] = new JSONObject(data[i]);			
			}
			return result;
		} catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static String getDayOfWeekString(int dayOfWeek)
	{
		switch (dayOfWeek)
		{
		case 1:
			return "星期天";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		}
		return "";
	}
	
	
}
