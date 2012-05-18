package com.damuzhi.travel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.CommendPlaceMap;
import com.damuzhi.travel.activity.common.PlaceActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.place.SceneryActivity;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.model.place.PlaceManager;
import com.damuzhi.travel.protos.AppProtos.City;
import com.damuzhi.travel.protos.AppProtos.CityArea;
import com.damuzhi.travel.protos.AppProtos.NameIdPair;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.LocationUtil;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**  
        * @description   
        * @version 1.0  
        * @author liuxiaokun  
        * @update 2012-5-11 下午5:25:22  
        */  
public class MainService extends Service implements Runnable
{
	private static final String TAG = "MainService";
	private static HashMap<PlaceCategoryType,List<NameIdPair>> subCatMap;
	private static HashMap<PlaceCategoryType,List<NameIdPair>> proSerMap;
	private HashMap<Integer,String> symbolMap;
	private static HashMap<Integer,List<CityArea>> cityAreaList;
	private static TravelApplication application;
	private Object object;
	//public static  String dataPath ="";
	
	public static ArrayList<Activity> allActivity = new ArrayList<Activity>();
	// 将所有任务放到任务集合中
	public static ArrayList<Task> allTask = new ArrayList<Task>();

	// 遍历所有activity 根据名称在 allActivity 中找到需要的activity
	public static Activity getActivityByName(String name) {
		for (Activity ac : allActivity) {
			if (ac.getClass().getName().indexOf(name) >= 0) {
				return ac;
			}
		}
		return null;
	}

	// 将当前的任务加到任务集合中
	public static void newTask(Task task) {
		allTask.add(task);
	}

