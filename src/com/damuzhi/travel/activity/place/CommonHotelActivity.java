/**  
        * @title CommonHotelActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-25 下午4:59:20  
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.protos.AppProtos.App;
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
 * @update 2012-5-25 下午4:59:20  
 */

public class CommonHotelActivity extends CommonPlaceActivity
{

	
	@Override
	public List<Place> getAllPlace(Activity activity)
	{		
		//TravelApplication.getInstance().addActivity(this);
		//ActivityManger.getInstance().addActivity(this);
		return PlaceMission.getInstance().getAllPlace(PlaceCategoryType.PLACE_HOTEL_VALUE,activity);
	}

	
	@Override
	public String getCategoryName()
	{
		
		return getString(R.string.hotel);
	}
	
	@Override
	public int getCategoryType()
	{
		return PlaceCategoryType.PLACE_HOTEL_VALUE;
	}

	
	@Override
	public void createFilterButtons(ViewGroup spinner)
	{
		LayoutInflater inflater = getLayoutInflater();
		View priceSpinner = inflater.inflate(R.layout.price_spinner, null);
		View areaSpinner = inflater.inflate(R.layout.area_spinner, null);
		View serviceSpinner = inflater.inflate(R.layout.service_spinner, null); 
		View sortSpinner = inflater.inflate(R.layout.sort_spinner, null);
		
		LinearLayout priceLayout = (LinearLayout) priceSpinner.findViewById(R.id.price_spinner);
		RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		priceParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		priceParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		priceParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		priceLayout.setLayoutParams(priceParams);			
		
		
		LinearLayout areaLayout = (LinearLayout) areaSpinner.findViewById(R.id.area_spinner);
		RelativeLayout.LayoutParams areaParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		areaParams.setMargins((int)getResources().getDimension(R.dimen.spinner_margin), 0, 0, 0);
		areaParams.addRule(RelativeLayout.RIGHT_OF,R.id.price_spinner);
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
		
		
		spinner.addView(priceSpinner);
		spinner.addView(areaSpinner);
		spinner.addView(serviceSpinner);
		spinner.addView(sortSpinner);
		
		priceSpinner.setOnClickListener(priceClickListener);
		areaSpinner.setOnClickListener(areaClickListener);
		serviceSpinner.setOnClickListener(serviceClickListener);
		sortSpinner.setOnClickListener(sortClickListener);
		

	}

	
	@Override
	boolean isSupportSubcategory()
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
			return new  ComparatorStartRank();		
		case 2:
			return new  ComparatorPrice();			
		case 3:
			return new  ComparatorPriceContrary();
		case 4:
			return new ComparatorDistance(TravelApplication.getInstance().getLocation());
		}
			return null;
	}


	
	@Override
	boolean isSupportPrice()
	{
		return true;
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
	public void initFilterButtonsData()
	{
		int cityID = AppManager.getInstance().getCurrentCityId();
		sortDisplayName = getResources().getStringArray(R.array.hotel);		
		price = PlaceMission.getInstance().getPriceRank(cityID);
		priceId = PlaceMission.getInstance().getPriceId(cityID);
		areaID = PlaceMission.getInstance().getCityAreaKeyList(cityID);
		areaName = PlaceMission.getInstance().getCityAreaNameList(cityID);
		serviceID = PlaceMission.getInstance().getProvidedServiceKeyList(PlaceCategoryType.PLACE_HOTEL);
		serviceName = PlaceMission.getInstance().getProvidedServiceNameList(PlaceCategoryType.PLACE_HOTEL);
		
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
		ActivityManger.getInstance().AppExit(this);
	}*/
}
