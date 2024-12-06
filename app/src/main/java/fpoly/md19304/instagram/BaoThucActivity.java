package fpoly.md19304.instagram;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fpoly.md19304.instagram.Model.sleepDTO;

public class BaoThucActivity extends AppCompatActivity {

    private EditText etSleepTime, etWakeUpTime;
    private TextView tvSleepDuration;
    private Button btnSaveSleep;
    private LineChart lineChart;

    private Calendar sleepTime, wakeUpTime;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference sleepRecordsRef;
    private ArrayList<sleepDTO> sleepList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_thuc);
        initializeFirebase();
        initializeUIComponents();
        setupEventListeners();
        displaySleepRecords();
        adjustPaddingForSystemBars();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Báo thức");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        checkForAdvice(); // Kiểm tra lời khuyên sau 3 ngày
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sleepRecordsRef = database.getReference("sleep").child(currentUser.getUid()).child("sleepRecords");
    }

    private void initializeUIComponents() {
        etSleepTime = findViewById(R.id.et_sleep_time);
        etWakeUpTime = findViewById(R.id.et_wake_up_time);
        tvSleepDuration = findViewById(R.id.tv_sleep_duration);
        btnSaveSleep = findViewById(R.id.btn_save_sleep);
        lineChart = findViewById(R.id.lineChart);
        sleepTime = Calendar.getInstance();
        wakeUpTime = Calendar.getInstance();
    }

    private void setupEventListeners() {
        etSleepTime.setOnClickListener(v -> showTimePickerDialog(sleepTime, etSleepTime));
        etWakeUpTime.setOnClickListener(v -> showTimePickerDialog(wakeUpTime, etWakeUpTime));
        btnSaveSleep.setOnClickListener(v -> saveSleepRecord());
    }

    private void adjustPaddingForSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void redirectToLogin() {
        startActivity(new Intent(BaoThucActivity.this, LoginActivity.class));
        finish();
    }

    private void showTimePickerDialog(Calendar time, EditText editText) {
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minutes) -> {
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minutes);
            editText.setText(String.format("%02d:%02d", hourOfDay, minutes));
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveSleepRecord() {
        if (validateSleepInputs()) {
            try {
                calculateAndDisplaySleepDuration();
                String sleepTimeStr = etSleepTime.getText().toString();
                String wakeUpTimeStr = etWakeUpTime.getText().toString();
                String durationStr = tvSleepDuration.getText().toString();
                String date = getCurrentDate();
                addSleepRecordToRealtimeDB(date, sleepTimeStr, wakeUpTimeStr, durationStr);
                setAlarms();
                displaySleepRecords();
            } catch (Exception e) {
                Log.e(TAG, "Error saving sleep record", e);
                showToast("Đã xảy ra lỗi khi lưu bản ghi giấc ngủ.");
            }
        }
    }

    private boolean validateSleepInputs() {
        String sleepTimeStr = etSleepTime.getText().toString();
        String wakeUpTimeStr = etWakeUpTime.getText().toString();
        if (sleepTimeStr.isEmpty() || wakeUpTimeStr.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thời gian ngủ và thức dậy.");
            return false;
        }
        return true;
    }

    private void addSleepRecordToRealtimeDB(String date, String sleepTimeStr, String wakeUpTimeStr, String durationStr) {
        String recordId = sleepRecordsRef.push().getKey();
        sleepDTO sleepRecord = new sleepDTO(date, sleepTimeStr, wakeUpTimeStr, durationStr);
        if (recordId != null) {
            sleepRecordsRef.child(recordId).setValue(sleepRecord)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Đã lưu bản ghi giấc ngủ");
                        saveSetupTime();
                    })
                    .addOnFailureListener(e -> showToast("Lỗi khi lưu bản ghi giấc ngủ"));
        }
    }

    private void saveSetupTime() {
        DatabaseReference setupTimeRef = sleepRecordsRef.child("setupTime");
        String setupTime = getCurrentDate();
        setupTimeRef.setValue(setupTime);
    }

    private void setAlarms() {
        setAlarmForEvent(wakeUpTime, "Đã đến lúc thức dậy!");
        setAlarmForEvent(sleepTime, "Đã đến lúc đi ngủ!");
    }

    private void setAlarmForEvent(Calendar time, String message) {
        Calendar alarmTime = (Calendar) time.clone();
        alarmTime.add(Calendar.MINUTE, 1);
        Intent intent = new Intent(this, SleepReminderReceiver.class);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void calculateAndDisplaySleepDuration() {
        long durationMillis = wakeUpTime.getTimeInMillis() - sleepTime.getTimeInMillis();
        if (durationMillis < 0) {
            durationMillis += 24 * 60 * 60 * 1000;
        }
        int hours = (int) (durationMillis / (1000 * 60 * 60));
        int minutes = (int) (durationMillis / (1000 * 60)) % 60;
        String durationStr = String.format(Locale.getDefault(), "%d giờ %d phút", hours, minutes);
        tvSleepDuration.setText(durationStr);
        if (hours > 8) {
            showReconfigureTimeDialog();
        }
    }

    private void showReconfigureTimeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thiết lập lại giờ")
                .setMessage("\uD83D\uDCA4 Giấc ngủ không cố định có thể gây hại.")
                .setPositiveButton("OK", (dialog, which) -> resetSleepInputs())
                .create()
                .show();
    }

    private void resetSleepInputs() {
        etSleepTime.setText("");
        etWakeUpTime.setText("");
        tvSleepDuration.setText("");
    }

    private void displaySleepRecords() {
        sleepRecordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sleepList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        sleepDTO record = snapshot.getValue(sleepDTO.class);
                        if (record != null) {
                            sleepList.add(record);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Invalid data format in sleepRecords: " + snapshot.getValue(), e);
                    }
                }
                updateChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error reading sleep records", databaseError.toException());
            }
        });
    }

    private void updateChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < sleepList.size(); i++) {
            sleepDTO record = sleepList.get(i);
            String[] durationParts = record.getDuration().split(" ");
            float durationHours = Float.parseFloat(durationParts[0]);
            entries.add(new Entry(i, durationHours));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Giấc Ngủ");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.invalidate();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void checkForAdvice() {
        DatabaseReference setupTimeRef = sleepRecordsRef.child("setupTime");
        setupTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String setupTime = dataSnapshot.getValue(String.class);
                if (setupTime != null && isMoreThanThreeDays(setupTime)) {
                    showToast("3 ngày không ghi nhật ký! Cập nhật lại giờ ngủ.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error checking setup time", databaseError.toException());
            }
        });
    }

    private boolean isMoreThanThreeDays(String setupTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date setupDate = sdf.parse(setupTime);
            long diffMillis = new Date().getTime() - setupDate.getTime();
            return diffMillis > 3 * 24 * 60 * 60 * 1000;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing setup time", e);
            return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(BaoThucActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
