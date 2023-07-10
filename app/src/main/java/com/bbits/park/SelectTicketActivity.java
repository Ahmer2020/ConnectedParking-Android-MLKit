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

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SelectTicketActivity extends AppCompatActivity {

    ListView lv;
    GlobalContext data = new GlobalContext();
    JSONArray tickets = null;

    SearchView search;
    List<String> ticketNames = new ArrayList<>();
    ArrayAdapter<String> adapter;

    public interface VolleyCallback{
        void onSuccess(JSONArray result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ticket);

        search = (SearchView) findViewById(R.id.searchBar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (ticketNames.contains(query.toLowerCase())) {
                    adapter.getFilter().filter(query);
//                    Toast.makeText(SelectZoneActivity.this, "Match found", Toast.LENGTH_LONG).show();

                } else {
//                    Toast.makeText(SelectZoneActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                for(int i =0; i<ticketNames.size(); i++){
                    if (ticketNames.get(i).toLowerCase().contains(s.toLowerCase())) {
                        adapter.getFilter().filter(s);
//                        Toast.makeText(SelectZoneActivity.this, "Match found", Toast.LENGTH_LONG).show();
                    } else {
//                        Toast.makeText(SelectZoneActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }
        });

        try {
            callApi("https://connectedparking.ca/api/getTickets",new SelectZoneActivity.VolleyCallback() {
                @Override
                public void onSuccess(JSONArray result) {
                    Toast t;
                    if(result.toString()!="[]") {

//                        data.setZones(result);
                        tickets=result;

                        lv = (ListView) findViewById(R.id.listView);

                        //Creating an empty ArrayList of type Object
                        List<Object> ticketsNames = new ArrayList<>();

//                        ArrayList zonesIds = new ArrayList();

                        //Checking whether the JSON array has some value or not
                        if (tickets != null) {
                            //Iterating JSON array
                            for (int i=0;i<tickets.length();i++){
                                //Adding each element of JSON array into ArrayList
                                try {
                                    JSONObject rec = tickets.getJSONObject(i);
                                    String ticketName = rec.getString("ticket_name");
//                                    String id = rec.getString("_id");
//                                    Log.i("Zones gg:",zone);
//                                    zonesIds.add(id);
                                    ticketsNames.add(ticketName);
                                    ticketNames.add(ticketName);

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }

//                            ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(
//                                    SelectTicketActivity.this,
//                                    android.R.layout.simple_list_item_1,
//                                    ticketsNames);
//
//                            lv.setAdapter(arrayAdapter);

                            adapter = new ArrayAdapter<String>(SelectTicketActivity.this, android.R.layout.simple_list_item_1,ticketNames);
                            lv.setAdapter(adapter);

                            AdapterView.OnItemClickListener cityClickedHandler = new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView parent, View v, int position, long id) {
                                    // Do something in response to the click
//                                    Log.i("Ticket clicked", String.valueOf(ticketsNames.get((int) id)));
//                                    data.setSelectedZoneName((String) zonesNames.get((int) id));
//                                    data.setSelectedZoneId((String) zonesIds.get((int) id));

//                                    Log.i("GetSelectedZone ", String.valueOf(data.getSelectedZoneName()));
                                    try {
                                        data.setSelectedTicket(tickets.getJSONObject((int) id));
//                                        Toast.makeText(SelectTicketActivity.this, String.valueOf(tickets.getJSONObject((int) id)) , Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SelectTicketActivity.this, SummaryActivity.class));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            };

                            lv.setOnItemClickListener(cityClickedHandler);
                        }


//                        t = Toast.makeText(SelectTicketActivity.this, "Zones:" + data.getZones(), Toast.LENGTH_SHORT);
//                        t.show();

                    }
                    else{
                        try {
                            t = Toast.makeText(SelectTicketActivity.this, result.getJSONObject(0).getString("msg"), Toast.LENGTH_SHORT);
                            t.show();
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
    void callApi(String api,final SelectZoneActivity.VolleyCallback callback) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
//        Toast.makeText(this, String.valueOf(jsonBody), Toast.LENGTH_SHORT).show();
//        jsonBody.put("id", String.valueOf(selectedCityId));
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