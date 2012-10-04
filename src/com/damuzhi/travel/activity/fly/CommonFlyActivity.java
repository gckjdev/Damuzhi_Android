/**  
        * @title CommonFlyActivity.java  
        * @package com.damuzhi.travel.activity.fly  
        * @description   
        * @author liuxiaokun  
        * @update 2012-9-28 下午3:54:44  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.fly;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;

import android.app.Activity;
import android.os.Bundle;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-9-28 下午3:54:44  
 */

public class CommonFlyActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_fly);
		ActivityMange.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}

}
