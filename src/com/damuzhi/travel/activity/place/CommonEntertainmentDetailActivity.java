/**  
        * @title CommonEntertainmentDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-31 上午10:21:33  
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
 * @update 2012-5-31 上午10:21:33  
 */

public class CommonEntertainmentDetailActivity extends
		CommonPlaceDetailActivity
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
		return getString(R.string.entertrainment_intro);
	}

	@Override
	public boolean isSupportSpecialTrafficStyle()
	{
		return false;
	}

	@Override
	public boolean isSupportTicket()
	{
		return false;
	}

	@Override
	public boolean isSupportKeyWords()
	{
		return true;
	}

	@Override
	public boolean isSupportTips()
	{
		tipsTitle = getString(R.string.entertainment_tips);
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
		return true;
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
