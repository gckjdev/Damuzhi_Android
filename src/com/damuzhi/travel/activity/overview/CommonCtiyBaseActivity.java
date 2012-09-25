/**  
        * @title CommonCtiyBaseOverViewActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 上午10:29:03  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.overview;

import android.app.Activity;

import com.damuzhi.travel.activity.common.ActivityManger;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.overview.OverviewMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.AppProtos.PlaceCategoryType;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverviewType;
import com.damuzhi.travel.R;


public class CommonCtiyBaseActivity extends CommonOverViewActivity
{

	@Override
	public CommonOverview loadData(Activity activity)
	{
		//TravelApplication.getInstance().addActivity(this);
		ActivityManger.getInstance().addActivity(this);
		return OverviewMission.getInstance().getOverview(CommonOverviewType.CITY_BASIC_VALUE,AppManager.getInstance().getCurrentCityId(),activity);
		
	}

	@Override
	public boolean isSupportViewpager()
	{
		return true;
	}

	@Override
	public String setTitleName()
	{
		return  getResources().getString(R.string.city_base);
	}

}
