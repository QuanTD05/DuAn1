package fpoly.md19304.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AnUongActivity extends AppCompatActivity {
    private EditText genderInput, heightInput, weightInput;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_an_uong);

        genderInput = findViewById(R.id.genderInput);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        saveButton = findViewById(R.id.saveButton);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profile");

        // Tải dữ liệu người dùng nếu đã lưu trước đó
        loadUserData();

        // Lưu dữ liệu khi nhấn nút
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String gender = snapshot.child("gender").getValue(String.class);
                    Double height = snapshot.child("height").getValue(Double.class);
                    Double weight = snapshot.child("weight").getValue(Double.class);

                    // Điền dữ liệu vào các trường nhập liệu
                    genderInput.setText(gender);
                    heightInput.setText(height != null ? String.valueOf(height) : "");
                    weightInput.setText(weight != null ? String.valueOf(weight) : "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnUongActivity.this, "Không thể tải dữ liệu.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void saveUserData() {
        String gender = genderInput.getText().toString().trim();
        String heightStr = heightInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr)) {
            Toast.makeText(this, "Vui lòng nhập chiều cao và cân nặng", Toast.LENGTH_SHORT).show();
            return;
        }

        double height = Double.parseDouble(heightStr);
        double weight = Double.parseDouble(weightStr);

        // Tạo dữ liệu để lưu
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("gender", gender);
        userMap.put("height", height);
        userMap.put("weight", weight);

        userRef.setValue(userMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AnUongActivity.this, "Dữ liệu đã được lưu.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AnUongActivity.this, AnUongTCActivity.class);
                intent.putExtra("Gender", gender);
                intent.putExtra("Height", height);
                intent.putExtra("Weight", weight);
                startActivity(intent);
            } else {
                Toast.makeText(AnUongActivity.this, "Không thể lưu dữ liệu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
