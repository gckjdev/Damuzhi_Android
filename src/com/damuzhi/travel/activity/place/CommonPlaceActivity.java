/**  
 * @title CommendPlaceActivity.java  
 * @package com.damuzhi.travel.activity.adapter.place  
 * @description   
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:51:48  
 * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import android.R.integer;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter.ViewHolder;
import com.damuzhi.travel.activity.adapter.common.SortAdapter;
import com.damuzhi.travel.activity.adapter.common.SortAdapter.SortViewHolder;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.PlaceGoogleMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.imageCache.AsyncLoader;
import com.damuzhi.travel.activity.common.location.LocationUtil;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.mission.more.BrowseHistoryMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.PlaceListProtos.PlaceList;

import dalvik.system.VMRuntime;
/**
 * @description
 * @version 1.0
 * @author liuxiaokun
 * @update 2012-5-24 下午3:51:48
 */

public abstract class CommonPlaceActivity extends ActivityGroup
{

	protected static final String TAG = "CommonPlaceActivity";

	abstract public List<Place> getAllPlace(Activity activity);

	abstract public String getCategoryName();
	
	abstract public int getCategoryType();
	
	abstract public int getPlaceTotalCount();

	abstract public void createFilterButtons(ViewGroup spinner);
	
	abstract public void initFilterButtonsData();

	abstract boolean isSupportSubcategory();

	abstract boolean isSupportPrice();

	abstract boolean isSupportArea();

	abstract boolean isSupportService();

	abstract Comparator<Place> getSortComparator(int index);

	//PullToRefreshListView refreshPlaceListView = null;
	ListView placeListView = null;
	ArrayList<Place> allPlaceList = new ArrayList<Place>();
	CommonPlaceListAdapter placeListAdapter = null;
	private int totalCount = 0;
	private ProgressDialog loadingDialog;
	// sort condition
	protected String[] sortDisplayName;
	private int sortPosition = 0;
	// mapview
	private ViewGroup mapviewGroup;
	private ViewGroup listViewGroup;
	private ImageView mapViewButton;
	private ImageView listViewButton;
	private TextView dataNotFoundTextView;
	private ViewGroup sortSpinner;
	protected String[] subCatName;
	protected int[] subCatKey;
	protected int[] subCatCount;
	protected int[] subCatSelectKey;
	protected String[] serviceName;
	protected int[] serviceID;
	protected int[] serviceSelect;
	protected String[] price;
	protected int[] priceId;
	protected int[] priceSelect;
	protected String[] areaName;
	protected int[] areaID;
	protected int[] areaSelect;
	private PopupWindow filterWindow;
	private int statusBarHeight;
	private FilterAdapter filterAdapter;
	private SortAdapter sortAdapter;
	private HashMap<Integer, Boolean> subCateIsSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> serviceIsSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> priceIsSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> areaIsSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> sortSelected = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> IsSelectedTemp = new HashMap<Integer, Boolean>();

	private int filterType = 0;
	private static final int subCateType = 1;
	private static final int serviceType = 2;
	private static final int areaType = 3;
	private static final int priceType = 4;
	private static int start = 0;
	private static int count = 1;
	private boolean localDataIsExist;
	private View listViewFooter;
	private ViewGroup footerViewGroup;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private boolean loadDataFlag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
		ActivityMange.getInstance().addActivity(this);
		setContentView(R.layout.common_place);
		setProgressBarVisibility(true); 
		
