package com.shawn_duan.wxtwitter.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.models.Tweet;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by sduan on 10/29/16.
 */

public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.TweetsViewHolder> {

    private Activity mActivity;
    private List<Tweet> mTweetList;

    public TweetsArrayAdapter(Activity activity, List<Tweet> tweets) {
        mActivity = activity;
        mTweetList = tweets;
    }

    @Override
    public TweetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_tweet_timeline, parent, false);
        return new TweetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TweetsViewHolder holder, int position) {
        Tweet tweet = mTweetList.get(position);
        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvUserAccountName.setText(tweet.getUser().getScreenName());
        holder.tvTweetBody.setText(tweet.getBody());
        holder.tvTweetTime.setText(tweet.getCreateAt());
        holder.ivUserAvatar.setImageResource(android.R.color.transparent);
        Glide.with(mActivity)
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(mActivity, 20, 0))
                .into(holder.ivUserAvatar);
    }

    @Override
    public int getItemCount() {
        return mTweetList.size();
    }

    public class TweetsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.userAvatar)
        ImageView ivUserAvatar;
        @BindView(R.id.userName)
        TextView tvUserName;
        @BindView(R.id.userAccountName)
        TextView tvUserAccountName;
        @BindView(R.id.tweetTime)
        TextView tvTweetTime;
        @BindView(R.id.tweetBody)
        TextView tvTweetBody;

        public TweetsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(TweetsViewHolder.this, itemView);
        }

    }
}
