package com.damuzhi.travel.activity.more;

import com.damuzhi.travel.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterVerificationActivity extends Activity {

	private EditText verifiCodeEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_register_verification);
		String phoneNum = getIntent().getStringExtra("phoneNum");
		TextView phoneNumTextView = (TextView) findViewById(R.id.phone_num);
		verifiCodeEditText = (EditText) findViewById(R.id.verification_code); 
		phoneNumTextView.setText(phoneNum);
	}

	
}
