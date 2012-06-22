/**  
        * @title FeedBackActivity.java  
        * @package com.damuzhi.travel.activity.more  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-20 上午9:51:18  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.MenuActivity;
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
		contentEditText = (EditText) findViewById(R.id.feedback_content);
		contactEditText = (EditText) findViewById(R.id.feedback_contact);
		ImageButton submit = (ImageButton) findViewById(R.id.submit);
		submit.setOnClickListener(submitOnClickListener);
		
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
					String userId = UserManager.getInstance().getUserId(FeedBackActivity.this);
					String feedBackUrl = String.format(ConstantField.FEED_BACK, userId,contact,content);
					FeedbackMission feedbackMission = new FeedbackMission();
					boolean result = feedbackMission.submitFeedback(feedBackUrl);
					if(result)
					{
						Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_submit_success), Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(FeedBackActivity.this, getString(R.string.feedback_submit_fail), Toast.LENGTH_SHORT).show();
					}
				}
			}
			
		}
	};
	
	
}
