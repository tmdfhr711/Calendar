package com.plplim.david.calendar.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.plplim.david.calendar.R;
import com.plplim.david.calendar.activity.RegisterActivity;
import com.plplim.david.calendar.adapter.TodoListAdapter;
import com.plplim.david.calendar.model.NotificationModel;
import com.plplim.david.calendar.model.Todo;
import com.plplim.david.calendar.util.RequestHandler;
import com.plplim.david.calendar.util.SaturdayDecorator;
import com.plplim.david.calendar.util.SendPushApi;
import com.plplim.david.calendar.util.SharedPreferenceUtil;
import com.plplim.david.calendar.util.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener,TimePicker.OnTimeChangedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private RequestHandler requestHandler = new RequestHandler();
    private SharedPreferenceUtil sharedPreferenceUtil;

    private LinearLayout calendarLayout, detailLayout;
    private MaterialCalendarView materialCalendarView;
    private TimePicker timePicker;
    private EditText titleText, contentText;
    private CheckBox shareCheck;
    private Button addButton, detailButton;

    private AlertDialog dialog;

    private int selectedDay = -1, selectedMonth = -1, selectedYear = -1, selectedHour = -1, selectedMinute = -1;
    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        calendarLayout = (LinearLayout) getView().findViewById(R.id.addfragment_linearlayout_calendarlayout);
        detailLayout = (LinearLayout) getView().findViewById(R.id.addfragment_linearlayout_detaillayout);
        materialCalendarView = (MaterialCalendarView) getView().findViewById(R.id.addfragment_calendarview_date);
        timePicker = (TimePicker) getView().findViewById(R.id.addfragment_timepicker_time);
        titleText = (EditText) getView().findViewById(R.id.addfragment_edittext_title);
        contentText = (EditText) getView().findViewById(R.id.addfragment_edittext_content);
        shareCheck = (CheckBox) getView().findViewById(R.id.addfragment_checkbox_share);
        addButton = (Button) getView().findViewById(R.id.addfragment_button_add);
        detailButton = (Button) getView().findViewById(R.id.addfragment_button_detail);
        sharedPreferenceUtil = new SharedPreferenceUtil(getView().getContext());
        timePicker.setIs24HourView(false);
        timePicker.setOnTimeChangedListener(this);

        materialCalendarView.setOnDateChangedListener(this);
        materialCalendarView.setOnMonthChangedListener(this);
        materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(1992, 1, 1))
                .setMaximumDate(CalendarDay.from(2100, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedHour <= 0 || selectedMinute <= 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("먼저 시간을 선택해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (titleText.getText().toString().equals("") || contentText.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("모든 정보를 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                new BackgroundTask().execute();
            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDay < 0 || selectedMonth < 0 || selectedYear < 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("먼저 날짜를 선택해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                calendarLayout.setVisibility(View.GONE);
                detailLayout.setVisibility(View.VISIBLE);
            }
        });




    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        selectedYear = date.getYear();
        selectedMonth = date.getMonth() + 1;
        selectedDay = date.getDay();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        selectedHour = hourOfDay;
        selectedMinute = minute;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void sendGcm(String title, String text, String to){
        Gson gson = new Gson();

        String[] tokens = to.split(",");
        NotificationModel notificationModel = new NotificationModel();
        for(int i = 0; i <= tokens.length - 1; i++) {
            Log.e("token[" + String.valueOf(i) + "]", tokens[i]);
            notificationModel.to = tokens[i];
            notificationModel.notification.title = title;
            notificationModel.notification.text = text;
            notificationModel.data.title = title;
            notificationModel.data.text = text;

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));
            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .addHeader("Authorization", "key=AIzaSyBFb4FwrWp_CnpFRqIcIhVfoZQ1_kMnIMk")
                    .url("https://gcm-http.googleapis.com/gcm/send")
                    .post(requestBody)
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }


    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;
        String date, time, title, content,share;
        @Override
        protected void onPreExecute() {
            target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/TodoAdd.php";
            date = String.valueOf(selectedYear) + "/" + String.valueOf(selectedMonth) + "/" + String.valueOf(selectedDay);
            time = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
            if (shareCheck.isChecked()) {
                share = "1";
            } else {
                share = "0";
            }
            title = titleText.getText().toString();
            content = contentText.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HashMap<String, String> data = new HashMap<>();

            data.put("date", date);
            data.put("time", time);
            data.put("title", title);
            data.put("content", content);
            data.put("userID", sharedPreferenceUtil.getValue("userID", "userID"));
            data.put("group", sharedPreferenceUtil.getValue("userGroup", "userGroup"));
            data.put("share", share);
            data.put("userAuth", FirebaseInstanceId.getInstance().getToken());

            String result = requestHandler.sendPostRequest(target, data);
            Log.e("TodoAdd result", result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                /*JSONObject jsonResponse = new JSONObject(result);
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("일정 등록에 성공했습니다")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new TodoFragment()).commit();
                                    //SendPushApi sendPushApi = new SendPushApi(getView().getContext());
                                    //sendPushApi.execute(title, content, date, time);
                                    sendGcm();
                                }
                            })
                            .create();
                    dialog.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("일정 등록에 실패했습니다")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                }*/
                JSONObject jsonObject = new JSONObject(result);
                Log.e("JSON RESULT", result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String userNum = "", userID = "", userPassword = "", userEmail = "", userGroup = "", userAuth = "";
                JSONObject object = jsonArray.getJSONObject(count);
                boolean success = object.getBoolean("success");
                String tokens = object.getString("tokens");
                tokens = tokens.replaceAll("\"", "");
                tokens = tokens.replaceAll("\\[", "");
                tokens = tokens.replaceAll("]", "");
                Log.e("tokens", tokens);
                Log.e("success", String.valueOf(success));
                if (success) {
                    sendGcm(title, content, tokens);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
