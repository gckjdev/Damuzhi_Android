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

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter.ViewHolder;
import com.damuzhi.travel.activity.adapter.common.SortAdapter;
import com.damuzhi.travel.activity.adapter.common.SortAdapter.SortViewHolder;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.mapview.CommonItemizedOverlay;
import com.damuzhi.travel.activity.common.mapview.CommonOverlayItem;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.mission.more.BrowseHistoryMission;
import com.damuzhi.travel.mission.place.LocalStorageMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

/**
 * @description
 * @version 1.0
 * @author liuxiaokun
 * @update 2012-5-24 下午3:51:48
 */

public abstract class CommonPlaceActivity extends TravelActivity
{

	protected static final String TAG = "CommonPlaceActivity";

	abstract public List<Place> getAllPlace(Activity activity);

	abstract public String getCategoryName();

	abstract public int getCategoryType();

	abstract public void createFilterButtons(ViewGroup spinner);

	abstract boolean isSupportSubcategory();

	abstract boolean isSupportPrice();

	abstract boolean isSupportArea();

	abstract boolean isSupportService();

	abstract Comparator<Place> getSortComparator(int index);

	ListView placeListView = null;
	ArrayList<Place> allPlaceList = new ArrayList<Place>();
	CommonPlaceListAdapter placeListAdapter = null;
	//CommonPlaceListPageAdapter placeListPageAdapter = null;
	//CommonPlaceAdapter placeListAdapter = null;
	private ProgressDialog loadingDialog;
	// sort condition
	protected String[] sortDisplayName;
	private int sortPosition = 0;
	// mapview
	private ViewGroup mapviewGroup;
	private ViewGroup listViewGroup;
	private TapControlledMapView mapView;
	private View popupView;
	private ImageView mapViewButton;
	private ImageView listViewButton;
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private ViewGroup sortSpinner;
	//private PlaceMapViewOverlay placeOverlay;
	// sub category name
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
	/*private int subCatPosition = 0;
	private int pricePosition = 0;
	private int areaPosition = 0;
	private int servicePosition = 0;*/
	private MapController mapc;
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
	private CheckBox selectAllCheckBox;
	private ViewGroup selectAllViewGroup;
	private boolean isSelectAll;
	private int filterType = 0;
	private static final int subCateType = 1;
	private static final int serviceType = 2;
	private static final int areaType = 3;
	private static final int priceType = 4;
	private View loadMoreView;
	private ProgressBar loadMoreProgressBar;
	//private TextView loadMoreButton;
	private CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay;
	private LocationClient mLocClient;
	private HashMap<String, Double> location ;
	private static int start = 0;
	private static int count = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		TravelApplication.getInstance().addActivity(this);
		setContentView(R.layout.common_place);
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
		mapviewGroup = (ViewGroup) findViewById(R.id.mapview_group);
		listViewGroup = (ViewGroup) findViewById(R.id.listview_group);
		mapViewButton = (ImageView) findViewById(R.id.map_view);
		listViewButton = (ImageView) findViewById(R.id.list_view);
		placeListView = (ListView) findViewById(R.id.place_listview);
		mapView = (TapControlledMapView) findViewById(R.id.common_place_mapview);
		mapc = mapView.getController();
		mapc.setZoom(19);
		mapView.setStreetView(true);
	
		int currentCityId = AppManager.getInstance().getCurrentCityId();
		boolean localDataIsExist = LocalStorageMission.getInstance().hasLocalCityData(currentCityId);
		if(!localDataIsExist)
		{
			loadMoreView = getLayoutInflater().inflate(R.layout.load_more_view, null);
			loadMoreView.setVisibility(View.GONE);
		//	loadMoreButton = (TextView) loadMoreView.findViewById(R.id.load_more_btn);
			loadMoreProgressBar = (ProgressBar) loadMoreView.findViewById(R.id.footprogress);
			loadMoreView.setOnClickListener(loadMoreOnClickListener);
			placeListView.addFooterView(loadMoreView,null,false);
			placeListView.setOnScrollListener(onScrollListener);
		}		
		placeListAdapter = new CommonPlaceListAdapter(this, null,getCategoryType());
		placeListView.setAdapter(placeListAdapter);
		
		placeListView.setOnItemClickListener(listViewOnItemClickListener);
		
