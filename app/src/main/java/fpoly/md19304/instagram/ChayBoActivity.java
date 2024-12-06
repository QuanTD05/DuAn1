package fpoly.md19304.instagram;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChayBoActivity extends AppCompatActivity implements SensorEventListener {

    private TextView targetSteps, currentSteps, status;
    private LineChart lineChart;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isSensorPresent = false;

    private int stepGoal = 5000; // Mục tiêu bước chân
    private int currentStepCount = 0; // Bước chân hiện tại
    private int dailyStepCount = 0; // Bước chân trong ngày
    private Handler dailySaveHandler = new Handler();
    private int initialStepCount = 0; // Đếm bước bắt đầu khi khởi động ứng dụng

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chay_bo);

        // Khởi tạo UI
        targetSteps = findViewById(R.id.target_steps);
        currentSteps = findViewById(R.id.current_steps);
        status = findViewById(R.id.status);
        lineChart = findViewById(R.id.lineChart);

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("steps");

        // Khởi tạo Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            Toast.makeText(this, "Cảm biến đếm bước chân không khả dụng!", Toast.LENGTH_LONG).show();
            isSensorPresent = false;
        }

        // Cấu hình biểu đồ
        setupLineChart();

        // Lấy mục tiêu bước chân từ Firebase
        fetchStepGoalFromFirebase();

        // Tải dữ liệu bước chân hàng ngày và hiển thị lên biểu đồ
        loadDailyStepsFromFirebase();

        // Lên lịch lưu bước chân hàng ngày
        scheduleDailySave();

        // Kiểm tra và yêu cầu quyền truy cập cảm biến
        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == 0) {
                initialStepCount = (int) event.values[0]; // Lưu số bước khi bắt đầu ứng dụng
            }
            currentStepCount = (int) event.values[0] - initialStepCount; // Tính số bước hiện tại
            dailyStepCount = currentStepCount;

            currentSteps.setText("Bước chân hiện tại: " + currentStepCount);
            updateStatus();

            // Lưu bước chân hiện tại lên Firebase
            saveCurrentStepsToFirebase(currentStepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Không cần xử lý trong trường hợp này
    }

    private void fetchStepGoalFromFirebase() {
        databaseReference.child("stepGoal").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                stepGoal = snapshot.getValue(Integer.class);
                targetSteps.setText("Mục tiêu hiện tại: " + stepGoal + " bước");
                updateStatus();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tải mục tiêu bước chân!", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveCurrentStepsToFirebase(int steps) {
        databaseReference.child("currentSteps").setValue(steps)
                .addOnSuccessListener(aVoid -> {
                    // Thành công
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể lưu bước chân hiện tại!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveDailyStepsToFirebase() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseReference.child("dailySteps").child(currentDate).setValue(dailyStepCount)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã lưu bước chân của ngày " + currentDate, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể lưu bước chân hàng ngày!", Toast.LENGTH_SHORT).show();
                });
    }

    private void scheduleDailySave() {
        long currentTime = System.currentTimeMillis();
        long endOfDay = getEndOfDayTime();

        dailySaveHandler.postDelayed(() -> {
            saveDailyStepsToFirebase();
            dailyStepCount = 0;
            scheduleDailySave(); // Lên lịch tiếp cho ngày hôm sau
        }, endOfDay - currentTime);
    }

    private long getEndOfDayTime() {
        return System.currentTimeMillis() + 24 * 60 * 60 * 1000; // Đơn giản hóa thời gian đến ngày tiếp theo
    }

    private void updateStatus() {
        if (currentStepCount >= stepGoal) {
            status.setText("Trạng thái: Đã đạt mục tiêu!");
            status.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            status.setText("Trạng thái: Chưa đạt mục tiêu đề ra");
            status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date((long) value));
            }
        });
    }

    private void loadDailyStepsFromFirebase() {
        databaseReference.child("dailySteps").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                List<Entry> entries = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String date = data.getKey();
                    Integer steps = data.getValue(Integer.class);

                    if (date != null && steps != null) {
                        try {
                            long timeInMillis = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .parse(date).getTime();
                            entries.add(new Entry(timeInMillis, steps));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                LineDataSet dataSet = new LineDataSet(entries, "Bước chân hàng ngày");
                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tải dữ liệu bước chân!", Toast.LENGTH_SHORT).show();
        });
    }
}
