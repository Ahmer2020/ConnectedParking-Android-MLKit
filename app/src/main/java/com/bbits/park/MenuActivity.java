package com.bbits.park;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;


public class MenuActivity extends AppCompatActivity {
    GlobalContext data= new GlobalContext();
    CardView enforcementBtn, issueTicketBtn, myTicketsBtn;
    MaterialToolbar headerTitle;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    TextView headerRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        headerRight = findViewById(R.id.headerRight);

        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPrefsEditor.remove("autoLogin");
                loginPrefsEditor.commit();
                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                finish();
//                loginPrefsEditor.clear();
//                loginPrefsEditor.commit();

            }
        });

        enforcementBtn= findViewById(R.id.plateCheckButton);
        issueTicketBtn = findViewById(R.id.issueTicketButton);
        myTicketsBtn = findViewById(R.id.myTicketsButton);
        headerTitle = findViewById(R.id.header);

        headerTitle.setTitle(data.getSelectedCityName().toString());

        enforcementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, AnprActivity.class));
            }
        });

        issueTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, AnprActivity.class));

            }
        });

        myTicketsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, IssueHistoryActivity.class));
            }
        });
    }
}