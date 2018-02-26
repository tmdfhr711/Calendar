package com.plplim.david.calendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.plplim.david.calendar.R;
import com.plplim.david.calendar.model.Todo;

import java.util.List;

/**
 * Created by OHRok on 2018-02-27.
 */

public class TodoListAdapter extends BaseAdapter {
    private Context context;
    private List<Todo> todoList;

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.item_todo, null);

        TextView titleText = (TextView) v.findViewById(R.id.todo_titleText);
        TextView idText = (TextView) v.findViewById(R.id.todo_idText);
        TextView dateText = (TextView) v.findViewById(R.id.todo_dateText);

        titleText.setText(todoList.get(i).getTitle());
        idText.setText(todoList.get(i).getId());
        dateText.setText(todoList.get(i).getDate());

        v.setTag(todoList.get(i).getTitle());

        return v;
    }
}
