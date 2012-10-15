/**  
        * @title CommonOverlayItem.java  
        * @package com.damuzhi.travel.activity.common.mapview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-7-9 下午4:11:43  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common.mapview;

import com.damuzhi.travel.protos.PlaceListProtos.Place;
import com.damuzhi.travel.protos.TouristRouteProtos.DepartPlace;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-7-9 下午4:11:43  
 */

public class CommonOverlayItem extends OverlayItem
{

	/**  
	* Constructor Method   
	* @param point
	* @param title
	* @param snippet  
	*/
	private Place place;
	private DepartPlace departPlace;
	
	public CommonOverlayItem(GeoPoint point, String title, String snippet,Place place,DepartPlace departPlace)
	{
		super(point, title, snippet);
		// TODO Auto-generated constructor stub
		this.place = place;
		this.departPlace = departPlace;
	}
	public Place getPlace()
	{
		return place;
	}
	public void setPlace(Place place)
	{
		this.place = place;
	}
	public DepartPlace getDepartPlace()
	{
		return departPlace;
	}
	public void setDepartPlace(DepartPlace departPlace)
	{
		this.departPlace = departPlace;
	}

}
