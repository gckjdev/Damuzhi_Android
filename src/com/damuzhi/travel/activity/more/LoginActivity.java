package com.damuzhi.travel.activity.more;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.common.CommonMission;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	
	protected static final String TAG = "LoginActivity";
	//private TextView registerTextView;
	//private TextView findPasswordTextView;
	private ViewGroup userRegisterViewGroup;
	private ViewGroup forgetPasswordViewGroup;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private CheckBox rememberUserNameCheckBox;
	private CheckBox rememberPasswordCheckBox;
	private Button   loginButton;
    private String userName;
    private String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_login);
		ActivityMange.getInstance().addActivity(this);
		userNameEditText = (EditText) findViewById(R.id.user_name);
		passwordEditText = (EditText) findViewById(R.id.password);
		/*registerTextView = (TextView) findViewById(R.id.free_register);
		findPasswordTextView = (TextView) findViewById(R.id.find_password);*/
		userRegisterViewGroup = (ViewGroup) findViewById(R.id.free_register);
		forgetPasswordViewGroup = (ViewGroup) findViewById(R.id.forget_password);
		rememberUserNameCheckBox = (CheckBox) findViewById(R.id.remember_user_name);
		rememberPasswordCheckBox = (CheckBox) findViewById(R.id.remember_password);
		loginButton = (Button) findViewById(R.id.login_button);
		userRegisterViewGroup.setOnClickListener(registerOnClickListener);
		loginButton.setOnClickListener(loginOnClickListener);
		forgetPasswordViewGroup.setOnClickListener(forgetPasswordOnClickListener);
		userName = UserManager.getInstance().getUserName(LoginActivity.this);
		password = UserManager.getInstance().getPassword(LoginActivity.this);
		
		if(userName != null && !userName.equals(""))
		{
			userNameEditText.setText(userName);
			rememberUserNameCheckBox.setChecked(true);
		}
		if(password != null && !password.equals(""))
		{
			passwordEditText.setText(password);
			rememberPasswordCheckBox.setChecked(true);
		}
		
	}
	
	
	private OnClickListener registerOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
		}
	};

	
	private OnClickListener forgetPasswordOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, FindPasswordActivity.class);
			startActivity(intent);
		}
	};
	
	private OnClickListener loginOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			v.setClickable(false);
			userName = userNameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			if(userName==null||userName.trim().equals(""))
			{
				Toast.makeText(LoginActivity.this, getString(R.string.contact_toast), Toast.LENGTH_SHORT).show();
			}else if (password == null ||password.trim().equals("")) {
				Toast.makeText(LoginActivity.this, getString(R.string.feedback_contact_emtpy), Toast.LENGTH_SHORT).show();
			}else
			{		
				try
				{
					password = URLEncoder.encode(password, "UTF-8");
				} catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}				
				boolean result = CommonMission.getInstance().memberLogin(userName,password);
				String resultInfo = CommonMission.getInstance().getResultInfo();
				Log.d(TAG, " member login result = "+result);
				if(result)
				{
					String token = CommonMission.getInstance().getToken();
					TravelApplication.getInstance().setToken(token);
					TravelApplication.getInstance().setLoginID(userName);
					Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
					finish();
				}else {
					v.setClickable(true);
					Toast.makeText(LoginActivity.this, resultInfo, Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG, "rememberUserNameCheckBox checked = "+rememberUserNameCheckBox.isChecked());
		Log.d(TAG, "rememberPasswordCheckBox checked = "+rememberPasswordCheckBox.isChecked());
		String userName = userNameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		if(rememberUserNameCheckBox.isChecked())
		{
			UserManager.getInstance().saveUserName(LoginActivity.this, userName);
		}else
		{
			UserManager.getInstance().saveUserName(LoginActivity.this, "");
		}
		if(rememberPasswordCheckBox.isChecked())
		{
			UserManager.getInstance().savePassword(LoginActivity.this, password);
		}else
		{
			UserManager.getInstance().savePassword(LoginActivity.this, "");
		}
	}
	
	
	
}
