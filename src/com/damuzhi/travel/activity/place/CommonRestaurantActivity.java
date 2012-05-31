/**  
        * @title CommonRestaurantActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-26 上午11:14:56  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.damuzhi.travel.util.TravelUtil.ComparatorPrice;
import com.damuzhi.travel.util.TravelUtil.ComparatorPriceContrary;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-26 上午11:14:56  
 */

public class CommonRestaurantActivity extends CommonPlaceActivity
{

	
	@Override
	public List<Place> getAllPlace(Activity activity)
	{
		return PlaceMission.getInstance().getAllPlace(PlaceCategoryType.PLACE_RESTRAURANT_VALUE,activity);
	}

	
	@Override
	public String getCategoryName()
	{
		return getString(R.string.restaurant);
	}

	
	@Override
	public int getCategoryType()
	{
		return PlaceCategoryType.PLACE_RESTRAURANT_VALUE;
	}

	
	@Override
	public void createFilterButtons(ViewGroup spinner)
	{
		LayoutInflater inflater = getLayoutInflater();
		View subCategorySpinner = inflater.inflate(R.layout.sub_category_spinner, null);
		View areaSpinner = inflater.inflate(R.layout.area_spinner, null);
		View serviceSpinner = inflater.inflate(R.layout.service_spinner, null); 
		View sortSpinner = inflater.inflate(R.layout.sort_spinner, null);
		
		LinearLayout subCateLayout = (LinearLayout) subCategorySpinner.findViewById(R.id.sub_cate_spinner);
		TextView subCateTitle = (TextView) subCategorySpinner.findViewById(R.id.sub_cate_title);
		subCateTitle.setText(R.string.food);
		RelativeLayout.LayoutParams subCateparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		subCateparams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		subCateparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		subCateparams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		subCateLayout.setLayoutParams(subCateparams);	
				
		LinearLayout areaLayout = (LinearLayout) areaSpinner.findViewById(R.id.area_spinner);
		RelativeLayout.LayoutParams areaParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		areaParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		areaParams.addRule(RelativeLayout.RIGHT_OF,R.id.sub_cate_spinner);
		areaParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		areaLayout.setLayoutParams(areaParams);	
		
		LinearLayout serviceLayout = (LinearLayout) serviceSpinner.findViewById(R.id.service_spinner);
		RelativeLayout.LayoutParams serviceParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		serviceParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		serviceParams.addRule(RelativeLayout.RIGHT_OF,R.id.area_spinner);
		serviceParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		serviceLayout.setLayoutParams(serviceParams);	
		
		LinearLayout sortLayout = (LinearLayout) sortSpinner.findViewById(R.id.sort_spinner);
		RelativeLayout.LayoutParams sortParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		sortParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		sortParams.addRule(RelativeLayout.RIGHT_OF,R.id.service_spinner);
		sortParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		sortLayout.setLayoutParams(sortParams);
		
		int cityID = Integer.parseInt(AppManager.getInstance().getCurrentCityId());
		sortDisplayName = getResources().getStringArray(R.array.restaurant);
		subCatName = AppManager.getInstance().getSubCatNameList(PlaceCategoryType.PLACE_RESTRAURANT);
		subCatKey = AppManager.getInstance().getSubCatKeyList(PlaceCategoryType.PLACE_RESTRAURANT);
		areaID = AppManager.getInstance().getCityAreaKeyList(cityID);
		areaName = AppManager.getInstance().getCityAreaNameList(cityID);
		serviceID = AppManager.getInstance().getProvidedServiceKeyList(PlaceCategoryType.PLACE_RESTRAURANT);
		serviceName = AppManager.getInstance().getProvidedServiceNameList(PlaceCategoryType.PLACE_RESTRAURANT);
		
		spinner.addView(subCategorySpinner);
		spinner.addView(areaSpinner);
		spinner.addView(serviceSpinner);
		spinner.addView(sortSpinner);	
		
		subCategorySpinner.setOnClickListener(subCategoryClickListener);
		areaSpinner.setOnClickListener(areaClickListener);
		serviceSpinner.setOnClickListener(serviceClickListener);
		sortSpinner.setOnClickListener(sortClickListener);
	}

	
	@Override
	boolean isSupportSubcategory()
	{
		return true;
	}

	
	@Override
	boolean isSupportPrice()
	{
		return false;
	}

	
	@Override
	boolean isSupportArea()
	{
		return true;
	}

	
	@Override
	boolean isSupportService()
	{
		return true;
	}

	
	@Override
	Comparator<Place> getSortComparator(int index)
	{
		switch (index)
		{
		case 0:
			 return new  ComparatorRank();
		case 1:
			 return new  ComparatorPrice();
		case 2:
			return  new  ComparatorPriceContrary();
		case 3:
			return  new  ComparatorDistance(TravelApplication.getInstance().getLocation());
		}
		return null;
	}

}
