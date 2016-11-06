package com.shawn_duan.wxtwitter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.fragments.ComposeTweetDialogFragment;
import com.shawn_duan.wxtwitter.fragments.TabAndPagerFragment;

import static com.shawn_duan.wxtwitter.utils.Constants.USER_PROFILE_IMAGE_URL_PREF_KEY;
import static com.shawn_duan.wxtwitter.utils.Constants.USER_SCREEN_NAME_PREF_KEY;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private FloatingActionButton mFab;
    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(ComposeTweetDialogFragment.newInstance(), true);
            }
        });

        pushFragment(new TabAndPagerFragment(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//            intent.
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void pushFragment(Fragment frag, boolean addToBackStack) {
        String name = frag.getClass().getSimpleName();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, frag, name);
        if (addToBackStack) {
            transaction.addToBackStack(name);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        if (mFragmentManager.getFragments() != null) {
            Log.d(TAG, "fragment # : " + mFragmentManager.getFragments().size());
        }
    }

    private void addFragment(Fragment frag, boolean addToBackStack) {
        String name = frag.getClass().getSimpleName();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.main_container, frag, name);
        if (addToBackStack) {
            transaction.addToBackStack(name);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        if (mFragmentManager.getFragments() != null) {
            Log.d(TAG, "fragment # : " + mFragmentManager.getFragments().size());
        }
    }

    public void hideFab() {
        if (mFab != null) {
            mFab.setVisibility(View.GONE);
        }
    }

    public void showFab() {
        if (mFab != null) {
            mFab.setVisibility(View.VISIBLE);
        }
    }
}
