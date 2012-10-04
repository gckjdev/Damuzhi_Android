/**  
        * @title CommonHappyRoute.java  
        * @package com.damuzhi.travel.activity.happyRoute  
        * @description   
        * @author liuxiaokun  
        * @update 2012-9-28 下午3:56:40  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.happyRoute;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;

import android.app.Activity;
import android.os.Bundle;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-9-28 下午3:56:40  
 */

public class CommonHappyRouteActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_happy_route);
		ActivityMange.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
	
	

}
