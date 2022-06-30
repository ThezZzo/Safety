package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.Objects;

public class InstructionsActivity extends AppCompatActivity {
    private static final String TAG = "MyApplication";
    private static final int REQUEST_CALL = 1;
    RelativeLayout relativeLayout;
    LinearLayout layoutmain;
    LinearLayout readLayout;
    Source source = Source.CACHE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);
        layoutmain = findViewById(R.id.linearlayoutinstructions);

        readLayout = findViewById(R.id.readid);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = 175;
        params.setMargins(50,30,50,30);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        relativeLayout = findViewById(R.id.relativeinstructions);
        RelativeLayout.LayoutParams paramBar = new RelativeLayout.LayoutParams(200, 200);
        paramBar.addRule(RelativeLayout.CENTER_IN_PARENT);
        ProgressBar progressBar = new ProgressBar(InstructionsActivity.this,null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        relativeLayout.addView(progressBar, paramBar);
        progressBar.setVisibility(View.VISIBLE);


        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(10);
        shape.setStroke(4, Color.rgb(94,209,52));
        shape.setColor(Color.WHITE);

        db.collection("Instructions")
                .get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                Button button = new Button(InstructionsActivity.this);
                                String name = document.getString("name");
                                button.setText(name);
                                button.setTextSize(13);
                                button.setTextColor(Color.BLACK);
                                button.setBackground(shape);
                                layoutmain.addView(button, params);
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(InstructionsActivity.this,
                                                    MenuActivity.class);
                                            intent.putExtra("id",id);
                                            startActivity(intent);
                                        }
                                    });
                            }
                        }
                        Button button = new Button(InstructionsActivity.this);
                        button.setText("Еще больше информации");
                        button.setTextSize(13);
                        button.setTextColor(Color.BLACK);
                        button.setBackground(shape);
                        layoutmain.addView(button, params);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telefon-doveria.ru/"));
                                startActivity(browserIntent);
                            }

                        });
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
    }
}