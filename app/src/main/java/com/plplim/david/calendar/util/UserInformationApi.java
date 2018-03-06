package com.plplim.david.calendar.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.plplim.david.calendar.activity.LoginActivity;
import com.plplim.david.calendar.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by OHRok on 2018-03-02.
 */

public class UserInformationApi extends AsyncTask<String, Void, String> {
    private String target;
    private Context context;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private RequestHandler requestHandler;
    public UserInformationApi(Context context) {
        this.context = context;
        sharedPreferenceUtil = new SharedPreferenceUtil(context);
        requestHandler = new RequestHandler();
    }

    @Override
    protected void onPreExecute() {
        target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/UserInformation.php";
    }

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> data = new HashMap<>();

        String query = params[0];

        data.put("userID", query);

        String result = requestHandler.sendPostRequest(target, data);

        try {
            JSONObject jsonObject = new JSONObject(result);
            Log.e("JSON RESULT", result.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            String userNum = "", userID = "", userPassword = "", userEmail = "", userGroup = "", userAuth = "";
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                userNum = object.getString("userNum");
                userID = object.getString("userID");
                userPassword = object.getString("userPassword");
                userEmail = object.getString("userEmail");
                userGroup = object.getString("userGroup");
                userAuth = object.getString("userAuth");
                count++;
            }
            sharedPreferenceUtil.put("userNum", userNum);
            sharedPreferenceUtil.put("userID", userID);
            sharedPreferenceUtil.put("userPassword", userPassword);
            sharedPreferenceUtil.put("userEmail", userEmail);
            sharedPreferenceUtil.put("userGroup", userGroup);
            sharedPreferenceUtil.put("userAuth", userAuth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
