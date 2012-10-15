/**  
        * @title ChangePasswordActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-9 下午5:28:04  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.more;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.common.CommonMission;
import com.damuzhi.travel.util.TravelUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-10-9 下午5:28:04  
 */

public class ChangePasswordActivity extends Activity
{

	protected static final String TAG = "ChangePasswordActivity";
	private EditText oldPasswordEditText;
	private EditText newPasswordEditText;
	private EditText newPasswordConfirmEditText;
	private Button okButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_change_password);
		ActivityMange.getInstance().addActivity(this);
		oldPasswordEditText = (EditText) findViewById(R.id.old_password);
		newPasswordEditText = (EditText) findViewById(R.id.new_password);
		newPasswordConfirmEditText = (EditText) findViewById(R.id.new_password_confirm);
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(okOnClickListener);
		
	}
	
	private OnClickListener okOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String oldPassword = oldPasswordEditText.getText().toString();
			String newPasswrod = newPasswordEditText.getText().toString();
			String newPasswordConfirm = newPasswordConfirmEditText.getText().toString();
			if(oldPassword==null||oldPassword.trim().equals("")||!TravelUtil.isShort(oldPassword))
			{
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.input_correct_password_toast), Toast.LENGTH_SHORT).show();
				return;
			}	
			
			if(newPasswrod==null||newPasswrod.trim().equals("")||!TravelUtil.isShort(newPasswrod))
			{
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.input_correct_password_toast), Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(newPasswordConfirm==null||newPasswordConfirm.trim().equals("")||!TravelUtil.isShort(newPasswordConfirm))
			{
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.input_correct_password_toast), Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(!newPasswordConfirm.equals(newPasswrod))
			{
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_not_the_same_toast), Toast.LENGTH_SHORT).show();
				return;
			}			
			String loginId = TravelApplication.getInstance().getLoginID();
			String token = TravelApplication.getInstance().getToken();
			boolean result = CommonMission.getInstance().changePassword(loginId,token,oldPassword,newPasswrod);
			//String resultInfo = CommonMission.getInstance().getResultInfo();
			Log.d(TAG, " change password  result = "+result);
			if(result)
			{
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.change_password_success), Toast.LENGTH_SHORT).show();
				finish();
			}else {
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.change_password_fail), Toast.LENGTH_SHORT).show();
			}
				
			
			
		}
	};
	@Override
	protected void onDestroy()
	{
		ActivityMange.getInstance().finishActivity();
		super.onDestroy();
	}

}
