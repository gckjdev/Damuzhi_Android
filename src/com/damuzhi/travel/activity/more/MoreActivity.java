package com.damuzhi.travel.activity.more;




import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.TravelActivity;

public class MoreActivity extends TravelActivity implements OnClickListener
{
	TextView openCtiy;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
		openCtiy = (TextView) findViewById(R.id.more_open_city);
		openCtiy.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		TextView textView = (TextView) v;
		Intent intent = new Intent();
		switch (textView.getId())
		{
		case R.id.more_open_city:
			intent.setClass(MoreActivity.this, OpenCityDataActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
