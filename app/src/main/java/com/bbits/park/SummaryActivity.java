package com.bbits.park;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    GlobalContext data= new GlobalContext();
    TextView zoneTv, plateTv, ticketNameTv, ticketNumTv, amountTv, headerRight;
    ImageView  addImageBtn;
    MaterialToolbar toolbar;
    Button summaryButton;
    ListView fromTv;
    Integer number;
    JSONArray ticketAging=null;
    LinearLayout imagesView;

    LinearLayout.LayoutParams layoutParams;

    Boolean print = false;

    private ProgressBar spinner;


    public interface VolleyCallback{
        void onSuccessArray(JSONArray result);
        void onSuccessObject(JSONObject result);

    }

    void callApiObject(String api,JSONObject requestBody,final SummaryActivity.VolleyCallback callback) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, api, requestBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        textView.setText("Response: " + response.toString());
                        Log.i("Response",response.toString());
                        callback.onSuccessObject(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(SummaryActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
    void callApiArray(String api,JSONObject requestBody,final SummaryActivity.VolleyCallback callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new JsonRequest<JSONArray>(Request.Method.POST, api, requestBody.toString(), new Response.Listener<JSONArray>() {
                      @Override
                      public void onResponse(JSONArray response) {
//                        textView.setText("Response: " + response.toString());
                          Log.i("Response",response.toString());
                          callback.onSuccessArray(response);
                      }
                  }, new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          // TODO: Handle error
                          Log.e("Error", error.toString());

                      }
                  }){
                      @Override
                      protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse) {

                          try {
                              String jsonString = new String(networkResponse.data,
                                      HttpHeaderParser
                                              .parseCharset(networkResponse.headers));
                              return Response.success(new JSONArray(jsonString),
                                      HttpHeaderParser
                                              .parseCacheHeaders(networkResponse));
                          } catch (UnsupportedEncodingException e) {
                              return Response.error(new ParseError(e));
                          } catch (JSONException je) {
                              return Response.error(new ParseError(je));
                          }
                      }
                  }
        );

    }

    ArrayList<String> convertToBase64(ArrayList<Bitmap> bitmapArray){
        ArrayList<String> imagesBase64 = new ArrayList<String>();
        if(bitmapArray!=null){
            for(int i=0;i<bitmapArray.size();i++){
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapArray.get(i).compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                StringBuilder _sb = new StringBuilder(encoded);
                _sb.insert(0, "data:image/jpeg;base64,");
                imagesBase64.add(_sb.toString());

//                imagesBase64.add(encoded);

            }
        }
        else
            imagesBase64.add(null);
        return imagesBase64;
    }
    void issueTicket(){

        JSONObject requestBody = new JSONObject();
        Log.e("Selected Images:", String.valueOf(convertToBase64(data.getSelectedImages())));
        try {
            spinner.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//            String base64 = String.valueOf(convertToBase64(data.getSelectedImages())).replace("[", "").replace("]", "");
            ArrayList<String> base64 = convertToBase64(data.getSelectedImages());

            JSONArray ssd = new JSONArray(base64);

            Log.e("Base64",base64.toString());
            requestBody.put("city",data.getSelectedCityId());
            requestBody.put("zone",data.getSelectedZoneId());
            requestBody.put("ticket",data.getSelectedTicket().getString("_id"));
            requestBody.put("plate",data.getPlateNum());
            requestBody.put("issued_by",data.getUserId());
            requestBody.put("parking_status",data.getParkingStatus());
            requestBody.put("images",ssd);

            Log.e("RequestBody",requestBody.toString());
            if(data.getParkingId()!=null)
                requestBody.put("parking",data.getParkingId());

            callApiObject("https://connectedparking.ca/api/IssueTicket", requestBody,new SummaryActivity.VolleyCallback() {
                @Override
                public void onSuccessArray(JSONArray result) {}
                @Override
                public void onSuccessObject(JSONObject result) {

                    spinner.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    if(result.has("_id")){
                        Toast.makeText(SummaryActivity.this, "Ticket Issued Successfully!", Toast.LENGTH_SHORT).show();
                        summaryButton.setText("PRINT");
                        print = true;
                        addImageBtn.setEnabled(false);
                    }
                    else {
                        try {
                            Toast.makeText(SummaryActivity.this, result.getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onBackPressed() {
        if(print){
            startActivity(new Intent(SummaryActivity.this, InfractionActivity.class));
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        spinner=(ProgressBar)findViewById(R.id.progress);
        spinner.setVisibility(View.GONE);

//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//
////        RecyclerView myList = (RecyclerView) findViewById(R.id.imagesView);
////        myList.setLayoutManager(layoutManager);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("ticket",data.getSelectedTicket().getString("_id"));
            callApiArray("https://connectedparking.ca/api/getAgingByTicket", requestBody,new SummaryActivity.VolleyCallback() {
                @Override
                public void onSuccessArray(JSONArray result) {
                    Log.e("kk",result.toString());

                    if(result.toString()!="[]"){
                        ticketAging = result;

                        try {
                            number = parseInt(result.getJSONObject(0).getString("rate"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        number /= 100;

                        Log.i("Number",number.toString());

                        amountTv.append(number.toString());
                        String fromTo;
                        Integer rate;
                        List<String> fromToList = new ArrayList<>();

                        for(int j=0; j<ticketAging.length();j++){
                            fromTo="$";
                            try {
                                rate = parseInt(ticketAging.getJSONObject(j).getString("rate"))/100;
                                fromTo+=rate.toString();
                                if(ticketAging.getJSONObject(j).getString("applied_from") == "0"){
                                    fromTo+=" Within";
                                }
                                else
                                    fromTo+=" After";

                                try {
                                    if (ticketAging.getJSONObject(j).getString("applied_to") == null) {
                                        rate = parseInt(ticketAging.getJSONObject(j).getString("applied_from")) / 1440;
                                        fromTo += rate;
                                    } else {
                                        rate = parseInt(ticketAging.getJSONObject(j).getString("applied_to")) / 1440;
                                        fromTo += rate;
                                    }
                                }
                                catch (NumberFormatException e)
                                {
// log e if you want...
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            fromTo+=" days";
                            fromToList.add(fromTo);
                        }

//                        fromTv.setText(fromTo);

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                SummaryActivity.this,
                                android.R.layout.simple_spinner_item,
                                fromToList);

                        fromTv.setAdapter(arrayAdapter);
                        Toast.makeText(SummaryActivity.this, "Ticket's Aging Fetched Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            Toast.makeText(SummaryActivity.this, result.getJSONObject(0).getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }

                @Override
                public void onSuccessObject(JSONObject result) {

                }

            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        zoneTv = findViewById(R.id.zoneTextView);
        ticketNameTv = findViewById(R.id.ticketNameTextView);
        ticketNumTv = findViewById(R.id.ticketNumTextView);
        plateTv = findViewById(R.id.plateTextView);
        amountTv = findViewById(R.id.amountTextView);
        fromTv = findViewById(R.id.fromToListView);
        summaryButton = findViewById(R.id.summaryButton);
        headerRight = findViewById(R.id.headerRight);
        toolbar = findViewById(R.id.header);
        imagesView = findViewById(R.id.imagesView);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        addImageBtn = findViewById(R.id.addImageBtn);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SummaryActivity.this, AddImageActivity.class));
            }
        });

//        ArrayAdapter<Bitmap> imageAdapter = new ArrayAdapter<Bitmap>(
//                SummaryActivity.this,
//                android.R.layout.simple_spinner_item,
//                data.getSelectedImages());;
//

//        imagesView.setAdapter(new ArrayAdapter<Bitmap>(this, android.R.layout.simple_list_item_1, data.getSelectedImages()));

        if(data.getSelectedImages()!=null){
//            Toast.makeText(this, "HH", Toast.LENGTH_SHORT).show();
//            imagesView.setAdapter(new ImageAdapter(this, data.getSelectedImages()));
            imagesView.removeAllViews();
            ArrayList<Bitmap> images = data.getSelectedImages();
            for (int i = 0; i < images.size(); i++) {
                layoutParams.setMargins(20, 20, 20, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(SummaryActivity.this);
//                Log.e("LOL",images.get(i).toString());
                imageView.setImageBitmap(images.get(i));
//                imageView.setOnClickListener(documentImageListener);
                imageView.setLayoutParams(layoutParams);

                imagesView.addView(imageView);

            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(print){
                    data.setSelectedImages(null);
                }
                startActivity(new Intent(SummaryActivity.this, InfractionActivity.class));
            }
        });

        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(print){
                    startActivity(new Intent(SummaryActivity.this, InfractionActivity.class));
                    data.setSelectedImages(null);
                }
                finish();
            }
        });

        summaryButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View view){
            issueTicket();
        }
       } );

        zoneTv.setText(data.getSelectedZoneName().toString());
        try {
            ticketNameTv.setText(data.getSelectedTicket().getString("ticket_name"));
            ticketNumTv.setText(data.getSelectedTicket().getString("ticket_num_next"));
            plateTv.setText(data.getPlateNum());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onResume(){
        super.onResume();

//        if(data.getSelectedImages()!=null){
////            Toast.makeText(this, "HH", Toast.LENGTH_SHORT).show();
//
////            imagesView.setAdapter(new ImageAdapter(this, data.getSelectedImages()));
//        }

        if(data.getSelectedImages()!=null){
//            Toast.makeText(this, "HH", Toast.LENGTH_SHORT).show();
//            imagesView.setAdapter(new ImageAdapter(this, data.getSelectedImages()));
            ArrayList<Bitmap> images = data.getSelectedImages();
            imagesView.removeAllViews();

            for (int i = 0; i < images.size(); i++) {
                layoutParams.setMargins(20, 20, 20, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(SummaryActivity.this);
                Log.e("LOL",images.get(i).toString());
                imageView.setImageBitmap(images.get(i));
//                imageView.setOnClickListener(documentImageListener);
                imageView.setLayoutParams(layoutParams);

                imagesView.addView(imageView);
            }
        }

    }

//    public class ImageAdapter extends BaseAdapter {
//
//        private Context context;
//        private ArrayList<Bitmap> bitmapList;
//
//        public ImageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
//            this.context = context;
//            this.bitmapList = bitmapList;
//        }
//
//        public int getCount() {
//            return this.bitmapList.size();
//        }
//
//        public Object getItem(int position) {
//            return null;
//        }
//
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView imageView;
//            if (convertView == null) {
//                imageView = new ImageView(this.context);
//                imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
//                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
////                imageView.setBackgroundResource(R.layout.spinner_style);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//
//            imageView.setImageBitmap(this.bitmapList.get(position));
//            return imageView;
//        }
//    }

}