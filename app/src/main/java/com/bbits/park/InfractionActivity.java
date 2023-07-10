package com.bbits.park;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

public class InfractionActivity extends AppCompatActivity {

    GlobalContext data= new GlobalContext();
    CardView enforcementBtn;
    MaterialToolbar headerTitle;
    AlertDialog.Builder builder;
    DialogInterface.OnClickListener dialogClickListener;
    TextView headerRight;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    public void onBackPressed() {
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infraction);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        headerRight = findViewById(R.id.headerRight);

        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPrefsEditor.remove("autoLogin");
                loginPrefsEditor.commit();
                startActivity(new Intent(InfractionActivity.this, LoginActivity.class));
                finish();
//                loginPrefsEditor.clear();
//                loginPrefsEditor.commit();

            }
        });

        builder = new AlertDialog.Builder(this);
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        finishAffinity();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        builder.setMessage("Are You Sure You Want To Exit?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);

        enforcementBtn= findViewById(R.id.enforcementButton);
        headerTitle = findViewById(R.id.header);

        headerTitle.setTitle(data.getSelectedCityName().toString());

        enforcementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InfractionActivity.this, MenuActivity.class));

            }
        });
    }
}