/**  
        * @title FindPasswordActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-10-4 上午10:27:29  
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
import android.app.ActivityManager;
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
 * @update 2012-10-4 上午10:27:29  
 */

public class FindPasswordActivity extends Activity
{

	protected static final String TAG = null;
	private Button okButton;
	private EditText telephoneEditText;
	private String telephone;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityMange.getInstance().addActivity(this);
		setContentView(R.layout.common_find_password);
		okButton = (Button) findViewById(R.id.ok_button);
		telephoneEditText = (EditText) findViewById(R.id.telephone);
		okButton.setOnClickListener(okOnClickListener);
	}

	
	private OnClickListener okOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{

			telephone = telephoneEditText.getText().toString();
			if(telephone==null||telephone.trim().equals("")||!TravelUtil.isPhoneNumber(telephone))
			{
				Toast.makeText(FindPasswordActivity.this, getString(R.string.contact_toast), Toast.LENGTH_SHORT).show();
			}else
			{		
				boolean result = CommonMission.getInstance().findPassword(telephone);
				String resultInfo = CommonMission.getInstance().getResultInfo();
				Log.d(TAG, " find password result = "+result);
				if(result)
				{
					Toast.makeText(FindPasswordActivity.this, resultInfo, Toast.LENGTH_SHORT).show();
					finish();
				}else {
					Toast.makeText(FindPasswordActivity.this, resultInfo, Toast.LENGTH_SHORT).show();
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
