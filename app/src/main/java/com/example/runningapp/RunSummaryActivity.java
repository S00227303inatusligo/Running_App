package com.example.runningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RunSummaryActivity extends AppCompatActivity {

    TextView dateTextView, metersRunTextView, caloriesBurnedTextView, timeTakenTextView;
    Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_summary);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        double metersRun = intent.getDoubleExtra("metersRun", 0.0);
        double caloriesBurned = intent.getDoubleExtra("caloriesBurned", 0.0);
        int timeTaken = intent.getIntExtra("timeTaken", 0);

        // Initialize TextViews to display the data
        dateTextView = findViewById(R.id.tvDate);
        metersRunTextView = findViewById(R.id.tvMeters);
        caloriesBurnedTextView = findViewById(R.id.tvCalories);
        timeTakenTextView = findViewById(R.id.tvTimeTaken);

        // Set the text for the TextViews
        dateTextView.setText("Date of run:\n" + date);
        metersRunTextView.setText("Meters Run:\n" + metersRun);
        caloriesBurnedTextView.setText("Calories Burned:\n" + caloriesBurned);
        timeTakenTextView.setText("Time Taken (secs):\n" + timeTaken);

        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
