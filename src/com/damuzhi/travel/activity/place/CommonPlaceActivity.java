/**  
 * @title CommendPlaceActivity.java  
 * @package com.damuzhi.travel.activity.adapter.place  
 * @description   
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:51:48  
 * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter;
import com.damuzhi.travel.activity.adapter.common.FilterAdapter.ViewHolder;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.activity.common.HelpActiviy;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.activity.common.PlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.entry.IndexActivity;
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
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.google.android.maps.MapView.LayoutParams;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

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
	protected int[] priceSelect;
	protected String[] areaName;
	protected int[] areaID;
	protected int[] areaSelect;
	private int subCatPosition = 0;
	private int pricePosition = 0;
	private int areaPosition = 0;
	private int servicePosition = 0;
	private MapController mapc;
	private PopupWindow filterWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_place);

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
				return getAllPlace(CommonPlaceActivity.this);
			}

			@Override
			protected void onCancelled()
			{
				// TODO Auto-generated method stub
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
					for (int providedServiceId : place
							.getProvidedServiceIdList())
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

		if (priceSelect.length > 0 && priceSelect[0] == 0)
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
		if (mapView != null)
		{
			mapView.getOverlays().clear();
			placeOverlay = new PlaceMapViewOverlay(null, list);
			mapView.getOverlays().add(placeOverlay);
			mapView.invalidate();
			placeOverlay.setOnFocusChangeListener(changeListener);
		}
		updateTitle();
	}

	private void updateTitle()
	{
		TextView placeSize = (TextView) findViewById(R.id.place_num);
		placeSize.setText(getCategorySize());
	}

	protected OnClickListener mapViewClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// TODO check correctness
			TravelApplication.getInstance().setPlaceCategoryID(PlaceCategoryType.PLACE_SPOT_VALUE);
			Intent intent = new Intent(CommonPlaceActivity.this, PlaceMap.class);
			startActivity(intent);
		}
	};

	protected OnClickListener subCategoryClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CommonPlaceActivity.this).setSingleChoiceItems(subCatName,
					subCatPosition, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int position)
						{
							subCatPosition = position;
							if (subCatPosition < 0|| subCatPosition >= subCatKey.length)
							{
								return;
							}

							// TODO support multiple selection
							subCatSelectKey = new int[1];
							subCatSelectKey[0] = subCatKey[subCatPosition];
							filterPlaceList();
							dialog.dismiss();
						}
					}).setTitle(
					CommonPlaceActivity.this.getResources().getString(
							R.string.sub_category));
			dialog = builder.create();
			dialog.show();

		}
	};

	protected OnClickListener priceClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CommonPlaceActivity.this).setSingleChoiceItems(price,
					pricePosition, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int position)
						{
							pricePosition = position;
							if (pricePosition < 0|| pricePosition >= price.length)
							{
								return;
							}

							// TODO support multiple selection
							priceSelect = new int[1];
							priceSelect[0] = pricePosition;
							filterPlaceList();
							dialog.dismiss();
						}
					}).setTitle(
					CommonPlaceActivity.this.getResources().getString(
							R.string.price));
			dialog = builder.create();
			dialog.show();

		}
	};

	protected OnClickListener areaClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CommonPlaceActivity.this).setSingleChoiceItems(areaName,
					areaPosition, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int position)
						{
							areaPosition = position;
							if (areaPosition < 0|| areaPosition >= areaName.length)
							{
								return;
							}

							// TODO support multiple selection
							areaSelect = new int[1];
							areaSelect[0] = areaID[areaPosition];
							filterPlaceList();
							dialog.dismiss();
						}
					}).setTitle(
					CommonPlaceActivity.this.getResources().getString(
							R.string.area));
			dialog = builder.create();
			dialog.show();

		}
	};

	protected OnClickListener serviceClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CommonPlaceActivity.this).setSingleChoiceItems(serviceName,
					servicePosition, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int position)
						{
							servicePosition = position;
							if (servicePosition < 0|| servicePosition >= serviceName.length)
							{
								return;
							}

							// TODO support multiple selection
							serviceSelect = new int[1];
							serviceSelect[0] = serviceID[servicePosition];
							filterPlaceList();
							dialog.dismiss();
						}
					}).setTitle(
					CommonPlaceActivity.this.getResources().getString(
							R.string.service));
			dialog = builder.create();
			dialog.show();

		}
	};

	protected OnClickListener sortClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CommonPlaceActivity.this).setSingleChoiceItems(
					sortDisplayName, sortPosition,
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int position)
						{
							sortPosition = position;
							filterPlaceList();
							dialog.cancel();
						}
					}).setTitle(
					CommonPlaceActivity.this.getResources().getString(
							R.string.sort));
			dialog = builder.create();
			dialog.show();
		}
	};
	
	
	/*protected OnClickListener sortClickListener = new OnClickListener()
	{

		boolean [] sss = new boolean[]{true,true,false ,false};
		@Override
		public void onClick(View v)
		{
			final AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CommonPlaceActivity.this).setMultiChoiceItems(sortDisplayName, sss, new DialogInterface.OnMultiChoiceClickListener()
					{
						
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked)
						{
							Log.i(TAG,"multi choice which="+ which);
							Log.i(TAG,"multi choice isCheck="+ isChecked);
						}
					});
			//builder.setPositiveButton(text, listener);
			dialog = builder.create();		
			dialog.show();
		}
	};*/

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
			mapView = (MapView) findViewById(R.id.common_place_mapview);

			mapView.setStreetView(true);
			mapc = mapView.getController();
			placeOverlay = new PlaceMapViewOverlay(null,placeListAdapter.getPlaceList());
			mapView.getOverlays().add(placeOverlay);
			myLocateButton = (ImageView) findViewById(R.id.my_locate);
			canceLocateButton = (ImageView)findViewById(R.id.cancel_locate);
			myLocateButton.setOnClickListener(myLocateOnClickListener);
			canceLocateButton.setOnClickListener(cancelLocateOnClickListener);
			mapc.setCenter(placeOverlay.getCenter());
			mapc.setZoom(16);
			popupView = LayoutInflater.from(CommonPlaceActivity.this).inflate(R.layout.overlay_popup, null);
			mapView.addView(popupView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			popupView.setVisibility(View.GONE);
			placeOverlay.setOnFocusChangeListener(changeListener);
			// popupView.setOnClickListener(onClickListener);
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
			HashMap<String, Double> location = LocationUtil
					.getLocationByGps(CommonPlaceActivity.this);
			if (location != null)
			{
				GeoPoint geoPoint = new GeoPoint(
						(int) (location.get(ConstantField.LATITUDE) * 1E6),
						(int) (location.get(ConstantField.LONGITUDE) * 1E6));
				mapc.animateTo(geoPoint);				
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
			mapc.stopAnimation(isRestricted());
		}
	};

	// TODO move this method to common
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
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0)
				{
					loadingDialog.dismiss();
					Intent intent = new Intent(CommonPlaceActivity.this,
							IndexActivity.class);
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
				GeoPoint geoPoint = new GeoPoint(
						(int) (place.getLatitude() * 1e6),
						(int) (place.getLongitude() * 1e6));
				OverlayItem overlayItem = new OverlayItem(geoPoint,
						place.getName(), Integer.toString(i));
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

				MapView.LayoutParams geoLP = (LayoutParams) popupView
						.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView titleView = (TextView) popupView
						.findViewById(R.id.map_bubbleTitle);
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
	
	
	
    private void filterWindow(View parent,List<String> filterTitleList,HashMap<Integer, Boolean> isSelected,boolean isSelectAll) {  
        if (filterWindow == null) {  
            LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            View v = lay.inflate(R.layout.filter_place_popup, null);  
            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  

            ListView filterList=(ListView)v.findViewById(R.id.filter_listview);  
            FilterAdapter adapter=new FilterAdapter(CommonPlaceActivity.this,filterTitleList,isSelected);  
            filterList.setAdapter(adapter);  
            filterList.setItemsCanFocus(false);  
            filterList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
            filterList.setOnItemClickListener(listClickListener);  
              
            filterWindow = new PopupWindow(v, LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
        }  
          
        filterWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.all_page_bg2));  
        filterWindow.setFocusable(true);  
        filterWindow.update();  
        filterWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);  
    }  
  
    OnItemClickListener listClickListener = new OnItemClickListener() {  
        @Override  
        public void onItemClick(AdapterView<?> parent, View view, int position,  
                long id) {  
            ViewHolder vHollder = (ViewHolder) view.getTag();    
            vHollder.cBox.toggle();  
            FilterAdapter.isSelected.put(position, vHollder.cBox.isChecked());  
        }  
    };  
}
