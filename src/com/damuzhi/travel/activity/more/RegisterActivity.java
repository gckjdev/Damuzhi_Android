package com.damuzhi.travel.activity.more;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.mission.common.CommonMission;
import com.damuzhi.travel.mission.more.FeedbackMission;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	
	protected static final String TAG = "RegisterActivity";
	private EditText userNameEditText;
	private EditText password1EditText;
	private EditText password2EditText;
	private Button   registerButton;
	String userName,password1,password2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_register);
		ActivityMange.getInstance().addActivity(this);
		userNameEditText = (EditText) findViewById(R.id.user_name);
		password1EditText = (EditText) findViewById(R.id.password);
		password2EditText = (EditText) findViewById(R.id.password2);
		registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(registerOnClickListener);
	}

	
	
	private OnClickListener registerOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			userName = userNameEditText.getText().toString();
			password1 = password1EditText.getText().toString();
			password2 = password2EditText.getText().toString();
			if(userName==null||userName.trim().equals(""))
			{
				Toast.makeText(RegisterActivity.this, getString(R.string.contact_toast), Toast.LENGTH_SHORT).show();
			}else if (password1 == null ||password1.trim().equals("")||password2 == null ||password2.trim().equals("")) {
				Toast.makeText(RegisterActivity.this, getString(R.string.feedback_contact_emtpy), Toast.LENGTH_SHORT).show();
			}else
			{
				//boolean isPhoneNum = TravelUtil.isNumber(userName);
				boolean isPhoneNum = TravelUtil.isPhoneNumber(userName);
				boolean isShort = TravelUtil.isShort(password1);
				boolean isShort2 = TravelUtil.isShort(password2);
				if(!isPhoneNum)
				{
					Toast.makeText(RegisterActivity.this, getString(R.string.phone_number_toast), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!isShort||!isShort2)
				{
					Toast.makeText(RegisterActivity.this, getString(R.string.password_length_need_toast), Toast.LENGTH_SHORT).show();
					return;
				}
				if(!password1.equals(password2))
				{
					Toast.makeText(RegisterActivity.this, getString(R.string.password_not_the_same_toast), Toast.LENGTH_SHORT).show();
					return;
				}
						
				try
				{
					password1 = URLEncoder.encode(password1, "UTF-8");
				} catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
				String registerURL = String.format(ConstantField.REGISTER_URL, userName,password1);
				boolean result = CommonMission.getInstance().registerMember(registerURL);
				String resultInfo = CommonMission.getInstance().getResultInfo();
				Log.d(TAG, "register member result = "+result);
				if(result)
				{
					result = CommonMission.getInstance().getVerification(userName,userName);
					resultInfo = CommonMission.getInstance().getResultInfo();
					Log.d(TAG, "get verification code result = "+result);
					if(result)
					{
						Toast.makeText(RegisterActivity.this, getString(R.string.verification_code_send_success), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("phoneNum", userName);
						intent.setClass(RegisterActivity.this, RegisterVerificationActivity.class);
						startActivity(intent);
					}else {
						Toast.makeText(RegisterActivity.this,resultInfo, Toast.LENGTH_SHORT).show();
					}
					
				}else {
					Toast.makeText(RegisterActivity.this, resultInfo, Toast.LENGTH_SHORT).show();
				}
				
			}
			
		}
	};
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
}
