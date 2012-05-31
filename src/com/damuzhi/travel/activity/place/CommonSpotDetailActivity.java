/**  
        * @title CommonSpotDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-30 上午11:30:23  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;


import com.damuzhi.travel.R;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-30 上午11:30:23  
 */

public class CommonSpotDetailActivity extends CommonPlaceDetailActivity
{

	@Override
	public Place getPlaceById()
	{
		int placeId = getIntent().getIntExtra(ConstantField.PLACE_CATEGORY_ID, -1);
		super.placeId = placeId;
		Place place = PlaceMission.getInstance().getPlaceById(placeId);
		return place;
	}

	@Override
	public String getPlaceIntroTitle()
	{
		return getString(R.string.spot_intro);
	}

	@Override
	public boolean isSupportSpecialTrafficStyle()
	{
		return false;
	}

	@Override
	public boolean isSupportTicket()
	{
		return true;
	}

	@Override
	public boolean isSupportKeyWords()
	{
		return false;
	}

	@Override
	public boolean isSupportTips()
	{
		tipsTitle = getString(R.string.tour_tips);
		return true;
	}

	@Override
	public boolean isSupportHotelStart()
	{		
		return false;
	}

	@Override
	public boolean isSupportService()
	{
		return false;
	}

	@Override
	public boolean isSupportRoomPrice()
	{
		return false;
	}

	@Override
	public boolean isSupportOpenTime()
	{
		return true;
	}

	@Override
	public boolean isSupportFood()
	{
		return false;
	}

	@Override
	public boolean isSupportAvgPrice()
	{
		return false;
	}

	@Override
	public boolean isSupportSpecialFood()
	{
		return false;
	}

	@Override
	public boolean isSupportPark()
	{
		return false;
	}

}