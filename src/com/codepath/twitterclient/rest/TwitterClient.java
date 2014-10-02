package com.codepath.twitterclient.rest;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "aFIY3NHmRFF61VGeNVgDNnXo7";       // Change this
	public static final String REST_CONSUMER_SECRET = "QJ7HolFyvBxKsEYVObRkKzE7dc66R51bDLUQdiSPLyH3ePIKlQ"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)
	
	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	
	public void getHomeTimeline(AsyncHttpResponseHandler handler, long maxId, long sinceId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if(maxId > 0 && sinceId >0){
			return;
		}
		if (maxId > 0) {
			params.put("max_id", maxId + "");
		} else if(sinceId > 0){
			params.put("since_id", sinceId + "");
		}
		params.put("count", 25 + "");
		client.get(apiUrl, params, handler);		
	}
	
	public void getUserProfile(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, null, handler);
	}
	
	public void postTweet(AsyncHttpResponseHandler handler, String tweetText, String replyToTweetId) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetText);
		if(replyToTweetId != null && !replyToTweetId.equals("")){
			params.put("in_reply_to_status_id", replyToTweetId);
		}
		client.post(apiUrl, params, handler);
	}
	
}