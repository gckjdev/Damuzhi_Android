/**  
        * @title CommonShoppingActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-28 上午10:22:57  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.util.TravelUtil.ComparatorDistance;
import com.damuzhi.travel.util.TravelUtil.ComparatorPrice;
import com.damuzhi.travel.util.TravelUtil.ComparatorPriceContrary;
import com.damuzhi.travel.util.TravelUtil.ComparatorRank;
import com.damuzhi.travel.util.TravelUtil.ComparatorStartRank;
import com.damuzhi.travel.R;
/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-28 上午10:22:57  
 */

public class CommonShoppingActivity extends CommonPlaceActivity
{

	@Override
	public List<Place> getAllPlace(Activity activity)
	{
		//TravelApplication.getInstance().addActivity(this);
		//ActivityManger.getInstance().addActivity(this);
		return PlaceMission.getInstance().getAllPlace(PlaceCategoryType.PLACE_SHOPPING_VALUE,activity);
	}

	@Override
	public String getCategoryName()
	{
		return getString(R.string.shopping);
	}

	@Override
	public int getCategoryType()
	{
		return PlaceCategoryType.PLACE_SHOPPING_VALUE;
	}

	@Override
	public void createFilterButtons(ViewGroup spinner)
	{
		LayoutInflater inflater = getLayoutInflater();
		View areaSpinner = inflater.inflate(R.layout.area_spinner, null);		
		View sortSpinner = inflater.inflate(R.layout.sort_spinner, null);
		
		LinearLayout areaLayout = (LinearLayout) areaSpinner.findViewById(R.id.area_spinner);
		RelativeLayout.LayoutParams areaParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		areaParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		areaParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		areaParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		
		LinearLayout sortLayout = (LinearLayout) sortSpinner.findViewById(R.id.sort_spinner);
		RelativeLayout.LayoutParams sortParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		sortParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		sortParams.addRule(RelativeLayout.RIGHT_OF,R.id.area_spinner);
		sortParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		sortLayout.setLayoutParams(sortParams);
		areaLayout.setLayoutParams(areaParams);
		
		spinner.addView(areaSpinner);		
		spinner.addView(sortSpinner);
		areaSpinner.setOnClickListener(areaClickListener);
		sortSpinner.setOnClickListener(sortClickListener);

	}

	@Override
	boolean isSupportSubcategory()
	{
		return false;
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
			return new  ComparatorRank();
		case 1:
			return new ComparatorDistance(TravelApplication.getInstance().getLocation());
		}
			return null;
	}

	@Override
	public void initFilterButtonsData()
	{
		sortDisplayName = getResources().getStringArray(R.array.shopping);		
		int cityID = AppManager.getInstance().getCurrentCityId();
		areaID = PlaceMission.getInstance().getCityAreaKeyList(cityID);
		areaName = PlaceMission.getInstance().getCityAreaNameList(cityID);
		
	}

	
	@Override
	public int getPlaceTotalCount()
	{
		int totalCount = PlaceMission.getInstance().getPlaceTotalCount();
		return totalCount;
	}
	
	
	/*@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityManger.getInstance().finishActivity();
	}*/
}
