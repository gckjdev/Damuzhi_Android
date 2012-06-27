package com.damuzhi.travel.activity.common.qweibo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.utils.TokenStore;
import com.damuzhi.travel.activity.share.Share2Weibo;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.OAuthClient;

public class MyWebView extends Activity {
	private WebView wb;
	private OAuth oauth;
	private OAuthClient auth;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();
		String url=intent.getStringExtra("URL");
		wb=new WebView(this);
		inti(wb);
		wb.loadUrl(url);
		
		oauth = Share2Weibo.qq_oauth;
		auth = Share2Weibo.qq_auth;
		
		setContentView(wb);
	}

	private void inti(WebView wv) {
		
		wv.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(newProgress==100){
					
					String url=wb.getUrl();
					System.out.println("get::"+url);
					
					if(url.contains("&checkType=verifycode")){
						wb.setVisibility(View.INVISIBLE);
						
						Uri uri=Uri.parse(url);
						//String value=url.substring(url.lastIndexOf("v=")+2,url.length());
						String value=uri.getQueryParameter("v");
						String orValue=uri.getQueryParameter("vcode");

						int verifycode = 0;
						int orVerifycode = 0;
						if(value!=null)
							verifycode=Integer.parseInt(value);
						if(orValue!=null)
							orVerifycode=Integer.parseInt(orValue);
						
						if( (verifycode>100000)&&(verifycode<999999) ){
							System.out.println("<><><><"+value);
							getToken(value, oauth.getOauth_token());
						}else if( (orVerifycode>100000)&&(orVerifycode<999999) ){
							System.out.println("<><><><"+orValue);
							getToken(orValue, oauth.getOauth_token());
						}else{
							System.out.println("verify wrong!!!!!!!!!!");
							return;
						}
						
						
						
						Toast.makeText(MyWebView.this, "绑定成功", Toast.LENGTH_LONG).show();
						MyWebView.this.finish();
					}
				}
				
				super.onProgressChanged(view, newProgress);
			}
			
		});
	}

	/**
	 * get token from verifier code
	 * @param oauth_verifier
	 * @param oauth_token
	 */
	public void getToken(String oauth_verifier, String oauth_token) {		
		oauth.setOauth_verifier(oauth_verifier);
		oauth.setOauth_token(oauth_token);

		try {
			oauth = auth.accessToken(oauth);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (oauth.getStatus() == 2) {
			System.out.println("Get Access Token failed!");
			return;
		} else {			
			TokenStore.store(this, oauth);
		}
			
	}

}
