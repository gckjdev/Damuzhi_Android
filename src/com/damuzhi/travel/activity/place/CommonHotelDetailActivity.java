/**  
        * @title CommonHotelDetailActivity.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-29 下午2:31:25  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.place;

import com.damuzhi.travel.R;
import com.damuzhi.travel.mission.PlaceMission;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import android.os.Bundle;
import android.util.Log;

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
		int placeId = getIntent().getIntExtra("placeId", -1);
		super.placeId = placeId;
		Place place = PlaceMission.getInstance().getPlaceById(placeId);
		return place;
	}

	@Override
	public String getPlaceIntroTitle()
	{
		// TODO Auto-generated method stub
		return getString(R.string.hotelIntro);
	}

	@Override
	public boolean isSupportSpecialTrafficStyle()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupportTicket()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupportKeyWords()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSupportTips()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSupportHotelStart()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSupportService()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSupportRoomPrice()
	{
		// TODO Auto-generated method stub
		return true;
	}

}
