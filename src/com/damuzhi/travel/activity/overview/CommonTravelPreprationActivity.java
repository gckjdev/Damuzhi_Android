/**  
        * @title CommonTravelPreprationActivity.java  
        * @package com.damuzhi.travel.activity.overview  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午12:07:58  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.overview;

import android.app.Activity;

import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.overview.OverviewMission;
import com.damuzhi.travel.model.app.AppManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverview;
import com.damuzhi.travel.protos.CityOverviewProtos.CommonOverviewType;
import com.damuzhi.travel.R;
/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-14 下午12:07:58  
 */

public class CommonTravelPreprationActivity extends CommonOverViewActivity
{

	@Override
	public CommonOverview loadData(Activity activity)
	{
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		return OverviewMission.getInstance().getOverview(CommonOverviewType.TRAVEL_PREPRATION_VALUE,AppManager.getInstance().getCurrentCityId(),activity);
	}

	@Override
	public boolean isSupportViewpager()
	{
		return false;
	}

	@Override
	public String setTitleName()
	{
		return getString(R.string.travel_prepration);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
}
