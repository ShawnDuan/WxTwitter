package com.shawn_duan.wxtwitter.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shawn_duan.wxtwitter.WxTwitterApplication;
import com.shawn_duan.wxtwitter.R;
import com.shawn_duan.wxtwitter.activities.MainActivity;
import com.shawn_duan.wxtwitter.models.Tweet;
import com.shawn_duan.wxtwitter.network.TwitterClient;
import com.shawn_duan.wxtwitter.rxbus.InsertNewTweetEvent;
import com.shawn_duan.wxtwitter.rxbus.RxBus;
import com.shawn_duan.wxtwitter.utils.Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

/**
 * Created by sduan on 10/30/16.
 */

public class ComposeTweetDialogFragment extends DialogFragment {
    private final static String TAG = ComposeTweetDialogFragment.class.getSimpleName();

    private MainActivity mActivity;
    private TwitterClient mClient;

    @BindView(R.id.etComposeBody)
    EditText etComposeBody;
    @BindView(R.id.btSubmit)
    Button btSubmit;
    @BindView(R.id.tvRemainingCount)
    TextView tvRemainingCounter;
    private Unbinder unbinder;

    private SharedPreferences mSharedPreferences;
    private String draft;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = WxTwitterApplication.getRestClient();     // singleton client
        mActivity = (MainActivity) getActivity();

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        draft = mSharedPreferences.getString(getString(R.string.compose_draft), "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        unbinder = ButterKnife.bind(this, view);
        etComposeBody.append(draft);

        return view;
    }

    @OnClick(R.id.btSubmit)
    void submitNewTweet() {
        if (etComposeBody != null) {
            String content = etComposeBody.getText().toString();

            mClient.composeTweet(content, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet newTweet = Tweet.fromJSONObject(response);
                    draft = "";

                    mActivity.onBackPressed();
                    Snackbar.make(mActivity.mToolbar, "Tweet submitted successfully!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    RxBus.getInstance().post(new InsertNewTweetEvent(newTweet));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "Failed to post new Tweet.");
                }
            });

        }
    }

    @OnTextChanged(R.id.etComposeBody)
    void updateRemainingCount(Editable editable) {
        draft = editable.toString();
        int remaining = 140 - draft.length();
        tvRemainingCounter.setText(String.valueOf(140 - draft.length()));
        if (remaining < 0) {
            tvRemainingCounter.setTextColor(getResources().getColor(R.color.red));
            btSubmit.setClickable(false);
        } else {
            tvRemainingCounter.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            btSubmit.setClickable(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).hideFab();
        Utils.showSoftKeyboard(getActivity(), etComposeBody);
    }

    @Override
    public void onPause() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.compose_draft), draft);
        editor.commit();

        ((MainActivity)getActivity()).showFab();
        Utils.hideSoftKeyboard(getActivity(), etComposeBody);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
