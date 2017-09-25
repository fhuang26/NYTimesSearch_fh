package com.example.felixh.nytimessearch.model;

import com.example.felixh.nytimessearch.data.SearchState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Felix Huang on 9/23/2017.
 */

@Parcel
public class Article implements Comparable<Article> {
    String webUrl;

    // empty constructor is needed by the Parceler library.
    public Article() {
    }

    public int compareTo(Article other) {
        String otherDate = other.getDate();
        int flag = this.date.compareTo(otherDate);
        if (SearchState.date_sort_sign > 0) { //  1: oldest --> newest
            return flag;
        } else {  // -1: newest --> oldest
            return -flag;
        }
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    String headLine;
    String thumbNail;

    public String getDate() {
        return date;
    }

    String date;

    public Article (JSONObject jsonObj) {
        try {
            this.webUrl = jsonObj.getString("web_url");
            String dateStr = jsonObj.getString("pub_date");
            if (dateStr == null || dateStr.isEmpty()) {
                this.date = "0";
            } else {
                this.date = dateStr.substring(0,10);
            }

            JSONObject hdJsonObj = jsonObj.getJSONObject("headline");
            if (hdJsonObj != null) {
                this.headLine = hdJsonObj.getString("main");
            } else {
                this.headLine = "";
            }
            JSONArray multimedia = jsonObj.getJSONArray("multimedia");
            if (multimedia != null && multimedia.length() > 0) {
                JSONObject mmJsonObj = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + mmJsonObj.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> parseJSONArray (JSONArray arr) {
        ArrayList<Article> res = new ArrayList<Article>();
        for (int k = 0; k < arr.length(); k++) {
            try {
                JSONObject jsonObj = arr.getJSONObject(k);
                Article article = new Article(jsonObj);
                String headline = article.getHeadLine();
                if (headline == null || headline.isEmpty()) continue;
                String dateStr = article.getDate();
                if (dateStr == null || dateStr.isEmpty()) continue;
                if (SearchState.beginDate != null && !SearchState.beginDate.isEmpty()) {
                    if (dateStr.compareTo(SearchState.beginDate) < 0) continue;
                }
                res.add(article);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return res;
    }
}
