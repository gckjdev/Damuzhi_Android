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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
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
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.more.FeedBackActivity;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.mission.more.BrowseHistoryMission;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.LocationUtil;
import com.damuzhi.travel.util.TravelUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;

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
	List<Place> allPlaceList = null;
	CommonPlaceListAdapter placeListAdapter = null;
	private ProgressDialog loadingDialog;
	// sort condition
	protected String[] sortDisplayName;
	private int sortPosition = 0;
	// mapview
	private ViewGroup mapviewGroup;
	private ViewGroup listViewGroup;
	private MapView mapView;
	private View popupView;
	private ImageView mapViewButton;
	private ImageView listViewButton;
	private ImageView myLocateButton;
	private ImageView canceLocateButton;
	private ViewGroup sortSpinner;
	private PlaceMapViewOverlay placeOverlay;
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
		
		
		loadPlace();
	}

	private void loadPlace()
	{
		// TODO Auto-generated method stub
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>()
		{

			@Override
			protected List<Place> doInBackground(String... params)
			{
				List<Place> placeList = getAllPlace(CommonPlaceActivity.this);
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

				// TODO Auto-generated method stub
				// hide loading dialog
				loadingDialog.dismiss();
				allPlaceList = resultList;
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
			placeListAdapter.setList(list);
			placeListAdapter.notifyDataSetChanged();
			if (mapView != null)
			{
				mapView.getOverlays().clear();
				placeOverlay = new PlaceMapViewOverlay(null, list);
				mapView.getOverlays().add(placeOverlay);
				mapView.invalidate();
				placeOverlay.setOnFocusChangeListener(changeListener);
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

	/*protected OnClickListener mapViewClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// TODO check correctness
			TravelApplication.getInstance().setPlaceCategoryID(PlaceCategoryType.PLACE_SPOT_VALUE);
			Intent intent = new Intent(CommonPlaceActivity.this, PlaceMap.class);
			startActivity(intent);
		}
	};*/

	
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
			openGPSSettings();
			sortSpinner = (ViewGroup) findViewById(R.id.sort_spinner);
			if (sortSpinner != null)
			{
				sortSpinner.setVisibility(View.GONE);
			}
			listViewGroup.setVisibility(View.GONE);
			mapViewButton.setVisibility(View.GONE);
			listViewButton.setVisibility(View.VISIBLE);
			mapviewGroup.setVisibility(View.VISIBLE);
			mapView = (MapView) findViewById(R.id.common_place_mapview);

			mapView.setStreetView(true);
			mapc = mapView.getController();
			List<Place> placeList = placeListAdapter.getPlaceList();
			if(placeList!=null&&placeList.size()>0)
			{
				placeOverlay = new PlaceMapViewOverlay(null,placeList);
				mapView.getOverlays().add(placeOverlay);
				myLocateButton = (ImageView) findViewById(R.id.my_locate);
				canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
				myLocateButton.setOnClickListener(myLocateOnClickListener);
				canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
				mapc.setCenter(placeOverlay.getCenter());
				mapc.setZoom(16);
				popupView = LayoutInflater.from(CommonPlaceActivity.this).inflate(R.layout.overlay_popup, null);
				mapView.addView(popupView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,MapView.LayoutParams.WRAP_CONTENT, null,MapView.LayoutParams.BOTTOM_CENTER));
				popupView.setVisibility(View.GONE);
				placeOverlay.setOnFocusChangeListener(changeListener);
				popupView.setOnClickListener(mapviewPopupItemOnClickListener);
			}
			
			// selectListButton.setOnClickListener(listClickListener);

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
		}
	};

	private OnClickListener myLocateOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			//HashMap<String, Double> location = LocationUtil.getLocation(CommonPlaceActivity.this);
			HashMap<String, Double> location = TravelApplication.getInstance().getLocation();
			if (location != null&&location.size()>0)
			{
				GeoPoint geoPoint = new GeoPoint((int) (location.get(ConstantField.LATITUDE) * 1E6),(int) (location.get(ConstantField.LONGITUDE) * 1E6));
				mapc.animateTo(geoPoint);	
				MyLocationOverlay myLocationOverlay = new MyLocationOverlay(CommonPlaceActivity.this,mapView);
				myLocationOverlay.enableMyLocation();
				mapView.getOverlays().add(myLocationOverlay);
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
			Place place = placeListAdapter.getPlaceList().get(0);
			GeoPoint geoPoint = new GeoPoint((int) (place.getLongitude()* 1E6),(int) (place.getLatitude() * 1E6));
			mapc.animateTo(geoPoint);
			mapView.invalidate();
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

	class PlaceMapViewOverlay extends ItemizedOverlay
	{
		private List<OverlayItem> locaions = new ArrayList<OverlayItem>();
		private Drawable markerIcon;

		public PlaceMapViewOverlay(Drawable marker, List<Place> placeList)
		{
			super(marker);
			this.markerIcon = getResources().getDrawable(TravelUtil.getForecastImage(getCategoryType()));
			markerIcon.setBounds(0, 0,
					(int) ((markerIcon.getIntrinsicWidth() / 2) * 1.5),
					(int) ((markerIcon.getIntrinsicHeight() / 2) * 1.5));
			int i = 0;
			for (Place place : placeList)
			{
				GeoPoint geoPoint = new GeoPoint((int) (place.getLatitude() * 1e6),(int) (place.getLongitude() * 1e6));
				OverlayItem overlayItem = new OverlayItem(geoPoint,place.getName(), Integer.toString(i));
				overlayItem.setMarker(markerIcon);
				locaions.add(overlayItem);
				i++;
			}
			populate();
		}

		@Override
		protected OverlayItem createItem(int i)
		{
			return locaions.get(i);
		}

		@Override
		public int size()
		{
			return locaions.size();
		}
		

	}

	private final ItemizedOverlay.OnFocusChangeListener changeListener = new ItemizedOverlay.OnFocusChangeListener()
	{

		@Override
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus)
		{
			if (popupView != null)
			{
				popupView.setVisibility(View.GONE);
			}
			if (newFocus != null)
			{

				MapView.LayoutParams geoLP = (LayoutParams) popupView.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView titleView = (TextView) popupView.findViewById(R.id.map_bubbleTitle);
				titleView.setText(newFocus.getTitle());
				popupView.setTag(newFocus.getSnippet());
				mapView.updateViewLayout(popupView, geoLP);
				popupView.setVisibility(View.VISIBLE);
			}
		}
	};
	
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
	
	
	private OnClickListener mapviewPopupItemOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String tag = (String)v.getTag();
			int position = Integer.parseInt(tag);
			Place place = placeListAdapter.getPlaceList().get(position);
			BrowseHistoryMission.getInstance().addBrowseHistory(place);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(CommonPlaceActivity.this, detailPlaceClass);
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
				Toast.makeText(this, getString(R.string.open_gps_tips), Toast.LENGTH_SHORT).show();
		}
	
}
