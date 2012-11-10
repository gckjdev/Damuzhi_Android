/**  
        * @title Share2SinaWeibo.java  
        * @package com.damuzhi.travel.activity.common  
        * @description   
        * @author liuxiaokun  
        * @update 2012-6-26 下午2:43:40  
        * @version V1.0  
 */
package com.damuzhi.travel.activity.share;


import java.io.IOException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.utils.TokenStore;
import com.baidu.location.f;
import com.damuzhi.travel.R;
import com.damuzhi.travel.activity.common.ActivityMange;
import com.damuzhi.travel.activity.common.TravelApplication;
import com.damuzhi.travel.activity.common.qweibo.GetQQTokenWebView;
import com.damuzhi.travel.activity.entry.IndexActivity;
import com.damuzhi.travel.activity.entry.MainActivity;
import com.damuzhi.travel.db.AccessTokenKeeper;
import com.damuzhi.travel.mission.app.AppMission;
import com.damuzhi.travel.model.constant.ConstantField;
import com.tencent.weibo.api.T_API;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.OAuthClient;
import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;


/**  
 * @description   
 * @version 1.0  
 * @author liuxiaokun  
 * @update 2012-6-26 下午2:43:40  
 */

public class Share2Weibo extends Activity implements RequestListener
{
	/** Called when the activity is first created. */
	private ImageButton sendButton;
	private ImageButton cancelButton;
	private EditText shareContent;

	private  String SINA_CONSUMER_KEY = "";
	private  String SINA_CONSUMER_SECRET = "";
	private  String QQ_CONSUMER_KEY = "";
	private  String QQ_CONSUMER_SECRET = "";
	private String CALL_BACK_URL = "";
	private static final String TAG = "Share2Weibo";
	

	
	//QQ
	String[] qq_oauth_token_array;
	String qq_oauth_token_secret;
	String qq_oauth_token;
	public static OAuthClient qq_auth;
	public static OAuth qq_oauth;
    
	//sina
	public static Oauth2AccessToken accessToken ;
	private Weibo mWeibo;
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
	 */
	 SsoHandler mSsoHandler;
	
	private static final String SHARE_CONFIG = "share_config";
	private static final  int SHARE_2_SINA = 1;
	private static final  int SHARE_2_QQ = 2;
	int shareConfig;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TravelApplication.getInstance().addActivity(this);
		ActivityMange.getInstance().addActivity(this);
		setContentView(R.layout.share_2_weibo);
		MobclickAgent.updateOnlineConfig(this);
		while (SINA_CONSUMER_KEY == null||SINA_CONSUMER_KEY.equals("") )
		{
			SINA_CONSUMER_KEY = MobclickAgent.getConfigParams(Share2Weibo.this, ConstantField.U_MENG_SINA_CONSUMER_KEY);
			SINA_CONSUMER_SECRET = MobclickAgent.getConfigParams(Share2Weibo.this, ConstantField.U_MENG_SINA_CONSUMER_SECRET);
			QQ_CONSUMER_KEY = MobclickAgent.getConfigParams(Share2Weibo.this, ConstantField.U_MENG_QQ_CONSUMER_KEY);
			QQ_CONSUMER_SECRET = MobclickAgent.getConfigParams(Share2Weibo.this, ConstantField.U_MENG_QQ_CONSUMER_SECRET);
			CALL_BACK_URL = MobclickAgent.getConfigParams(Share2Weibo.this, ConstantField.U_MENG_CALL_BACK_URL);
			
		}
		
