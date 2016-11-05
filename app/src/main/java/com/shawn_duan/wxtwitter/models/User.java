package com.shawn_duan.wxtwitter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by sduan on 10/29/16.
 */

@Parcel
public class User {
    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;
    private int followersCount;
    private int followingsCount;
    private String description;

    public static User fromJSONObject(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url").replace("_normal", "");
            user.followersCount = jsonObject.getInt("followers_count");
            user.followingsCount = jsonObject.getInt("friends_count");
            user.description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getDescription() {
        return description;
    }
}
