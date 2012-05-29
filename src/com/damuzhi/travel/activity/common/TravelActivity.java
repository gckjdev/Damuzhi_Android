package com.damuzhi.travel.activity.common;

import com.damuzhi.travel.R;
import com.google.android.maps.MapActivity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TravelActivity extends MapActivity
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

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	

}
