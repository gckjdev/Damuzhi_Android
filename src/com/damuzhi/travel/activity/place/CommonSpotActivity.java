/**  
        * @title SpotActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午4:07:02  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.adapter.place.CommonPlaceListAdapter;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.damuzhi.travel.util.TravelUtil.ComparatorPrice;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午4:07:02  
 */

public class CommonSpotActivity extends CommonPlaceActivity
{

	
	@Override
	public List<Place> getAllPlace(Activity activity)
	{
		TravelApplication.getInstance().addActivity(this);
		return PlaceMission.getInstance().getAllPlace(PlaceCategoryType.PLACE_SPOT_VALUE,activity);
	}
	
	@Override
	public String getCategoryName()
	{
		return getString(R.string.scenery);
	}
	
	@Override
	public void createFilterButtons(ViewGroup spinner)
	{
		LayoutInflater inflater = getLayoutInflater();
		View subCategorySpinner = inflater.inflate(R.layout.sub_category_spinner, null);
		View sortSpinner = inflater.inflate(R.layout.sort_spinner, null);
		
		LinearLayout subCateLayout = (LinearLayout) subCategorySpinner.findViewById(R.id.sub_cate_spinner);
		RelativeLayout.LayoutParams subCateparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		subCateparams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		subCateparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		subCateparams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		subCateLayout.setLayoutParams(subCateparams);			
		
		LinearLayout sortLayout = (LinearLayout) sortSpinner.findViewById(R.id.sort_spinner);
		RelativeLayout.LayoutParams sortParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		sortParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		sortParams.addRule(RelativeLayout.RIGHT_OF,R.id.sub_cate_spinner);
		sortParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		sortLayout.setLayoutParams(sortParams);
		
		sortDisplayName = getResources().getStringArray(R.array.spot);
		subCatName = AppManager.getInstance().getSubCatNameList(PlaceCategoryType.PLACE_SPOT);
		subCatKey = AppManager.getInstance().getSubCatKeyList(PlaceCategoryType.PLACE_SPOT);
		spinner.addView(subCategorySpinner);
		spinner.addView(sortSpinner);
		subCategorySpinner.setOnClickListener(subCategoryClickListener);
		sortSpinner.setOnClickListener(sortClickListener);
	}
	
	@Override
	public int getCategoryType()
	{
		return PlaceCategoryType.PLACE_SPOT_VALUE;
	}


	@Override
	boolean isSupportSubcategory()
	{
		return true;
	}

	@Override
	Comparator<Place> getSortComparator(int index)
	{
		switch (index)
		{
		case 0:
			return new ComparatorRank();
		case 1:
			return new ComparatorDistance(TravelApplication.getInstance().getLocation());
		case 2:
			return new ComparatorPrice();
		}
		return null;
	}

	
	@Override
	boolean isSupportPrice()
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	boolean isSupportArea()
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	boolean isSupportService()
	{
		// TODO Auto-generated method stub
		return false;
	}


	
	
	
	

}
