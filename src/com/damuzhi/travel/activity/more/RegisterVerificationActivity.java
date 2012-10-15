package com.damuzhi.travel.activity.more;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.mission.common.CommonMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterVerificationActivity extends Activity
{

	protected static final String TAG = "RegisterVerificationActivity";
	private EditText verifiCodeEditText;
	private Button verificationButton;
	private Button getVerifiCodeButton;
	String phoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_register_verification);
		ActivityMange.getInstance().addActivity(this);
		phoneNum = getIntent().getStringExtra("phoneNum");
		TextView phoneNumTextView = (TextView) findViewById(R.id.phone_num);
		verifiCodeEditText = (EditText) findViewById(R.id.verification_code);
		verificationButton = (Button) findViewById(R.id.verification_button);
		getVerifiCodeButton = (Button) findViewById(R.id.get_verificode_button);
		phoneNumTextView.setText(phoneNum);
		verificationButton.setOnClickListener(verificationOnClickListener);
		getVerifiCodeButton.setOnClickListener(getVerifiCodeOnClickListener);
	}

	private OnClickListener verificationOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String verificationCode = verifiCodeEditText.getText().toString();

			if (verificationCode == null || verificationCode.trim().equals(""))
			{
				Toast.makeText(RegisterVerificationActivity.this,getString(R.string.please_enter_verification_code), Toast.LENGTH_SHORT).show();
				return;
			}

			try
			{
				verificationCode = URLEncoder.encode(verificationCode, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			boolean result = CommonMission.getInstance().verificationCode(phoneNum, verificationCode);
			Log.d(TAG, "verification  result = " + result);
			if (result)
			{
				Toast.makeText(RegisterVerificationActivity.this,getString(R.string.register_success),Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(RegisterVerificationActivity.this,MoreActivity.class);
				startActivity(intent);
			} else
			{
				Toast.makeText(RegisterVerificationActivity.this,getString(R.string.verification_code_fail), Toast.LENGTH_SHORT).show();
			}

		}
	};
	
	
	private OnClickListener getVerifiCodeOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			boolean result = CommonMission.getInstance().getVerification(phoneNum,phoneNum);
			String resultInfo = CommonMission.getInstance().getResultInfo();
			Log.d(TAG, "get verification code result = "+result);
			Toast.makeText(RegisterVerificationActivity.this, resultInfo, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}

}
