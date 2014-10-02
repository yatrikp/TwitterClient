package com.codepath.twitterclient.models;

import java.util.List;

import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "users")
public class User extends Model {
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "screen_name")
	private String screenName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "freinds_count")
	private int friendsCount;
	
	@Column(name = "statuses_count")
	private int statusesCount;
	
	@Column(name = "followers_count")
	private int followersCount;
	
	@Column(name = "profile_image_url")
	private String profileImageUrl;
	
	@Column(name = "background_image_url")
	private String profileBackGroundImageUrl;
	
	public User() {
		super();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public String getProfileBackGroundImageUrl() {
		return profileBackGroundImageUrl;
	}

	public void setProfileBackGroundImageUrl(String profileBackGroundImageUrl) {
		this.profileBackGroundImageUrl = profileBackGroundImageUrl;
	}

	public static User fromJson(JSONObject jsonObject) {
		User user = new User();
		try{
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			if (jsonObject.get("profile_image_url") != null){
				user.profileImageUrl = jsonObject.getString("profile_image_url");
				
			}
			if (jsonObject.get("profile_background_image_url") != null) {
				user.setProfileBackGroundImageUrl(jsonObject
						.getString("profile_background_image_url"));
			}
			if (jsonObject.get("description") != null) {
				user.setDescription(jsonObject.getString("description"));
			}
			if (jsonObject.get("friends_count") != null) {
				user.setFriendsCount(jsonObject.getInt("friends_count"));
			}
			if (jsonObject.get("followers_count") != null) {
				user.setFollowersCount(jsonObject.getInt("followers_count"));
			}
//			user.save();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return user;
	}
	
	public static void deleteAllUsers(){
		new Delete().from(User.class).execute();
	}
	
	public static List<User> getSavedUsers() {
		return new Select().from(User.class)
				.execute();
	}
	
	public static User getSavedUser(long uid){
		return new Select().from(User.class).where("user_id = ?" , uid).executeSingle();
	}
	
//	
//	public List<Tweet> tweets() {
//		return getMany(Tweet.class, "User");
//	}
//		
}
