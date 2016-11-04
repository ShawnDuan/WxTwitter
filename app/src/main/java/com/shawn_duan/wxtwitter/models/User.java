package com.shawn_duan.wxtwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sduan on 10/29/16.
 */

public class User {
    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;

    public static User fromJSONObject(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url").replace("_normal", "");
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

}
