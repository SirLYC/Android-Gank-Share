package com.lyc.gank.Util;

import com.google.gson.Gson;
import com.lyc.gank.Bean.Results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理返回的json数据
 */

public class Utility {
    private Utility(){}

    public static Results handleResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            return new Gson().fromJson(jsonObject.toString(), Results.class);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
