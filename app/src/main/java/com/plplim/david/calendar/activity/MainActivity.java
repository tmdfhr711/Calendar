package com.plplim.david.calendar.activity;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.plplim.david.calendar.R;
import com.plplim.david.calendar.fragment.AccountFragment;
import com.plplim.david.calendar.fragment.AddFragment;
import com.plplim.david.calendar.fragment.TodoFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottomnavigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_todo:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new TodoFragment()).commit();
                        return true;

                    case R.id.action_add:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new AddFragment()).commit();
                        return true;

                    case R.id.action_account:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new AccountFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }
}
