package com.bbits.park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectZoneActivity extends AppCompatActivity {

    ListView lv;
    GlobalContext data= new GlobalContext();
    JSONArray zones=null;
    Object selectedCityId= data.getSelectedCityId();
    String agentZone = "";

    SearchView search;
    List<String> zoneNames = new ArrayList<>();
    ArrayAdapter<String> adapter;

    public interface VolleyCallback{
        void onSuccess(JSONArray result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);


        search = (SearchView) findViewById(R.id.searchBar);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (zoneNames.contains(query.toLowerCase())) {
                    adapter.getFilter().filter(query);
//                    Toast.makeText(SelectZoneActivity.this, "Match found", Toast.LENGTH_LONG).show();

                } else {
//                    Toast.makeText(SelectZoneActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                for(int i =0; i<zoneNames.size(); i++){

                    if (zoneNames.get(i).toLowerCase().contains(s.toLowerCase())) {
                        adapter.getFilter().filter(s);
//                        Toast.makeText(SelectZoneActivity.this, "Match found", Toast.LENGTH_LONG).show();
                    } else {
//                        Toast.makeText(SelectZoneActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }
        });
//        Bundle extras = getIntent().getExtras();
        String extras = getIntent().getStringExtra("setAgentZone");
//        Toast.makeText(this, extras, Toast.LENGTH_SHORT).show();

        if (extras != null) {
//            String value = extras.getString("setAgentZone");
            agentZone = extras;

            //The key argument here must match that used in the other activity
        }
        try {
            callApi("https://connectedparking.ca/api/getZonesById",new SelectZoneActivity.VolleyCallback() {
                @Override
                public void onSuccess(JSONArray result) {
                        Toast t;
                        if(result.toString()!="[]") {

                                data.setZones(result);
                                zones=result;

                            lv = (ListView) findViewById(R.id.listView);

                            //Creating an empty ArrayList of type Object
                            List<Object> zonesNames = new ArrayList<>();

                            ArrayList zonesIds = new ArrayList();

                            //Checking whether the JSON array has some value or not
                            if (zones != null) {
                                //Iterating JSON array
                                for (int i=0;i<zones.length();i++){
                                    //Adding each element of JSON array into ArrayList
                                    try {
                                        JSONObject rec = zones.getJSONObject(i);
                                        String zone = rec.getString("zone_name");
                                        String id = rec.getString("_id");
                                        Log.i("Zones gg:",zone);
                                        zonesIds.add(id);
                                        zonesNames.add(zone);
                                        zoneNames.add(zone);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }

//                                ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(
//                                        SelectZoneActivity.this,
//                                        android.R.layout.simple_list_item_1,
//                                        zonesNames);
//
//                                lv.setAdapter(arrayAdapter);

                                adapter = new ArrayAdapter<String>(SelectZoneActivity.this, android.R.layout.simple_list_item_1,zoneNames);
                                lv.setAdapter(adapter);

                                AdapterView.OnItemClickListener cityClickedHandler = new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                                        // Do something in response to the click
                                        Log.i("City clicked", String.valueOf(zonesIds.get((int) id)));
//                                        data.setSelectedZoneName((String) zonesNames.get((int) id));
//                                        data.setSelectedZoneId((String) zonesIds.get((int) id));

                                        Log.i("GetSelectedZone ", String.valueOf(data.getSelectedZoneName()));
//                                        Toast.makeText(SelectZoneActivity.this, data.getSelectedZoneId().toString(), Toast.LENGTH_SHORT).show();

//                                        Log.e("LL",agentZone);

                                        if(agentZone.equals("true")){
//                                            Log.e("LLjjk",agentZone);
//                                            Toast.makeText(SelectZoneActivity.this, "AgentZone", Toast.LENGTH_SHORT).show();
                                            data.setAgentZoneName((String) zonesNames.get((int) id));
                                            data.setAgentZoneId((String) zonesIds.get((int) id));
                                            finish();
                                        }
                                        else{
                                            data.setSelectedZoneName((String) zonesNames.get((int) id));
                                            data.setSelectedZoneId((String) zonesIds.get((int) id));
                                            startActivity(new Intent(SelectZoneActivity.this, SelectTicketActivity.class));
                                        }


                                    }
                                };

                                lv.setOnItemClickListener(cityClickedHandler);
                            }


//                            t = Toast.makeText(SelectZoneActivity.this, "Zones:" + data.getZones(), Toast.LENGTH_SHORT);
//                                t.show();

                        }
                        else{
                            t = Toast.makeText(SelectZoneActivity.this, "ERROR!", Toast.LENGTH_SHORT);
                            t.show();
                        }

                    }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

//        zones = data.getZones();

    }

//    @Override
//    public void onBackPressed(){
////        data.setPlateNum("");
////        data.setParkingStatus("Status");
//        finish();
////        startActivity(new Intent(SelectZoneActivity.this, AnprActivity.class));
//    }
    void callApi(String api,final SelectZoneActivity.VolleyCallback callback) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("password", String.valueOf(password.getText()));

//        Toast.makeText(this, String.valueOf(selectedCityId), Toast.LENGTH_SHORT).show();
        jsonBody.put("id", String.valueOf(selectedCityId));

        queue.add(new JsonRequest<JSONArray>(Request.Method.POST,api,jsonBody.toString(),new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("Response", String.valueOf(response));
                callback.onSuccess(response);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.e("Error",error.toString());
            }
        }) {
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

}