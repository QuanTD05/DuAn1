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



public class YoGaNangCaoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoItem> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_yo_ga_nang_cao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yoga nâng cao");
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
        videoList.add(new VideoItem("Ngày 1: Giảm mở bụng dưới " , "tHPzPLKRoIc?si=04MKRmdcA8ueg41r")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 2: Chinh Phục Tư Thế Bánh Xe" , "8GrnEiq0A14?si=3hYNnbE6TXG4RrKm"));
        videoList.add(new VideoItem("Ngày 3: Kéo giãn toàn thân căng tràn sức sống" , "Ybn4Zcc34v4?si=KB-N4jN-tY8hIoKn")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 4: Mở háng , ngã sau" , "RO6cE10KKk8?si=1tgPdzhZr3tu7OhJ")); // Replace with real video ID
        videoList.add(new VideoItem("Ngày 5: Chuyên đề Backbend Ngả sau" , "lLgyHvWS5CA?si=GGP2dDiWSzATR3G-"));
        videoList.add(new VideoItem("Ngày 6: Vặn xoắn thải độc , giảm mỡ bụng nhanh" , "w2q3nDB4-BU?si=NrjooK3ek8-jSRaY"));
        videoList.add(new VideoItem("Ngày 7: Vắt mỡ toàn thân" , "6H8UcqBM2PE?si=_onFvz0kvXUZm0zM"));

        // Set adapter
        adapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(adapter);
    }
}