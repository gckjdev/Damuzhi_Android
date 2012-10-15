/**  
        * @title CommonItemizedOverlay.java  
        * @package com.damuzhi.travel.activity.common.mapview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-7-9 下午4:10:26  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common.mapview;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.damuzhi.travel.activity.place.CommonPlaceDetailActivity;
import com.damuzhi.travel.mission.more.BrowseHistoryMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-9 下午4:10:26  
 */

public class CommonItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CommonOverlayItem>
{

	private ArrayList<CommonOverlayItem> m_overlays = new ArrayList<CommonOverlayItem>();
	private Context c;
	
	public CommonItemizedOverlay(Drawable defaultMarker, MapView mapView) {	
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}

	public void addOverlay(CommonOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected CommonOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CommonOverlayItem item) {
		Place place = item.getPlace();
		if(place !=null)
		{
			BrowseHistoryMission.getInstance().addBrowseHistory(place);
			Intent intent = new Intent();
			intent.putExtra(ConstantField.PLACE_DETAIL, place.toByteArray());
			Class detailPlaceClass = CommonPlaceDetailActivity.getClassByPlaceType(place.getCategoryId());
			intent.setClass(c, detailPlaceClass);
			c.startActivity(intent);
			
		}		
		return true;
	}

	@Override
	protected BalloonOverlayView<CommonOverlayItem> createBalloonOverlayView() {
		int index = getBalloonBottomOffset();
		CommonOverlayItem commonOverlayItem= m_overlays.get(index);
		//if(commonOverlayItem.getPlace() != null)
		
			return new CommonOverlayView<CommonOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
		
		
	}


}
