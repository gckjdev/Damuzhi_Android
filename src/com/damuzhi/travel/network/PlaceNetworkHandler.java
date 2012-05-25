/**  
        * @title PlaceNetworkHandler.java  
        * @package com.damuzhi.travel.network  
        * @description   
        * @author liuxiaokun  
        * @update 2012-5-25 下午3:14:31  
        * @version V1.0  
        */
package com.damuzhi.travel.network;

import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-5-25 下午3:14:31  
 */

public class PlaceNetworkHandler
{


	public static int categoryIdToObjectType(int categoryId)
	{
		// TODO Auto-generated method stub
		int objectType = 0;
		switch (categoryId)
		{
		case PlaceCategoryType.PLACE_SPOT_VALUE:
			objectType = Integer.parseInt(ConstantField.SPOT);
			break;
		case PlaceCategoryType.PLACE_HOTEL_VALUE:
			objectType = Integer.parseInt(ConstantField.HOTEL);
			break;
		case PlaceCategoryType.PLACE_RESTRAURANT_VALUE:
			objectType = Integer.parseInt(ConstantField.RESTAURANT);
			break;
		case PlaceCategoryType.PLACE_SHOPPING_VALUE:
			objectType = Integer.parseInt(ConstantField.SHOPPING);
			break;
		case PlaceCategoryType.PLACE_ENTERTAINMENT_VALUE:
			objectType = Integer.parseInt(ConstantField.ENTERTAINMENT);
			break;
		default:
			break;
		}
		return objectType;
	}

}
