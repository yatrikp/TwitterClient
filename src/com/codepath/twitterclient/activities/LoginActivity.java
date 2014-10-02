package com.codepath.twitterclient.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.codepath.oauth.OAuthLoginActivity;
import com.codepath.twitterclient.R;
import com.codepath.twitterclient.models.IntentData;
import com.codepath.twitterclient.models.User;
import com.codepath.twitterclient.rest.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {
	
	private IntentData intentData;
	private Button btnConnectToTwitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initialize();
	}
	
	private void initialize(){
		intentData = IntentData.getInstance();
		btnConnectToTwitter = (Button) findViewById(R.id.btnConnectToTwitter);
//		styleActionBar();
	}

	private void styleActionBar() {
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ABF1")));
		getActionBar().setTitle(Html.fromHtml("<font color=\"#E0EAEF\">" + getString(R.string.app_name) + "</font>"));
	}	
	
	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		if (intentData.getLoggedInUser() == null){
			TwitterApplication.getRestClient().getUserProfile(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject jsonUser) {
					User user = User.fromJson(jsonUser);
					intentData.setLoggedInUser(user);
					
				}				
			});
		}
		startTimeline();
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		if (getClient() != null) {
			if(getClient().isAuthenticated()) {
				btnConnectToTwitter.setVisibility(View.GONE);
			}
			else{
				btnConnectToTwitter.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void startTimeline(){
		Intent i = new Intent(this, TimelineActivity.class);
		startActivity(i);		
	}	

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
