package com.plplim.david.calendar.fragment;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.plplim.david.calendar.R;
import com.plplim.david.calendar.activity.LoginActivity;
import com.plplim.david.calendar.adapter.TodoListAdapter;
import com.plplim.david.calendar.model.Todo;
import com.plplim.david.calendar.util.EventDecorator;
import com.plplim.david.calendar.util.RequestHandler;
import com.plplim.david.calendar.util.SaturdayDecorator;
import com.plplim.david.calendar.util.SharedPreferenceUtil;
import com.plplim.david.calendar.util.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
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

    private String todoTitle;
    private String todoContent;

    private ListView todoListview;
    private TodoListAdapter todoListAdapter;
    private List<Todo> todoList;
    private AlertDialog dialog;
    //Create CalendarView
    private MaterialCalendarView materialCalendarView;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private RequestHandler requestHandler = new RequestHandler();
    private SharedPreferenceUtil sharedPreferenceUtil;
    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        Log.e("TodoFragment", "onActivityCreated TodoFragment");
        todoListview = (ListView) getView().findViewById(R.id.todofragment_listview);
        todoList = new ArrayList<Todo>();
        materialCalendarView = (MaterialCalendarView) getView().findViewById(R.id.todofragment_calendarview);
        sharedPreferenceUtil = new SharedPreferenceUtil(getView().getContext());
        //init Calendar
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
        new AllTodoListTask().execute();
        todoListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                String title = ((Todo)todoListAdapter.getItem(position)).title;
                String content = ((Todo)todoListAdapter.getItem(position)).content;
                String id = ((Todo)todoListAdapter.getItem(position)).id;
                dialog = builder
                        .setTitle(title)
                        .setMessage(content + "\n\n\n" + "작성자 : " + id)
                        .setIcon(R.drawable.ic_textsms_white_24dp)
                        .setNegativeButton("확인", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        String query;
        if((date.getMonth() + 1) < 10){
            query = String.valueOf(date.getYear()) + "/" + "0"+String.valueOf(date.getMonth() + 1) + "/" + String.valueOf(date.getDay());
        } else {
            query = String.valueOf(date.getYear()) + "/" + String.valueOf(date.getMonth() + 1) + "/" + String.valueOf(date.getDay());
        }

        new BackgroundTask().execute(query);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo, container, false);
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

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String target;
        @Override
        protected void onPreExecute() {
            target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/TodoList.php";
            todoList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();

            String query = params[0];
            String group = sharedPreferenceUtil.getValue("userGroup","");

            data.put("date", query);
            data.put("group", group);

            String result = requestHandler.sendPostRequest(target, data);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("JSON RESULT", result.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String todoNum,todoTitle, todoContent, todoID, todoDate, todoTime,share;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    todoNum = object.getString("todoNum");
                    todoTitle = object.getString("todoTitle");
                    todoContent = object.getString("todoContent");
                    todoID = object.getString("todoID");
                    todoDate = object.getString("todoDate");
                    todoTime = object.getString("todoTime");
                    share = object.getString("share");
                    Log.e("todoTitle", todoTitle);
                    Todo todo = new Todo(todoNum,todoID, todoTitle, todoContent, todoDate, todoTime,share);
                    todoList.add(todo);
                    count++;
                }
                todoListAdapter = new TodoListAdapter(getActivity().getApplicationContext(), todoList);
                todoListview.setAdapter(todoListAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AllTodoListTask extends AsyncTask<String, Void, String> {

        String target;
        ArrayList<CalendarDay> days = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/GroupTodoList.php";
            todoList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();

            String group = sharedPreferenceUtil.getValue("userGroup","");

            data.put("group", group);

            String result = requestHandler.sendPostRequest(target, data);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String todoTitle, todoContent, todoID, todoDate, todoTime;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    todoTitle = object.getString("todoTitle");
                    todoContent = object.getString("todoContent");
                    todoID = object.getString("todoID");
                    todoDate = object.getString("todoDate");
                    todoTime = object.getString("todoTime");
                    String[] date = todoDate.split("/");
                    CalendarDay day = CalendarDay.from(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
                    days.add(day);
                    count++;
                }
                materialCalendarView.addDecorator(new EventDecorator(Color.RED, days));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