		if(statusBarHeight == 0)
		{
			Class<?> c = null;
			Object obj = null;
			Field field = null;
			int x = 0;
		    try
			{
				c = Class.forName("com.android.internal.R$dimen");
				obj = c.newInstance();
			    field = c.getField("status_bar_height");
			    x = Integer.parseInt(field.get(obj).toString());
			    statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	   
		

		TextView placeTitle = (TextView) findViewById(R.id.place_title);
		TextView placeSize = (TextView) findViewById(R.id.place_num);
		ViewGroup spinner = (ViewGroup) findViewById(R.id.spinner_group);
		ImageButton helpButton = (ImageButton) findViewById(R.id.help_button);
		dataNotFoundTextView = (TextView) findViewById(R.id.data_not_found);
		mapviewGroup = (ViewGroup) findViewById(R.id.mapview_group);
		listViewGroup = (ViewGroup) findViewById(R.id.listview_group);
		mapViewButton = (ImageView) findViewById(R.id.map_view);
		listViewButton = (ImageView) findViewById(R.id.list_view);
		
		
		placeListView = (ListView) findViewById(R.id.place_listview);
		
	
		listViewFooter = getLayoutInflater().inflate(R.layout.load_more_view, null, false);
		placeListView.addFooterView(listViewFooter, allPlaceList, false);
		placeListView.setFooterDividersEnabled(false);
		footerViewGroup = (ViewGroup) listViewFooter.findViewById(R.id.listView_load_more_footer);
		footerViewGroup.setVisibility(View.INVISIBLE);
		
		placeListAdapter = new CommonPlaceListAdapter(this, null,getCategoryType());
		placeListView.setAdapter(placeListAdapter);
		placeListView.setOnItemClickListener(listViewOnItemClickListener);
		
		mapViewButton.setOnClickListener(mapViewOnClickListener);
		listViewButton.setOnClickListener(listViewOnClickListener);		
		helpButton.setOnClickListener(helpOnClickListener);
		createFilterButtons(spinner);
		placeTitle.setText(getCategoryName());
		placeSize.setText(getCategorySize());
		sortSelected.put(0, true);
		int currentCityId = AppManager.getInstance().getCurrentCityId();
		localDataIsExist = LocalStorageMission.getInstance().hasLocalCityData(CommonPlaceActivity.this,currentCityId);
		if(!localDataIsExist)
		{
			placeListView.setOnScrollListener(listviewOnScrollListener);
		}else
		{
			placeListView.removeFooterView(listViewFooter);
		}
		loadingDialog = new ProgressDialog(this);
		loadPlace();
	}

	long startTime =0;
	long endTime =0;
	private void loadPlace()
	{
		
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(String... params)
			{
				startTime = System.currentTimeMillis();
				Log.d(TAG, "load place data start time = "+startTime/1000);
				List<Place> placeList = getAllPlace(CommonPlaceActivity.this);
				endTime = System.currentTimeMillis();
				Log.d(TAG, "load place data end time = "+endTime/1000);
				Log.d(TAG, "load place data from http time spent= "+(endTime - startTime)/1000);
				//totalCount = getPlaceTotalCount();
				start = 0;
				count = 1;
				LocationUtil.getInstance().getLocation(CommonPlaceActivity.this);
				if(TravelApplication.getInstance().mLocationClient !=null)
				{
					LocationUtil.stop();
				}
				return placeList;
			}

			@Override
			protected void onCancelled()
			{
				loadingDialog.dismiss();
				super.onCancelled();
			}

			@Override
			protected void onPostExecute(List<Place> resultList)
			{
				
				allPlaceList.clear();
				allPlaceList.addAll(resultList);
				//init filter title data
				initFilterButtonsData();
				// set data and reload place list
				//refreshPlaceView(allPlaceList);
				filterPlaceList();

				// TODO update sub category name and key and count
				// hide loading dialog
				loadingDialog.dismiss();
				loadDataFlag = true;
				super.onPostExecute(resultList);
			}

			@Override
			protected void onPreExecute()
			{
				// TODO show loading here
				showRoundProcessDialog();
				super.onPreExecute();
			}

		};

		task.execute();
	}

	private boolean isMatchSubCategory(Place place)
	{
		if (isSupportSubcategory() && !selectAllSubCategory())
		{
			boolean inSubCategory = false;
			if (subCatSelectKey != null)
			{
				for (int i = 0; i < subCatSelectKey.length; i++)
				{
					if (place.getSubCategoryId() == subCatSelectKey[i])
					{
						inSubCategory = true;
						break;
					}
				}
			}

			if (!inSubCategory)
				return false;
		}

		return true;
	}

	private boolean isMatchPrice(Place place)
	{
		if (isSupportPrice() && !selectAllprice())
		{
			boolean inPrice = false;
			if (priceSelect != null)
			{
				for (int i = 0; i < priceSelect.length; i++)
				{
					if (place.getPriceRank() == priceSelect[i])
					{
						inPrice = true;
						break;
					}
				}
			}

			if (!inPrice)
				return false;
		}

		return true;
	}

