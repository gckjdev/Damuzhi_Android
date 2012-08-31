/**  
        * @title FeedBackActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-20 上午9:51:18  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.more;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.mission.common.UserMission;
import com.damuzhi.travel.mission.more.FeedbackMission;
import com.damuzhi.travel.model.common.UserManager;
import com.damuzhi.travel.model.constant.ConstantField;
import com.damuzhi.travel.util.TravelUtil;

/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-20 上午9:51:18  
 */

public class FeedBackActivity extends MenuActivity
{

	private EditText contentEditText;
	private EditText contactEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		TravelApplication.getInstance().addActivity(this);
		contentEditText = (EditText) findViewById(R.id.feedback_content);
		contactEditText = (EditText) findViewById(R.id.feedback_contact);
		ViewGroup feedbackGroup = (ViewGroup) findViewById(R.id.feedback_group);
		ImageButton submit = (ImageButton) findViewById(R.id.submit);
		submit.setOnClickListener(submitOnClickListener);
		feedbackGroup.setOnClickListener(feedbackGroupOnClickListener);
		InputMethodManager imm = (InputMethodManager)feedbackGroup.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){ 
		imm.hideSoftInputFromWindow(feedbackGroup.getApplicationWindowToken(), 0 );   
		}  
	}

	private OnClickListener submitOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String content = contentEditText.getText().toString();
			String contact = contactEditText.getText().toString();
			if(content==null||content.trim().equals(""))
			{
				Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_content_emtpy), Toast.LENGTH_SHORT).show();
			}else if (contact == null ||contact.trim().equals("")) {
				Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_contact_emtpy), Toast.LENGTH_SHORT).show();
			}else
			{
				boolean isNumber = TravelUtil.isNumber(contact);
				boolean isEmail = TravelUtil.isEmail(contact);
				if(!isNumber&&!isEmail)
				{
					Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_contact_error), Toast.LENGTH_SHORT).show();
				}else
				{
					try
					{
						content = URLEncoder.encode(content, "UTF-8");
					} catch (UnsupportedEncodingException e)
					{
						e.printStackTrace();
					}
					String userId = UserManager.getInstance().getUserId(FeedBackActivity.this);
					String feedBackUrl = String.format(ConstantField.FEED_BACK, userId,contact,content);
					FeedbackMission feedbackMission = new FeedbackMission();
					boolean result = feedbackMission.submitFeedback(feedBackUrl);
					if(result)
					{
						Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_submit_success), Toast.LENGTH_SHORT).show();
						contactEditText.setText("");
						contentEditText.setText("");
					}else {
						Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_submit_fail), Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		}
	};
	
	private OnClickListener feedbackGroupOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm.isActive()){ 
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );   
			}   
		}
	};
	
	
}
