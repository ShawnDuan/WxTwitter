package com.shawn_duan.wxtwitter.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.fragments.UserTimelineFragment;
import com.shawn_duan.wxtwitter.models.User;
import com.shawn_duan.wxtwitter.network.TwitterClient;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.shawn_duan.wxtwitter.utils.Constants.SCREEN_NAME_KEY;

public class ProfileActivity extends AppCompatActivity {
    private final static String TAG = ProfileActivity.class.getSimpleName();

    private TwitterClient mClient;
    private Unbinder unbinder;
    private User mUser;

    public Toolbar mToolbar;

    @BindView(R.id.userAvatar)
    ImageView ivAvatar;
    @BindView(R.id.userName)
    TextView tvUserName;
    @BindView(R.id.userAccountName)
    TextView tvUserAccountName;
    @BindView(R.id.userDescription)
    TextView tvUserDescription;
    @BindView(R.id.following_count)
    TextView tvFollowingCount;
    @BindView(R.id.follower_count)
    TextView tvFollowerCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(mToolbar);

        unbinder = ButterKnife.bind(this);

        mClient = WxTwitterApplication.getRestClient();
        mClient.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mUser = User.fromJSONObject(response);
                getSupportActionBar().setTitle(mUser.getName());

                populateProfileHeader(mUser);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        String screenName = getIntent().getStringExtra(SCREEN_NAME_KEY);
        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.profile_timeline_container, userTimelineFragment);
            ft.commit();
        }

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void populateProfileHeader(User user) {
        Glide.with(this).load(user.getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 20, 0))
                .into(ivAvatar);
        tvUserName.setText(user.getName());
        tvUserAccountName.setText(user.getScreenName());
        tvUserDescription.setText(user.getDescription());
        tvFollowingCount.setText(String.valueOf(user.getFollowingsCount()));
        tvFollowerCount.setText(String.valueOf(user.getFollowersCount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