	private boolean isMatchArea(Place place)
	{
		if (isSupportArea() && !selectAllArea())
		{
			boolean inArea = false;
			if (areaSelect != null)
			{
				for (int i = 0; i < areaSelect.length; i++)
				{
					if (place.getAreaId() == areaSelect[i])
					{
						inArea = true;
						break;
					}
				}
			}

			if (!inArea)
				return false;
		}
		return true;
	}

	private boolean isMatchService(Place place)
	{
		if (isSupportService() && !selectAllService())
		{
			boolean inService = false;
			if (serviceSelect != null)
			{
				for (int i = 0; i < serviceSelect.length; i++)
				{
					for (int providedServiceId : place.getProvidedServiceIdList())
					{
						if (providedServiceId == serviceSelect[i])
						{
							inService = true;
							break;
						}
					}

				}
			}

			if (!inService)
				return false;
		}
		return true;
	}

	private void filterPlaceList()
	{

		List<Place> newList = new ArrayList<Place>();

		// step 1 : filter place
		List<Place> origList = allPlaceList;
		for (Place place : origList)
		{
			if (!isMatchSubCategory(place))
			{
				continue;
			} else if (!isMatchPrice(place))
			{
				continue;
			} else if (!isMatchArea(place))
			{
				continue;
			} else if (!isMatchService(place))
			{
				continue;
			}
			newList.add(place);
		}

		// step 2 : sort places by conditions
		Comparator<Place> comparator = getSortComparator(sortPosition);
		if (comparator != null)
		{
			Collections.sort(newList, comparator);
		}
		//totalCount = newList.size();
		refreshPlaceView(newList);
		return;
	}

	private boolean selectAllSubCategory()
	{
		if (subCatSelectKey == null)
			return true;

		if (subCatSelectKey.length > 0 && subCatSelectKey[0] == -1)
			return true;

		return false;
	}

	private boolean selectAllprice()
	{
		if (priceSelect == null)
			return true;

		if (priceSelect.length > 0 && priceSelect[0] == -1)
			return true;
		return false;
	}

	private boolean selectAllArea()
	{
		if (areaSelect == null)
			return true;

		if (areaSelect.length > 0 && areaSelect[0] == -1)
			return true;
		return false;
	}

	private boolean selectAllService()
	{
		if (serviceSelect == null)
			return true;

		if (serviceSelect.length > 0 && serviceSelect[0] == -1)
			return true;
		return false;
	}

	private void refreshPlaceView(List<Place> list)
	{
		placeListAdapter.setList(list);
		placeListAdapter.notifyDataSetChanged();
		updateTitle();
		if(list.size()>0)
		{
			dataNotFoundTextView.setVisibility(View.GONE);
			if(listViewButton.getVisibility() == View.VISIBLE)
			{			
				mapviewGroup.setVisibility(View.VISIBLE);
				goMapView(list);
			}else {
				listViewGroup.setVisibility(View.VISIBLE);	
			}		
		}else
		{
			dataNotFoundTextView.setVisibility(View.VISIBLE);
			totalCount = 0;
			updateTitle();
			listViewGroup.setVisibility(View.GONE);
			mapviewGroup.setVisibility(View.GONE);
		}
		
	}

