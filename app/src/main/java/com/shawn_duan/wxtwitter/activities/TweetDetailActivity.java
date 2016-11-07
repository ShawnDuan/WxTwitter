package com.shawn_duan.wxtwitter.activities;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.network.TwitterClient;
import com.shawn_duan.wxtwitter.utils.PatternEditableBuilder;

import org.parceler.Parcels;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.shawn_duan.wxtwitter.utils.Constants.SCREEN_NAME_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.TAG_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.USER_KEY;

public class TweetDetailActivity extends AppCompatActivity {

    Tweet mTweet;

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

    private Toolbar mToolbar;
    private Unbinder unbinder;
    private TwitterClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(mToolbar);

        unbinder = ButterKnife.bind(this);
        mClient = WxTwitterApplication.getRestClient();

        mTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        setTweet();

        // don't focus on the editview
        tvTweetBody.setFocusableInTouchMode(true);
        tvTweetBody.requestFocus();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    public void setTweet() {
        tvUserName.setText(mTweet.getUser().getName());
        tvUserAccountName.setText(mTweet.getUser().getScreenName());
        tvTweetBody.setText(mTweet.getBody());
        tvTweetTime.setText(mTweet.getCreateAt());
        tvRetweetCount.setText(String.valueOf(mTweet.getRetweetCount()));
        tvRetweetCount.setVisibility((mTweet.getRetweetCount() == 0) ? View.INVISIBLE : View.VISIBLE);
        tvFavourite.setText(String.valueOf(mTweet.getFavouritesCount()));
        tvFavourite.setVisibility((mTweet.getFavouritesCount() == 0) ? View.INVISIBLE : View.VISIBLE);
        ivUserAvatar.setImageResource(android.R.color.transparent);

        String replyToScreenName = mTweet.getReplyToScreenName();
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

        Glide.with(this)
                .load(mTweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 20, 0))
                .into(ivUserAvatar);

        ivUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TweetDetailActivity.this, ProfileActivity.class);
                intent.putExtra(USER_KEY, Parcels.wrap(mTweet.getUser()));
                startActivity(intent);
            }
        });

        if (mTweet.getMedia() != null && mTweet.getMedia().getType().equals("photo")) {
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(mTweet.getMedia().getMediaUrl())
                    .into(ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
        }

        new PatternEditableBuilder()
                .addPattern(Pattern.compile("\\@(\\w+)"), getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(TweetDetailActivity.this, ProfileActivity.class);
                                intent.putExtra(SCREEN_NAME_KEY, text);
                                TweetDetailActivity.this.startActivity(intent);
                            }
                        })
                .addPattern(Pattern.compile("\\#(\\w+)"), getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent intent = new Intent(TweetDetailActivity.this, TagTimelineActivity.class);
                                intent.putExtra(TAG_KEY, text);
                                TweetDetailActivity.this.startActivity(intent);
                            }
                        }).into(tvTweetBody);
    }
}
