package com.codepath.twitterclient.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;

import com.codepath.twitterclient.R;
import com.codepath.twitterclient.models.User;

public class Utils {
	
	private static final int MAX_TWEET_CHAR_COUNT = 140;
	private static final long WEEK_IN_MILLIS = 604800000;
	private static final long SECOND_IN_MILLIS = 1000;
	
	public static void validateTweetMsg(String tweetMsg, TextView tvTweetCount) {
		int tweetCountLeft = MAX_TWEET_CHAR_COUNT - tweetMsg.length();
		tvTweetCount.setText("" + tweetCountLeft);
		
		if(tweetCountLeft < 10) {
			tvTweetCount.setTextColor(Color.parseColor("#99E53D38"));
		} else if(tweetCountLeft < 70) {
			tvTweetCount.setTextColor(Color.parseColor("#99E88E23"));
		} else {
			tvTweetCount.setTextColor(Color.parseColor("#9900AB17"));
		}
	}
}
