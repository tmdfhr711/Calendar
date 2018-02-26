package com.plplim.david.calendar.util;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OHRok on 2018-02-26.
 */

public class RegisterRequest extends StringRequest{
    final static private String URL = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userEmail, String userGroup, String userAuth, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userEmail", userEmail);
        parameters.put("userGroup", userGroup);
        parameters.put("userAuth", userAuth);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