	private void updateTitle()
	{
		TextView placeSize = (TextView) findViewById(R.id.place_num);
		placeSize.setText(getCategorySize());
	}

	

	
	protected OnClickListener subCategoryClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{		
			String filterTitle = getCategoryName()+getString(R.string.sub_category);
			if(subCatName != null)
			{
				if(subCateIsSelected != null &&subCateIsSelected.size() == 0)
				{
					subCateIsSelected.put(0, true);
				}
				filterWindow(v, subCatName,subCatKey, subCateIsSelected,true,subCateType,filterTitle);
			}
			
		}
	};
	
	protected OnClickListener priceClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String filterTitle = getCategoryName()+getString(R.string.price);
			if(price != null )
			{
				if(priceIsSelected != null && priceIsSelected.size() == 0)
				{
					priceIsSelected.put(0, true);
				}
				filterWindow(v, price, priceId,priceIsSelected,true,priceType,filterTitle);
			}
			
		}
	};
	
	protected OnClickListener areaClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String filterTitle = getCategoryName()+getString(R.string.area);
			if(areaName != null)
			{
				if(areaIsSelected != null && areaIsSelected.size() == 0)
				{
					areaIsSelected.put(0, true);
				}
				filterWindow(v, areaName,areaID, areaIsSelected,true,areaType,filterTitle);
			}			
		}
	};
	
	
	protected OnClickListener serviceClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String filterTitle = getCategoryName()+getString(R.string.service);
			if(serviceName != null)
			{
				if(serviceIsSelected !=null && serviceIsSelected.size() ==0)
				{
					serviceIsSelected.put(0, true);
				}
				filterWindow(v, serviceName,serviceID, serviceIsSelected,true,serviceType,filterTitle);
			}
		}
	};
	
	protected OnClickListener sortClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String sortTitle = getCategoryName()+getString(R.string.sort);
			if (sortDisplayName != null)
			{
				sortWindow(v, sortDisplayName, sortSelected,sortTitle);
			}
			
		}
	};
	

	
	
	

	private OnClickListener mapViewOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			try {
				Class.forName("com.google.android.maps.MapActivity");
				sortSpinner = (ViewGroup) findViewById(R.id.sort_spinner);
				if (sortSpinner != null)
				{
					sortSpinner.setVisibility(View.GONE);
				}
				listViewGroup.setVisibility(View.GONE);
				mapViewButton.setVisibility(View.GONE);
				listViewButton.setVisibility(View.VISIBLE);
			 	mapviewGroup.setVisibility(View.VISIBLE);
				goMapView(placeListAdapter.getPlaceList());		
			 }catch(Exception  e) {
	                Toast.makeText(CommonPlaceActivity.this, R.string.google_map_not_found2, Toast.LENGTH_LONG).show();
	         }
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		//AsyncLoader.getInstance().recycleBitmap();
		Log.d(TAG, "recycle plac detail image bitmap");
	}
	private void goMapView(List<Place> list) {
		if(list!=null&&list.size()>0)
		{	 	
			
		 	PlaceList.Builder placeList = PlaceList.newBuilder();
		 	placeList.addAllList(list);
		 	Log.d(TAG, "send map view place list size = "+placeList.getListCount());
		 	Intent intent = new Intent(CommonPlaceActivity.this, PlaceGoogleMap.class);
		 	intent.putExtra(ConstantField.PLACE_GOOGLE_MAP, placeList.build().toByteArray());		 	
	 		mapviewGroup.removeAllViews();
		 	mapviewGroup.addView(getLocalActivityManager().startActivity(ConstantField.PLACE_GOOGLE_MAP,intent).getDecorView()); 	
		}	
		
	}
	
	
	
	
	
	
	
	
	
	
	private OnClickListener listViewOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			mapviewGroup.setVisibility(View.GONE);
			listViewButton.setVisibility(View.GONE);
			listViewGroup.setVisibility(View.VISIBLE);
			sortSpinner.setVisibility(View.VISIBLE);
			mapViewButton.setVisibility(View.VISIBLE);
			LocationUtil.stop();
		}
	};
	
	
	
	private int visibleLastIndex = 0;
	private OnScrollListener listviewOnScrollListener = new OnScrollListener()
	{	
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			
			//数据为空--不用继续下面代码了
			if(allPlaceList.size() == 0) return;
			
			//判断是否滚动到底部
			boolean scrollEnd = false;
			try {
				if(view.getPositionForView(footerViewGroup) == view.getLastVisiblePosition()){
					Log.d(TAG, "footerview position = "+view.getPositionForView(footerViewGroup));
					Log.d(TAG, "visibleLastIndex position = "+visibleLastIndex);
					scrollEnd = true;
				}
			} catch (Exception e) {
				scrollEnd = false;
			}
			
			
			if(!loadDataFlag){
				return ;
			}
			
			if(totalCount !=0 && visibleLastIndex == totalCount)
			{
			   placeListView.removeFooterView(listViewFooter);	
			}
			  	
			  if(loadDataFlag&&scrollEnd)
			  {
				  footerViewGroup.setVisibility(View.VISIBLE);
				  Log.d(TAG, "load more");
				  Log.d(TAG, "listview visibleLastIndex = "+visibleLastIndex);	  
				  loadMore();
			  } 
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{
			visibleLastIndex = firstVisibleItem + visibleItemCount -1;
		}
	};
			
	

	
	
	
	
	
	
	
	
	
	
	
	private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			Place place = placeListAdapter.getPlaceList().get(arg2);
			addBrowseHistory(place);
			Intent intent = new Intent();
			//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(CommonPlaceActivity.this, detailPlaceClass);
			startActivity(intent);
			detailPlaceClass = null;
		}
	};

	
	private void addBrowseHistory(final Place place)
	{
		/*AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params)
			{
				
				return null;
			}};
			asyncTask.execute();*/
			
			//BrowseHistoryMission.getInstance().addBrowseHistory(place);
	}
	
	
	
	private OnClickListener helpOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent  intent = new Intent();
			intent.putExtra(ConstantField.HELP_TITLE, getResources().getString(R.string.help));
			intent.setClass(CommonPlaceActivity.this, HelpActiviy.class);
			startActivity(intent);
			
		}
	};
	
	


	
	
	private  OnItemClickListener filterlistClickListener = new OnItemClickListener() {  
        @Override  
        public void onItemClick(AdapterView<?> parent, View view, int position,  
                long id) {    
        	if(position == 0)
        	{
        	  IsSelectedTemp.clear();	
        	}else
        	{
        		if (IsSelectedTemp.containsKey(0))
				{
					IsSelectedTemp.remove(0);
				}
        	}
            ViewHolder vHollder = (ViewHolder) view.getTag();    
            vHollder.cBox.toggle();
            IsSelectedTemp.put(position, vHollder.cBox.isChecked());
            filterAdapter.setIsSelected(IsSelectedTemp);
	        filterAdapter.notifyDataSetChanged();
        }  
    };  
    
 
	
	private OnClickListener filterOnClickListener = new OnClickListener()
	{		
		@Override
		public void onClick(View v)
		{
			 IsSelectedTemp = filterAdapter.getIsSelected(); 
			 int[] position = null;
			 List<Integer> keyList = new ArrayList<Integer>();
			for(int key:IsSelectedTemp.keySet())
			{
				if(!IsSelectedTemp.get(key))
				{
					keyList.add(key);
				}
			}
			
			for(int key :keyList)
			{
				IsSelectedTemp.remove(key);
			}
			position = new int[IsSelectedTemp.size()];
			int i = 0;
			for(int key:IsSelectedTemp.keySet())
			{
				position[i] = key;
				i++;
			}
			 setFilterPosition(filterType, position,IsSelectedTemp);
			 if(!localDataIsExist)
			 {
				 start = 0;
				 count = 1;
				 getFileterData();	
			 }else
			 {
				 filterPlaceList();
			 }			 	 		
			 if(filterWindow !=null)
			{
				filterWindow.dismiss();
			}
		}
	};
	
	private OnClickListener cancelFilterOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(filterWindow !=null)
			{
				filterWindow.dismiss();
			}
			
		}
	};
	
	
	 private  OnItemClickListener sortListClickListener = new OnItemClickListener() {  
	        @Override  
	        public void onItemClick(AdapterView<?> parent, View view, int position,  
	                long id) {  
	        	
	        	SortViewHolder vHollder = (SortViewHolder) view.getTag();    
	            vHollder.cBox.toggle();
	            IsSelectedTemp.clear();
	            IsSelectedTemp.put(position, vHollder.cBox.isChecked());
	            sortAdapter.setIsSelected(IsSelectedTemp);
	            sortAdapter.notifyDataSetChanged();
	        }  
	    };  
	    
	    
	    
		
		private OnClickListener sortOnClickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				 IsSelectedTemp = sortAdapter.getIsSelected(); 
				for( int key:IsSelectedTemp.keySet())
				{
					sortPosition = key;
				}
				 filterPlaceList();
				 if(filterWindow !=null)
				{
					filterWindow.dismiss();
				}
			}
		};
	
		
	
	
	
	
	
	
	
	private String getCategorySize()
	{
		if(localDataIsExist)
		{
			totalCount = placeListAdapter.getCount();
		}else
		{
			totalCount = getPlaceTotalCount();
		}
		
		String sizeString = "(" + totalCount + ")";
		return sizeString;
	}

	public void showRoundProcessDialog()
	{

		OnKeyListener keyListener = new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(CommonPlaceActivity.this,MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(false);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}


	
	
	
	
    private void filterWindow(View parent,String[] filterTitleName,int[] filterKey,HashMap<Integer, Boolean> isSelected,boolean isSelectAll,int filterType,String filterTitle) {  
    	this.filterType = filterType;
    	//filterFlag = 1;
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View v = lay.inflate(R.layout.filter_place_popup, null); 
        v.setPadding(0, statusBarHeight, 0, 0);
        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  

        ListView filterList=(ListView)v.findViewById(R.id.filter_listview);  
        Button cancelButton = (Button) v.findViewById(R.id.cancel_button);
        Button filterButton = (Button) v.findViewById(R.id.ok_button);
        TextView titleTextView = (TextView) v.findViewById(R.id.filter_title);
        titleTextView.setText(filterTitle);
        cancelButton.setOnClickListener(cancelFilterOnClickListener);
        filterButton.setOnClickListener(filterOnClickListener);
        filterAdapter=new FilterAdapter(CommonPlaceActivity.this,filterTitleName);
        filterAdapter.setIsSelected(isSelected);
        filterList.setAdapter(filterAdapter);  
        filterAdapter.notifyDataSetChanged();
        filterList.setItemsCanFocus(false);  
        filterList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
        filterList.setOnItemClickListener(filterlistClickListener);  
       
        filterWindow = new PopupWindow(v, android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT,true);  
          
        IsSelectedTemp = filterAdapter.getIsSelected();
        filterWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
        filterWindow.setFocusable(true);  
        filterWindow.update();  
        filterWindow.showAtLocation(parent, Gravity.TOP, 0, 0);  
    }  
  
    
    
    
   
	
	
	private void setFilterPosition(int filterType,int[] position,HashMap<Integer, Boolean> selected)
	{
		switch (filterType)
		{
			case subCateType:
				if(position!=null&&position.length>0)
				{
					subCatSelectKey = new int[position.length];
					for(int i=0;i<position.length;i++)
					{
						subCatSelectKey[i] = subCatKey[position[i]];
					}
				}else
				{
					subCatSelectKey = null;
				}
				
				subCateIsSelected = selected;
				break;
			case serviceType:
				if(position!=null&&position.length>0)
				{
					serviceSelect = new int[position.length];
					for(int i=0;i<position.length;i++)
					{
						serviceSelect[i] = serviceID[position[i]];
					}
				}else
				{
					serviceSelect = null;
				}
				
				serviceIsSelected = selected;
				break;
			case priceType:
				if(position!=null&&position.length>0)
				{
					priceSelect = new int[position.length];
					for(int i=0;i<position.length;i++)
					{
						priceSelect[i] = priceId[position[i]];			
					}		
				}else
				{
					priceSelect = null;
				}
				
				priceIsSelected = selected;
				break;
			case areaType:
				if(position!=null&&position.length>0)
				{
					areaSelect = new int[position.length];
					for(int i=0;i<position.length;i++)
					{
						areaSelect[i] = areaID[position[i]];
					}
				}else
				{
					areaSelect = null;
				}			
				areaIsSelected = selected;
				break;
	
			default:
				break;
		}
	}
	
	
	
	
	
	
	
	 private void sortWindow(View parent,String[] sortTitleName,HashMap<Integer, Boolean> isSelected,String filterTitle) {  

        
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View v = lay.inflate(R.layout.filter_place_popup, null); 
       // v.setAnimation(AnimationUtils.loadAnimation(CommonPlaceActivity.this,R.anim.filter_window_in));
        v.setPadding(0, statusBarHeight, 0, 0);
        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));        
        ListView sortList=(ListView)v.findViewById(R.id.filter_listview);
        v.findViewById(R.id.listview_group).setPadding(0, (int)getResources().getDimension(R.dimen.sort_list_padding_top), 0, 0);
        
        Button cancelButton = (Button) v.findViewById(R.id.cancel_button);
        Button filterButton = (Button) v.findViewById(R.id.ok_button);
        TextView titleTextView = (TextView) v.findViewById(R.id.filter_title);
        titleTextView.setText(filterTitle);
        cancelButton.setOnClickListener(cancelFilterOnClickListener);
        filterButton.setOnClickListener(sortOnClickListener);
    
       
        sortAdapter=new SortAdapter(CommonPlaceActivity.this,sortTitleName,false);
        sortAdapter.setIsSelected(isSelected);
        sortList.setAdapter(sortAdapter);  
       
        sortAdapter.notifyDataSetChanged();
        sortList.setItemsCanFocus(false);  
        sortList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
        sortList.setOnItemClickListener(sortListClickListener);  
       
        filterWindow = new PopupWindow(v, android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.FILL_PARENT,true);  
          
        IsSelectedTemp = sortAdapter.getIsSelected();
        filterWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
        filterWindow.setFocusable(true);  
        filterWindow.update();  
        filterWindow.showAtLocation(parent, Gravity.TOP, 0, 0);  
	  }  
	  
	    
	    
	    
	  
		/*private void openGPSSettings() {
			LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				return;
			}
				Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
		}*/
	
		
		
		
		
	
		
		
		private void loadMore()
		{
			AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
			{

				@Override
				protected List<Place> doInBackground(String... params)
				{
					startTime = System.currentTimeMillis();
					Log.d(TAG, "load place data start time = "+startTime/1000);
					loadDataFlag = false;
					List<Place> placeList = null;
					if(!localDataIsExist)
					{
						start = count *20;
						count++;
						String subcateType = getSubcateType();
						String serviceType = getServiceType();
						String areaType = getAreaType();
						String priceType = getPriceType();
						String sortType = Integer.toString(sortPosition+1);
						placeList = PlaceMission.getInstance().loadMorePlace(getCategoryType(),CommonPlaceActivity.this,start, subcateType, areaType, serviceType, priceType, sortType);
					}
					endTime = System.currentTimeMillis();
					Log.d(TAG, "load place data end time = "+endTime/1000);
					Log.d(TAG, "load place data from http user time = "+(endTime - startTime)/1000);								
					return placeList;
				}
				@Override
				protected void onPostExecute(List<Place> resultList)
				{
					loadDataFlag = true;
					addMoreData(resultList);
					//refreshPlaceListView.onRefreshComplete();
					footerViewGroup.setVisibility(View.GONE);
					super.onPostExecute(resultList);
				}			
			};

			task.execute();		
		}
		
		private void getFileterData()
		{
			AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
			{

				@Override
				protected List<Place> doInBackground(String... params)
				{
					String subcateType = getSubcateType();
					String serviceType = getServiceType();
					String areaType = getAreaType();
					String priceType = getPriceType();
					String sortType = Integer.toString(sortPosition+1);
					List<Place> placeList = PlaceMission.getInstance().filterPlace(getCategoryType(),CommonPlaceActivity.this,  subcateType, areaType, serviceType, priceType, sortType,start);
					//totalCount = getPlaceTotalCount();
					return placeList;
				}
				@Override
				protected void onPostExecute(List<Place> resultList)
				{
					loadingDialog.dismiss();
					allPlaceList.clear();
					allPlaceList.addAll(resultList);
					filterPlaceList();
				}
				@Override
				protected void onCancelled()
				{
					loadingDialog.dismiss();
					super.onCancelled();
				}
				@Override
				protected void onPreExecute()
				{
					loadingDialog.show();
					super.onPreExecute();
				}			
				
			};

			task.execute();		
		}
		
		
		
		
		private void addMoreData(List<Place> placeList)
		{
			if(placeList!= null &&placeList.size()>0)
			{
				List<Place> newMoreList = new ArrayList<Place>();
				allPlaceList.addAll(placeList);
				newMoreList.addAll(placeList);
				Comparator<Place> comparator = getSortComparator(sortPosition);
				if (comparator != null)
				{
					Collections.sort(newMoreList, comparator);
				}
				if(newMoreList.size()>0)
				{
					placeListAdapter.addPlaceList(newMoreList);
					placeListAdapter.notifyDataSetChanged();				
				}
			}	
			return;
		}
		
		
		
		
		
		
		
		
	/*	private boolean checkGPSisOpen() {
			LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				return true;
			}
				Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
				return false;
		}
		*/

		@Override
		protected void onDestroy()
		{	
			Log.d(TAG, "onDestroy");
			placeListAdapter.recycleBitmap();
			LocationUtil.stop();
			if(loadingDialog  != null)
			{
				loadingDialog.dismiss();
			}
			ActivityMange.getInstance().finishActivity();
			System.gc();
			super.onDestroy();
		}	
		
		@Override
		protected void onStop() {
			super.onStop();
		}

		
		private String getSubcateType()
		{
			String subCateType = "-1";
			if (isSupportSubcategory())
			{
				if(selectAllSubCategory())
				{
					return subCateType;
				}
				subCateType = "";
				if (subCatSelectKey != null)
				{
					for (int i = 0; i < subCatSelectKey.length; i++)
					{
						subCateType = subCateType+subCatSelectKey[i]+",";
						
					}
					return subCateType.substring(0, subCateType.length()-1);
				}
			}
			return subCateType.trim();
		}
		
		private String getPriceType()
		{
			String priceType = "-1";
			if (isSupportPrice())
			{
				if(selectAllprice())
				{
					return priceType;
				}
				priceType = "";
				if (priceSelect != null)
				{
					for (int i = 0; i < priceSelect.length; i++)
					{
						priceType = priceType+priceSelect[i]+",";	
					}
					return priceType.substring(0, priceType.length()-1);
				}
			}
			return priceType.trim();
		}
		
		
		private String getServiceType()
		{
			String serviceType = "-1";
			if (isSupportService())
			{
				if(selectAllService())
				{
					return serviceType;
				}
				serviceType = "";
				if (serviceSelect != null)
				{
					for (int i = 0; i < serviceSelect.length; i++)
					{
						serviceType = serviceType+serviceSelect[i]+",";
						
					}
					return serviceType.substring(0, serviceType.length()-1);
				}
			}
			return serviceType.trim();
		}
		
		
		private String getAreaType()
		{
			String areaType = "-1";
			if (isSupportArea())
			{
				if(selectAllArea())
				{
					return areaType;
				}
				areaType = "";
				if (areaSelect != null)
				{
					for (int i = 0; i < areaSelect.length; i++)
					{
						areaType = areaType+areaSelect[i]+",";
						
					}
					return areaType.substring(0, areaType.length()-1);
				}
			}
			return areaType.trim();
		}

		
		@Override
		public boolean onCreateOptionsMenu(Menu menu)
		{
			// TODO Auto-generated method stub
			//TravelApplication.getInstance().addActivity(this);
			MenuInflater menuInflater = getMenuInflater();
			menuInflater.inflate(R.menu.menu, menu);
			return super.onCreateOptionsMenu(menu);
		}

		
		
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			Intent  intent;
			switch (item.getItemId())	
			{		
			case R.id.menu_refresh:
				loadPlace();
				break;
			case R.id.menu_help:
				intent = new Intent();
				intent.putExtra(ConstantField.HELP_TITLE, getResources().getString(R.string.help));
				intent.setClass(CommonPlaceActivity.this, HelpActiviy.class);
				startActivity(intent);
				break;
			case R.id.menu_feedback:
				intent = new Intent();			
				intent.setClass(CommonPlaceActivity.this, FeedBackActivity.class);
				startActivity(intent);
				break;
			case R.id.menu_about:
				intent = new Intent();
				String about = getResources().getString(R.string.about_damuzhi);
				intent.putExtra(ConstantField.HELP_TITLE, about);
				intent.setClass(CommonPlaceActivity.this, HelpActiviy.class);
				startActivity(intent);
				break;
			case R.id.menu_exit:
				//TravelApplication.getInstance().exit();
				ActivityMange.getInstance().AppExit(CommonPlaceActivity.this);
				break;

			default:
				break;
			}
			return super.onOptionsItemSelected(item);
		}
		


		
		
}
