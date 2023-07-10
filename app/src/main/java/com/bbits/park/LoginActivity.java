package com.bbits.park;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText email,password;
    boolean isValid=true;
    boolean auth=false;
    boolean shouldRemember, shouldAutologin;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    CheckBox remember,autoLogin;
    GlobalContext data= new GlobalContext();
    AlertDialog.Builder builder;
    DialogInterface.OnClickListener dialogClickListener;
    private ProgressBar spinner;
    ImageButton showPassword;

    public interface VolleyCallback{
         void onSuccess(JSONObject result);
    }

    @Override
    public void onBackPressed() {
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.Theme_ConnectedParking);
        super.onCreate(savedInstanceState);
//        "@style/Theme.ConnectedParking"

        setContentView(R.layout.activity_login);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
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

        login = findViewById(R.id.login_btn);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        remember = findViewById(R.id.rememberCheckbox);
        autoLogin = findViewById(R.id.autologinCheckbox);

        showPassword = findViewById(R.id.showPassword);
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(LoginActivity.this,String.valueOf(password.getInputType()), Toast.LENGTH_SHORT).show();
//                if(password.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                    Toast.makeText(LoginActivity.this,"kkk", Toast.LENGTH_SHORT).show();
//                }
//                else if(password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
//                    Toast.makeText(LoginActivity.this,"llll", Toast.LENGTH_SHORT).show();
//
//                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//
//                }

                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            }
        });

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        shouldRemember = loginPreferences.getBoolean("remember", false);

        if (shouldRemember == true) {
            email.setText(loginPreferences.getString("email", ""));
            password.setText(loginPreferences.getString("password", ""));
            remember.setChecked(true);
        }

        shouldAutologin = loginPreferences.getBoolean("autoLogin", false);
        if (shouldAutologin == true) {
            autoLogin.setChecked(true);
            data.setUserId(loginPreferences.getString("userId", ""));
            data.setEmail(loginPreferences.getString("email", ""));
            String strJson = loginPreferences.getString("cities", "0");
            if (strJson != null) {
                try {
                    JSONArray response = new JSONArray(strJson);
                    data.setCities(response);
                    startActivity(new Intent(LoginActivity.this, SelectCityActivity.class));
                } catch (JSONException e) {
                    Toast.makeText(this, "Cities could not be fetched!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    loginUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.e("Error", String.valueOf(b));
                remember.setChecked(b);
            }
        });

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Log.e("Error", String.valueOf(b));
                autoLogin.setChecked(b);
            }
        });
    }
    void callApi(String api,final VolleyCallback callback) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", String.valueOf(email.getText()));
        jsonBody.put("password", String.valueOf(password.getText()));
//        jsonBody.put("email", "ahmer@bbits.solutions");
//        jsonBody.put("password", "12345");
        final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, api, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        textView.setText("Response: " + response.toString());
                        Log.i("Response",response.toString());

                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    void loginUser() throws JSONException {

        isValid= true;
        Log.i("EMAIL:", String.valueOf(email.getText()));
                if (isEmpty(email)){
                    Toast t= Toast.makeText (this,"You must enter email to login!", Toast.LENGTH_SHORT);
                    t.show();
                    isValid=false;
                }
                if (isEmpty(password)){
                    password.setError("Password is required!") ;
                    isValid=false;
                }

                if (isEmail(email) == false) {
                    email.setError("Enter valid email!");
                    isValid=false;
                }

        if(isValid){
            spinner.setVisibility(View.VISIBLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//            getWindow().setContentView(View.INVISIBLE);


            callApi("https://connectedparking.ca/api/agent_login", new LoginActivity.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    try {
                        Toast t;
                        if(result.getString("auth")=="true") {

                            auth= true;
                            try {
                                data.setUserId(String.valueOf(result.getJSONObject("result").getString("_id")));
                                data.setEmail(String.valueOf(result.getJSONObject("result").getString("_id")));
                                data.setCities((result.getJSONObject("result").getJSONArray("cities")));

                                if (remember.isChecked()) {
//                                    Toast.makeText(LoginActivity.this, "HHHH", Toast.LENGTH_SHORT).show();
                                    loginPrefsEditor.putBoolean("remember", true);
                                    loginPrefsEditor.putString("email", String.valueOf(email.getText()));
                                    loginPrefsEditor.putString("password",String.valueOf( password.getText()));
                                    loginPrefsEditor.commit();
                                }
//                                else {
//                                    loginPrefsEditor.clear();
//                                    loginPrefsEditor.commit();
//                                }

                                if (autoLogin.isChecked()) {
                                    loginPrefsEditor.putBoolean("autoLogin", true);
                                    loginPrefsEditor.putString("userId", data.getUserId());
                                    loginPrefsEditor.putString("email",String.valueOf(email.getText()));
                                    loginPrefsEditor.putString("cities", data.getCities().toString());
                                    loginPrefsEditor.commit();
                                }
//                                else {
//                                    loginPrefsEditor.clear();
//                                    loginPrefsEditor.commit();
//                                }
                                if(!autoLogin.isChecked()&&!remember.isChecked()){
                                    loginPrefsEditor.clear();
                                    loginPrefsEditor.commit();
                                }

                                 if(result.getJSONObject("result").getString("forget_password") == "true"){
                                     Toast.makeText(LoginActivity.this, "Please Change Your Password", Toast.LENGTH_SHORT).show();
                                     startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
                                 }
                                 else{
                                     Toast.makeText(LoginActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                                     startActivity(new Intent(LoginActivity.this, SelectCityActivity.class));
                                 }

//                                 Log.e("RESSSSULT", result.toString());

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                        else{
                            t = Toast.makeText(LoginActivity.this, "Authentication Failed: "+result.getString("msg"), Toast.LENGTH_SHORT);
                            t.show();

                        }
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//                        getWindow().setContentView(View.VISIBLE);
                    } catch (JSONException e) {
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        throw new RuntimeException(e);
                    }
                }

            });
        }
    }
}