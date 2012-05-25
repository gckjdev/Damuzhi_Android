/**  
        * @title CommonPlaceInterface.java  
        * @package com.damuzhi.travel.activity.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-24 下午3:53:59  
        * @version V1.0  
        */
package com.damuzhi.travel.activity.place;

import java.util.List;

import com.damuzhi.travel.protos.PlaceListProtos.Place;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-24 下午3:53:59  
 */

public interface CommonPlaceInterface
{
	public List<Place> getAllPlace();
 
	public String getCategoryName();
}
