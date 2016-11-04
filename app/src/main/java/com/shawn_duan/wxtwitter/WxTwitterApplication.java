package com.shawn_duan.wxtwitter;

import android.app.Application;
import android.content.Context;

import com.shawn_duan.wxtwitter.network.TwitterClient;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = WxTwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class WxTwitterApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		WxTwitterApplication.context = this;
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, WxTwitterApplication.context);
	}
}