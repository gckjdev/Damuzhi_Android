/**  
        * @title CommonRestaurantDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-30 下午5:27:36  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;



import android.util.Log;

import com.damuzhi.travel.R;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.google.protobuf.InvalidProtocolBufferException;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-30 下午5:27:36  
 */

public class CommonRestaurantDetailActivity extends CommonPlaceDetailActivity
{

	private static final String TAG = "CommonRestaurantDetailActivity";

	@Override
	public Place getPlaceById()
	{
		Place place = null;
		try
		{
			place = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));
		} catch (InvalidProtocolBufferException e)
		{
			Log.e(TAG, "<CommonRestaurantDetailActivity> get place data but catch exception = "+e.toString(),e);
		}
		super.placeId = place.getPlaceId();
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

	@Override
	public boolean isSupportPark()
	{
		return false;
	}

}
