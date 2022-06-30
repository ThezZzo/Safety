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
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.components.Component;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.core.DocumentViewChange;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    LinearLayout layout;
    RelativeLayout relativeLayout;
    Source source = Source.CACHE;
    private int dpHeight;
    private int dpWidth;
    private float dDensity;
    private int designHeight = 812;
    private int designWidth = 375;

    private static final String TAG = "MyApplication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Подключаем базу данных
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getData();

        // Установка параметров Layout
        layout = findViewById(R.id.linearlayoutid);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = 20;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = 175;
        params.setMargins(50,30,50,0);
        int tvtextsize = 14;
        //

        RelativeLayout viewHeader = new RelativeLayout(this.getApplicationContext());
        viewHeader.setBackgroundColor(Color.BLACK);
        viewHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 100));
        viewHeader.setGravity(Gravity.TOP);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.addContentView(viewHeader,params1);


        DisplayMetrics metrics = getResources().getDisplayMetrics();

        dpHeight = (metrics.heightPixels);
        dpWidth = (metrics.widthPixels);
        dDensity = (metrics.scaledDensity);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }

        //
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(10);
        shape.setStroke(4, Color.rgb(94,209,52));
        shape.setColor(Color.WHITE);

        // Индикатор загрузки
        relativeLayout = findViewById(R.id.relativelayotid);
        RelativeLayout.LayoutParams paramBar = new RelativeLayout.LayoutParams(200, 200);
        paramBar.addRule(RelativeLayout.CENTER_IN_PARENT);
        ProgressBar progressBar = new ProgressBar(MainActivity.this,null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        relativeLayout.addView(progressBar, paramBar);
        progressBar.setVisibility(View.VISIBLE);
        //


        // Берем информацию из базы данных

        db.collection("Buttons")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // Цикл для прослушивания всех документов в коллекции
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Создание кнопок
                                Button button = new Button(MainActivity.this);
                                String name = document.getString("name");
                                button.setText(name);
                                button.setTextColor(Color.BLACK);
                                button.setBackground(shape);
                                button.setTextSize(tvtextsize);
                                layout.addView(button, params);

                                //

                                // Настройка кликабельности
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String phone = document.getString("phone");
                                        AlertDialog(phone);
                                    }
                                });
                            }
                        }

                        // Создание кнопки с инструкцией
                        Button button = new Button(MainActivity.this);
                        button.setText("Что делать, если...\uD83D\uDCDA");
                        button.setTextColor(Color.BLACK);
                        button.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        layout.addView(button, params);
                        button.setTextSize(tvtextsize);
                        button.setBackground(shape);

                        // Настройка обработчика событий
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this,
                                        InstructionsActivity.class);
                                startActivity(intent);
                            }
                        });
                        progressBar.setVisibility(View.INVISIBLE); // Скрытие индикатора прогресса
                        // после звершения чтения данных и создания кнопок
                    }
                });
    }

    // Метод создания диалогового окна
    public void AlertDialog(String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Предупреждение");
        builder.setMessage("Требуется подтверждение вызова. " +
                "При нажатии «Да» ваш номер телефона и местоположение будут " +
                "автоматически направлены в Центр обработки вызовов");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (phone.trim().length() > 0) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
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

    public void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collectionGroup("ButtonPhone").get();
        db.collectionGroup("ButtonText").get();
        db.collection("Instructions").get();
    }

    public int calcHeight(float value) {
        return(int) (dpHeight * (value/designHeight));
    }

    public int calcWidth(float value) {
        return(int) (dpWidth * (value/designWidth));
    }
}