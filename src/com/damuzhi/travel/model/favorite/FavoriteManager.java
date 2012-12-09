/**  
        * @title CollectManger.java  
        * @package com.damuzhi.travel.model.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-2 上午10:23:56  
        * @version V1.0  
 */
package com.damuzhi.travel.model.favorite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRoute;
import com.damuzhi.travel.protos.TouristRouteProtos.LocalRouteList;
import com.damuzhi.travel.util.FileUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-2 上午10:23:56  
 */

public class FavoriteManager
{

	
	private static final String TAG = "FavoriteManager";

	public boolean addFavoritePlace(Place place)
	{
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }
        
		FileOutputStream output = null;
		try
		{
			if (place != null){
				//File favoriteFile = new File(ConstantField.FAVORITE_FILE_PATH);
				Map<Integer, Integer> favoritePlaceMap = TravelApplication.getInstance().getFavoritePlaceMap();
				favoritePlaceMap.put(place.getPlaceId(), place.getCityId());
				output = new FileOutputStream(ConstantField.FAVORITE_PLACE_FILE_PATH,true);
				PlaceList.Builder placeBuilder = PlaceList.newBuilder();
				placeBuilder.addList(place);
				placeBuilder.build().writeTo(output);		
				result = true;
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<addFavoritePlace> catch exception = "+e.toString(), e);
			result = false;
		}		
		try
		{
			output.close();
		} catch (IOException e)
		{
		}	
		return result;
		
	}

	
	/*public boolean checkPlaceIsCollected(int placeId)
	{
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_PLACE_FILE_PATH))
		{
			Log.d(TAG, "load favorite place data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH+ " but file not found");
			return false;
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_PLACE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			//Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_FILE_PATH);
			PlaceList favoritePlaceList = PlaceList.parseFrom(inputStream);
			for(Place place:favoritePlaceList.getListList())
			{
				if(place.getPlaceId() == placeId)
				{
					return true;
				}
			}
			return false;
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite place data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return false;
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}*/


	
	public Map<Integer, Integer> getFavoritePlace()
	{
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_PLACE_FILE_PATH))
		{
			Log.d(TAG, "load favorite place data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH+ " but file not found");
			return Collections.emptyMap();
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_PLACE_FILE_PATH);
		FileInputStream inputStream = null;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			//Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_FILE_PATH);
			PlaceList favoritePlaceList = PlaceList.parseFrom(inputStream);
			for(Place place:favoritePlaceList.getListList())
			{	
				map.put(place.getPlaceId(), place.getCityId());
			}
			return map;
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite place data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return Collections.emptyMap();
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}
	
	public List<Place> getMyFavoritePlace(int cityId)
	{
		List<Place> list = null;
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_PLACE_FILE_PATH))
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
					+ " but file not found");
			return Collections.emptyList();
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_PLACE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH);
			if(inputStream != null)
			{
				PlaceList favoritePlaceList = PlaceList.parseFrom(inputStream);
				if(favoritePlaceList!=null &&favoritePlaceList.getListCount()>0)
				{
					list = new ArrayList<Place>();
					for(Place place :favoritePlaceList.getListList())
					{
						if(place.getCityId() == cityId)
						{
							list.add(place);
						}
					}
					return list;
				}
			}
			
			return Collections.emptyList();
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return Collections.emptyList();
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}

	
	public List<Place> getMyFavoritePlace(int cityId,int placeCategoryId)
	{
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_PLACE_FILE_PATH))
		{
			Log.e(TAG, "load favorite place data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
					+ " but file not found");
			return Collections.emptyList();
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_PLACE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH);
			if(inputStream != null)
			{
				List<Place> list = new ArrayList<Place>();
				PlaceList favoritePlaceList = PlaceList.parseFrom(inputStream);
				if(favoritePlaceList!=null &&favoritePlaceList.getListCount()>0)
				{
					for(Place place:favoritePlaceList.getListList())
					{
						if(place.getCityId() == cityId && place.getCategoryId() == placeCategoryId)
						{
							list.add(place);
						}
					}
					return list;
				}
			}
			
			return Collections.emptyList();
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return Collections.emptyList();
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}


	
	public boolean deleteFavoritePlace(int placeId)
	{
		boolean result = false;
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_PLACE_FILE_PATH))
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
					+ " but file not found");
			return false;
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_PLACE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH);
			PlaceList favoritePlaceList = PlaceList.parseFrom(inputStream);
			if(favoritePlaceList.getListCount()>0)
			{
				List<Place> list = new ArrayList<Place>();
				list.addAll(favoritePlaceList.getListList());
				int i = 0;
				for(Place place:list)
				{
					if(place.getPlaceId() == placeId)
					{
						list.remove(i);
						break;
					}
					i++;
				}
				FileOutputStream output = null;			
				output = new FileOutputStream(ConstantField.FAVORITE_PLACE_FILE_PATH);
				PlaceList.Builder placeBuilder = PlaceList.newBuilder();
				placeBuilder.addAllList(list);
				placeBuilder.build().writeTo(output);		
				result = true;
			}
			
			return result;
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return false;
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}


	
	public boolean addFavoriteRoute(LocalRoute localRoute)
	{
		boolean result = false;
		File tempFile = new File(ConstantField.APP_DATA_PATH);       
        if (!tempFile.exists())
        {
          tempFile.mkdirs();
        }
        
		FileOutputStream output = null;
		result = checkLocalRouteIsFollow(localRoute.getRouteId());
		if(result)
		{
			return !result;
		}
		try
		{
			
			if (localRoute != null){
				//File favoriteFile = new File(ConstantField.FAVORITE_FILE_PATH);
				output = new FileOutputStream(ConstantField.FAVORITE_ROUTE_FILE_PATH,true);
				LocalRouteList.Builder localRouteListBuilder = LocalRouteList.newBuilder();
				localRouteListBuilder.addRoutes(localRoute);
				localRouteListBuilder.build().writeTo(output);		
				result = true;
				Log.d(TAG, "add favorite route success");
			}
		} catch (Exception e)
		{
			Log.e(TAG, "<addFavoriteRoute> catch exception = "+e.toString(), e);
			result = false;
		}		
		try
		{
			output.close();
		} catch (IOException e)
		{
		}	
		return result;
	}
	
	
	public List<LocalRoute> getMyFavoriteRoute()
	{
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_ROUTE_FILE_PATH))
		{
			Log.e(TAG, "load favorite route data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH
					+ " but file not found");
			return Collections.emptyList();
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_ROUTE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			Log.i(TAG, "load favorite route data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH);
			if(inputStream != null)
			{
				LocalRouteList localRouteList = LocalRouteList.parseFrom(inputStream);
				return localRouteList.getRoutesList();
			}
			return Collections.emptyList();
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite route data from file = " + ConstantField.FAVORITE_PLACE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return Collections.emptyList();
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}
	
	
	
	public boolean deleteFavoriteRoute(int routeId)
	{
		boolean result = false;
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_ROUTE_FILE_PATH))
		{
			Log.e(TAG, "delete one favorite route data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH
					+ " but file not found");
			return false;
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_ROUTE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH);
			LocalRouteList localRouteList = LocalRouteList.parseFrom(inputStream);
			if(localRouteList.getRoutesCount()>0)
			{
				List<LocalRoute> list = new ArrayList<LocalRoute>();
				list.addAll(localRouteList.getRoutesList());
				int i = 0;
				for(LocalRoute localRoute:list)
				{
					if(localRoute.getRouteId() == routeId)
					{
						list.remove(i);
						break;
					}
					i++;
				}
				FileOutputStream output = null;			
				output = new FileOutputStream(ConstantField.FAVORITE_ROUTE_FILE_PATH);
				LocalRouteList.Builder locaBuilder = LocalRouteList.newBuilder();
				locaBuilder.addAllRoutes(list);
				locaBuilder.build().writeTo(output);		
				result = true;
			}
			
			return result;
		} catch (Exception e)
		{
			Log.e(TAG, "delete one  favorite rotue data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return false;
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}


	
	public boolean clearFavoriteRoute()
	{
		boolean result = false;
		if(FileUtil.checkFileIsExits(ConstantField.FAVORITE_ROUTE_FILE_PATH))
		{
			Log.e(TAG, "clear favorite data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH);
			result = FileUtil.deleteFile(ConstantField.FAVORITE_ROUTE_FILE_PATH);
			
		}
		return result;
	}


	
	public boolean checkLocalRouteIsFollow(int routeId)
	{
		if(!FileUtil.checkFileIsExits(ConstantField.FAVORITE_ROUTE_FILE_PATH))
		{
			Log.d(TAG, "load favorite route data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH+ " but file not found");
			return false;
			
		}
		File favoriteFile = new File(ConstantField.FAVORITE_ROUTE_FILE_PATH);
		FileInputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(favoriteFile);
			//Log.i(TAG, "load favorite data from file = " + ConstantField.FAVORITE_FILE_PATH);
			LocalRouteList localRouteList = LocalRouteList.parseFrom(inputStream);
			for(LocalRoute localRoute:localRouteList.getRoutesList())
			{
				if(localRoute.getRouteId() == routeId)
				{
					return true;
				}
			}
			return false;
		} catch (Exception e)
		{
			Log.e(TAG, "load favorite route data from file = " + ConstantField.FAVORITE_ROUTE_FILE_PATH
				+ " but catch exception = " + e.toString(), e);
			return false;
		}
		finally
		{
			try
			{
				inputStream.close();
			} catch (Exception e)
			{
			}
		}
	}
	
	
}
