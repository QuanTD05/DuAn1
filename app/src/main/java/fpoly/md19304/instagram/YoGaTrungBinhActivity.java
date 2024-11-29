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

public class YoGaTrungBinhActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoItem> videoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_yo_ga_trung_binh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yoga Trung Bình");
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
        videoList.add(new VideoItem("Ngày 1:Tác động toàn thân : dẻo dai , thăng bằng " , "w1SLjUOF_JM?si=fmwlwKheRGTBZUwm")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 2:Săn đùi khỏe bụng eo thon" , "BbUUMFh8JK0?si=yIDYLGKnDmc4UXZx"));
        videoList.add(new VideoItem("Ngày 3: Chăm sóc cột sống dẻo dai khỏe mạnh" , "K22TiARWamM?si=jOwC4K34fAm0X0He")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 4: Kéo giãn cơ thể, khỏe cơ xương khớp" , "irGb0UKCevc?si=_o16Tsm-tQ5AXZ2z")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 5: Tăng cường nội tiết tố, làm khỏe cơ lõi, cơ sàn chậu " , "ckOzCF-L_hA?si=ZofxFQ59u78VwuhF"));
        videoList.add(new VideoItem("Ngày 6:Mở Háng Kéo Giãn Toàn Thân" , "AYwhcDiz6IM?si=hPfscSOozVOYVXOn"));
        videoList.add(new VideoItem("Ngày 7: Vận Động Toàn Thân" , "TyyanvYjFT8?si=bmijF3zXBHW3Wg9Q"));

        // Set adapter
        adapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(adapter);
    }
}