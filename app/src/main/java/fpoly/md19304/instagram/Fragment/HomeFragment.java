package fpoly.md19304.instagram.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fpoly.md19304.instagram.AnUongActivity;
import fpoly.md19304.instagram.BaoThucActivity;
import fpoly.md19304.instagram.ChayBoActivity;
import fpoly.md19304.instagram.Model.User;
import fpoly.md19304.instagram.R;
import fpoly.md19304.instagram.ThienActivity;
import fpoly.md19304.instagram.TintucActivity;
import fpoly.md19304.instagram.YogaActivity;

public class HomeFragment extends Fragment {
    WebView webView;
    private ImageView imageProfile;
    private TextView fullname;
    private FirebaseUser firebaseUser;
    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid", "none");
        imageProfile = view.findViewById(R.id.profile_image);
        fullname = view.findViewById(R.id.name_text);
        loadUserInfo();
         LinearLayout baothuc = view.findViewById(R.id.baothuc);

         baothuc.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getContext(), BaoThucActivity.class));
             }
         });


         LinearLayout yoga = view.findViewById(R.id.Yoga);
         yoga.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getContext(), YogaActivity.class));
             }
         });
         LinearLayout thien = view.findViewById(R.id.thien);
         thien.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getContext(), ThienActivity.class));
             }
         });
         LinearLayout chaybo = view.findViewById(R.id.chayBo);
         chaybo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getContext(), ChayBoActivity.class));
             }
         });


         LinearLayout anuong = view.findViewById(R.id.anuong);
         anuong.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getContext(), AnUongActivity.class));
             }
         });










        webView =view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set up WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Open the link in DetailActivity instead of external browser
                Intent intent = new Intent(getContext(), TintucActivity.class);
                intent.putExtra("URL", url);  // Pass the URL to DetailActivity
                startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject JavaScript to hide an element by class or id
                view.evaluateJavascript(
                        "(function() { " +
                                "   var element = document.querySelector('.header');" +  // Change '.header' to the specific class or ID you want to hide
                                "   if (element) element.style.display = 'none';" +
                                "})()",
                        null
                );
            }
        });

        // Load the initial URL
        webView.loadUrl("https://tuoitre.vn/suc-khoe.htm");



        return view;
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) return;
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(getContext()).load(user.getImageurl()).into(imageProfile);

                    fullname.setText(user.getFullname());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}