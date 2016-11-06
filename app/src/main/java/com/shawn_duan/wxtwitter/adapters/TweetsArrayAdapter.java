package com.shawn_duan.wxtwitter.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.activities.MainActivity;
import com.shawn_duan.wxtwitter.activities.ProfileActivity;
import com.shawn_duan.wxtwitter.activities.TagTimelineActivity;
import com.shawn_duan.wxtwitter.fragments.ComposeTweetDialogFragment;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.models.User;
import com.shawn_duan.wxtwitter.utils.PatternEditableBuilder;

import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.shawn_duan.wxtwitter.utils.Constants.SCREEN_NAME_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.TAG_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.USER_KEY;

/**
 * Created by sduan on 10/29/16.
 */

public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.TweetsViewHolder> {

    private Activity mActivity;
    private List<Tweet> mTweetList;


    private PatternEditableBuilder patternEditableBuilder;

    public TweetsArrayAdapter(Activity activity, List<Tweet> tweets) {
        mActivity = activity;
        mTweetList = tweets;

        patternEditableBuilder = new PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"), mActivity.getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(mActivity, ProfileActivity.class);
                                intent.putExtra(SCREEN_NAME_KEY, text);
                                mActivity.startActivity(intent);
                            }
                        })
                .addPattern(Pattern.compile("\\#(\\w+)"), mActivity.getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(mActivity, TagTimelineActivity.class);
                                intent.putExtra(TAG_KEY, text);
                                mActivity.startActivity(intent);
                            }
                        });
    }

    @Override
    public TweetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_tweet_timeline, parent, false);
        return new TweetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TweetsViewHolder holder, int position) {
        final Tweet tweet = mTweetList.get(position);
        holder.setTweet(tweet);
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
        @BindView(R.id.ibReply)
        ImageButton ibReply;
        @BindView(R.id.ibRetweet)
        ImageButton ibRetweet;
        @BindView(R.id.ibLike)
        ImageButton ibLike;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;
        @BindView(R.id.tvFavouriteCount)
        TextView tvFavourite;
        @BindView(R.id.note_reply_icon)
        ImageView ivNoteReplyIcon;
        @BindView(R.id.note_text_in_reply_to)
        TextView tvTextInReplyTo;
        @BindView(R.id.note_reply_to_whom)
        TextView tvReplyToScreenName;
        @BindView(R.id.tweetMedia)
        ImageView ivMedia;

        Tweet tweetInHolder;

        public TweetsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(TweetsViewHolder.this, itemView);
        }

        public void setTweet(final Tweet tweet) {
            tweetInHolder = tweet;
            tvUserName.setText(tweet.getUser().getName());
            tvUserAccountName.setText(tweet.getUser().getScreenName());
            tvTweetBody.setText(tweet.getBody());
            tvTweetTime.setText(tweet.getCreateAt());
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            tvRetweetCount.setVisibility((tweet.getRetweetCount() == 0) ? View.INVISIBLE : View.VISIBLE);
            tvFavourite.setText(String.valueOf(tweet.getFavouritesCount()));
            tvFavourite.setVisibility((tweet.getFavouritesCount() == 0) ? View.INVISIBLE : View.VISIBLE);
            ivUserAvatar.setImageResource(android.R.color.transparent);

            String replyToScreenName = tweet.getReplyToScreenName();
            if (replyToScreenName != null && replyToScreenName.length() > 0) {
                ivNoteReplyIcon.setVisibility(View.VISIBLE);
                tvTextInReplyTo.setVisibility(View.VISIBLE);
                tvReplyToScreenName.setVisibility(View.VISIBLE);
                tvReplyToScreenName.setText(replyToScreenName);
            } else {
                ivNoteReplyIcon.setVisibility(View.GONE);
                tvTextInReplyTo.setVisibility(View.GONE);
                tvReplyToScreenName.setVisibility(View.GONE);
            }

            Glide.with(mActivity)
                    .load(tweet.getUser().getProfileImageUrl())
                    .bitmapTransform(new RoundedCornersTransformation(mActivity, 20, 0))
                    .into(ivUserAvatar);

            ivUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    intent.putExtra(USER_KEY, Parcels.wrap(tweet.getUser()));
                    mActivity.startActivity(intent);
                }
            });

            if (tweet.getMedia() != null && tweet.getMedia().getType().equals("photo")) {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(mActivity)
                        .load(tweet.getMedia().getMediaUrl())
                        .into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }


            patternEditableBuilder.into(tvTweetBody);
        }

        @OnClick(R.id.ibReply)
        public void replyTweet() {
            ComposeTweetDialogFragment fragment = ComposeTweetDialogFragment.newInstance(tweetInHolder.getUid(), tweetInHolder.getUser().getScreenName());
            fragment.show(((AppCompatActivity) mActivity).getSupportFragmentManager(), "ComposeTweetDialogFragment");
        }
    }
}