		shareConfig = getIntent().getIntExtra(SHARE_CONFIG,0);
		ImageView shareImageView = (ImageView) findViewById(R.id.share_image);
		String title ;
		if (shareConfig == SHARE_2_SINA)
		{
			title = getString(R.string.share_2_sina_title);
			Share2Weibo.accessToken=AccessTokenKeeper.readAccessToken(this);
			if (!Share2Weibo.accessToken.isSessionValid()) 
			getSinaOauthToken();
		}else
		{
			shareImageView.setImageDrawable(getResources().getDrawable(R.drawable.qq_weibo_logo));
			title = getString(R.string.share_2_qq_title);
			getQQOauthToken();			
		}
		TextView shareTitle = (TextView) findViewById(R.id.share_title);
		shareTitle.setText(title);
		sendButton = (ImageButton) this.findViewById(R.id.send_button);
		cancelButton = (ImageButton) this.findViewById(R.id.cancel_button);
		shareContent = (EditText) this.findViewById(R.id.share_content);		
		sendButton.setOnClickListener(sendOnClickListener);
		cancelButton.setOnClickListener(cancelOnClickListener);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private OnClickListener sendOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String content = shareContent.getText().toString();
			if (shareConfig == SHARE_2_SINA)
			{				
				share2sinaWeibo(content);
			}else
			{
				share2qqWeibo(content);
			}
			
		}
	};
	
	private OnClickListener cancelOnClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent();
			intent.setClass(Share2Weibo.this, MainActivity.class);
			startActivity(intent);
		}
	};
	
	private void share2sinaWeibo(String content) {    
		StatusesAPI api = new StatusesAPI(Share2Weibo.accessToken);
		api.update( content, "", "", this);
    }
	
	
	
	private void getSinaOauthToken()
	{
		
		if(SINA_CONSUMER_KEY!=null&&!SINA_CONSUMER_KEY.equals("")&&SINA_CONSUMER_SECRET!=null&&!SINA_CONSUMER_SECRET.equals("")&&CALL_BACK_URL!=null&&!CALL_BACK_URL.equals(""))
		{			
			mWeibo = Weibo.getInstance(SINA_CONSUMER_KEY, CALL_BACK_URL);
			try {
	            Class sso=Class.forName("com.weibo.sdk.android.sso.SsoHandler");
	            mSsoHandler =new SsoHandler(Share2Weibo.this,mWeibo);
	            mSsoHandler.authorize( new AuthDialogListener());
	        } catch (ClassNotFoundException e) {
	            Log.i(TAG, "com.weibo.sdk.android.sso.SsoHandler not found");
	            mWeibo.authorize(Share2Weibo.this, new AuthDialogListener());
	        }
			
		}
		
	}
	
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			Share2Weibo.accessToken = new Oauth2AccessToken(token, expires_in);
			if (Share2Weibo.accessToken.isSessionValid()) {
				try {
	                Class sso=Class.forName("com.weibo.sdk.android.api.WeiboAPI");//如果支持weiboapi的话，显示api功能演示入口按钮
	            } catch (ClassNotFoundException e) {
	                Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");
	               
	            }
				AccessTokenKeeper.keepAccessToken(Share2Weibo.this, accessToken);
				Toast.makeText(Share2Weibo.this, "认证成功", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
        if(mSsoHandler!=null){
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
	
	

	@Override
	public void onComplete(String response) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(Share2Weibo.this, R.string.share_success, Toast.LENGTH_LONG).show();
			}
		});

		this.finish();
	}

	@Override
	public void onIOException(IOException e) {

	}

	@Override
	public void onError(final WeiboException e) {
		System.out.println("exception = "+e.getMessage());		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = null;
				try
				{
					jsonObject = new JSONObject(e.getMessage());
				
				if(jsonObject != null&&jsonObject.getString("error_code").equals("20019")){
					Toast.makeText(Share2Weibo.this,R.string.repeat_content_error, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(Share2Weibo.this,R.string.share_fail, Toast.LENGTH_LONG).show();
				}
				} catch (JSONException e1)
				{
					e1.printStackTrace();
				}
			}
		});

	}
	
	
	
	
	

	
	
	private void share2qqWeibo(String content)
	{					
			qq_oauth_token_array = TokenStore.fetch(Share2Weibo.this);
			qq_oauth_token = qq_oauth_token_array[0];
			qq_oauth_token_secret = qq_oauth_token_array[1];
			qq_oauth.setOauth_token(qq_oauth_token);
			qq_oauth.setOauth_token_secret(qq_oauth_token_secret);
			T_API tapi = new T_API();
			try {
				if (!content.equals("")) {
					String result = tapi.add(qq_oauth, "json", content, "", "", "");
					JSONObject registerData = new JSONObject(result);
					if (registerData == null || registerData.getInt("ret")!= 0){
						Toast.makeText(Share2Weibo.this, R.string.share_fail, Toast.LENGTH_SHORT).show();
					}else
					{
						Toast.makeText(Share2Weibo.this, R.string.share_success,Toast.LENGTH_SHORT).show();
					}
					Log.i(TAG, "qq share result code = "+result);					
				}
			} catch (Exception e) {
				Log.e(TAG, "<share2qqWeibo> but catch exception :"+e.toString(),e);
			}
	}
	
	private void getQQOauthToken()
	{
		try {
			if (QQ_CONSUMER_KEY!=null&&!QQ_CONSUMER_KEY.equals("")&&QQ_CONSUMER_SECRET!=null&&!QQ_CONSUMER_SECRET.equals(""))
			{
				qq_oauth = new OAuth(QQ_CONSUMER_KEY, QQ_CONSUMER_SECRET,"null");			
				qq_oauth_token_array = TokenStore.fetch(Share2Weibo.this);
				qq_oauth_token = qq_oauth_token_array[0];
				qq_oauth_token_secret = qq_oauth_token_array[1];
				if (qq_oauth_token == null || qq_oauth_token_secret == null) 
				{
					qq_auth = new OAuthClient();
					qq_oauth = qq_auth.requestToken(qq_oauth);
					if (qq_oauth.getStatus() == 1) {
						Log.i(TAG, "Get Request Token failed!");
						return;
					} else {
						qq_oauth_token = qq_oauth.getOauth_token();
						String url = "http://open.t.qq.com/cgi-bin/authorize?oauth_token="+ qq_oauth_token;
						Intent intent = new Intent(Share2Weibo.this,GetQQTokenWebView.class);
						intent.putExtra("URL", url);
						startActivity(intent);
					}
				}		
			}		
		} catch (Exception e) {
			Log.e(TAG, "<getQQOauthToken> but catch exception :"+e.toString(),e);
		}
	}
	
	public void setQQToken(String oauth_token, String oauth_token_secret) {

		qq_oauth.setOauth_token(oauth_token);
		qq_oauth.setOauth_token_secret(oauth_token_secret);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityMange.getInstance().finishActivity();
	}
	
	
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}  */
}
