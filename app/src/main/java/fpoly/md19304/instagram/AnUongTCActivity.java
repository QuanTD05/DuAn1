package fpoly.md19304.instagram;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnUongTCActivity extends AppCompatActivity {
    private TextView bmiResultTextView, bmiAdviceTextView;
    private TextView breakfastTextView, lunchTextView, dinnerTextView;
    private TextView heightTextView, weightTextView, genderTextView;

    private DatabaseReference userRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_an_uong_tcactivity);

        bmiResultTextView = findViewById(R.id.bmiResult);
        bmiAdviceTextView = findViewById(R.id.bmiAdvice);
        genderTextView = findViewById(R.id.genderInfo);
        heightTextView = findViewById(R.id.heightInfo);
        weightTextView = findViewById(R.id.weightInfo);
        breakfastTextView = findViewById(R.id.breakfastInfo);
        lunchTextView = findViewById(R.id.lunchInfo);
        dinnerTextView = findViewById(R.id.dinnerInfo);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profile");

        // Tải và hiển thị dữ liệu người dùng
        loadUserData();
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String gender = snapshot.child("gender").getValue(String.class);
                    Double height = snapshot.child("height").getValue(Double.class);
                    Double weight = snapshot.child("weight").getValue(Double.class);

                    if (height != null && weight != null) {
                        double bmi = weight / Math.pow(height / 100, 2);

                        // Hiển thị thông tin
                        genderTextView.setText(gender);
                        heightTextView.setText(height + " cm");
                        weightTextView.setText(weight + " kg");
                        bmiResultTextView.setText("BMI của bạn là: " + String.format("%.1f", bmi));

                        // Hiển thị lời khuyên
                        showAdviceBasedOnBMI(bmi);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnUongTCActivity.this, "Không thể tải dữ liệu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAdviceBasedOnBMI(double bmi) {
        String advice;
        String breakfast, lunch, dinner;

        if (bmi < 18.5) {
            advice = "Bạn có cân nặng thấp. Hãy bổ sung dinh dưỡng đầy đủ để tăng cân.";
            breakfast = "Cháo yến mạch - 300 cal, Trái cây - 100 cal";
            lunch = "Cơm gà - 500 cal, Rau luộc - 100 cal";
            dinner = "Cá hồi nướng - 400 cal, Khoai lang - 200 cal";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            advice = "Cân nặng của bạn bình thường. Hãy duy trì chế độ ăn uống lành mạnh.";
            breakfast = "Ngũ cốc - 200 cal, Sữa tươi - 100 cal";
            lunch = "Thịt gà xào rau - 350 cal, Cơm - 250 cal";
            dinner = "Salad cá ngừ - 300 cal, Rau củ nướng - 150 cal";
        } else if (bmi >= 25 && bmi < 29.9) {
            advice = "Bạn đang thừa cân. Hãy điều chỉnh chế độ ăn uống và tập luyện hợp lý.";
            breakfast = "Trứng luộc - 150 cal, Trái cây - 100 cal";
            lunch = "Gà nướng - 250 cal, Rau xanh - 100 cal";
            dinner = "Salad rau quả - 200 cal, Cá nướng - 200 cal";
        } else {
            advice = "Bạn bị béo phì. Hãy tham khảo bác sĩ và thay đổi chế độ ăn uống.";
            breakfast = "Trứng luộc - 150 cal, Bánh mỳ nguyên cám - 100 cal";
            lunch = "Gà luộc - 300 cal, Rau xanh - 150 cal";
            dinner = "Súp rau - 200 cal, Cá hấp - 200 cal";
        }

        bmiAdviceTextView.setText(advice);
        breakfastTextView.setText("Bữa sáng: " + breakfast);
        lunchTextView.setText("Bữa trưa: " + lunch);
        dinnerTextView.setText("Bữa tối: " + dinner);
    }
}
