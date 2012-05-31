/**  
        * @title CommonRestaurantDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-30 下午5:27:36  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;



import com.damuzhi.travel.R;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.protos.PlaceListProtos.Place;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-30 下午5:27:36  
 */

public class CommonRestaurantDetailActivity extends CommonPlaceDetailActivity
{

	@Override
	public Place getPlaceById()
	{
		int placeId = getIntent().getIntExtra("placeId", -1);
		super.placeId = placeId;
		Place place = PlaceMission.getInstance().getPlaceById(placeId);
		return place;
	}

	@Override
	public String getPlaceIntroTitle()
	{
		return getString(R.string.restaurant_intro);
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
		return false;
	}

	@Override
	public boolean isSupportHotelStart()
	{
		return false;
	}

	@Override
	public boolean isSupportService()
	{
		return true;
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
		return true;
	}

	@Override
	public boolean isSupportAvgPrice()
	{
		return true;
	}

	@Override
	public boolean isSupportSpecialFood()
	{
		return true;
	}

}
