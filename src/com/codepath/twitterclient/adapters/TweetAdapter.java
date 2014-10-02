package com.codepath.twitterclient.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.util.Log;
import com.codepath.twitterclient.R;
import com.codepath.twitterclient.models.Tweet;
import com.codepath.twitterclient.utils.FormatUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetAdapter extends ArrayAdapter<Tweet> {

//	private IntentData intentData = IntentData.getInstance();
	
	private static class ViewHolder {
		ImageView ivProfileImageView;
		ImageView ivMediaImageView;
		TextView tvUserName;
		TextView tvScreenName;
		TextView tvBody;
		TextView tvTimeStamp;
		ImageView ivFavouritesImage;
		TextView tvFavoritesCount;
		ImageView ivRetweetsImage;
		TextView tvRetweetsCount;		
	}
	
	public TweetAdapter(Context context, List<Tweet> objects) {
		super(context, R.layout.tweet_item, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Tweet tweet = getItem(position);
		ViewHolder viewHolder = null;
		if (convertView == null){
			viewHolder = new ViewHolder();
			LayoutInflater inflator = LayoutInflater.from(getContext());
			convertView = inflator.inflate(R.layout.tweet_item, parent, false);
			viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
			viewHolder.ivProfileImageView = (ImageView) convertView.findViewById(R.id.ivProfileImage);
			viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
			viewHolder.ivMediaImageView = (ImageView) convertView.findViewById(R.id.ivMediaImageThumb);
			viewHolder.ivFavouritesImage = (ImageView) convertView.findViewById(R.id.ivFavouritesImage);
			viewHolder.tvFavoritesCount = (TextView) convertView.findViewById(R.id.tvFavoritesCount);
			viewHolder.ivRetweetsImage = (ImageView) convertView.findViewById(R.id.ivRetweetsImage);
			viewHolder.tvRetweetsCount = (TextView) convertView.findViewById(R.id.tvRetweetsCount);
			convertView.setTag(viewHolder);
		}
		else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
//		ivProfileImageView.setImageResource(android.R.color.transparent);
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		try {
			imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.ivProfileImageView);
		}
		catch(Exception e){
			Log.e("error while loading image");
		}
		viewHolder.tvUserName.setText(Html.fromHtml(tweet.getUser().getName()));
		viewHolder.tvScreenName.setText(Html.fromHtml("@" + tweet.getUser().getScreenName()));
		viewHolder.tvBody.setText(Html.fromHtml(tweet.getBody()));
		viewHolder.tvTimeStamp.setText(FormatUtils.getFormattedTimestamp(convertView.getContext(), tweet.getCreatedAt()));
		if (tweet.getMediaUrl() != null && tweet.getMediaUrl() != "") {
			String mediaUrl = tweet.getMediaUrl(); //+ ":thumb";
			try {
				viewHolder.ivMediaImageView.getLayoutParams().height=300;
				ImageLoader.getInstance().displayImage(mediaUrl, viewHolder.ivMediaImageView);
				viewHolder.ivMediaImageView.setVisibility(View.VISIBLE);
			}
			catch(Exception e){
				Log.e("error while loading image");
			}
		}
		else{
			viewHolder.ivMediaImageView.setVisibility(View.GONE);
		}
		
		if (tweet.getRetweetCount() > 0){
			viewHolder.tvRetweetsCount.setText(String.valueOf(tweet.getRetweetCount()));
		}else{
			viewHolder.tvRetweetsCount.setText("   ");
		}			
		
		if (tweet.getFavouritesCount() > 0){
			viewHolder.tvFavoritesCount.setText(String.valueOf(tweet.getFavouritesCount()));
		}
		else{
			viewHolder.tvFavoritesCount.setText("   ");
		}
				
//		convertView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View paramView) {
//				Intent i = new Intent(paramView.getContext(), TweetDetailsActivity.class);
//				intentData.setTweetDetailed(tweet);
//				paramView.getContext().startActivity(i);				
//			}
//		});
		
		return convertView;
	}
}
