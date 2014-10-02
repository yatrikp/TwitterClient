package com.codepath.twitterclient.models;

public class IntentData {

	private Tweet tweetDetailed;
	private User loggedInUser;
	private static IntentData instanceData;
	
	public static synchronized IntentData getInstance() {
		if (null == instanceData) {
			instanceData = new IntentData();
		}
		return instanceData;
	}

	public Tweet getTweetDetailed() {
		return tweetDetailed;
	}

	public void setTweetDetailed(Tweet tweetDetailed) {
		this.tweetDetailed = tweetDetailed;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
		
}
