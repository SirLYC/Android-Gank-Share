package com.lyc.gank.Util;

import android.util.Log;

import com.google.gson.Gson;
import com.lyc.gank.Bean.EveryDayRecommend;
import com.lyc.gank.Bean.RecommendResults;
import com.lyc.gank.Bean.Results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理返回的json数据
 */

public class Utility {
    private Utility(){}

    public static Results getResultsFromJson(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), Results.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static EveryDayRecommend getRecommendFromJson(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), RecommendResults.class).recommend;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
