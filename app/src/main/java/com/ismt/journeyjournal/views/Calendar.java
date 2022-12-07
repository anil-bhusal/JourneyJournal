package com.ismt.journeyjournal.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ismt.journeyjournal.R;
import com.ismt.journeyjournal.journal.JournalDashboard;

public class Calendar extends AppCompatActivity {
    CalendarView calendar;
    TextView tv;
    private ImageView DashHome, calender1, mapAddNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        calendar = findViewById(R.id.calendarView);
        DashHome = findViewById(R.id.DashHome);
        calender1 = findViewById(R.id.calender1);
        mapAddNote = findViewById(R.id.mapAddNote);


        /* For DashHome */
        DashHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Calendar.this, JournalDashboard.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

            }
        });

        /* For Map */
        /*Sets the listener to be notified upon selected map change.*/
        mapAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Calendar.this, LocationMap.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        /* For Calendar */
        /*Sets the listener to be notified upon selected date change.*/
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                /*Get String date*/
                String d = i2 + "/" + i1 + "/" + 2;
                

            }
        });

    }
}