/**  
        * @title CommonHotelDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-29 下午2:31:25  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.google.protobuf.InvalidProtocolBufferException;

import android.os.Bundle;
import android.util.Log;
import com.damuzhi.travel.R;
/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-29 下午2:31:25  
 */

public class CommonHotelDetailActivity extends CommonPlaceDetailActivity
{

	private static final String TAG = "CommonHotelDetailActivity";

	@Override
	public Place getPlaceById()
	{
		Place place = null;
		try
		{
			place = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));
		} catch (InvalidProtocolBufferException e)
		{
			Log.e(TAG, "<CommonHotelDetailActivity> get place data but catch exception = "+e.toString(),e);
		}
		super.placeId = place.getPlaceId();
		return place;
	}

	@Override
	public String getPlaceIntroTitle()
	{
		return getString(R.string.hotelIntro);
	}

	@Override
	public boolean isSupportSpecialTrafficStyle()
	{
		return true;
	}

	@Override
	public boolean isSupportTicket()
	{
		return false;
	}

	@Override
	public boolean isSupportKeyWords()
	{
		return false;
	}

	@Override
	public boolean isSupportTips()
	{
		return false;
	}

	@Override
	public boolean isSupportHotelStart()
	{
		return true;
	}

	@Override
	public boolean isSupportService()
	{
		return true;
	}

	@Override
	public boolean isSupportRoomPrice()
	{
		return true;
	}

	@Override
	public boolean isSupportOpenTime()
	{
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	
}
