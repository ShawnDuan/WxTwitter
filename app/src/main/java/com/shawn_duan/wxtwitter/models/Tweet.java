package com.shawn_duan.wxtwitter.models;

import com.shawn_duan.wxtwitter.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sduan on 10/29/16.
 */

@Parcel
public class Tweet {

    String body;
    long uid;
    User user;
    String createAt;
    int retweetCount;
    int favouritesCount;
    String replyToScreenName;
    Media media;

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
            tweet.retweetCount = jsonObject.optInt("retweet_count");
            tweet.favouritesCount = jsonObject.optInt("favorite_count");
            if (!jsonObject.isNull("in_reply_to_screen_name")) {
                tweet.replyToScreenName = jsonObject.getString("in_reply_to_screen_name");
            }
            JSONObject entities = jsonObject.optJSONObject("entities");
            if (entities != null) {
                JSONArray mediaJsonArray = entities.optJSONArray("media");
                if (mediaJsonArray != null && mediaJsonArray.length() > 0) {
                    tweet.media = Media.fromJSONObject(mediaJsonArray.getJSONObject(0));
                }
            }

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

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public String getReplyToScreenName() {
        return replyToScreenName;
    }

    public Media getMedia() {
        return media;
    }
}
