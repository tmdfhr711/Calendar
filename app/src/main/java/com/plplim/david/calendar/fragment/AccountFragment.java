package com.plplim.david.calendar.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.plplim.david.calendar.R;
import com.plplim.david.calendar.activity.LoginActivity;
import com.plplim.david.calendar.adapter.TodoListAdapter;
import com.plplim.david.calendar.model.Todo;
import com.plplim.david.calendar.util.RequestHandler;
import com.plplim.david.calendar.util.SendPushApi;
import com.plplim.david.calendar.util.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

    private Button logoutButton;
    private TextView userIdText;
    private TextView userEmailText;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private RequestHandler requestHandler = new RequestHandler();
    private ListView myTodoListview;
    private TodoListAdapter todoListAdapter;
    private List<Todo> todoList;
    private AlertDialog dialog;

    //수정하기 리소소들
    private LinearLayout profileLinearlayout;

    private LinearLayout updateLinearlayout;
    private Button updateButton;
    private EditText yearText, monthText, dayText, hourText, minuteText, titleText, contentText;
    private CheckBox shareCheckbox;
    private String updateNum;

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        myTodoListview = (ListView) getView().findViewById(R.id.accountfragment_listview);
        todoList = new ArrayList<Todo>();
        sharedPreferenceUtil = new SharedPreferenceUtil(getView().getContext());
        userIdText = (TextView) getView().findViewById(R.id.accountfragment_textview_id);
        userEmailText = (TextView) getView().findViewById(R.id.accountfragment_textview_email);
        userIdText.setText(sharedPreferenceUtil.getValue("userID", ""));
        userEmailText.setText(sharedPreferenceUtil.getValue("userEmail", ""));

        logoutButton = (Button) getView().findViewById(R.id.accountfragment_button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferenceUtil.removeAllValue();
                sharedPreferenceUtil.put("loginStatus", "logout");
                Log.e("share", sharedPreferenceUtil.getValue("loginStatus", "null"));
                Intent intent = new Intent(getView().getContext(), LoginActivity.class);
                startActivity(intent);
                ((Activity)getView().getContext()).finish();
            }
        });

        //수정하기 리소스 초기화
        profileLinearlayout = (LinearLayout) getView().findViewById(R.id.accountfragment_linearlayout_top);
        updateLinearlayout = (LinearLayout) getView().findViewById(R.id.accountfragment_update_layout);
        updateButton = (Button) getView().findViewById(R.id.accountfragment_update_button_update);
        yearText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_year);
        monthText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_month);
        dayText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_day);
        hourText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_hour);
        minuteText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_minute);
        titleText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_title);
        contentText = (EditText) getView().findViewById(R.id.accountfragment_update_edittext_content);
        shareCheckbox = (CheckBox) getView().findViewById(R.id.accountfragment_update_checkbox_share);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업데이트완료
                updateButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);
                updateLinearlayout.setVisibility(View.GONE);
                myTodoListview.setVisibility(View.VISIBLE);
                profileLinearlayout.setVisibility(View.VISIBLE);
                String year, month, day, hour, minute, title, content, share;
                year = yearText.getText().toString();
                month = monthText.getText().toString();
                day = dayText.getText().toString();
                hour = hourText.getText().toString();
                minute = minuteText.getText().toString();
                title = titleText.getText().toString();
                content = contentText.getText().toString();
                if (shareCheckbox.isChecked()) {
                    share = "1";
                } else {
                    share = "0";
                }

                if (year.length() != 4 || month.length() != 2 || day.length() != 2 || hour.length() != 2 || minute.length() != 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("데이터 포맷을 지켜주세요!\nYYYY/MM/DD HH:mm")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (title.length() <= 0 || content.length() <= 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("모든 정보를 입력해야 합니다")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                UpdateTask updateTask = new UpdateTask();
                updateTask.execute(updateNum, year + "/" + month + "/" + day, hour + ":" + minute, title, content, share);
                Log.e("UPDATE", "글 번호 : " + updateNum + "\n" + "날짜 : " + year + "/" + month + "/" + day + "\n" + "시간 : " + hour + ":" + minute
                        + "\n" + "제목 : " + title + "\n" + "내용 : " + content + "\n" + "공유여부 : " + share + "\nlenth : " + String.valueOf(monthText.getText().length()));
            }
        });
        myTodoListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                final String title = ((Todo)todoListAdapter.getItem(position)).title;
                final String content = ((Todo)todoListAdapter.getItem(position)).content;
                final String id = ((Todo)todoListAdapter.getItem(position)).id;
                final String share = ((Todo)todoListAdapter.getItem(position)).share;
                final String num = ((Todo)todoListAdapter.getItem(position)).num;
                final String date = ((Todo)todoListAdapter.getItem(position)).date;
                final String time = ((Todo)todoListAdapter.getItem(position)).time;

                dialog = builder
                        .setTitle(title)
                        .setMessage(content + "\n\n\n" + shareCheck(share))
                        .setIcon(R.drawable.ic_textsms_white_24dp)
                        .setNegativeButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //수정하기 완료
                                logoutButton.setVisibility(View.GONE);
                                updateButton.setVisibility(View.VISIBLE);
                                updateLinearlayout.setVisibility(View.VISIBLE);
                                myTodoListview.setVisibility(View.GONE);
                                profileLinearlayout.setVisibility(View.GONE);
                                String[] dateStr = date.split("/");
                                String[] timeStr = time.split(":");

                                updateNum = num;
                                titleText.setText(title);
                                contentText.setText(content);
                                yearText.setText(dateStr[0]);
                                monthText.setText(dateStr[1]);
                                dayText.setText(dateStr[2]);
                                hourText.setText(timeStr[0]);
                                minuteText.setText(timeStr[1]);
                                return;
                            }
                        })
                        .setPositiveButton("확인",null)
                        .create();
                dialog.show();
            }
        });

        BackgroundTask task = new BackgroundTask();
        task.execute();

    }

    public String shareCheck(String share) {
        if (share.equals("1")) {
            return "공유중인 글입니다";
        } else {
            return "공유하지 않은 글입니다";
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
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
            target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/MyTodoList.php";
            todoList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();

            String userID = sharedPreferenceUtil.getValue("userID","");

            data.put("userID", userID);

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
                String todoNum,todoTitle, todoContent, share, todoDate, todoTime;
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    todoNum = object.getString("todoNum");
                    todoTitle = object.getString("todoTitle");
                    todoContent = object.getString("todoContent");
                    share = object.getString("share");
                    todoDate = object.getString("todoDate");
                    todoTime = object.getString("todoTime");
                    Log.e("todoTitle", todoTitle);
                    Todo todo = new Todo(todoNum,sharedPreferenceUtil.getValue("userID",""), todoTitle, todoContent, todoDate, todoTime, share);
                    todoList.add(todo);
                    count++;
                }
                todoListAdapter = new TodoListAdapter(getActivity().getApplicationContext(), todoList);
                myTodoListview.setAdapter(todoListAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UpdateTask extends AsyncTask<String, Void, String> {

        String target;
        @Override
        protected void onPreExecute() {
            target = "http://plplim.ipdisk.co.kr:8000/todosharecalendar/UpdateTodo.php";
            todoList.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();

            String todoNum = params[0];
            String todoDate = params[1];
            String todoTime = params[2];
            String todoTitle = params[3];
            String todoContent = params[4];
            String share = params[5];

            data.put("todoNum", todoNum);
            data.put("todoDate", todoDate);
            data.put("todoTime", todoTime);
            data.put("todoTitle", todoTitle);
            data.put("todoContent", todoContent);
            data.put("share", share);

            String result = requestHandler.sendPostRequest(target, data);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.equals("success")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("정보 수정을 완료하였습니다")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    new BackgroundTask().execute();
                    return;
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    dialog = builder.setMessage("정보 수정에 오류가 있습니다")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    new BackgroundTask().execute();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
