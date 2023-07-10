package com.bbits.park;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    GlobalContext data= new GlobalContext();
    Button changePassword;
    EditText password1,password2;
    boolean isValid=true;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    AlertDialog.Builder builder;
    DialogInterface.OnClickListener dialogClickListener;

    private ProgressBar spinner;

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        changePassword = findViewById(R.id.change_password_btn);
        password1 = findViewById(R.id.editTextNewPassword);
        password2 = findViewById(R.id.editTextConfirmPassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changePassword();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    void callApi(String api,final LoginActivity.VolleyCallback callback) throws JSONException {

        spinner.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("email", data.getEmail());
        jsonBody.put("new_password", String.valueOf(password2.getText()));
        final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, api, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.i("Response",response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ChangePasswordActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    void changePassword() throws JSONException {

        isValid= true;
        if (isEmpty(password1)){
            Toast t= Toast.makeText (this,"You must enter a new password to login!", Toast.LENGTH_SHORT);
            t.show();
            password1.setError("Password is required!") ;
            isValid=false;
        }

        if (isEmpty(password2)){
            Toast t= Toast.makeText (this,"You must enter a new password to login!", Toast.LENGTH_SHORT);
            t.show();
            password2.setError("Password is required!") ;
            isValid=false;
        }

        if(!(isEmpty(password1)||isEmpty(password2))){
            if(!password1.getText().toString().equals(password2.getText().toString())){
                Toast.makeText(ChangePasswordActivity.this,"Passwords Not Matching!",Toast.LENGTH_SHORT).show();
                isValid=false;
            }
        }

        if(isValid){
//            spinner.setVisibility(View.VISIBLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            callApi("https://connectedparking.ca/api/changePassword", new LoginActivity.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        Toast t;
                        if(result.getString("auth")=="true") {
                            Toast.makeText(ChangePasswordActivity.this, "Password Has Been Changed!", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(ChangePasswordActivity.this, SelectCityActivity.class));
                            finish();
                        }
                        else{
                            t = Toast.makeText(ChangePasswordActivity.this, "Password Change Was Unsuccessful!", Toast.LENGTH_SHORT);
                            t.show();
                        }
//                        spinner.setVisibility(View.GONE);
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    } catch (JSONException e) {
//                        spinner.setVisibility(View.GONE);
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }


}