	public boolean isrun = true;// 线程开关
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			DataService placeDataService = new DataService(application);	
			HashMap<Integer, String> cityAreaMap = MainService.getCityAreaMap(application.getCityID());
			HashMap<Integer, String> subCatNameMap;
			String[] subCatNameList ;
			int[] subCatKeyList ;
			String [] proSerName ;
			int [] proSerKey ;
			String [] cityAreasName ;
			int [] cityAreasKey ;
			HashMap<Integer, String> proServiceMap ;
			switch (msg.what) {
			case Task.TASK_LOGIN_SCENERY:// 
				subCatNameList = MainService.getSubCatNameList(PlaceCategoryType.PLACE_SPOT);
				subCatKeyList = MainService.getSubCatKeyList(PlaceCategoryType.PLACE_SPOT);
				subCatNameMap = MainService.getSubCatMap(PlaceCategoryType.PLACE_SPOT);				
				placeDataService.getPlace(ConstantField.SCENERY, application.getCityID(), ConstantField.LANG_HANS);
				PlaceActivity sceneryActivity = (PlaceActivity)object;
				sceneryActivity.refresh(symbolMap,cityAreaMap,subCatNameList,subCatKeyList,subCatNameMap);
				break;
			case Task.TASK_LOGIN_HOTEL:// 
				proSerName = MainService.getProvidedServiceNameList(PlaceCategoryType.PLACE_HOTEL);
				proSerKey = MainService.getProvidedServiceKeyList(PlaceCategoryType.PLACE_HOTEL);
				cityAreasName = MainService.getCityAreaNameList(application.getCityID());
				cityAreasKey = MainService.getCityAreaKeyList(application.getCityID());
				proServiceMap = MainService.getProServiceMap(PlaceCategoryType.PLACE_HOTEL);
				placeDataService.getPlace(ConstantField.HOTEL, application.getCityID(), ConstantField.LANG_HANS);
				PlaceActivity hotelActivity = (PlaceActivity)object;
				hotelActivity.refresh(symbolMap,cityAreaMap,cityAreasName,cityAreasKey,proSerName,proSerKey,proServiceMap);
				break;
			case Task.TASK_LOGIN_RESTAURANT:// 
				subCatNameList = MainService.getSubCatNameList(PlaceCategoryType.PLACE_RESTRAURANT);
				subCatKeyList = MainService.getSubCatKeyList(PlaceCategoryType.PLACE_RESTRAURANT);
				subCatNameMap = MainService.getSubCatMap(PlaceCategoryType.PLACE_RESTRAURANT);				
				proSerName = MainService.getProvidedServiceNameList(PlaceCategoryType.PLACE_RESTRAURANT);
				proSerKey = MainService.getProvidedServiceKeyList(PlaceCategoryType.PLACE_RESTRAURANT);
				cityAreasName = MainService.getCityAreaNameList(application.getCityID());
				cityAreasKey = MainService.getCityAreaKeyList(application.getCityID());
				proServiceMap = MainService.getProServiceMap(PlaceCategoryType.PLACE_RESTRAURANT);
				application.setSubCatNameMap(subCatNameMap);
				placeDataService.getPlace(ConstantField.RESTAURANT, application.getCityID(), ConstantField.LANG_HANS);
				PlaceActivity restaurantActivity = (PlaceActivity)object;
				restaurantActivity.refresh(symbolMap,cityAreaMap,subCatNameMap,subCatNameList,subCatKeyList,cityAreasName,cityAreasKey,proSerName,proSerKey,proServiceMap);
				break;
			case Task.TASK_LOGIN_SHOPPING:// 
				subCatNameMap = MainService.getSubCatMap(PlaceCategoryType.PLACE_SHOPPING);								
				cityAreasName = MainService.getCityAreaNameList(application.getCityID());
				cityAreasKey = MainService.getCityAreaKeyList(application.getCityID());		
				placeDataService.getPlace(ConstantField.SHOPPING, application.getCityID(), ConstantField.LANG_HANS);
				PlaceActivity shoppingActivity = (PlaceActivity)object;
				shoppingActivity.refresh(symbolMap,cityAreaMap,subCatNameMap,cityAreasName,cityAreasKey);
				break;
			case Task.TASK_LOGIN_ENTERTAINMNET:// 
				subCatNameMap = MainService.getSubCatMap(PlaceCategoryType.PLACE_ENTERTAINMENT);		
				subCatNameList = MainService.getSubCatNameList(PlaceCategoryType.PLACE_ENTERTAINMENT);
				subCatKeyList = MainService.getSubCatKeyList(PlaceCategoryType.PLACE_ENTERTAINMENT);
				cityAreasName = MainService.getCityAreaNameList(application.getCityID());
				cityAreasKey = MainService.getCityAreaKeyList(application.getCityID());		
				placeDataService.getPlace(ConstantField.ENTERTAINMENT, application.getCityID(), ConstantField.LANG_HANS);
				PlaceActivity entertainmentActivity = (PlaceActivity)object;
				entertainmentActivity.refresh(symbolMap,cityAreaMap,subCatNameMap,subCatNameList,subCatKeyList,cityAreasName,cityAreasKey);
				break;
			case Task.TASK_LOGIN_NEARBY:// 
				subCatNameMap = MainService.getAllSubCatMap();				
				placeDataService.getPlace(ConstantField.PLACE, application.getCityID(), ConstantField.LANG_HANS);
				PlaceActivity nearbyActivity = (PlaceActivity)object;
				nearbyActivity.refresh(symbolMap,cityAreaMap,subCatNameMap);
				break;
			case Task.MAP_NEARBY:// 
				ArrayList<Place> placeList = placeDataService.getAllPlaceInArea(application.getPlace(), ConstantField.DISTANCE, ConstantField.ALL_PLACE_ORDER_BY_RANK, application.getCityID(), ConstantField.LANG_HANS);
				CommendPlaceMap commendPlaceMap =  (CommendPlaceMap) object;
				commendPlaceMap.refresh(placeList);
				break;
			}

		}
	};

	
	
	private void doTask(Task task) {
		Message mess = handler.obtainMessage();
		mess.what = task.getTaskID();// 将当前任务的ID 放到Message中	
		/*switch (task.getTaskID()) {
		case Task.TASK_LOGIN_SCENERY:// 
			break;
		case Task.MAP_AROUND:
			break;
		default:
			break;	
		}*/
		handler.sendMessage(mess);// 发送当前消息
		allTask.remove(task);// 当前任务执行完毕 把任务从任务集合中remove 不然会重复执行
	}

	
	@Override
	public void run() {
		while (isrun) {
			Task lastTask = null;
			synchronized (allTask) 
			{
				if (allTask.size() > 0)
				{
					lastTask = allTask.get(0);
					Log.d(TAG, "taskID ="+lastTask.getTaskID());
					object = lastTask.getObject();
					doTask(lastTask);
				}
			}			
			try
			{
				Thread.sleep(1000);// 每隔一秒钟检查是否有任务
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
		application = TravelApplication.getInstance();
		symbolMap = application.getSymbolMap();
		cityAreaList = application.getCityAreaList();
		subCatMap = application.getSubCategoryMap();	
		proSerMap = application.getProvidedServiceMap();	
		isrun = true;// 启动线程
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.stopSelf();// 停止服务
		isrun = false;// 关闭线程
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**  
	        * @param placeCategoryType
	        * @param subCatMap
	        * @return  
	        * @description   地点分类下的所有子分类
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-11 下午5:20:21  
	        */
	private static HashMap<Integer, String> getSubCatMap(PlaceCategoryType placeCategoryType)
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
		for(NameIdPair nameIdPair:nameIdPairs)
		{
			map.put(nameIdPair.getId(), nameIdPair.getName());
		}	
		return map;
	}
	
	
	
		/**  
	     * @param placeCategoryType
	     * @param subCatMap
	     * @return  
	     * @description   地点分类下的所有子分类
	     * @version 1.0  
	     * @author liuxiaokun  
	     * @update 2012-5-11 下午5:20:21  
	     */
	private static HashMap<Integer, String> getAllSubCatMap()
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		Set<PlaceCategoryType> keyset = subCatMap.keySet();
		for(PlaceCategoryType placeCategoryType:keyset)
		{
			List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
			for(NameIdPair nameIdPair:nameIdPairs)
			{
				map.put(nameIdPair.getId(), nameIdPair.getName());
			}
		}
			
		return map;
	}
	/**  
	        * @param placeCategoryType
	        * @param subCatMap
	        * @return  
	        * @description   地点分类下的所有子分类
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-11 下午5:21:10  
	        */
	private static String[] getSubCatNameList(PlaceCategoryType placeCategoryType)
	{
		int i=1;		
		List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
		String [] subCat = new String[nameIdPairs.size()+1];
		subCat[0] = ConstantField.ALL_PLACE;
		for(NameIdPair nameIdPair:nameIdPairs)
		{
			subCat[i] = nameIdPair.getName();
			i++;
		}	
		return subCat;
	}
	
	/**  
     * @param placeCategoryType
     * @param subCatMap
     * @return  
     * @description   地点分类下的所有子分类
     * @version 1.0  
     * @author liuxiaokun  
     * @update 2012-5-11 下午5:21:10  
     */
private static int[] getSubCatKeyList(PlaceCategoryType placeCategoryType)
{
	int i=1;		
	List<NameIdPair> nameIdPairs = subCatMap.get(placeCategoryType);
	int [] subCatKey = new int[nameIdPairs.size()+1];
	subCatKey[0] = -1;
	for(NameIdPair nameIdPair:nameIdPairs)
	{
		subCatKey[i] = nameIdPair.getId();
		i++;
	}	
	return subCatKey;
}
	
		
	/**  
	        * @param placeCategoryType
	        * @param subCatMap
	        * @return  
	        * @description   地点分类下的可用的所有服务选项列表
	        * @version 1.0  
	        * @author liuxiaokun  
	        * @update 2012-5-11 下午5:25:24  
	        */
	private static String[] getProvidedServiceNameList(PlaceCategoryType placeCategoryType)
	{
		int i=1;
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		String [] proServiceName = new String[nameIdPairs.size()+1];
		proServiceName[0] = ConstantField.ALL_PLACE;
		for(NameIdPair nameIdPair:nameIdPairs)
		{
			proServiceName[i] = nameIdPair.getName();
			i++;
		}
		return proServiceName;
	}
	/**  
     * @param placeCategoryType
     * @param subCatMap
     * @return  
     * @description   地点分类下的可用的所有服务选项列表
     * @version 1.0  
     * @author liuxiaokun  
     * @update 2012-5-11 下午5:25:24  
     */
	private static int[] getProvidedServiceKeyList(PlaceCategoryType placeCategoryType)
	{
		int i=1;
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		int [] proServiceKey = new int[nameIdPairs.size()+1];
		proServiceKey[0] = -1;
		for(NameIdPair nameIdPair:nameIdPairs)
		{
			proServiceKey[i] = nameIdPair.getId();
			i++;
		}
		return proServiceKey;
	}
		
		
	private static HashMap<Integer, String> getProServiceMap(PlaceCategoryType placeCategoryType)
	{
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		List<NameIdPair> nameIdPairs = proSerMap.get(placeCategoryType);
		for(NameIdPair nameIdPair:nameIdPairs)
		{
			map.put(nameIdPair.getId(), nameIdPair.getName());
		}	
		return map;
	}
	
	
		/**  
	     * @param placeCategoryType
	     * @param subCatMap
	     * @return  
	     * @description   地点分类下的可用的所有服务选项列表
	     * @version 1.0  
	     * @author liuxiaokun  
	     * @update 2012-5-11 下午5:25:24  
	     */
	private static String[] getCityAreaNameList(int cityID)
	{
		int i=1;
		List<CityArea> ctiyAreaList = cityAreaList.get(cityID);
		String [] ctiyAreasName = new String[ctiyAreaList.size()+1];
		ctiyAreasName[0] = ConstantField.ALL_PLACE;
		for(CityArea cityArea:ctiyAreaList)
		{
			ctiyAreasName[i] = cityArea.getAreaName();
			i++;
		}
		return ctiyAreasName;
	}
	
	/**  
	 * @param placeCategoryType
	 * @param subCatMap
	 * @return  
	 * @description   地点分类下的可用的所有服务选项列表
	 * @version 1.0  
	 * @author liuxiaokun  
	 * @update 2012-5-11 下午5:25:24  
	 */
	private static int[] getCityAreaKeyList(int cityID)
	{
	int i=1;
	List<CityArea> ctiyAreaList = cityAreaList.get(cityID);
	int [] ctiyAreasKey = new int[ctiyAreaList.size()+1];
	ctiyAreasKey[0] = -1;
	for(CityArea cityArea:ctiyAreaList)
	{
		ctiyAreasKey[i] = cityArea.getAreaId();
		i++;
	}
	return ctiyAreasKey;
	}
	
	
	
	/**  
	 * @param placeCategoryType
	 * @param subCatMap
	 * @return  
	 * @description   地点分类下的可用的所有服务选项列表
	 * @version 1.0  
	 * @author liuxiaokun  
	 * @update 2012-5-11 下午5:25:24  
	 */
	private static HashMap<Integer, String> getCityAreaMap(int cityID)
	{
	 HashMap<Integer, String> map = new HashMap<Integer, String>();
	 List<CityArea> ctiyAreaList = cityAreaList.get(cityID);
	for(CityArea cityArea:ctiyAreaList)
	{
		map.put(cityArea.getAreaId(), cityArea.getAreaName());
	}
	return map;
	}

	/**
	 * 退出应用程序
	 * 
	 * @param context
	 */
	public static void exitAPP(Context context) {
		Intent it = new Intent(ConstantField.MAIN_SERVICE);
		context.stopService(it);// 停止服务		
		for (Activity activity : allActivity) {// 遍历所有activity 一个一个删除
			activity.finish();
		}
		// 杀死进程 我感觉这种方式最直接了当
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
	
	
   public static void finshall(){
		for (Activity activity : allActivity) {// 遍历所有activity 一个一个删除
			activity.finish();
		}
   }
   
	/**
	 * 网络连接异常
	 * 
	 * @param context
	 */
	public static void AlertNetError(final Context context) {
		AlertDialog.Builder alerError = new AlertDialog.Builder(context);
		alerError.setTitle(R.string.main_fetch_fail);
		alerError.setMessage(R.string.NoSignalException);
		alerError.setNegativeButton(R.string.apn_is_wrong1_exit,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						exitAPP(context);
					}
				});
		alerError.setPositiveButton(R.string.apn_is_wrong1_setnet,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						context.startActivity(new Intent(
								android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		alerError.create().show();
	}
	


}
