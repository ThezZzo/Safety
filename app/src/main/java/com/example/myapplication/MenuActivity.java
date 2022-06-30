package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MyApplication";
    private static final int REQUEST_CALL = 1;
    LinearLayout layout;
    Source source = Source.CACHE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_menu);
        TextView tv = (TextView) new TextView(MenuActivity.this);
        layout = findViewById(R.id.linearlayoutmenu);
        layout.addView(tv);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = 175;
        params.setMargins(50,30,50,30);

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(10);
        shape.setStroke(4, Color.rgb(94,209,52));
        shape.setColor(Color.WHITE);

        String id = getIntent().getStringExtra("id");
        db
                .collection("Instructions")
                .document(id)
                .get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        String text = document.getString("text").replace("_b", "\n");
                        tv.setText(text);
                        tv.setTextColor(Color.rgb(0,0,0));
                        tv.setPadding(30,50,30,0);
                        tv.setTextSize(18);
                    }
                });
        db
                .collection("Instructions")
                .document(id)
                .collection("ButtonPhone")
                .get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                Button button = new Button(MenuActivity.this);
                                button.setText(name);
                                button.setTextSize(13);
                                button.setTextColor(Color.BLACK);
                                button.setBackground(shape);
                                layout.addView(button, params);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String phone = document.getString("phone");
                                        AlertDialog(phone);
                                    }
                                });
                            }
                        }
                    }
                });
        db
                .collection("Instructions")
                .document(id)
                .collection("ButtonText")
                .get(source)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                Button button = new Button(MenuActivity.this);
                                button.setText(name);
                                button.setTextSize(14);
                                button.setTextColor(Color.BLACK);
                                button.setBackground(shape);
                                layout.addView(button, params);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String text = document.getString("text").replace("_b", "\n");
                                        setContentView(R.layout.read);
                                        TextView tv = (TextView) findViewById(R.id.textViewInstructions);
                                        tv.setText(text);
                                        tv.setTextColor(Color.rgb(0,0,0));
                                        tv.setPadding(30,50,30,0);
                                        tv.setTextSize(18);
                                    }
                                });
                            }
                        }
                    }
                });
    }
    public void AlertDialog(String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setTitle("Предупреждение");
        builder.setMessage("Требуется подтверждение вызова. При нажатии «Да» ваш номер телефона и местоположение будут автоматически направлены в Центр обработки вызовов");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (phone.trim().length() > 0) {
                    if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                    } else {
                        String dial = "tel:" + phone;
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    }
                }
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
