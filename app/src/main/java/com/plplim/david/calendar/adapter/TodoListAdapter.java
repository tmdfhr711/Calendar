package com.plplim.david.calendar.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.plplim.david.calendar.R;
import com.plplim.david.calendar.model.Todo;

import java.util.List;

/**
 * Created by OHRok on 2018-02-27.
 */

public class TodoListAdapter extends BaseAdapter {
    private Context context;
    private List<Todo> todoList;
    private AlertDialog dialog;
    public TodoListAdapter(Context context, List<Todo> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @Override
    public int getCount() {
        return todoList.size();
    }

    @Override
    public Object getItem(int i) {
        return todoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.item_todo, null);

        TextView titleText = (TextView) v.findViewById(R.id.todo_titleText);
        TextView idText = (TextView) v.findViewById(R.id.todo_idText);
        TextView dateText = (TextView) v.findViewById(R.id.todo_dateText);

        final String title, content, id, date;
        title = todoList.get(i).getTitle();
        content = todoList.get(i).getContent();
        id = todoList.get(i).getId();
        date = todoList.get(i).getDate() + " " + todoList.get(i).getTime();

        titleText.setText(title);
        idText.setText(id);
        dateText.setText(date);

        v.setTag(title);

        return v;
    }
}
