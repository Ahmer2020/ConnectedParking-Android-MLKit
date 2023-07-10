package com.bbits.park;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
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
import java.util.Set;

public class IssueHistoryActivity extends AppCompatActivity {

    ListView lv;
    GlobalContext data= new GlobalContext();
    JSONArray tickets=null;
    private ProgressBar spinner;
//    JSONAdapter jSONAdapter ;
    TextView headerRight;
    SearchView search;
    List<String> ticketNames = new ArrayList<>();
    SearchableAdapter sA;

    JSONObject selectedTicket;

   DialogFragment ticketDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_history);

        selectedTicket = new JSONObject();
        ticketDetail = new TicketDetailFragment();

        headerRight = findViewById(R.id.headerRight);
        headerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search = (SearchView) findViewById(R.id.searchBar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (ticketNames.contains(query)) {
                    sA.getFilter().filter(query);
//                    Toast.makeText(IssueHistoryActivity.this, "Match found", Toast.LENGTH_SHORT).show();

                } else {
//                    Toast.makeText(IssueHistoryActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                for(int i =0; i<ticketNames.size(); i++){
                    if (ticketNames.get(i).contains(s)) {
                        sA.getFilter().filter(s);
//                        Toast.makeText(IssueHistoryActivity.this, "Match found", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(IssueHistoryActivity.this, "No Match found", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }
        });

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        try {
            callApi("https://connectedparking.ca/api/getTicketsIssuedByAgent",new SelectZoneActivity.VolleyCallback() {
                @Override
                public void onSuccess(JSONArray result) {
                    spinner.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast t;
                    if(result.toString()!="[]") {

                        tickets=result;
                        lv =  findViewById(R.id.listView);

                        //Checking whether the JSON array has some value or not
                        if (tickets != null) {

                            JSONArray limited = new JSONArray();

                            for( int i = 0; i<tickets.length(); i++){
                                try {
                                    if(i==19) {
                                        break;
                                    }

                                    limited.put(tickets.getJSONObject(i));
                                    ticketNames.add(tickets.getJSONObject(i).getString("plate"));

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

//                            jSONAdapter = new JSONAdapter (IssueHistoryActivity.this,limited);//jArray is your json array
//                            lv.setAdapter(jSONAdapter);

                            sA = new SearchableAdapter (IssueHistoryActivity.this,limited);//jArray is your json array
                            lv.setAdapter(sA);

                            AdapterView.OnItemClickListener ticketClickedHandler = new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView parent, View v, int position, long id) {
                                    try {
                                        selectedTicket = limited.getJSONObject(position);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("data",selectedTicket.toString());
                                        ticketDetail.setArguments(bundle);
//                                        data.setSelectedHistoryTicket(limited.getJSONObject(position));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    ticketDetail.show(getSupportFragmentManager(), "Ticket Detail");
                                }
                            };
                            lv.setOnItemClickListener(ticketClickedHandler);
//                            adapter = new ArrayAdapter<String>(IssueHistoryActivity.this, android.R.layout.simple_list_item_1,);
//                            lv.setAdapter(adapter);

                        }
                    }
                    else{
                        try {
                            t = Toast.makeText(IssueHistoryActivity.this, result.getJSONObject(0).getString("msg"), Toast.LENGTH_SHORT);
                            t.show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            });
        } catch (JSONException e) {
            spinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            throw new RuntimeException(e);
        }
    }

    void callApi(String api,final SelectZoneActivity.VolleyCallback callback) throws JSONException {

        spinner.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("agent", data.getUserId());

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

//    class JSONAdapter extends BaseAdapter implements ListAdapter, Filterable {
//
//        private ItemFilter mFilter = new ItemFilter();
//        private List<String>originalData = null;
//        private List<String>filteredData = null;
//
//        private final Activity activity;
//        private final JSONArray jsonArray;
//
//        private JSONAdapter(Activity activity, JSONArray jsonArray) {
//            assert activity != null;
//            assert jsonArray != null;
//
//            this.jsonArray = jsonArray;
//            this.activity = activity;
//        }
//
//        @Override
//        public int getCount() {
//            if (null == jsonArray)
//                return 0;
//            else
//                return jsonArray.length();
//        }
//
//        @Override
//        public JSONObject getItem(int position) {
//            if (null == jsonArray) return null;
//            else
//                return jsonArray.optJSONObject(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            JSONObject jsonObject = getItem(position);
//
//            return jsonObject.optLong("id");
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null)
//                convertView = activity.getLayoutInflater().inflate(R.layout.tickethistory_layout, null);
//
//
//            TextView plate = (TextView) convertView.findViewById(R.id.plateNumTv);
//
//            TextView issuedAt = (TextView) convertView.findViewById(R.id.issuedAtTv);
//
//            TextView status = (TextView) convertView.findViewById(R.id.statusTv);
//
//            TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTv);
//
//            TextView zone = (TextView) convertView.findViewById(R.id.zoneTv);
//
//            TextView city = (TextView) convertView.findViewById(R.id.cityTv);
//
//            JSONObject json_data = getItem(position);
//            if (null != json_data) {
//                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
//                try {
//                    plateS = json_data.getString("plate");
//                    String[] parts = json_data.getString("issued_at").split("T"); //returns an array with the 2 parts
//                    String date = parts[0];
//                    String time = parts[1].substring(0, 5);
//                    issuedAtS = date + " " + time;
//
//                    statusS = json_data.getString("ticket_status").toUpperCase();
//                    ticketNumS = json_data.getString("ticket_num");
//                    zoneS = json_data.getJSONObject("zone").getString("zone_name");
//                    cityS = json_data.getJSONObject("city").getString("city_name");
//
//                    plate.setText(plateS);
//                    issuedAt.setText(issuedAtS);
//                    status.setText(statusS);
//                    ticketNum.setText(ticketNumS);
//                    zone.setText(zoneS);
//                    city.setText(cityS);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//            }
//
//            return convertView;
//        }
//
////        @Override
////        public  Filter getFilter() {
////            return null;
////        }
//
//        @Override
//        public Filter getFilter() {
//            return mFilter;
//        }
//
//        private class ItemFilter extends Filter {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//
//                String filterString = constraint.toString().toLowerCase();
//
//                FilterResults results = new FilterResults();
//
//                final List<String> list = originalData;
//
//                int count = list.size();
//                final ArrayList<String> nlist = new ArrayList<String>(count);
//
//                String filterableString;
//
//                for (int i = 0; i < count; i++) {
//                    filterableString = list.get(i);
//                    if (filterableStr.contains(filterString)) {
//                        nlist.add(filterableString);
//                    }
//                }
//
//                results.values = nlist;
//                results.count = nlist.size();
//
//                return results;
//            }
//
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                filteredData = (ArrayList<String>) results.values;
//                notifyDataSetChanged();
//            }
//
//
//        }
//
//    }

    public class SearchableAdapter extends BaseAdapter implements Filterable {

        private JSONArray originalData = null;
        private JSONArray filteredData = null;
        private LayoutInflater mInflater;
        private ItemFilter mFilter = new ItemFilter();

        public SearchableAdapter(Context context, JSONArray data) {
            this.filteredData = data;
            this.originalData = data;
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return filteredData.length();
        }

        public JSONObject getItem(int position) {
            try {
                return filteredData.getJSONObject(position);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public long getItemId(int position) {
            return position;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null)
//                convertView = IssueHistoryActivity.this.getLayoutInflater().inflate(R.layout.tickethistory_layout, null);
//
//
//            TextView plate = (TextView) convertView.findViewById(R.id.plateNumTv);
//
//            TextView issuedAt = (TextView) convertView.findViewById(R.id.issuedAtTv);
//
//            TextView status = (TextView) convertView.findViewById(R.id.statusTv);
//
//            TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTv);
//
//            TextView zone = (TextView) convertView.findViewById(R.id.zoneTv);
//
//            TextView city = (TextView) convertView.findViewById(R.id.cityTv);
//
//            JSONObject json_data = getItem(position);
//            if (null != json_data) {
//                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
//                try {
//                    plateS = json_data.getString("plate");
//                    String[] parts = json_data.getString("issued_at").split("T"); //returns an array with the 2 parts
//                    String date = parts[0];
//                    String time = parts[1].substring(0, 5);
//                    issuedAtS = date + " " + time;
//
//                    statusS = json_data.getString("ticket_status").toUpperCase();
//                    ticketNumS = json_data.getString("ticket_num");
//                    zoneS = json_data.getJSONObject("zone").getString("zone_name");
//                    cityS = json_data.getJSONObject("city").getString("city_name");
//
//                    plate.setText(plateS);
//                    issuedAt.setText(issuedAtS);
//                    status.setText(statusS);
//                    ticketNum.setText(ticketNumS);
//                    zone.setText(zoneS);
//                    city.setText(cityS);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//            }
//
//            return convertView;
//        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.tickethistory_layout, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.plate = convertView.findViewById(R.id.plateNumTv);
                holder.issuedAt = convertView.findViewById(R.id.issuedAtTv);
                holder.status = convertView.findViewById(R.id.statusTv);
                holder.ticketNum = convertView.findViewById(R.id.ticketNumTv);
                holder.zone = convertView.findViewById(R.id.zoneTv);
                holder.city = convertView.findViewById(R.id.cityTv);

                // Bind the data efficiently with the holder.

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // If weren't re-ordering this you could rely on what you set last time
            try {

                holder.plate.setText(filteredData.getJSONObject(position).getString("plate"));
                String[] parts = filteredData.getJSONObject(position).getString("issued_at").split("T"); //returns an array with the 2 parts
                String date = parts[0];
                String time = parts[1].substring(0, 5);
                holder.issuedAt.setText(date + " " + time);

                holder.status.setText(filteredData.getJSONObject(position).getString("ticket_status").toUpperCase());
                holder.ticketNum.setText(filteredData.getJSONObject(position).getString("ticket_num"));
                holder.zone.setText(filteredData.getJSONObject(position).getJSONObject("zone").getString("zone_name"));
                holder.city.setText(filteredData.getJSONObject(position).getJSONObject("city").getString("city_name"));

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return convertView;

        }

        class ViewHolder {
//            TextView text;


            TextView plate ;

            TextView issuedAt;

            TextView status;

            TextView ticketNum;

            TextView zone ;

            TextView city ;
        }

        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final JSONArray list = originalData;

                int count = list.length();
                final JSONArray nlist = new JSONArray();

                String filterableString;

                for (int i = 0; i < count; i++) {
                    try {
                        filterableString = list.getJSONObject(i).getString("plate");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (filterableString.toLowerCase().contains(filterString)) {
                        try {
                            nlist.put(list.getJSONObject(i));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                results.values = nlist;
                results.count = nlist.length();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (JSONArray) results.values;
                notifyDataSetChanged();
            }

        }
    }

    public static class TicketDetailFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View convertView = (inflater.inflate(R.layout.ticket_history_detail_layout, null));

            if (convertView == null)
                convertView = inflater.inflate(R.layout.ticket_history_detail_layout, null);

            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);

            TextView plate = (TextView) convertView.findViewById(R.id.plateTextView);

            TextView zone =  (TextView) convertView.findViewById(R.id.zoneTextView);

            TextView ticketNameTv =  (TextView) convertView.findViewById(R.id.ticketNameTextView);

            TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTextView);

            ListView fromTv =  convertView.findViewById(R.id.fromToListView);

            LinearLayout imagesView = convertView.findViewById(R.id.imagesView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            Bundle bundle = getArguments();
            String stringData = bundle.getString("data","");

            JSONObject json_data = null;
            try {
                json_data = new JSONObject(stringData);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (null != json_data) {
                Log.e("GGGGGG", json_data.toString());
                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
                try {

//                    if(json_data.getJSONObject("images")!=null){
////
//                        imagesView.removeAllViews();
//                        ArrayList<JSONArray> images = json_data.getJSONArray("images");
//                        for (int i = 0; i < images.size(); i++) {
//                            layoutParams.setMargins(20, 20, 20, 20);
//                            layoutParams.gravity = Gravity.CENTER;
//                            ImageView imageView = new ImageView(getActivity());
//                            imageView.setImageBitmap(images.get(i));
//                            imageView.setLayoutParams(layoutParams);
//
//                            imagesView.addView(imageView);
//
//                        }
//                    }


                    ticketName.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
                    plate.setText(json_data.getString("plate"));
                    zone.setText(json_data.getJSONObject("zone").getString("zone_name"));
                    ticketNameTv.setText(json_data.getJSONObject("ticket").getString("ticket_name"));
                    ticketNum.setText(json_data.getString("ticket_num"));

                    String[] parts = json_data.getString("issued_at").split("T");
                    String date = parts[0];
                    String time = parts[1].substring(0, 5);
                    List<String> fromToList = new ArrayList<>();

                    fromToList.add(date);
                    fromToList.add(time);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            fromToList);

                    fromTv.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            builder.setView(convertView);

//            builder.setView(inflater.inflate(R.layout.ticket_history_detail_layout, null));
            return builder.create();
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null)
//                convertView = getActivity().getLayoutInflater().inflate(R.layout.ticket_history_detail_layout, null);
//
//
//            TextView ticketName =  (TextView) convertView.findViewById(R.id.ticketNameTv);
//
//            TextView plate = (TextView) convertView.findViewById(R.id.plateTextView);
//
//            TextView zone =  (TextView) convertView.findViewById(R.id.zoneTextView);
//
//            TextView ticketNameTv =  (TextView) convertView.findViewById(R.id.ticketNameTextView);
//
//            TextView ticketNum = (TextView) convertView.findViewById(R.id.ticketNumTextView);
//
//            ListView fromTv =  convertView.findViewById(R.id.fromToListView);
//
//            JSONObject json_data = getItem(position);
//            if (null != selectedTicket) {
//                String plateS = null, issuedAtS = null, statusS = null, ticketNumS = null, zoneS = null, cityS = null;
//                try {
//                    plateS = json_data.getString("plate");
//                    String[] parts = json_data.getString("issued_at").split("T"); //returns an array with the 2 parts
//                    String date = parts[0];
//                    String time = parts[1].substring(0, 5);
//                    issuedAtS = date + " " + time;
//
//                    statusS = json_data.getString("ticket_status").toUpperCase();
//                    ticketNumS = json_data.getString("ticket_num");
//                    zoneS = json_data.getJSONObject("zone").getString("zone_name");
//                    cityS = json_data.getJSONObject("city").getString("city_name");
//
//                    plate.setText(plateS);
//                    issuedAt.setText(issuedAtS);
//                    status.setText(statusS);
//                    ticketNum.setText(ticketNumS);
//                    zone.setText(zoneS);
//                    city.setText(cityS);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//            }
//
//            return convertView;
//        }



    }
}