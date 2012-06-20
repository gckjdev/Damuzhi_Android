/**  
        * @title MenuActivity.java  
        * @package com.damuzhi.travel.activity.adapter.place  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-14 下午2:23:17  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.common;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.place.CommonPlaceActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-14 下午2:23:17  
 */

public class MenuActivity extends Activity
{
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case R.id.menu_refresh:
			
			break;
		case R.id.menu_help:
			Intent  intent = new Intent();
			intent.setClass(MenuActivity.this, HelpActiviy.class);
			startActivity(intent);
			break;
		case R.id.menu_feedback:
			
			break;
		case R.id.menu_about:
			
			break;
		case R.id.menu_exit:
			
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
