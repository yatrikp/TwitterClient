package com.codepath.twitterclient.activities;

import org.json.JSONArray;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.twitterclient.R;
import com.codepath.twitterclient.models.IntentData;
import com.codepath.twitterclient.models.User;
import com.codepath.twitterclient.utils.FormatUtils;
import com.codepath.twitterclient.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostTweetActivity extends Activity {

	private IntentData intentData;
	private EditText etTweetMessage;
	private TextView tvScreenName, tvUsername, tvTweetCount;
	private ImageView ivProfileImage;
	private TextView tvDialogTweet;
	private TextView tvDialogCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_tweet);
		intentData = IntentData.getInstance();
		initialize();
	}

	private void initialize() {
//		styleActionBar();
		setContentView(R.layout.activity_post_tweet);
		etTweetMessage = (EditText) findViewById(R.id.etTweetMessage);
		tvScreenName = (TextView) findViewById(R.id.tvScreenNameCompose);
		tvUsername = (TextView) findViewById(R.id.tvUserNameCompose);
		tvTweetCount = (TextView) findViewById(R.id.tvTweetCount);
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileCompose);
		tvDialogTweet = (TextView) findViewById(R.id.tvDialogTweet);
		tvDialogCancel = (TextView) findViewById(R.id.tvDialogCancel);
		User user = intentData.getLoggedInUser();
		tvScreenName.setText("@" + user.getScreenName());
		tvUsername.setText(user.getName());
		etTweetMessage.requestFocus();
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(),
				ivProfileImage);
		setListeners();
	}

	private void styleActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#00ABF1")));
		getActionBar().setLogo(R.drawable.ic_launcher);
		getActionBar().setTitle(
				Html.fromHtml(FormatUtils.getComposeTitle("Compose")));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_tweet, menu);
		MenuItem item = menu.findItem(R.id.itTweet);
		item.setTitle(Html.fromHtml(FormatUtils.getComposeTitle("Tweet")));
		invalidateOptionsMenu();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_OK);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setListeners() {
		etTweetMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null) {
					String tweetMsg = s.toString();
					Utils.validateTweetMsg(tweetMsg, tvTweetCount);
				}
			}
		});
		
		tvDialogTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				postTweet();
			}
		});
		
		tvDialogCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		
	}

	private void postTweet(){
		String tweetMsg = etTweetMessage.getText().toString();
		if (TextUtils.isEmpty(tweetMsg)) {
			return;
		}

		TwitterApplication.getRestClient().postTweet(
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonTweet) {
						finish();
					}
					
					@Override
					public void onFailure(Throwable e, String s) {
						Toast.makeText(PostTweetActivity.this, "Failure to post tweet", Toast.LENGTH_SHORT).show();						
					}
					
				}, tweetMsg, "");
	}
	
	public void onClickTweet(MenuItem mi) {
		postTweet();
	}
}
