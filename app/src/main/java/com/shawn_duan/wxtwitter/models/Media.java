package com.shawn_duan.wxtwitter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by sduan on 11/6/16.
 */

@Parcel
public class Media {
    long id;
    String mediaUrl;
    String url;
    String type;
    int width;
    int height;
    String resize;

    public Media() {

    }

    public long getId() {
        return id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getResize() {
        return resize;
    }

    public static Media fromJSONObject(JSONObject jsonObject) {
        Media media = new Media();
        // Extract the values from the json, store them
        try {
            media.id = jsonObject.getLong("id");
            media.mediaUrl = jsonObject.getString("media_url");
            media.url = jsonObject.getString("url");
            media.type = jsonObject.getString("type");

            JSONObject sizesObj = jsonObject.getJSONObject("sizes");
            if (sizesObj != null) {
                JSONObject largeSize = sizesObj.getJSONObject("large");
                if (largeSize != null) {
                    media.width = largeSize.getInt("w");
                    media.height = largeSize.getInt("h");
                    media.resize = largeSize.getString("resize");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }
}
