package com.shawn_duan.wxtwitter.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.fragments.TagsTimelineFragment;

import static com.shawn_duan.wxtwitter.utils.Constants.TAG_KEY;

public class TagTimelineActivity extends AppCompatActivity {

    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_timeline);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(mToolbar);

        // Within the activity
        String tag = getIntent().getStringExtra(TAG_KEY);
        // set toolbar title
        getSupportActionBar().setTitle(tag);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TagsTimelineFragment tagsTimelineFragment = TagsTimelineFragment.newInstance(tag);
        ft.add(R.id.tag_timeline_container, tagsTimelineFragment);
        ft.commit();

    }
}
