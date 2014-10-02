package com.codepath.twitterclient.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "tweets")
public class Tweet extends Model {

	@Column(name = "body")
	private String body;
	
	@Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
	private long uid;
	
	@Column(name = "created_at")
	private String createdAt;
	
	@Column(name = "retweet_count")
	private int retweetCount;
	
	@Column(name = "favourites_count")
	private int favouritesCount;
	
	@Column(name = "media_url")
	private String mediaUrl;
	
	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	private User user;
	
	public Tweet(){
		super();
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}
	
	private static String mediaUrlFromJsonEntity(JSONObject entitiesObject) throws JSONException {
		String mediaUrl = "";
		if (entitiesObject.has("media") && !entitiesObject.isNull("media")) {
			JSONArray mediaArray = entitiesObject.getJSONArray("media");
			if (mediaArray.length() > 0) {
				JSONObject mediaJsonObj = mediaArray.getJSONObject(0);
				if (mediaJsonObj.has("media_url") && !mediaJsonObj.isNull("media_url")) {
					mediaUrl = mediaJsonObj.getString("media_url");
				}
			}
		}
		return mediaUrl;
	}

	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try{
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
			tweet.retweetCount = jsonObject.getInt("retweet_count");
			tweet.favouritesCount = jsonObject.getInt("favorite_count");
			try {
				tweet.mediaUrl = mediaUrlFromJsonEntity(jsonObject.getJSONObject("entities"));
			} catch (JSONException e) {
				tweet.mediaUrl = "";
			}
			
		}
		catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		
		return tweet;
	}
	
	
	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		for (int count = 0; count < jsonArray.length(); count++){
			JSONObject tweetJson = null;
			try{
				tweetJson = jsonArray.getJSONObject(count);
			}
			catch(JSONException e){
				e.printStackTrace();
				continue;
			}
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet != null){
				tweets.add(tweet);
			}
		}
		return tweets;
	}
	
	public static List<Tweet> getSavedTweets() {
		return new Select().from(Tweet.class)
				.orderBy("created_at DESC").execute();
	}
	
	public static void deleteAllTweets(){
		new Delete().from(Tweet.class).execute();
	}

	public void persist() {
		User existingUser = User.getSavedUser(this.user.getUid());
		if(existingUser == null){
			this.user.save();
		}
		else{
			this.user = existingUser;
		}
		this.save();		
	}

}