		mapViewButton.setOnClickListener(mapViewOnClickListener);
		listViewButton.setOnClickListener(listViewOnClickListener);		
		helpButton.setOnClickListener(helpOnClickListener);
		createFilterButtons(spinner);
		placeTitle.setText(getCategoryName());
		placeSize.setText(getCategorySize());
		mapView.setOnSingleTapListener(onSingleTapListener);
		myLocateButton = (ImageView) findViewById(R.id.my_locate);
		canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
		myLocateButton.setOnClickListener(myLocateOnClickListener);
		canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
		sortSelected.put(0, true);
		
		
		loadPlace();
	}

	private void loadPlace()
	{
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(String... params)
			{
				List<Place> placeList = getAllPlace(CommonPlaceActivity.this);
				start = 0;
				count = 1;
				location = TravelApplication.getInstance().getLocation();
				if(TravelApplication.getInstance().mLocationClient !=null)
				{
					TravelApplication.getInstance().mLocationClient.stop();
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
				// hide loading dialog
				loadingDialog.dismiss();
				//allPlaceList = resultList;
				allPlaceList.clear();
				allPlaceList.addAll(resultList);
				// set data and reload place list
				//refreshPlaceView(allPlaceList);
				filterPlaceList();

				// TODO update sub category name and key and count

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
			// check sub category match
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

			// check

			newList.add(place);
		}

		// step 2 : sort places by conditions
		Comparator<Place> comparator = getSortComparator(sortPosition);
		if (comparator != null)
		{
			Collections.sort(newList, comparator);
		}

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

		/*if (priceSelect.length > 0 && priceSelect[0] == 0)
			return true;*/
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
		if(list.size()>0)
		{
			findViewById(R.id.data_not_found).setVisibility(View.GONE);
			if(listViewButton.getVisibility() == View.VISIBLE)
			{
				mapviewGroup.setVisibility(View.VISIBLE);
			}else {
				listViewGroup.setVisibility(View.VISIBLE);
			}		
			placeListAdapter.setList(list);
			placeListAdapter.notifyDataSetChanged();
			if (mapView != null)
			{
				initMapView();
			}
			updateTitle();
		}else
		{
			findViewById(R.id.data_not_found).setVisibility(View.VISIBLE);
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
			filterWindow(v, subCatName,subCatKey, subCateIsSelected,true,subCateType,filterTitle);

		}
	};
	
	protected OnClickListener priceClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String filterTitle = getCategoryName()+getString(R.string.price);
			filterWindow(v, price, priceId,priceIsSelected,true,priceType,filterTitle);
		}
	};
	
	protected OnClickListener areaClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String filterTitle = getCategoryName()+getString(R.string.area);
			filterWindow(v, areaName,areaID, areaIsSelected,true,areaType,filterTitle);
		}
	};
	
	
	protected OnClickListener serviceClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String filterTitle = getCategoryName()+getString(R.string.service);
			filterWindow(v, serviceName,serviceID, serviceIsSelected,true,serviceType,filterTitle);

		}
	};
	
	protected OnClickListener sortClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String sortTitle = getCategoryName()+getString(R.string.sort);
			sortWindow(v, sortDisplayName, sortSelected,sortTitle);
		}
	};
	

	
	
	

	private OnClickListener mapViewOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			sortSpinner = (ViewGroup) findViewById(R.id.sort_spinner);
			if (sortSpinner != null)
			{
				sortSpinner.setVisibility(View.GONE);
			}
			listViewGroup.setVisibility(View.GONE);
			mapViewButton.setVisibility(View.GONE);
			listViewButton.setVisibility(View.VISIBLE);
			mapviewGroup.setVisibility(View.VISIBLE);
			initMapView();			
		}
	};

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
			if(mLocClient !=null)
			{
				mLocClient.stop();
			}
		}
	};
	
	
	private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {		
		@Override
		public boolean onSingleTap(MotionEvent e) {
			itemizedOverlay.hideAllBalloons();
			return true;
		}
	};
	
	
	private void initMapView()
	{
		openGPSSettings();
		//mapviewGroup.setVisibility(View.VISIBLE);
		List<Place> placeList = placeListAdapter.getPlaceList();
		if(placeList!=null&&placeList.size()>0)
		{
			mapView.getOverlays().clear();
			mapView.removeAllViews();
			initMapOverlayView(placeList);
		}
	}
	
	
	private OnClickListener myLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			boolean gpsEnable = checkGPSisOpen();			
			if(location == null || location.size()==0)
			{
				getLocation(CommonPlaceActivity.this);
				location = TravelApplication.getInstance().getLocation();
			}
			String address = TravelApplication.getInstance().address;
			if (address == null||address.equals(""))
			{
				location = null;
				return;
			}
			if(mLocClient !=null)
			{
				mLocClient.stop();
			}				
			mapView.getOverlays().clear();
			mapView.removeAllViews();
			if (location != null&&location.size()>0)
			{
				GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));	
				Drawable drawable = getResources().getDrawable(R.drawable.my_location);
				CommonOverlayItem overlayItem = new CommonOverlayItem(geoPoint, "", "", null);
				CommonItemizedOverlay<CommonOverlayItem> itemizedOverlay3 = new CommonItemizedOverlay<CommonOverlayItem>(drawable, mapView);
				itemizedOverlay3.addOverlay(overlayItem);
				mapView.getOverlays().add(itemizedOverlay3);
				mapc.animateTo(geoPoint);
			}else
			{
				Toast.makeText(CommonPlaceActivity.this, getString(R.string.get_location_fail), Toast.LENGTH_LONG).show();
			}
			myLocateButton.setVisibility(View.GONE);
			canceLocateButton.setVisibility(View.VISIBLE);
		}
	};
	
	private OnClickListener cancelLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			canceLocateButton.setVisibility(View.GONE);
			myLocateButton.setVisibility(View.VISIBLE);
			initMapView();
			
		}
	};

	private String getCategorySize()
	{
		int size = 0;
		if (placeListAdapter.getPlaceList() != null)
			size = placeListAdapter.getPlaceList().size();
		String sizeString = "(" + size + ")";
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
					Intent intent = new Intent(CommonPlaceActivity.this,IndexActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					return true;
				} else
				{
					return false;
				}
			}
		};

		loadingDialog = new ProgressDialog(this);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage(getResources().getString(R.string.loading));
		loadingDialog.setIndeterminate(false);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnKeyListener(keyListener);
		loadingDialog.show();
	}


	
	private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			Place place = placeListAdapter.getPlaceList().get(arg2);
			BrowseHistoryMission.getInstance().addBrowseHistory(place);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(CommonPlaceActivity.this, detailPlaceClass);
			startActivity(intent);
		}
	};

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
	
	
    private void filterWindow(View parent,String[] filterTitleName,int[] filterKey,HashMap<Integer, Boolean> isSelected,boolean isSelectAll,int filterType,String filterTitle) {  
    	this.filterType = filterType;
    	
    	String[] titleName = countPlaceByfilterType(filterType, filterTitleName, filterKey);
        
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View v = lay.inflate(R.layout.filter_place_popup, null); 
        v.setPadding(0, statusBarHeight, 0, 0);
        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  

        ListView filterList=(ListView)v.findViewById(R.id.filter_listview);  
        ImageButton cancelButton = (ImageButton) v.findViewById(R.id.cancel_button);
        ImageButton filterButton = (ImageButton) v.findViewById(R.id.ok_button);
        TextView titleTextView = (TextView) v.findViewById(R.id.filter_title);
        titleTextView.setText(filterTitle);
        cancelButton.setOnClickListener(cancelFilterOnClickListener);
        filterButton.setOnClickListener(filterOnClickListener);
        selectAllViewGroup = (ViewGroup) v.findViewById(R.id.select_all_group);
        selectAllViewGroup.setOnClickListener(selectAllOnClickListener);
        selectAllCheckBox = (CheckBox) v.findViewById(R.id.select_all_checkbox);
        if(isSelected.size()>0)
        {
        	selectAllCheckBox.setChecked(false);
        }else {
			selectAllCheckBox.setChecked(true);
		}
        filterAdapter=new FilterAdapter(CommonPlaceActivity.this,titleName);
        filterAdapter.setIsSelected(isSelected);
        filterList.setAdapter(filterAdapter);  
        filterAdapter.notifyDataSetChanged();
        filterList.setItemsCanFocus(false);  
        filterList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
        filterList.setOnItemClickListener(listClickListener);  
       
        filterWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,true);  
          
        IsSelectedTemp = filterAdapter.getIsSelected();
        filterWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
        filterWindow.setFocusable(true);  
        filterWindow.update();  
        filterWindow.showAtLocation(parent, Gravity.TOP, 0, 0);  
    }  
  
    
    
    
   private  OnItemClickListener listClickListener = new OnItemClickListener() {  
        @Override  
        public void onItemClick(AdapterView<?> parent, View view, int position,  
                long id) {  
        	if(selectAllCheckBox.isChecked())
        	{
        		selectAllCheckBox.setChecked(false);
        	}      	
            ViewHolder vHollder = (ViewHolder) view.getTag();    
            vHollder.cBox.toggle();
            IsSelectedTemp.put(position, vHollder.cBox.isChecked());
            filterAdapter.setIsSelected(IsSelectedTemp);
	        filterAdapter.notifyDataSetChanged();
        }  
    };  
    
    private OnClickListener selectAllOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			selectAllCheckBox.toggle();
			HashMap<Integer, Boolean> isSelected = filterAdapter.getIsSelected();
	        isSelected.clear();
            filterAdapter.setIsSelected(isSelected);
	        filterAdapter.notifyDataSetChanged();			
		}
	};
    
	
	private OnClickListener filterOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			 IsSelectedTemp = filterAdapter.getIsSelected(); 
			 isSelectAll = selectAllCheckBox.isChecked();
			 int[] position = null;
			 List<Integer> keyList = new ArrayList<Integer>();
			 if(isSelectAll)
			 {
				 position = null;
			 }else {
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
			}
			 setFilterPosition(filterType, position,IsSelectedTemp);
			 filterPlaceList();
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
						priceSelect[i] = position[i]+1;
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
	
	
	
	private String[] countPlaceByfilterType(int filterType,String[] Name,int[]key)
	{
		switch (filterType)
		{
		case subCateType:
			return PlaceMission.getInstance().countPlaceBySubcate(Name,key);
		case priceType:
			return PlaceMission.getInstance().countPlaceByPrice(Name,key);
		case areaType:
			return PlaceMission.getInstance().countPlaceByArea(Name,key);
		case serviceType:
			return PlaceMission.getInstance().countPlaceByService(Name,key);
		}
		return null;
		
	}
	
	
	
	 private void sortWindow(View parent,String[] sortTitleName,HashMap<Integer, Boolean> isSelected,String filterTitle) {  

        
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View v = lay.inflate(R.layout.filter_place_popup, null); 
        v.setPadding(0, statusBarHeight, 0, 0);
        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
        v.findViewById(R.id.select_all_group).setVisibility(View.GONE);        
        ListView sortList=(ListView)v.findViewById(R.id.filter_listview);
        v.findViewById(R.id.listview_group).setPadding(0, (int)getResources().getDimension(R.dimen.sort_list_padding_top), 0, 0);
        
        ImageButton cancelButton = (ImageButton) v.findViewById(R.id.cancel_button);
        ImageButton filterButton = (ImageButton) v.findViewById(R.id.ok_button);
        TextView titleTextView = (TextView) v.findViewById(R.id.filter_title);
        titleTextView.setText(filterTitle);
        cancelButton.setOnClickListener(cancelFilterOnClickListener);
        filterButton.setOnClickListener(sortOnClickListener);
    
       
        sortAdapter=new SortAdapter(CommonPlaceActivity.this,sortTitleName);
        sortAdapter.setIsSelected(isSelected);
        sortList.setAdapter(sortAdapter);  
       
        sortAdapter.notifyDataSetChanged();
        sortList.setItemsCanFocus(false);  
        sortList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
        sortList.setOnItemClickListener(sortListClickListener);  
       
        filterWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,true);  
          
        IsSelectedTemp = sortAdapter.getIsSelected();
        filterWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
        filterWindow.setFocusable(true);  
        filterWindow.update();  
        filterWindow.showAtLocation(parent, Gravity.TOP, 0, 0);  
	  }  
	  
	    
	    
	    
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
	
		@Override
		public boolean onOptionsItemSelected(MenuItem item)
		{
			switch (item.getItemId())	
			{		
			case R.id.menu_refresh:
				loadPlace();
				break;
			default:
				break;
			}
			return super.onOptionsItemSelected(item);
		}
	
		private void openGPSSettings() {
			LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				return;
			}
				Toast.makeText(this, getString(R.string.open_gps_tips3), Toast.LENGTH_SHORT).show();
		}
	
		
		private OnClickListener loadMoreOnClickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				loadMoreProgressBar.setVisibility(View.VISIBLE); 
				setProgressBarVisibility(true);
				start = count *20;
				count++;
				loadMore();
			}
		};
		
		private OnScrollListener onScrollListener = new OnScrollListener()
		{
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				loadMoreView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{				
			}
		};
		
		
		
		private void loadMore()
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
					String sortType = Integer.toString(sortPosition);
					List<Place> placeList = PlaceMission.getInstance().loadMorePlace(getCategoryType(),CommonPlaceActivity.this, start, subcateType, areaType, serviceType, priceType, sortType);
					return placeList;
				}
				@Override
				protected void onPostExecute(List<Place> resultList)
				{
					setProgressBarVisibility(false);
					loadMoreProgressBar.setVisibility(View.GONE);
					addMoreData(resultList);
					super.onPostExecute(resultList);
				}

				

			};

			task.execute();		
		}
		
		private void addMoreData(List<Place> placeList)
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
				if (mapView != null)
				{
					mapView.getOverlays().clear();
					initMapOverlayView(newMoreList);
				}
				updateTitle();
			}else
			{
				loadMoreView.setVisibility(View.GONE);
				placeListView.removeFooterView(loadMoreView);
				Toast.makeText(CommonPlaceActivity.this, getString(R.string.not_more_data), Toast.LENGTH_SHORT).show();
			}
			return;
		}
		
		
		
		/*private void refreshMapview(final List<Place> placeList)
		{
			AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>()
			{

			
				@Override
				protected void onCancelled()
				{
					loadingDialog.dismiss();
					super.onCancelled();
				}

			
				@Override
				protected void onPreExecute()
				{
					// TODO show loading here
					showRoundProcessDialog();
					super.onPreExecute();
				}


				@Override
				protected void onPostExecute(Void result)
				{
					super.onPostExecute(result);
				}


				@Override
				protected Void doInBackground(String... params)
				{
					initMapOverlayView(placeList);
					return null;
				}

			};
			task.execute();
		}
		*/
		
		private void initMapOverlayView(List<Place> placeList)
		{	
			Drawable markerIcon = getResources().getDrawable(TravelUtil.getForecastImage(getCategoryType()));
			itemizedOverlay = new CommonItemizedOverlay<CommonOverlayItem>(markerIcon, mapView);
			for (Place place : placeList)
			{
				GeoPoint geoPoint = new GeoPoint((int) (place.getLatitude() * 1e6),(int) (place.getLongitude() * 1e6));
				CommonOverlayItem commonOverlayItem = new CommonOverlayItem(geoPoint,place.getName(), null,place);
				itemizedOverlay.addOverlay(commonOverlayItem);
			}
			if(itemizedOverlay.size()>0)
			{
				mapc.setCenter(itemizedOverlay.getCenter());
			}
			List<Overlay> mapOverlays = mapView.getOverlays();		
			mapOverlays.add(itemizedOverlay);
		}
		
		
		private boolean checkGPSisOpen() {
			LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				return true;
			}
				Toast.makeText(this, getString(R.string.open_gps_tips2), Toast.LENGTH_SHORT).show();
				return false;
		}
		
		public  void getLocation(Context context)
		{
			
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);	
			option.setAddrType("detail");				
			option.setCoorType("bd09ll");		
			option.setScanSpan(10000);
			mLocClient = TravelApplication.getInstance().mLocationClient;
			mLocClient.setLocOption(option);
			mLocClient.start();
			if (mLocClient != null && mLocClient.isStarted())
				mLocClient.requestLocation();
			else 
				Log.d(TAG, " baidu locationSDK locClient is null or not started");
		}


		@Override
		protected void onDestroy()
		{
			super.onDestroy();			
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
						subCateType = subCatSelectKey[i]+",";
						
					}
					return subCateType.substring(0, subCateType.length()-1);
				}
			}
			return subCateType;
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
						priceType = priceSelect[i]+",";
						
					}
					return priceType.substring(0, priceType.length()-1);
				}
			}
			return priceType;
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
						serviceType = serviceSelect[i]+",";
						
					}
					return serviceType.substring(0, serviceType.length()-1);
				}
			}
			return serviceType;
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
						areaType = areaSelect[i]+",";
						
					}
					return areaType.substring(0, areaType.length()-1);
				}
			}
			return areaType;
		}
		
		
}
