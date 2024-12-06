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

public class ThienActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<VideoItem> videoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thien);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thiền");
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
        videoList.add(new VideoItem("Bài thiền 5 phút - Thay đổi cuộc sống", "P_AG6tLafH4")); // Replace with real video ID
        videoList.add(new VideoItem("Thiền 10 phút thư giãn", "fthnDEm29nc?si=qnDX-deJUSBslriT")); // Replace with real video ID
        videoList.add(new VideoItem("Thiền 10 phút thư giãn", "wJOAC8EXWcE?si=is-mBVgmQS4CcrLf"));
        videoList.add(new VideoItem("Thiền 15 phút thư giãn", "ZOn3fneDkxU?si=9O-dfRz21Nf5eqEQ"));
        videoList.add(new VideoItem("Thiền 20 phút thư giãn", "ZOn3fneDkxU?si=wsPL0N7WJKQT_3Hj"));
        // Set adapter
        adapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(adapter);
    }


}