package com.shawn_duan.wxtwitter.models;

import com.shawn_duan.wxtwitter.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sduan on 10/29/16.
 */

public class Tweet {

    String body;
    long uid;
    User user;
    String createAt;

    public Tweet() {

    }

    public static Tweet fromJSONObject(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // Extract the values from the json, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.user = User.fromJSONObject(jsonObject.getJSONObject("user"));
            tweet.createAt = DateUtils.getRelativeTimeAgo(jsonObject.getString("created_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet tweet = fromJSONObject(jsonArray.getJSONObject(i));
                if (tweet != null) {
                    results.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return results;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreateAt() {
        return createAt;
    }
}
