/**  
        * @title UserInfoActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-9 下午3:44:03  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.more;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.common.CommonMission;
import com.damuzhi.travel.mission.common.UserMission;
import com.damuzhi.travel.protos.PackageProtos.UserInfo;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-9 下午3:44:03  
 */

public class UserInfoActivity extends Activity
{

	private TextView userNameTextView;
	private EditText nickNameEditText;
	private EditText nameEditText;
	private EditText emailEditText;
	private ViewGroup changePasswordViewGroup;
	private Button okButton;
	private String loginId;
	private String token;
	private UserInfo userInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_user_info);
		ActivityMange.getInstance().addActivity(this);
		userNameTextView = (TextView) findViewById(R.id.user_name);
		nickNameEditText = (EditText) findViewById(R.id.nick_name);
		nameEditText = (EditText) findViewById(R.id.name);
		emailEditText = (EditText) findViewById(R.id.email);
		okButton = (Button) findViewById(R.id.ok_button);
		changePasswordViewGroup = (ViewGroup) findViewById(R.id.change_password_group);
		loginId = TravelApplication.getInstance().getLoginID();
		token = TravelApplication.getInstance().getToken();
		userInfo = CommonMission.getInstance().getUserInfo(loginId,token);
		if(userInfo != null)
		{
			userNameTextView.setText(userInfo.getLoginId());
			if(userInfo.getNickName() != null)
			{
				nickNameEditText.setText(userInfo.getNickName());
			}
			if(userInfo.getFullName() != null)
			{
				nameEditText.setText(userInfo.getFullName());
			}
			if(userInfo.getEmail() != null)
			{
				emailEditText.setText(userInfo.getEmail());
			}
		}
		changePasswordViewGroup.setOnClickListener(changePasswordOnClickListener);
		okButton.setOnClickListener(okButtOnClickListener);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}

	
	private  OnClickListener changePasswordOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(UserInfoActivity.this, ChangePasswordActivity.class);
			startActivity(intent);
			
		}
	};
	
	
	private OnClickListener okButtOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String nickName = nickNameEditText.getText().toString();
			String name = nameEditText.getText().toString();
			String email = emailEditText.getText().toString();
			if(nickName == null ||nickName.equals(""))
			{
				Toast.makeText(UserInfoActivity.this, getString(R.string.nick_name_hint), Toast.LENGTH_SHORT).show();
				return;
			}
			if(name == null||name.equals(""))
			{
				Toast.makeText(UserInfoActivity.this, getString(R.string.name_hint), Toast.LENGTH_SHORT).show();
				return;
			}
			if(email == null||email.equals("")||!TravelUtil.isEmail(email))
			{
				Toast.makeText(UserInfoActivity.this, getString(R.string.email_hint), Toast.LENGTH_SHORT).show();
				return;
			}
			try
			{
				nickName = URLEncoder.encode(nickName, "UTF-8");
				name = URLEncoder.encode(name,"UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			boolean result = CommonMission.getInstance().changeUserInfo(loginId,token,nickName,name,userInfo.getTelephone(),email);
			String resultInfo = CommonMission.getInstance().getResultInfo();
			
			if(result)
			{
				finish();
				Toast.makeText(UserInfoActivity.this,getString(R.string.change_user_info_success), Toast.LENGTH_SHORT).show();
			}else
			{
				Toast.makeText(UserInfoActivity.this,getString(R.string.change_user_info_fail), Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	

}
