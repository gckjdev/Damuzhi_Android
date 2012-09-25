/**  
        * @title CommonSpotDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-30 上午11:30:23  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;


import android.util.Log;

import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.place.PlaceMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.google.protobuf.InvalidProtocolBufferException;
import com.damuzhi.travel.R;
/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-30 上午11:30:23  
 */

public class CommonSpotDetailActivity extends CommonPlaceDetailActivity
{

	private static final String TAG = "CommonSpotDetailActivity";

	@Override
	public Place getPlaceById()
	{
		//TravelApplication.getInstance().addActivity(this);
		Place place = null;
		try
		{
			place = Place.parseFrom(getIntent().getByteArrayExtra(ConstantField.PLACE_DETAIL));
		} catch (InvalidProtocolBufferException e)
		{
			Log.e(TAG, "<CommonSpotDetailActivity> get place data but catch exception = "+e.toString(),e);
		}
		super.placeId = place.getPlaceId();
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
