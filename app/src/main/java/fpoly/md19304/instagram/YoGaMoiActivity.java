package fpoly.md19304.instagram;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpoly.md19304.instagram.Adapter.VideoAdapter;
import fpoly.md19304.instagram.Model.VideoItem;

public class YoGaMoiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoItem> videoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_yo_ga_moi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yoga Cho người mới bắt đầu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String url = getIntent().getStringExtra("URL");
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add sample video data
        videoList = new ArrayList<>();
        videoList.add(new VideoItem("Ngày 1: Chuyển động cơ bản" , "Qz8s4-ul2Js?si=lp9sf9kg6dKeBBul")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 2: Kết nối hơi thỏ" , "bsaYicSIc84?si=V1GMoVQQcnSYwamQ"));
        videoList.add(new VideoItem("Ngày 3: Khởi động mạnh mẽ" , "_JAJYkIPVzc?si=pDhCNlYEPHciIzpg")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 4: Chuỗi chào mặt trời" , "1KpU1srbE3c?si=dzCX7OStPBBACOiJ")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 5: Săn chắc toàn thân" , "ubK5xWzl1bQ?si=h35QbdgiUqjh9H5B"));
        videoList.add(new VideoItem("Ngày 6: Trị liệu đau mỗi cổ vai gáy" , "8vSJtBtL95E?si=yPGI3Nc0Jm8rwzRg"));
        videoList.add(new VideoItem("Ngày 7: Đôi chân chắc khỏe" , "7DmHGaiIE1c?si=MPTIxkp9jDyzLy91"));

        // Set adapter
        adapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(adapter);
    }
}