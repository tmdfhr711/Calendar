package com.plplim.david.calendar.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by OHRok on 2018-03-06.
 */

public class SendPushApi extends AsyncTask<String, Void, String> {
    private String target;
    private Context context;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private RequestHandler requestHandler;
    private String userID, userGroup, title, content, date, time;
    public SendPushApi(Context context) {
        this.context = context;
        sharedPreferenceUtil = new SharedPreferenceUtil(context);
        requestHandler = new RequestHandler();
    }

    @Override
    protected void onPreExecute() {
        target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/SendPush.php";
        Log.e("SendPush", target);
        userID = sharedPreferenceUtil.getValue("userID", "");
        userGroup = sharedPreferenceUtil.getValue("userGroup", "");
    }

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> data = new HashMap<>();

        Log.e("doing", userID);
        //data.put("userID", userID);
        //data.put("userGroup", userGroup);
        data.put("userID", params[4]);
        data.put("userGroup", params[5]);
        data.put("todoTitle", params[0]);
        data.put("todoContent", params[1]);
        data.put("todoDate", params[2]);
        data.put("todoTime", params[3]);


        String result = requestHandler.sendPostRequest(target, data);
        Log.e("SendPushApiBack", result);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("SendPushApi", result);
    }


}
