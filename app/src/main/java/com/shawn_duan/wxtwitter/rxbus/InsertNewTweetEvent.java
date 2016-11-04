package com.shawn_duan.wxtwitter.rxbus;

import com.shawn_duan.wxtwitter.models.Tweet;

/**
 * Created by sduan on 10/30/16.
 */

public class InsertNewTweetEvent {

    private Tweet tweet;

    public InsertNewTweetEvent(Tweet tweet) {
        this.tweet = tweet;
    }

    public Tweet getTweet() {
        return tweet;
    }

}
