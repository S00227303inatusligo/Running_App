package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private final double HI_STEP = 11.0;
    private final double LO_STEP = 8.0;
    boolean highLimit = false, countingSteps = false;
    int counter = 0;

    TextView tvSteps, tvTime;
    Button btnStart, btnStop, btnReset, btnShowRun;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private long startTime = 0; // Timestamp when counting starts
    private int elapsedSeconds = 0; // Elapsed time since counting started
    private boolean timerRunning = false;

    Date currentDate = new Date();

    // A handler to update the timer TextView
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvSteps = findViewById(R.id.tvSteps);
        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);
        btnShowRun = findViewById(R.id.btnShowRun);

        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCountingSteps();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countingSteps = false;
                timerRunning = false;
                // Handle any other actions you want to take on "Stop"
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countingSteps = false;
                timerRunning = false;
                counter = 0;
                tvSteps.setText("0");
                tvTime.setText("0"); // Reset the timer
                btnStart.setVisibility(View.VISIBLE); // Show the "Start" button
                btnStop.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.INVISIBLE);
                btnShowRun.setVisibility(View.INVISIBLE);// Hide the "Stop" button
                // Handle any other actions you want to take on "Reset"
            }
        });

        btnShowRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // You can customize the date format
                String formattedDate = dateFormat.format(currentDate);
                // Create an Intent to start the RunSummaryActivity
                Intent intent = new Intent(MainActivity.this, RunSummaryActivity.class);

                // Pass data to the second activity using extras
                intent.putExtra("date", formattedDate); // Replace 'currentDate' with the actual date
                intent.putExtra("metersRun", round(counter * 0.8, 2));
                intent.putExtra("caloriesBurned", round(counter * 0.04, 2));
                intent.putExtra("timeTaken", elapsedSeconds);

                // Start the second activity
                startActivity(intent);
            }
        });


    }


    /*
     * When the app is brought to the foreground - using app on screen
     */
    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * App running but not on screen - in the background
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (countingSteps) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate the magnitude using Pythagoras's theorem
            double mag = round(Math.sqrt((x * x) + (y * y) + (z * z)), 2);

            // Detect steps based on your logic
            if ((mag > HI_STEP) && (highLimit == false)) {
                highLimit = true;
            }
            if ((mag < LO_STEP) && (highLimit == true)) {
                // We have a step
                counter++;
                tvSteps.setText(String.valueOf(counter));
                highLimit = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void startCountingSteps() {
        countingSteps = true;
        btnStop.setVisibility(View.VISIBLE); // Show the "Stop" button
        btnReset.setVisibility(View.VISIBLE); // Show the "Reset" button
        btnShowRun.setVisibility(View.VISIBLE); // Show the "Show Run" button
        startTime = System.currentTimeMillis(); // Record the start time
        timerRunning = true;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    // Method to update the timer TextView
    private void updateTimer() {
        if (timerRunning) {
            long currentTime = System.currentTimeMillis();
            elapsedSeconds = (int) ((currentTime - startTime) / 1000);
            tvTime.setText(String.valueOf(elapsedSeconds));
            timerHandler.postDelayed(timerRunnable, 1000);
        }
    }
}