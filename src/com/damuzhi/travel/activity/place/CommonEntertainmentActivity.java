/**  
        * @title CommonEntertainmentActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-28 上午11:02:14  
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

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.PlaceMission;
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
 * @update 2012-5-28 上午11:02:14  
 */

public class CommonEntertainmentActivity extends CommonPlaceActivity
{

	@Override
	public List<Place> getAllPlace(Activity activity)
	{
		return PlaceMission.getInstance().getAllPlace(PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE,activity);
	}

	@Override
	public String getCategoryName()
	{
		return getString(R.string.entertainment);
	}

	@Override
	public int getCategoryType()
	{
		return PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE;
	}

	@Override
	public void createFilterButtons(ViewGroup spinner)
	{
		LayoutInflater inflater = getLayoutInflater();
		View subCategorySpinner = inflater.inflate(R.layout.sub_category_spinner, null);
		View areaSpinner = inflater.inflate(R.layout.area_spinner, null);
		View sortSpinner = inflater.inflate(R.layout.sort_spinner, null);
		
		LinearLayout subCateLayout = (LinearLayout) subCategorySpinner.findViewById(R.id.sub_cate_spinner);
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
		
		LinearLayout sortLayout = (LinearLayout) sortSpinner.findViewById(R.id.sort_spinner);
		RelativeLayout.LayoutParams sortParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		sortParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		sortParams.addRule(RelativeLayout.RIGHT_OF,R.id.area_spinner);
		sortParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		sortLayout.setLayoutParams(sortParams);
		int cityID = AppManager.getInstance().getCurrentCityId();
		sortDisplayName = getResources().getStringArray(R.array.entertainment);
		subCatName = AppManager.getInstance().getSubCatNameList(PlaceCategoryType.PLACE_ENTERTAINMENT);
		subCatKey = AppManager.getInstance().getSubCatKeyList(PlaceCategoryType.PLACE_ENTERTAINMENT);
		areaID = AppManager.getInstance().getCityAreaKeyList(cityID);
		areaName = AppManager.getInstance().getCityAreaNameList(cityID);
		spinner.addView(subCategorySpinner);
		spinner.addView(areaSpinner);
		spinner.addView(sortSpinner);
		
		subCategorySpinner.setOnClickListener(subCategoryClickListener);
		areaSpinner.setOnClickListener(areaClickListener);
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
		return false;
	}

	@Override
	Comparator<Place> getSortComparator(int index)
	{
		switch (index)
		{
		case 0:
			return new ComparatorRank();			
		case 1:
			return new ComparatorPrice();			
		case 2:
			return new ComparatorPriceContrary();		
		case 3:
			return new ComparatorDistance(TravelApplication.getInstance().getLocation());
		}
		return null;
	}

}
