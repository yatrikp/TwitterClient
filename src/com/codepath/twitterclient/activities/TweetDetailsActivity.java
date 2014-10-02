package com.codepath.twitterclient.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.codepath.twitterclient.R;
import com.codepath.twitterclient.models.IntentData;
import com.codepath.twitterclient.models.Tweet;
import com.codepath.twitterclient.models.User;
import com.codepath.twitterclient.utils.FormatUtils;
import com.codepath.twitterclient.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailsActivity extends Activity {

	private IntentData intentData;
	private Tweet tweetDetails;
	private EditText etTweetBox;
	private TextView tvScreenName;
	private TextView tvUsername;
	private TextView tvTweetBody;
	private TextView tvFavouritesCount;
	private TextView tvRetweetsCounts;
	private TextView tvTweetCharCount;
	private TextView tvSendTweet;
	private ImageView ivProfileImage;
	private ImageView ivDetailImage;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_details);
		intentData = IntentData.getInstance();
		tweetDetails = intentData.getTweetDetailed();
		if (tweetDetails != null){
			this.tweetDetails = tweetDetails;
		}
		else{
			this.finish();
		}
		initialize();
		styleActionBar();
	}
	
	private void initialize() {
		User user = tweetDetails.getUser();
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileCompose);
		
		try{
			ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage);
		}
		catch(Exception e){
			Log.e("error while loading image");
		}
		ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
		if (!TextUtils.isEmpty(tweetDetails.getMediaUrl())) {
			try{
				ImageLoader.getInstance().displayImage(tweetDetails.getMediaUrl(), ivDetailImage);
				ivDetailImage.setVisibility(View.VISIBLE);
			}
			catch(Exception e){
				Log.e("error while loading image");
			}
		}
		tvScreenName = (TextView) findViewById(R.id.tvScreenNameCompose);
		tvUsername = (TextView) findViewById(R.id.tvUserNameCompose);
		tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
		tvFavouritesCount = (TextView) findViewById(R.id.tvFavoritesCount); 
		tvRetweetsCounts  = (TextView) findViewById(R.id.tvRetweetsCount);
		tvTweetCharCount  = (TextView) findViewById(R.id.tvReplyTweetCount); 
		tvSendTweet = (TextView) findViewById(R.id.tvTweet);
		etTweetBox = (EditText) findViewById(R.id.etTweetBox);
		tvScreenName.setText("@" + user.getScreenName());
		tvUsername.setText(user.getName());
		tvTweetBody.setText(tweetDetails.getBody());
		tvRetweetsCounts.setText(tweetDetails.getRetweetCount() + " Retweets");
		tvFavouritesCount.setText(tweetDetails.getFavouritesCount() + " Favourites");
		etTweetBox.setHint("Reply to " + user.getName());
		setListeners();
	}
	
	private void setListeners() {
		etTweetBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s!=null) {
					String tweetMsg = s.toString();
					Utils.validateTweetMsg(tweetMsg, tvTweetCharCount);
				}
			}
		});
		ivProfileImage.requestFocus();
		hideSoftKeyboard(etTweetBox);
		
		etTweetBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String screenName = "@" + tweetDetails.getUser().getScreenName() + " ";
				etTweetBox.setText(screenName);
				etTweetBox.setSelection(screenName.length());
			}
		});
		
		tvSendTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				postTweet();
			}
		});
	}

	private void hideSoftKeyboard(View view) {
		InputMethodManager inputMethodMgr = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		inputMethodMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	private void postTweet() {
		String tweetMsg = etTweetBox.getText().toString();
		if (TextUtils.isEmpty(tweetMsg)) {
			Toast.makeText(TweetDetailsActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
			return;
		} 

		TwitterApplication.getRestClient().postTweet(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonTweet) {
				goBackToTimeline();
			}
			

			@Override
			public void onFailure(Throwable e, String s) {
				Toast.makeText(TweetDetailsActivity.this, "Failure to post tweet", Toast.LENGTH_SHORT).show();				
			}
		}, tweetMsg, tweetDetails.getId().toString());
	}
	
	private void goBackToTimeline(){
		setResult(RESULT_OK);
		intentData.setTweetDetailed(null);
		this.finish();			
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			goBackToTimeline();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet_details, menu);
		return true;
	}
	
	private void styleActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00ABF1")));
		getActionBar().setLogo(R.drawable.ic_launcher);
		getActionBar().setTitle(
				Html.fromHtml(FormatUtils.getComposeTitle("Compose")));
	}


}
