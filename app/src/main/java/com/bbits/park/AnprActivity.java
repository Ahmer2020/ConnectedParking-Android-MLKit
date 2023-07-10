package com.bbits.park;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bbits.park.textdetector.TextRecognitionProcessor;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Function;

import com.bbits.park.GraphicOverlay;


public class AnprActivity extends AppCompatActivity {

    Button ticketBtn;
    CardView keyboardBtn;
    EditText plateNumEditText;
    TextView statusTv, automaticTv, headerRight;
    GlobalContext data= new GlobalContext();

    InputMethodManager imm;

    LinearLayout setAgentZone;

    ImageView watchTicketBtn;

    MaterialToolbar toolbar;

    public static InputFilter getAlphaNumericInputFilter(){
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                String jjj= source.toString().toUpperCase().replaceAll("\\s+", "");
                Log.e("jjj",jjj);
                return jjj;
            }
        };
    }


    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    void callApi(String api,final AnprActivity.VolleyCallback callback) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("plate", plateNumEditText.getText());
        jsonBody.put("city_id", data.getSelectedCityId());
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
                        Toast.makeText(AnprActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);

    }
    private GraphicOverlay graphicOverlay;
    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
                    cameraSource.setMachineLearningFrameProcessor(
                            (VisionImageProcessor) new TextRecognitionProcessor(this, new TextRecognizerOptions.Builder().build(), plateNumEditText));
//            plateNumEditText.setText(data.getPlateNum());
//            plateNumEditText.setText("lllll");



//            Log.i("kjijHHHHH:",data.getPlateNum());


        } catch (RuntimeException e) {
            Log.e("Can not create image processor: " + model, String.valueOf(e));
            Toast.makeText(
                            getApplicationContext(),
                            "Can not create image processor: " + e.getMessage(),
                            Toast.LENGTH_LONG)
                    .show();
        }
    }
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d("ll", "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d("ll", "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e("ll", "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;

    private static final int CAMERA_REQUEST = 1888;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anpr);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(AnprActivity.this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST);


        headerRight = findViewById(R.id.headerRight);
        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        setAgentZone = findViewById(R.id.setAgentZone);
        setAgentZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(AnprActivity.this, SelectZoneActivity.class);
                i.putExtra("setAgentZone","true");
                startActivity(i);

            }
        });
        watchTicketBtn = findViewById(R.id.watchTicketBtn);
        watchTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previousTickets activity
                startActivity(new Intent(AnprActivity.this, PreviousTicketsActivity.class));
            }
        });

        toolbar = findViewById(R.id.header);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AnprActivity.this, AddImageActivity.class));
            }
        });

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d("ll","Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d("ll","graphicOverlay is null");
        }

        createCameraSource("Text Recognition Latin");

        ticketBtn = findViewById(R.id.ticketBtn);
        keyboardBtn = findViewById(R.id.keyboardBtn);
        plateNumEditText = findViewById(R.id.editTextPlate);
        plateNumEditText.setEnabled(false);
        statusTv = findViewById(R.id.statusTextView);
        automaticTv = findViewById(R.id.automaticTv);

        if(data.getAgentZoneName()!=null)
            automaticTv.setText(data.getAgentZoneName().toString());

        ticketBtn.setEnabled(false);
        ticketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.getParkingStatus().equals("unpaid")) {
                    Toast.makeText(AnprActivity.this, "Parking Unpaid", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AnprActivity.this, SelectZoneActivity.class));
//                    finish();
                }

            }
        });

        keyboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plateNumEditText.setEnabled(true);
                plateNumEditText.requestFocus();
