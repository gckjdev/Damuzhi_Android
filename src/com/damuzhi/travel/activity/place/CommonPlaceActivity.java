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
import java.util.List;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.activity.common.PlaceMap;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @description
 * @version 1.0
 * @author liuxiaokun
 * @update 2012-5-24 下午3:51:48
 */

public abstract class CommonPlaceActivity extends Activity
{

	abstract public List<Place> getAllPlace();
	abstract public String getCategoryName();
	//abstract public String getCategorySize();
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
	
	// sort condition
	protected String[] sortDisplayName;
	private int sortPosition = 0;

	
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
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_place);
		
		TextView placeTitle = (TextView) findViewById(R.id.place_title);
		TextView placeSize = (TextView) findViewById(R.id.place_num);
		ViewGroup spinner = (ViewGroup) findViewById(R.id.spinner_group);
		
		placeListView = (ListView) findViewById(R.id.place_listview);
		placeListAdapter = new CommonPlaceListAdapter(this, null, getCategoryType());
		placeListView.setAdapter(placeListAdapter);
		
		createFilterButtons(spinner);
		placeTitle.setText(getCategoryName());
		placeSize.setText(getCategorySize());
		
		loadPlace();
	}


	private void loadPlace()
	{
		// TODO Auto-generated method stub
		AsyncTask<String, Void, List<Place>> task = new AsyncTask<String, Void, List<Place>>(){

			@Override
			protected List<Place> doInBackground(String... params)
			{
				return getAllPlace();				
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
				
				allPlaceList = resultList;
				
				// set data and reload place list
				refreshPlaceView(resultList);
				
				// TODO update sub category name and key and count
				
				super.onPostExecute(resultList);
			}



			@Override
			protected void onPreExecute()
			{
				// TODO show loading here
				super.onPreExecute();
			}
			
			
		};
		
		task.execute();
	}
	
	private boolean isMatchSubCategory(Place place){
		if (isSupportSubcategory() && !selectAllSubCategory()){
			boolean inSubCategory = false;
			if (subCatSelectKey != null){
				for (int i=0; i<subCatSelectKey.length; i++){
					if (place.getSubCategoryId() == subCatSelectKey[i]){
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
	
	private boolean isMatchPrice(Place place){
		if (isSupportPrice() && !selectAllprice()){
			boolean inPrice = false;
			if (priceSelect != null){
				for (int i=0; i<priceSelect.length; i++){
					if (place.getPriceRank() == priceSelect[i]){
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
		if (isSupportArea()&& !selectAllArea()){
			boolean inArea = false;
			if (areaSelect != null){
				for (int i=0; i<areaSelect.length; i++){
					if (place.getAreaId()== areaSelect[i]){
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
		if (isSupportService()&& !selectAllService()){
			boolean inService = false;
			if (serviceSelect != null){
				for (int i=0; i<serviceSelect.length; i++){
					for(int providedServiceId :place.getProvidedServiceIdList())
					{
						if (providedServiceId == serviceSelect[i]){
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
	
	
	
	
	
	
	
	private void filterPlaceList(){
		
		List<Place> newList = new ArrayList<Place>();
		
		// step 1 : filter place
		List<Place> origList = allPlaceList;
		for (Place place : origList){
			
			// check sub category match
			if (!isMatchSubCategory(place)){
				continue;
			}else if (!isMatchPrice(place)) {
				continue;
			}else if (!isMatchArea(place)) {
				continue;
			}else if (!isMatchService(place)) {
				
			}
			
			// check 
			
			
			
			
			newList.add(place);
		}
		
		// step 2 : sort places by conditions
		Comparator<Place> comparator = getSortComparator(sortPosition);
		if (comparator != null){
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
            AlertDialog.Builder builder=new AlertDialog.Builder(CommonPlaceActivity.this)
            	.setSingleChoiceItems(subCatName, subCatPosition, new DialogInterface.OnClickListener() {
            	
                @Override
                public void onClick(DialogInterface dialog, int position) 
                {
                	subCatPosition = position;
                	if (subCatPosition < 0 || subCatPosition >= subCatKey.length){
                		return;
                	}
                	
                	// TODO support multiple selection
                	subCatSelectKey = new int[1];
                	subCatSelectKey[0] = subCatKey[subCatPosition];
                	filterPlaceList();              	
                    dialog.dismiss();
                }
            }).setTitle(CommonPlaceActivity.this.getResources().getString(R.string.sub_category));
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
            AlertDialog.Builder builder=new AlertDialog.Builder(CommonPlaceActivity.this)
            	.setSingleChoiceItems(price, pricePosition, new DialogInterface.OnClickListener() {
            	
                @Override
                public void onClick(DialogInterface dialog, int position) 
                {
                	pricePosition = position;
                	if (pricePosition < 0 || pricePosition >= price.length){
                		return;
                	}
                	
                	// TODO support multiple selection
                	priceSelect = new int[1];
                	priceSelect[0] = pricePosition;
                	filterPlaceList();              	
                    dialog.dismiss();
                }
            }).setTitle(CommonPlaceActivity.this.getResources().getString(R.string.price));
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
            AlertDialog.Builder builder=new AlertDialog.Builder(CommonPlaceActivity.this)
            	.setSingleChoiceItems(areaName, areaPosition, new DialogInterface.OnClickListener() {
            	
                @Override
                public void onClick(DialogInterface dialog, int position) 
                {
                	areaPosition = position;
                	if (areaPosition < 0 || areaPosition >= areaName.length){
                		return;
                	}
                	
                	// TODO support multiple selection
                	areaSelect = new int[1];
                	areaSelect[0] = areaID[areaPosition];
                	filterPlaceList();              	
                    dialog.dismiss();
                }
            }).setTitle(CommonPlaceActivity.this.getResources().getString(R.string.area));
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
            AlertDialog.Builder builder=new AlertDialog.Builder(CommonPlaceActivity.this)
            	.setSingleChoiceItems(serviceName, servicePosition, new DialogInterface.OnClickListener() {
            	
                @Override
                public void onClick(DialogInterface dialog, int position) 
                {
                	servicePosition = position;
                	if (servicePosition < 0 || servicePosition >= serviceName.length){
                		return;
                	}
                	
                	// TODO support multiple selection
                	serviceSelect = new int[1];
                	serviceSelect[0] = serviceID[servicePosition];
                	filterPlaceList();              	
                    dialog.dismiss();
                }
            }).setTitle(CommonPlaceActivity.this.getResources().getString(R.string.service));
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
            AlertDialog.Builder builder=new AlertDialog.Builder(CommonPlaceActivity.this)
            .setSingleChoiceItems(sortDisplayName, sortPosition,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) 
                {
                	sortPosition = position;
                	filterPlaceList();
                    dialog.cancel();
                }
            }).setTitle(CommonPlaceActivity.this.getResources().getString(R.string.sort));
            dialog = builder.create();
            dialog.show();
		}
	};
	
	// TODO move this method to common
	private String getCategorySize()
	{
		int size = 0;
		if (placeListAdapter.getPlaceList() != null)
			size = placeListAdapter.getPlaceList().size();
		
		String sizeString ="("+size+")";
		return sizeString;
	}
}
