package com.damuzhi.travel.activity.more;




import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelActivity;
import com.damuzhi.travel.model.app.AppManager;

public class MoreActivity extends TravelActivity implements OnClickListener
{
	ViewGroup openCtiyGroup;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		openCtiyGroup = (ViewGroup) findViewById(R.id.open_city_group);
		TextView currentCityName = (TextView) findViewById(R.id.current_city_name);
		currentCityName.setText(AppManager.getInstance().getCurrentCityName());
		openCtiyGroup.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		Intent intent = new Intent();
		switch (v.getId())
		{
		case R.id.open_city_group:
			intent.setClass(MoreActivity.this, OpenCityDataActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