//                plateNumEditText.setFocusableInTouchMode(true);

                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(plateNumEditText, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        plateNumEditText.setFilters(new InputFilter[]{getAlphaNumericInputFilter()});


        plateNumEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                data.setPlateNum(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
//                if(data.getPlateNum().trim().length() != 0)

                    if(s.length() != 0) {
                        try {
                            callApi("https://connectedparking.ca/api/getParkingStatus", new AnprActivity.VolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {

                                    try {
                                        ticketBtn.setEnabled(true);
                                        watchTicketBtn.setEnabled(false);

                                        watchTicketBtn.setImageResource(R.drawable.watchticketgray);


                                        statusTv.setBackgroundColor(getColor(R.color.unpaidColor));
                                        statusTv.setTextColor(getColor(R.color.white));


                                        Log.i("Result", result.getString("status"));
                                        data.setParkingStatus(result.getString("status"));
                                        statusTv.setText(Character.toUpperCase(result.getString("status").charAt(0)) + result.getString("status").substring(1));
//                                data.setPlateNum(plateNumEditText.getText().toString());
                                        if (result.has("scofflaw")) {
                                            Toast.makeText(AnprActivity.this, "Scofflaw", Toast.LENGTH_SHORT).show();
//                                            Log.e("ticketsIssued", result.toString());
                                            data.setTicketsIssued(result.getJSONArray("ticket_issued"));
                                            watchTicketBtn.setImageResource(R.drawable.watchticketblue);
                                            watchTicketBtn.setEnabled(true);
                                        }
//                                    if(data.getParkingStatus().equals("unpaid")) {
//                                        Toast.makeText(AnprActivity.this, "Parking Unpaid", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(AnprActivity.this, SelectZoneActivity.class));
//                                    }
                                        else if (data.getParkingStatus().equals("paid")) {

                                            data.setParkingId(result.getString("parking_id"));
                                            Toast.makeText(AnprActivity.this, "Parking Paid", Toast.LENGTH_SHORT).show();
                                            data.setFrom(result.getString("from"));
                                            data.setTo(result.getString("to"));

                                            statusTv.append("\nStart: ");
                                            statusTv.append(data.getFrom());
                                            statusTv.append("\nEnd: ");
                                            statusTv.append(data.getTo());

                                            ticketBtn.setEnabled(false);
                                            statusTv.setBackgroundColor(getColor(R.color.paidColor));
                                            statusTv.setTextColor(getColor(R.color.paidExpiredTextColor));

                                            //implement paid in another zone
                                            if(data.getAgentZoneId()!=null){
                                                if(result.has("zone")){
                                                    if(!data.getAgentZoneId().equals(result.getJSONObject("zone").getJSONObject("_id"))){
                                                        data.setParkingStatus("paidInAnotherZone");
                                                        Toast.makeText(AnprActivity.this, "Parking Paid in Another Zone", Toast.LENGTH_SHORT).show();
                                                        statusTv.setText("Paid In Another Zone");
                                                        statusTv.append("\nZone: ");
                                                        statusTv.append(data.getAgentZoneName().toString());
                                                        statusTv.append("\nStart: ");
                                                        statusTv.append(data.getFrom());
                                                        statusTv.append("\nEnd: ");
                                                        statusTv.append(data.getTo());
                                                        statusTv.setBackgroundColor(getColor(R.color.paidInAnotherZoneColor));
                                                        statusTv.setTextColor(getColor(R.color.white));
                                                    }
                                                }
                                                else{
                                                    //preston visitor parking
                                                    if(!data.getAgentZoneName().toString().toLowerCase(Locale.ROOT).trim().equals("prestonvisitorparking")){
                                                        data.setParkingStatus("paidInAnotherZone");
                                                        Toast.makeText(AnprActivity.this, "Parking Paid in Another Zone", Toast.LENGTH_SHORT).show();
                                                        statusTv.setText("Paid In Another Zone");
                                                        statusTv.append("\nZone: ");
                                                        statusTv.append(data.getAgentZoneName().toString());
                                                        statusTv.append("\nStart: ");
                                                        statusTv.append(data.getFrom());
                                                        statusTv.append("\nEnd: ");
                                                        statusTv.append(data.getTo());
                                                        statusTv.setBackgroundColor(getColor(R.color.paidInAnotherZoneColor));
                                                        statusTv.setTextColor(getColor(R.color.white));
                                                    }
                                                }

//                                            Log.e("Zone",result.toString());

                                            }



                                        } else if (data.getParkingStatus().equals("expired")) {

                                            statusTv.append("\nStart: ");
                                            statusTv.append(result.getString("from"));
                                            statusTv.append("\nEnd: ");
                                            statusTv.append(result.getString("to"));


                                            statusTv.setBackgroundColor(getColor(R.color.expiredColor));
                                            statusTv.setTextColor(getColor(R.color.paidExpiredTextColor));

                                            data.setFrom(result.getString("from"));
                                            data.setTo(result.getString("to"));
                                            Toast.makeText(AnprActivity.this, "Parking Expired", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(AnprActivity.this, SelectZoneActivity.class));

                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
            }
        });

//        data.setOnPlateChangeListener(new GlobalContext.OnPlateChangeListener()
//        {
//            @Override
//            public void onPlateChanged(String newValue)
//            {
//                //Do something here
//                statusTv.setText(data.getPlateNum());
//                Toast.makeText(AnprActivity.this, newValue, Toast.LENGTH_SHORT).show();
//            }
//        });


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("ll,", "onResume");
        createCameraSource("Text Recognition Latin");
        startCameraSource();
        if(data.getAgentZoneName()!=null)
            automaticTv.setText(data.getAgentZoneName().toString());
        else
            automaticTv.setText("Automatic");

        data.setPlateNum("");
        data.setParkingStatus("Status");
        statusTv.setText("Status");
        statusTv.setBackgroundColor(getColor(R.color.unpaidColor));
        statusTv.setTextColor(getColor(R.color.white));
        plateNumEditText.setText("");
        ticketBtn.setEnabled(false);


        watchTicketBtn.setImageResource(R.drawable.watchticketgray);
        watchTicketBtn.setEnabled(false);
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }


}