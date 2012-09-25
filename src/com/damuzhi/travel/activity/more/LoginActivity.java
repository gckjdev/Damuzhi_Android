package com.damuzhi.travel.activity.more;

import com.damuzhi.travel.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LoginActivity extends Activity {

	
	private TextView registerTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_login);
		registerTextView = (TextView) findViewById(R.id.free_register);
		registerTextView.setOnClickListener(registerOnClickListener);
	}
	
	
	private OnClickListener registerOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
		}
	};

}
