package com.codepath.twitterclient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.twitterclient.R;
import com.codepath.twitterclient.adapters.EndlessScrollListener;
import com.codepath.twitterclient.adapters.TweetAdapter;
import com.codepath.twitterclient.models.IntentData;
import com.codepath.twitterclient.models.Tweet;
import com.codepath.twitterclient.models.User;
import com.codepath.twitterclient.rest.TwitterClient;
import com.codepath.twitterclient.utils.FormatUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity {

	public static final String TWEET_DETAILS_PARAM = "tweet_detail";
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private TweetAdapter aTweets;
	private PullToRefreshListView lvTweets;
	private Context context;
	private RelativeLayout rlTweetContainer;
	private IntentData intentData = IntentData.getInstance();
	private long oldestTweetId = -1;
	private long newestTweetId = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		context = this;
		initialize();
	}
	
	private void initialize(){
		client = TwitterApplication.getRestClient();
		rlTweetContainer = (RelativeLayout) findViewById(R.id.rl_tweet_container);
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		setListeners();		
		styleActionBar();
	}
	
	private void setListeners() {
		rlTweetContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				postNewTweet();
			}
		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (aTweets.getCount() > 0) {
					if(!isNetworkAvailable()){
						Toast.makeText(TimelineActivity.this, "Network Unavailable", Toast.LENGTH_SHORT).show();
						return;
					}
					getTweetsInAsync(aTweets.getItem(aTweets.getCount() - 1).getUid() - 1, -1, false);
				}
			}
		});

		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getTweetsInAsync(-1, aTweets.getItem(0).getUid() + 1, false);
				lvTweets.onRefreshComplete();
				if(isNetworkAvailable()){
					Tweet.deleteAllTweets();	
					User.deleteAllUsers();
				}
			}
		});
		lvTweets.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int position, long paramLong) {
				Intent i = new Intent(context, TweetDetailsActivity.class);
				intentData.setTweetDetailed(aTweets.getItem(position));
				startActivity(i);
			}			
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		populateTimeline();
	}
	
	
	
	private void populateTimeline() {
		if (aTweets.getCount() > 0){
			getTweetsInAsync(-1, aTweets.getItem(0).getUid() + 1, false);
		}
		else {
			getTweetsInAsync(-1, -1, true);
		}
	}
	
	public void getTweetsInAsync(final long maxId, final long sinceId, final boolean initialLoad) {
		
		if(isNetworkAvailable()){
			client.getHomeTimeline(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray json) {
					tweets = Tweet.fromJSONArray(json);
					
					if (maxId > 0 || initialLoad) {
						aTweets.addAll(tweets);
					}
					if (sinceId > 0) {
						for (int i = 0; i < tweets.size(); i++) {
							aTweets.insert(tweets.get(i), i);
						}
					}					
					aTweets.notifyDataSetChanged();
					for (Tweet tw : tweets){
						tw.persist();
					}
				}
				
				@Override
				public void onFailure(Throwable e, String s) {
					Toast.makeText(TimelineActivity.this, "Failure to fetch user timeline", Toast.LENGTH_SHORT).show();
					super.onFailure(e);
				}
			}, maxId, sinceId);			
		}	
		else{
			tweets.clear();
			tweets.addAll(loadSavedTweets());
			aTweets.notifyDataSetChanged();
		}
	}
	
	public synchronized List<Tweet> loadSavedTweets() {
		List<Tweet> savedTweets = Tweet.getSavedTweets();
		if (savedTweets != null && savedTweets.size() > 0) {

			Tweet lastTweet = savedTweets.get(savedTweets.size() - 1);
			oldestTweetId = lastTweet.getId();

			Tweet firstTweet = savedTweets.get(0);
			newestTweetId = firstTweet.getId();

			return savedTweets;
		}

		return savedTweets;
	}
	
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
	private void postNewTweet() {
		Intent i = new Intent(this, PostTweetActivity.class);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mi_compose_tweet:
			postNewTweet();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void styleActionBar() {
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ABF1")));
		getActionBar().setLogo(R.drawable.ic_launcher);
		if (intentData != null && intentData.getLoggedInUser() != null){
			getActionBar().setTitle(Html.fromHtml(FormatUtils.getUserTitleText(context, intentData.getLoggedInUser().getScreenName())));
		}
	}
}
